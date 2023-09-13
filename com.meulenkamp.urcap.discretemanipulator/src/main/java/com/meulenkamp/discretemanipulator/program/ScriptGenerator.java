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

    private void forward() {
        writer.appendLine("forward()");
    }

    private void reverse() {
        writer.appendLine("reverse()");
    }

    private void slow() {
        writer.appendLine("slow()");
    }

    private void fast() {
        writer.appendLine("fast()");
    }

    public void stop() {
        writer.appendLine("stop()");
    }

    public void awaitInputState(final String inputFn, final boolean state) {
        writer.whileCondition(String.format(
            "%s() == %s", inputFn, state ? "False" : "True"
        ));
        writer.end();
    }

    public void awaitSensor(final int input) {
        final String inputFn = "read_sensor" + input;
        // while sensor 1 low
        awaitInputState(inputFn, false);
        // while sensor 1 high
        awaitInputState(inputFn, true);
    }

    public void move(final boolean forward) {
        if (forward) {
            forward();
        } else {
            reverse();
        }
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
