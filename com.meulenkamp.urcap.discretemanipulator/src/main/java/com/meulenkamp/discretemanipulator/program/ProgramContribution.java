package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.IOHandler;
import com.meulenkamp.discretemanipulator.general.LiveControl;
import com.meulenkamp.discretemanipulator.installation.InstallationContribution;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

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
    private LiveControl liveControl;

    private final IOHandler ioHandler;

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
        this.ioHandler = new IOHandler(apiProvider.getProgramAPI().getIOModel());
    }

    @Override
    public void openView() {
        view.setDirection(getDirection());
        view.setMoves(getMoves());
        view.startUpdating(
            ioHandler.getDigitalIO(getInstallation().getLeftSensorInput()),
            ioHandler.getDigitalIO(getInstallation().getRightSensorInput())
        );
    }

    @Override
    public void closeView() {
        view.stopUpdating();
    }

    @Override
    public String getTitle() {
        return String.format(
                "Move manipulator %s %d places", getDirection() ? "up" : "down",
                getMoves()
        );
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public void generateScript(final ScriptWriter writer) {
        new ScriptGenerator(writer, model).move(getDirection(), getMoves());
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

    public LiveControl getLiveControl() {
        // FIXME I want to create LiveControl directly in ProgramView, but it's unclear if the values are updating in
        // between renders 🤔
        if (liveControl == null) {
            InstallationContribution installation = getInstallation();

            this.liveControl = new LiveControl(
                ioHandler.getDigitalIO(installation.getLeftSensorInput()),
                ioHandler.getDigitalIO(installation.getRightSensorInput()),
                ioHandler.getDigitalIO(installation.getFastOutput()),
                ioHandler.getDigitalIO(installation.getSlowOutput()),
                ioHandler.getDigitalIO(installation.getReverseOutput())
            );
        }
        return liveControl;
    }

    private InstallationContribution getInstallation() {
        return apiProvider.getProgramAPI()
                .getInstallationNode(InstallationContribution.class);
    }
}
