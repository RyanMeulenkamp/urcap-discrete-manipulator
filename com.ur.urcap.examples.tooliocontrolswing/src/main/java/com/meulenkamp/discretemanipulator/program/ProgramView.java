package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.StyledView;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.meulenkamp.discretemanipulator.general.Style;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProgramView
        extends StyledView
        implements SwingProgramNodeView<ProgramContribution> {

    protected final ViewAPIProvider apiProvider;
    protected JTextField textField;
    protected JLabel errorLabel = new JLabel("");
    private final JRadioButton clockwise = new JRadioButton();
    private final JRadioButton counterClockwise = new JRadioButton();

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
        panel.add(createJogButtons(provider));
        panel.add(style.createLargeVerticalSpacing());
        panel.add(new JSeparator());
        panel.add(createMovesInput(provider));
        panel.add(style.createVerticalSpacing());
        panel.add(createDirectionPanel(provider));
    }

    private Component createSensorPanel() {
        final Box sensorStateBox = Box.createHorizontalBox();
        final JCheckBox leftSensorState = new JCheckBox("", false);
        final JCheckBox rightSensorState = new JCheckBox("", false);
        final JLabel arrows = new JLabel("⏴ ⏵");

        leftSensorState.setEnabled(false);
        rightSensorState.setEnabled(false);
        arrows.setFont(arrows.getFont().deriveFont(30.0f));

        sensorStateBox.add(new JLabel("Sensors:"));
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(leftSensorState);
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(arrows);
        sensorStateBox.add(style.createHorizontalSpacing());
        sensorStateBox.add(rightSensorState);

        return sensorStateBox;
    }

    private Box createJogButtons(
            final ContributionProvider<ProgramContribution> provider
    ) {
        final Box jogButtons = Box.createHorizontalBox();

        final JButton previous = new JButton("⏮");
        final JButton reverseFast = new JButton("⏪");
        final JButton reverseSlow = new JButton("⏴");
        final JButton stop = new JButton("⏹");
        final JButton forwardSlow = new JButton("⏵");
        final JButton forwardFast = new JButton("⏩");
        final JButton next = new JButton("⏭");

        previous.addChangeListener(event -> {
            if (previous.getModel().isPressed()) {
                provider.get().getLiveControl().previous();
            }
        });
        previous.setToolTipText("Move to previous discrete position");
        reverseFast.addChangeListener(event -> {
            if (reverseFast.getModel().isPressed()) {
                provider.get().getLiveControl().fastReverse();
            } else {
                provider.get().getLiveControl().stop();
            }
        });
        reverseFast.setToolTipText("Jog counter-clockwise (fast)");
        reverseSlow.addChangeListener(event -> {
            if (reverseSlow.getModel().isPressed()) {
                provider.get().getLiveControl().slowReverse();
            } else {
                provider.get().getLiveControl().stop();
            }
        });
        reverseSlow.setToolTipText("Jog counter-clockwise (slow)");
        stop.addChangeListener(event -> {
            if (stop.getModel().isPressed()) {
                provider.get().getLiveControl().stop();
            }
        });
        stop.setToolTipText("Stop movement");
        forwardSlow.addChangeListener(event -> {
            if (forwardSlow.getModel().isPressed()) {
                provider.get().getLiveControl().slowForward();
            } else {
                provider.get().getLiveControl().stop();
            }
        });
        forwardSlow.setToolTipText("Jog clockwise (slow)");
        forwardFast.addChangeListener(event -> {
            if (forwardFast.getModel().isPressed()) {
                provider.get().getLiveControl().fastForward();
            } else {
                provider.get().getLiveControl().stop();
            }
        });
        forwardFast.setToolTipText("Jog clockwise (fast)");
        next.addChangeListener(event -> {
            if (next.getModel().isPressed()) {
                provider.get().getLiveControl().next();
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
        final JLabel counterClockwiseLabel = new JLabel("⟲");
        final JLabel clockwiseLabel = new JLabel("⟳");

        clockwise.setSelected(true);
        clockwise.addChangeListener(
                event -> provider.get().setDirection(clockwise.isSelected())
        );

        counterClockwiseLabel.setFont(
                counterClockwiseLabel.getFont().deriveFont(30.0f)
        );
        clockwiseLabel.setFont(clockwiseLabel.getFont().deriveFont(30.0f));

        group.add(counterClockwise);
        group.add(clockwise);

        directionBox.add(new JLabel("Direction"));
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(counterClockwise);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(counterClockwise);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(counterClockwiseLabel);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(clockwiseLabel);
        directionBox.add(style.createHorizontalSpacing());
        directionBox.add(clockwise);

        return directionBox;
    }

    public void errorMessage(final String message) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(message);
    }

    public void setDirection(final boolean direction) {
        if (direction) {
            clockwise.setSelected(true);
        } else {
            counterClockwise.setSelected(true);
        }
    }

    public void setMoves(final int moves) {
        textField.setText(Integer.toString(moves));
    }
}

