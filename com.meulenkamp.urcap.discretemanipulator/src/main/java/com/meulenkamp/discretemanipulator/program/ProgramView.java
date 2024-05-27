package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.DashboardClient;
import com.meulenkamp.discretemanipulator.general.Style;
import com.meulenkamp.discretemanipulator.general.StyledView;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.meulenkamp.discretemanipulator.general.DashboardClient.ProgramState.*;

public class ProgramView
        extends StyledView
        implements SwingProgramNodeView<ProgramContribution> {
    protected final ViewAPIProvider apiProvider;
    protected JTextField textField;
    protected JLabel errorLabel = new JLabel("");
    private final JRadioButton clockwise = new JRadioButton();
    private final JRadioButton counterClockwise = new JRadioButton();

    private final JCheckBox leftSensorState = new JCheckBox("", false);
    private final JCheckBox rightSensorState = new JCheckBox("", false);

    private final AtomicBoolean shouldUpdate = new AtomicBoolean(true);

    private LiveControl liveControl;
    private Box jogButtons;

    private volatile DashboardClient.ProgramState state = UNDEFINED;


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

    private ChangeListener createChangeListener(
            final JButton button, final Runnable action
    ) {
        return event -> {
            if (button.isEnabled()) {
                final Stream<Component> others = Stream
                        .of(jogButtons.getComponents())
                        .filter(other -> !Objects.equals(other, button));
                if (button.getModel().isPressed()) {
                    others.forEach(component -> component.setEnabled(false));
                    action.run();
                } else {
                    liveControl.stop();
                    others.forEach(component -> component.setEnabled(true));
                }
            }
        };
    }

    private Box createJogButtons() {
        jogButtons = Box.createHorizontalBox();

        final JButton previous = iconButton("previous", 24);
        final JButton reverseFast = iconButton("fast-reverse", 24);
        final JButton reverseSlow = iconButton("reverse", 24);
        final JButton forwardSlow = iconButton("forward", 24);
        final JButton forwardFast = iconButton("fast-forward", 24);
        final JButton next = iconButton("next", 24);

        jogButtons.add(new JLabel("Jog:"));
        jogButtons.add(style.createHorizontalSpacing());
        jogButtons.add(previous);
        jogButtons.add(reverseFast);
        jogButtons.add(reverseSlow);
        jogButtons.add(forwardSlow);
        jogButtons.add(forwardFast);
        jogButtons.add(next);

        Arrays.asList(jogButtons.getComponents())
                .forEach(component -> component.setEnabled(this.state == PLAYING || this.state == UNDEFINED));

        previous.addChangeListener(createChangeListener(
                previous, () -> liveControl.previous()
        ));
        previous.setToolTipText("Move to previous discrete position");
        reverseFast.addChangeListener(createChangeListener(
                reverseFast, () -> liveControl.fastReverse()
        ));
        reverseFast.setToolTipText("Jog counter-clockwise (fast)");
        reverseSlow.addChangeListener(createChangeListener(
                reverseSlow, () -> liveControl.slowReverse()
        ));
        reverseSlow.setToolTipText("Jog counter-clockwise (slow)");
        forwardSlow.addChangeListener(createChangeListener(
                forwardSlow, () -> liveControl.slowForward()
        ));
        forwardSlow.setToolTipText("Jog clockwise (slow)");
        forwardFast.addChangeListener(createChangeListener(
                forwardFast, () -> liveControl.fastForward()
        ));
        forwardFast.setToolTipText("Jog clockwise (fast)");
        next.addChangeListener(createChangeListener(
                next, () -> liveControl.next())
        );
        next.setToolTipText("Move to next discrete position");

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
        final DashboardClient dashboardClient,
        final DigitalIO leftSensor,
        final DigitalIO rightSensor,
        final DigitalIO fastOutput,
        final DigitalIO slowOutput,
        final DigitalIO reverseOutput
    ) {
        if (liveControl != null) {
            liveControl.stop();
        }

        shouldUpdate.set(true);

        liveControl = new LiveControl(
            leftSensor,
            rightSensor,
            fastOutput,
            slowOutput,
            reverseOutput
        );

        new Thread(() -> {
            try {
                while (this.shouldUpdate.get()) {
                    final DashboardClient.ProgramState newState = dashboardClient.programState();

                    this.leftSensorState.setSelected(leftSensor.getValue());
                    this.rightSensorState.setSelected(rightSensor.getValue());

                    if (newState != state) {
                        Arrays.asList(this.jogButtons.getComponents())
                                .forEach(component -> component.setEnabled(PLAYING != newState));
                        state = newState;
                    }

                    Thread.sleep(50);
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "UI <-> IO sync").start();
    }

    public void stopUpdating() {
        shouldUpdate.set(false);
        liveControl.stop();
    }
}
