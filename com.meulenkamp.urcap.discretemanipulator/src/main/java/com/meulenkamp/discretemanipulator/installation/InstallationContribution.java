package com.meulenkamp.discretemanipulator.installation;

import com.meulenkamp.discretemanipulator.general.RunState;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.io.IO;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;

import java.util.stream.Stream;

public class InstallationContribution implements InstallationNodeContribution {
    private static final String INPUT = "get_%s_in";
    private static final String OUTPUT = "set_%s_out";
    private static final String RUNSTATE = "set_runstate_%s_output_to_value";
    public static final String LEFT_SENSOR_INPUT_KEY = "sensor_1_input";
    public static final String RIGHT_SENSOR_INPUT_KEY = "sensor_2_input";
    public static final String FAST_OUTPUT_KEY = "fast_output";
    public static final String SLOW_OUTPUT_KEY = "slow_output";
    public static final String REVERSE_OUTPUT_KEY = "reverse_output";

    private final InstallationView view;
    private final DataModel model;
    private final InstallationAPIProvider apiProvider;
    volatile boolean active = false;

    public InstallationContribution(
            final InstallationAPIProvider apiProvider,
            final InstallationView view,
            final DataModel model
    ) {
        this.apiProvider = apiProvider;
        this.view = view;
        this.model = model;
    }

    private IO[] getIos(boolean input) {
        return apiProvider
                .getInstallationAPI()
                .getIOModel()
                .getIOs()
                .stream()
                .filter(io -> io.getType() == IO.IOType.DIGITAL)
                .filter(io -> io.isInput() == input)
                .toArray(IO[]::new);
    }

    @Override
    public void openView() {
        view.setInputs(getIos(true));
        view.setOutputs(getIos(false));

        view.setLeftSensorInput(model.get(LEFT_SENSOR_INPUT_KEY, ""));
        view.setRightSensorInput(model.get(RIGHT_SENSOR_INPUT_KEY, ""));
        view.setFastOutput(model.get(FAST_OUTPUT_KEY, ""));
        view.setSlowOutput(model.get(SLOW_OUTPUT_KEY, ""));
        view.setReverseOutput(model.get(REVERSE_OUTPUT_KEY, ""));
        active = true;
    }

    @Override
    public void closeView() {
        active = false;
    }

    private String functionNameStub(final String io) {
        if (io.startsWith("config")) {
            return "configurable_digital";
        } else if (io.startsWith("tool")) {
            return "tool_digital";
        } else {
            return "standard_digital";
        }
    }

    private String functionName(final String format, final String io) {
        return String.format(format, functionNameStub(io));
    }

    private int getIoNumber(final String name) {
        return Integer.parseInt(name.replaceAll("\\D+", ""));
    }

    private void setRunstate(
            final ScriptWriter writer, final String output,
            final RunState runState
    ) {
        writer.appendLine(String.format(
                "%s(%s, %d)", functionName(RUNSTATE, output),
                getIoNumber(output), runState.getValue()
        ));
    }

    private void readInput(
            final ScriptWriter writer, final String input
    ) {
        writer.appendLine(String.format(
                "return %s(%s)", functionName(INPUT, input),
                getIoNumber(input)
        ));
    }

    private void setOutput(
            final ScriptWriter writer, final String output, final boolean value
    ) {
        writer.appendLine(String.format(
                "%s(%s, %s)", functionName(OUTPUT, output),
                getIoNumber(output), value ? "True" : "False"
        ));
    }

    @Override
    public void generateScript(final ScriptWriter writer) {
//        FIXME This is now handled in ProgramView
//        Stream.of(getFastOutput(), getSlowOutput(), getReverseOutput())
//                .forEach(output -> setRunstate(writer, output, RunState.PRESERVE_STATE));

        writer.defineFunction("read_left_sensor");
        readInput(writer, getLeftSensorInput());
        writer.end();

        writer.defineFunction("read_right_sensor");
        readInput(writer, getRightSensorInput());
        writer.end();

        writer.defineFunction("fast");
        setOutput(writer, getSlowOutput(), false);
        setOutput(writer, getFastOutput(), true);
        writer.end();

        writer.defineFunction("slow");
        setOutput(writer, getFastOutput(), false);
        setOutput(writer, getSlowOutput(), true);
        writer.end();

        writer.defineFunction("stop");
        setOutput(writer, getFastOutput(), false);
        setOutput(writer, getSlowOutput(), false);
        writer.end();

        writer.defineFunction("forward");
        setOutput(writer, getReverseOutput(), false);
        writer.end();

        writer.defineFunction("reverse");
        setOutput(writer, getReverseOutput(), true);
        writer.end();

        System.out.println("Resulting (install) script:\n\n" + writer.generateScript());
    }

    public void ioChanged(final String ioKey, final String ioPin) {
        model.set(ioKey, ioPin);
    }

    public String getLeftSensorInput() {
        return model.get(LEFT_SENSOR_INPUT_KEY, "");
    }

    public String getRightSensorInput() {
        return model.get(RIGHT_SENSOR_INPUT_KEY, "");
    }

    public String getFastOutput() {
        return model.get(FAST_OUTPUT_KEY, "");
    }

    public String getSlowOutput() {
        return model.get(SLOW_OUTPUT_KEY, "");
    }

    public String getReverseOutput() {
        return model.get(REVERSE_OUTPUT_KEY, "");
    }
}
