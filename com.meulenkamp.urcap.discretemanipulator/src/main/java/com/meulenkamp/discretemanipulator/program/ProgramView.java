package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.DashboardClient;
import com.meulenkamp.discretemanipulator.general.RobotRealtimeReader;
import com.meulenkamp.discretemanipulator.general.StyledView;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.meulenkamp.discretemanipulator.general.Style;
import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.meulenkamp.discretemanipulator.general.DashboardClient.ProgramState.PLAYING;

public class ProgramView
        extends StyledView
        implements SwingProgramNodeView<ProgramContribution> {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    protected final ViewAPIProvider apiProvider;
    protected JTextField textField;
    protected JLabel errorLabel = new JLabel("");
    private final JRadioButton clockwise = new JRadioButton();
    private final JRadioButton counterClockwise = new JRadioButton();

    private final JCheckBox leftSensorState = new JCheckBox("", false);
    private final JCheckBox rightSensorState = new JCheckBox("", false);

    private LiveControl liveControl;
    private Box jogButtons;

    private final AtomicBoolean shouldUpdate = new AtomicBoolean(true);

    public ProgramView(final ViewAPIProvider apiProvider, final Style style) {
        super(style);
        this.apiProvider = apiProvider;
    }

    @Override
    public void buildUI(
            final JPanel panel,
            final ContributionProvider<ProgramContribution> provider
    ) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(errorLabel);
        panel.add(style.createLargeVerticalSpacing());
        panel.add(createSensorPanel());
        panel.add(style.createLargeVerticalSpacing());
        panel.add(createJogButtons());
        panel.add(style.createLargeVerticalSpacing());
        panel.add(new JSeparator());
        panel.add(style.createVerticalSpacing());
        panel.add(createMovesInput(provider));
        panel.add(style.createVerticalSpacing());
        panel.add(createDirectionPanel(provider));
        panel.add(style.createLargeVerticalSpacing());
        panel.add(new JSeparator());
        panel.add(style.createLargeVerticalSpacing());
    }

    private ImageIcon scaledIcon(final String name, final int size) {
        return new ImageIcon(new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/icons/" + name + ".png"
        ))).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private JButton iconButton(final String name, final int size) {
        return new JButton(scaledIcon(name, size));
    }

    private JLabel iconLabel(final String name, final int size) {
        return new JLabel(scaledIcon(name, size));
    }

    private Component createSensorPanel() {
        final Box sensorStateBox = Box.createHorizontalBox();
        leftSensorState.setEnabled(false);
        rightSensorState.setEnabled(false);

        sensorStateBox.add(new JLabel("Sensors:"));
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(leftSensorState);
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(iconLabel("proximity-sensor", 48));
        sensorStateBox.add(new JLabel("  "));
        sensorStateBox.add(iconLabel("proximity-sensor", 48));
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(rightSensorState);

        return sensorStateBox;
    }

    private Box createJogButtons() {
        jogButtons = Box.createHorizontalBox();

        final JButton previous = iconButton("previous", 24);
        final JButton reverseFast = iconButton("fast-reverse", 24);
        final JButton reverseSlow = iconButton("reverse", 24);
        final JButton stop = iconButton("stop", 24);
        final JButton forwardSlow = iconButton("forward", 24);
        final JButton forwardFast = iconButton("fast-forward", 24);
        final JButton next = iconButton("next", 24);

        previous.addChangeListener(event -> {
            if (previous.getModel().isPressed()) {
                liveControl.previous();
            } else {
                liveControl.stop();
            }
        });
        previous.setToolTipText("Move to previous discrete position");
        reverseFast.addChangeListener(event -> {
            if (reverseFast.getModel().isPressed()) {
                liveControl.fastReverse();
            } else {
                liveControl.stop();
            }
        });
        reverseFast.setToolTipText("Jog counter-clockwise (fast)");
        reverseSlow.addChangeListener(event -> {
            if (reverseSlow.getModel().isPressed()) {
                liveControl.slowReverse();
            } else {
                liveControl.stop();
            }
        });
        reverseSlow.setToolTipText("Jog counter-clockwise (slow)");
        stop.addChangeListener(event -> {
            if (stop.getModel().isPressed()) {
                liveControl.stop();
            }
        });
        stop.setToolTipText("Stop movement");
        forwardSlow.addChangeListener(event -> {
            if (forwardSlow.getModel().isPressed()) {
                liveControl.slowForward();
            } else {
                liveControl.stop();
            }
        });
        forwardSlow.setToolTipText("Jog clockwise (slow)");
        forwardFast.addChangeListener(event -> {
            if (forwardFast.getModel().isPressed()) {
                liveControl.fastForward();
            } else {
                liveControl.stop();
            }
        });
        forwardFast.setToolTipText("Jog clockwise (fast)");
        next.addChangeListener(event -> {
            if (next.getModel().isPressed()) {
                this.liveControl.next();
            } else {
                liveControl.stop();
            }
        });
        next.setToolTipText("Move to next discrete position");

        jogButtons.add(new JLabel("Jog:"));
        jogButtons.add(style.createHorizontalSpacing());
        jogButtons.add(previous);
        jogButtons.add(reverseFast);
        jogButtons.add(reverseSlow);
        jogButtons.add(stop);
        jogButtons.add(forwardSlow);
        jogButtons.add(forwardFast);
        jogButtons.add(next);

        return jogButtons;
    }

    private Component createMovesInput(
            final ContributionProvider<ProgramContribution> provider
    ) {
        final Box box = Box.createHorizontalBox();

        box.add(new JLabel("Move"));
        box.add(style.createHorizontalSpacing());

        textField = new JTextField("1");
        textField.setFocusable(false);
        textField.setPreferredSize(style.getInputfieldSize());
        textField.setMaximumSize(textField.getPreferredSize());
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final KeyboardNumberInput<Integer> keyboardInput = provider
                        .get()
                        .getKeyboardForTextField();
                keyboardInput.show(
                        textField,
                        provider.get().getTextFieldCallback()
                );
            }
        });
        box.add(textField);
        box.add(style.createHorizontalSpacing());
        box.add(new JLabel("places"));

        return box;
    }

    public void setMovesText(final String text) {
        textField.setText(text);
    }

    private Component createDirectionPanel(
            final ContributionProvider<ProgramContribution> provider
    ) {
        final Box directionBox = Box.createHorizontalBox();
        final ButtonGroup group = new ButtonGroup();

        clockwise.setSelected(true);
        clockwise.addChangeListener(
                event -> provider.get().setDirection(clockwise.isSelected())
        );

        group.add(counterClockwise);
        group.add(clockwise);

        directionBox.add(new JLabel("Direction"));
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(counterClockwise);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(counterClockwise);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(iconLabel("turn-left", 24));
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(iconLabel("turn-right", 24));
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(clockwise);

        return directionBox;
    }

    public void errorMessage(final String message) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(message);
    }

    public void setDirection(final boolean direction) {
        clockwise.setSelected(direction);
        counterClockwise.setSelected(!direction);
    }

    public void setMoves(final int moves) {
        textField.setText(Integer.toString(moves));
    }

    public void startUpdating(
        DigitalIO leftSensor,
        DigitalIO rightSensor,
        DigitalIO fastOutput,
        DigitalIO slowOutput,
        DigitalIO reverseOutput
    ) {
        if(liveControl != null) this.liveControl.stop();

        this.shouldUpdate.set(true);

        this.liveControl = new LiveControl(
            leftSensor,
            rightSensor,
            fastOutput,
            slowOutput,
            reverseOutput
        );

        new Thread(() -> {
            try {
                final DashboardClient dashboardClient = new DashboardClient();
                if (!dashboardClient.connect("127.0.0.1")) {
                    errorMessage("Could not connect to dashboard server");
                    return;
                }
                while (this.shouldUpdate.get()) {
                    this.leftSensorState.setSelected(leftSensor.getValue());
                    this.rightSensorState.setSelected(rightSensor.getValue());
                    this.jogButtons.setEnabled(PLAYING != dashboardClient.programState());
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stopUpdating() {
        this.shouldUpdate.set(false);
        this.liveControl.stop();
    }
}
