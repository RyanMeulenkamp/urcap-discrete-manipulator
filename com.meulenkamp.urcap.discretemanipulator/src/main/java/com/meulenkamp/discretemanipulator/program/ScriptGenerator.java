package com.meulenkamp.discretemanipulator.program;

import com.ur.urcap.api.domain.script.ScriptWriter;

public class ScriptGenerator {
    enum Sensor {
        LEFT("read_left_sensor"),
        RIGHT("read_right_sensor");

        final String readfn;

        Sensor(final String readfn) { this.readfn = readfn; }
    }

    private final ScriptWriter writer;

    public ScriptGenerator(final ScriptWriter writer) {
        this.writer = writer;
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

    public void awaitSensorState(final Sensor sensor, final boolean state) {
        writer.whileCondition(String.format(
            "%s() != %s", sensor.readfn, state ? "True" : "False"
        ));
        writer.appendLine("continue");
        writer.end();
    }

    public void moveForward(final int moves) {
        forward();
        fast();
        for (int i = 0; i < moves; i++) {
            awaitSensorState(Sensor.LEFT, false);
            awaitSensorState(Sensor.LEFT, true);
        }
        slow();
        awaitSensorState(Sensor.RIGHT, false);
        awaitSensorState(Sensor.RIGHT, true);
        stop();
    }

    public void moveReverse(final int moves) {
        reverse();
        fast();
        for (int i = 0; i < moves; i++) {
            awaitSensorState(Sensor.RIGHT, false);
            awaitSensorState(Sensor.RIGHT, true);
        }
        slow();
        awaitSensorState(Sensor.LEFT, false);
        awaitSensorState(Sensor.LEFT, true);
        stop();
    }
}
