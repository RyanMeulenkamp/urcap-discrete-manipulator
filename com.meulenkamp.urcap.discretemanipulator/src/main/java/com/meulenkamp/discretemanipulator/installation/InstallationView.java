package com.meulenkamp.discretemanipulator.installation;

import com.meulenkamp.discretemanipulator.general.Style;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.io.IO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

import static com.meulenkamp.discretemanipulator.installation.InstallationContribution.*;

public class InstallationView
		implements SwingInstallationNodeView<InstallationContribution> {

	private final JComboBox<String> leftSensor = new JComboBox<>();
	private final JComboBox<String> rightSensor = new JComboBox<>();
	private final JComboBox<String> slowOut = new JComboBox<>();
	private final JComboBox<String> fastOut = new JComboBox<>();
	private final JComboBox<String> reverseOut = new JComboBox<>();
	private final Style style;

	public InstallationView(final Style style) {
		this.style = style;
	}

	public void addChooser(
			final JComboBox<String> chooser, final JLabel label,
			final String key, final InstallationContribution contribution,
			final JPanel panel
	) {
		chooser.setPreferredSize(
				new Dimension(200, chooser.getPreferredSize().height)
		);
		chooser.setAlignmentX(Component.RIGHT_ALIGNMENT);
		label.setPreferredSize(
				new Dimension(200, chooser.getPreferredSize().height)
		);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		final Box sensorBox = Box.createHorizontalBox();
		sensorBox.add(label);
		sensorBox.add(style.createHorizontalSpacing());
		sensorBox.add(chooser);
		panel.add(sensorBox);
		panel.add(style.createVerticalSpacing());

		chooser.addItemListener(itemEvent -> {
			if (contribution.active && itemEvent.getStateChange() == ItemEvent.SELECTED) {
				contribution.ioChanged(key, itemEvent.getItem().toString());
			}
		});
	}

	@Override
	public void buildUI(
			final JPanel panel, final InstallationContribution contribution
	) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		addChooser(
				leftSensor, new JLabel("Left sensor"),
				LEFT_SENSOR_INPUT_KEY, contribution, panel
		);
		addChooser(
				rightSensor, new JLabel("Right sensor"),
				RIGHT_SENSOR_INPUT_KEY, contribution, panel
		);
		addChooser(
				slowOut, new JLabel("Slow output"),
				SLOW_OUTPUT_KEY, contribution, panel
		);
		addChooser(
				fastOut, new JLabel("Fast output"),
				FAST_OUTPUT_KEY, contribution, panel
		);
		addChooser(
				reverseOut, new JLabel("Reverse output"),
				REVERSE_OUTPUT_KEY, contribution, panel
		);
	}

	private static void setInputsFor(
			final JComboBox<String> sensor, final IO ... ios
	) {
		sensor.removeAllItems();
		for (final IO io: ios) {
			sensor.addItem(io.getDefaultName());
		}

		sensor.setMaximumSize(sensor.getPreferredSize());
	}

	private static void setOutputsFor(
			final JComboBox<String> output, final IO ... ios
	) {
		output.removeAllItems();
		for (final IO io: ios) {
			output.addItem(io.getDefaultName());
		}
		output.setMaximumSize(output.getPreferredSize());
	}

	public void setInputs(final IO ... ios) {
		setInputsFor(leftSensor, ios);
		setInputsFor(rightSensor, ios);
	}

	public void setOutputs(final IO ... ios) {
		setOutputsFor(slowOut, ios);
		setOutputsFor(fastOut, ios);
		setOutputsFor(reverseOut, ios);
	}

	public void setLeftSensorInput(final String input) {
		leftSensor.setSelectedItem(input);
	}

	public void setRightSensorInput(final String input) {
		rightSensor.setSelectedItem(input);
	}

	public void setSlowOutput(final String output) {
		slowOut.setSelectedItem(output);
	}

	public void setFastOutput(final String output) {
		fastOut.setSelectedItem(output);
	}

	public void setReverseOutput(final String output) {
		reverseOut.setSelectedItem(output);
	}
}
