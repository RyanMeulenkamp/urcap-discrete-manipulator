package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.installation.InstallationContribution;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class ScriptGenerator {
    private final ScriptWriter writer;
    private final DataModel model;

    public ScriptGenerator(
            final ScriptWriter writer,
            final DataModel model
    ) {
        this.writer = writer;
        this.model = model;
    }

    private String getFunctionName(final String name) {
        if (name.startsWith("config_in")) {
            return "get_configurable_digital_in";
        } else if (name.startsWith("config_out")) {
            return "set_configurable_digital_out";
        } else if (name.startsWith("digital_in")) {
            return "get_standard_digital_in";
        } else if (name.startsWith("digital_out")) {
            return "set_standard_digital_out";
        } else if (name.startsWith("tool_in")) {
            return "get_tool_digital_in";
        } else if (name.startsWith("tool1_out")) {
            return "set_tool_digital_out";
        } else {
            throw new RuntimeException("Unknown IO type");
        }
    }

    private int getIoNumber(final String name) {
        final String[] parts = name.split("\\[");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid IO name");
        }
        return Integer.parseInt(parts[1].replace("]", ""));
    }

    private String getIoPin(final String key) {
        return model.get(key, "");
    }

    private void setOutput(final String output, final boolean value) {
        final String ioPin = getIoPin(output);
        final String line = String.format(
                "%s(%s, %s)",
                getFunctionName(ioPin),
                getIoNumber(output),
                value ? "True" : "False"
        );
        System.out.printf(
                "Setting output %s %s %n", ioPin, value ? "high" : "low"
        );
        writer.appendLine(line);
    }

    private void direction(final boolean forward) {
        // Set reverse output low for forward and high for reverse
        setOutput("output_reverse", !forward);
    }

    private void forward() {
        direction(false);
    }

    private void reverse() {
        direction(true);
    }

    private void slow() {
        // Set "Slow" output high and "Fast" output low
        setOutput(InstallationContribution.FAST_OUTPUT_KEY, false);
        setOutput(InstallationContribution.SLOW_OUTPUT_KEY, true);
    }

    private void fast() {
        // Set "Fast" output high and "Slow" output low
        setOutput(InstallationContribution.FAST_OUTPUT_KEY, true);
        setOutput(InstallationContribution.SLOW_OUTPUT_KEY, false);
    }

    public void stop() {
        // Set both "Fast" and "Slow" output low
        setOutput(InstallationContribution.FAST_OUTPUT_KEY, false);
        setOutput(InstallationContribution.SLOW_OUTPUT_KEY, false);
    }

    public void awaitInputState(final String input, final boolean state) {
        final String ioPin = getIoPin(input);
        writer.whileCondition(String.format(
                "%s(%d) == %s",
                getFunctionName(ioPin),
                getIoNumber(ioPin),
                state ? "True" : "False"
        ));
    }

    public void awaitSensor(final int sensor_input) {
        final String sensor = sensor_input == 1
                ? InstallationContribution.SENSOR_1_INPUT_KEY
                : InstallationContribution.SENSOR_2_INPUT_KEY;
        System.out.println("Awaiting sensor: " + sensor);
        // while sensor 1 low
        awaitInputState(sensor, false);
        // while sensor 1 high
        awaitInputState(sensor, true);
    }

    public void move(final boolean forward) {
        direction(forward);
        fast();
        awaitSensor(forward ? 1 : 2);
        slow();
        awaitSensor(forward ? 2 : 1);
        stop();
    }

    public void next() {
        move(true);
    }

    public void previous() {
        move(false);
    }
}
