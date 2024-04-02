package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.DashboardClient;
import com.meulenkamp.discretemanipulator.general.IOHandler;
import com.meulenkamp.discretemanipulator.installation.InstallationContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import static com.meulenkamp.discretemanipulator.general.DashboardClient.ProgramState.*;

public class ProgramContribution
        implements ProgramNodeContribution {
    public static final boolean FORWARD = true;
    private static final String DIRECTION = "direction";
    private static final boolean DEFAULT_DIRECTION = FORWARD;

    private static final String MOVES = "moves";
    private static final int DEFAULT_MOVES = 1;

    private final ProgramAPIProvider apiProvider;
    private final ProgramView view;
    private final UndoRedoManager undoRedoManager;
    private final KeyboardInputFactory keyboardFactory;
    private final DataModel model;

    private final IOHandler ioHandler;
    private volatile InstallationContribution installation;
    private final DashboardClient dashboardClient;

    public ProgramContribution(
            final ProgramAPIProvider apiProvider,
            final ProgramView view,
            final DataModel model
    ) {
        this.apiProvider = apiProvider;
        this.undoRedoManager = apiProvider
                .getProgramAPI()
                .getUndoRedoManager();
        this.keyboardFactory = apiProvider
                .getUserInterfaceAPI()
                .getUserInteraction()
                .getKeyboardInputFactory();
        this.view = view;
        this.model = model;

        this.installation = getInstallation();
        this.ioHandler = new IOHandler(apiProvider.getProgramAPI().getIOModel());
        this.dashboardClient = new DashboardClient();

        if (!dashboardClient.connect("127.0.0.1")) {
            view.errorMessage("Could not connect to dashboard server");
            throw new RuntimeException("Could not connect to dashboard server");
        }

        final Thread resetDaemon = new Thread(() -> {
            final DigitalIO fastOutput = ioHandler.getDigitalIO(installation.getFastOutput());
            final DigitalIO slowOutput = ioHandler.getDigitalIO(installation.getSlowOutput());
            final DigitalIO reverseOutput = ioHandler.getDigitalIO(installation.getReverseOutput());

            try {
                DashboardClient.ProgramState previousState = UNDEFINED;

                while(true) {
                    final DashboardClient.ProgramState newState = dashboardClient.programState();

                    // Stop the moving attachments when the program isn't playing anymore
                    if (previousState == PLAYING && newState != previousState) {
                        fastOutput.setValue(false);
                        slowOutput.setValue(false);
                        reverseOutput.setValue(false);
                    }
                    previousState = newState;
                    Thread.sleep(1);
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // last effort attempt at stopping the moving attachments
                fastOutput.setValue(false);
                slowOutput.setValue(false);
                reverseOutput.setValue(false);
            }
        }, "Reset IO daemon");
        resetDaemon.setDaemon(true);
        resetDaemon.start();
    }

    @Override
    public void openView() {
        this.installation = getInstallation();

        view.setDirection(getDirection());
        view.setMoves(getMoves());
        view.startUpdating(
                dashboardClient,
                ioHandler.getDigitalIO(installation.getLeftSensorInput()),
                ioHandler.getDigitalIO(installation.getRightSensorInput()),
                ioHandler.getDigitalIO(installation.getFastOutput()),
                ioHandler.getDigitalIO(installation.getSlowOutput()),
                ioHandler.getDigitalIO(installation.getReverseOutput())
        );
    }

    @Override
    public void closeView() {
        view.stopUpdating();
    }

    @Override
    public String getTitle() {
        return String.format(
                "Move manipulator %s %d places", getDirection() == FORWARD ? "up" : "down",
                getMoves()
        );
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public void generateScript(final ScriptWriter writer) {
        ScriptGenerator generator = new ScriptGenerator(writer);
        if (getDirection() == FORWARD) {
            generator.moveForward(getMoves());
        } else {
            generator.moveReverse(getMoves());
        }
        System.out.println("Resulting (program) script:\n\n" + writer.generateScript());
    }

    public boolean getDirection() {
        return model.get(DIRECTION, DEFAULT_DIRECTION);
    }

    public void setDirection(boolean direction) {
        undoRedoManager.recordChanges(() -> model.set(DIRECTION, direction));
    }

    public int getMoves() {
        return model.get(MOVES, DEFAULT_MOVES);
    }

    public void setMoves(int moves) {
        undoRedoManager.recordChanges(() -> model.set(MOVES, moves));
    }

    public KeyboardNumberInput<Integer> getKeyboardForTextField() {
        final KeyboardNumberInput<Integer> keyboardInput = keyboardFactory
                .createPositiveIntegerKeypadInput();
        keyboardInput.setInitialValue(this.model.get(MOVES, DEFAULT_MOVES));
        return keyboardInput;
    }

    public KeyboardInputCallback<Integer> getTextFieldCallback() {
        return new KeyboardInputCallback<Integer>() {
            @Override
            public void onOk(final Integer value) {
                if (value <= 0) {
                    view.errorMessage("Please enter a number greater than 0");
                    return;
                }
                setMoves(value);
                view.setMovesText(value.toString());
                view.errorMessage("");
            }
        };
    }

    private InstallationContribution getInstallation() {
        return apiProvider.getProgramAPI()
                .getInstallationNode(InstallationContribution.class);
    }
}
