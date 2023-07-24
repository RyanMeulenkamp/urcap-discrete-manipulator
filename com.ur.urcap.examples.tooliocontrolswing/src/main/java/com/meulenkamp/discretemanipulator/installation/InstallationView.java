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

	private final JComboBox<String> sensor1 = new JComboBox<>();
	private final JComboBox<String> sensor2 = new JComboBox<>();
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
		final Box sensorBox = Box.createHorizontalBox();
		sensorBox.setAlignmentX(Component.LEFT_ALIGNMENT);
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

	public static int getWidest(final  JComponent... components) {
		int widest = 0;
		for (final JComponent component: components) {
			final int width = component.getPreferredSize().width;
			System.out.println("width: " + width);
			if (width > widest) {
				widest = width;
			}
		}
		System.out.println("widest: " + widest);
		return widest;
	}

	public static void setWidth(
			final int width, final JComponent ... components
	) {
		for (final JComponent component: components) {
			component.setPreferredSize(
					new Dimension(width, component.getPreferredSize().height)
			);
		}
	}

	public void setWidest(final JComponent... components) {
		setWidth(getWidest(components), components);
	}

	@Override
	public void buildUI(
			final JPanel panel, final InstallationContribution contribution
	) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		final JLabel sensor1Label = new JLabel("Sensor 1");
		final JLabel sensor2Label = new JLabel("Sensor 2");
		final JLabel slowOutLabel = new JLabel("Slow output");
		final JLabel fastOutLabel = new JLabel("Fast output");
		final JLabel reverseOutLabel = new JLabel("Reverse output");

		setWidest(
				sensor1Label, sensor2Label, slowOutLabel, fastOutLabel,
				reverseOutLabel
		);
		addChooser(
				sensor1, sensor1Label, SENSOR_1_INPUT_KEY, contribution, panel
		);
		addChooser(
				sensor2, sensor2Label, SENSOR_2_INPUT_KEY, contribution, panel
		);
		addChooser(slowOut, slowOutLabel, SLOW_OUTPUT_KEY, contribution, panel);
		addChooser(fastOut, fastOutLabel, FAST_OUTPUT_KEY, contribution, panel);
		addChooser(
				reverseOut, reverseOutLabel, REVERSE_OUTPUT_KEY, contribution,
				panel
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
		setInputsFor(sensor1, ios);
		setInputsFor(sensor2, ios);
		setWidest(sensor1, sensor2, slowOut, fastOut, reverseOut);
	}

	public void setOutputs(final IO ... ios) {
		setOutputsFor(slowOut, ios);
		setOutputsFor(fastOut, ios);
		setOutputsFor(reverseOut, ios);
		setWidest(sensor1, sensor2, slowOut, fastOut, reverseOut);
	}

	public void setSensor1Input(final String input) {
		sensor1.setSelectedItem(input);
	}

	public void setSensor2Input(final String input) {
		sensor2.setSelectedItem(input);
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

	public String getSensor1Input() {
		return (String) sensor1.getSelectedItem();
	}

	public String getSensor2Input() {
		return (String) sensor2.getSelectedItem();
	}

	public String getSlowOutput() {
		return (String) slowOut.getSelectedItem();
	}

	public String getFastOutput() {
		return (String) fastOut.getSelectedItem();
	}

	public String getReverseOutput() {
		return (String) reverseOut.getSelectedItem();
	}
}
