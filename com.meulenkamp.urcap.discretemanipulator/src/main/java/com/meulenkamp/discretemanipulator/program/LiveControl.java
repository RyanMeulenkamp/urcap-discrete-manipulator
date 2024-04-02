package com.meulenkamp.discretemanipulator.program;

import com.ur.urcap.api.domain.io.DigitalIO;

import java.util.concurrent.atomic.AtomicBoolean;

public class LiveControl {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final DigitalIO leftSensorInput;
    private final DigitalIO rightSensorInput;
    private final DigitalIO fastOutput;
    private final DigitalIO slowOutput;
    private final DigitalIO reverseOutput;

    public LiveControl(
            final DigitalIO leftSensorInput,
            final DigitalIO rightSensorInput,
            final DigitalIO fastOutput,
            final DigitalIO slowOutput,
            final DigitalIO reverseOutput
    ) {
        this.leftSensorInput = leftSensorInput;
        this.rightSensorInput = rightSensorInput;
        this.fastOutput = fastOutput;
        this.slowOutput = slowOutput;
        this.reverseOutput = reverseOutput;
    }

    public void awaitSensorState(final DigitalIO sensor, final boolean state) {
        while (sensor.getValue() != state && isRunning.get());
    }

    public void fastForward() {
        reverseOutput.setValue(false);
        fastOutput.setValue(true);
        slowOutput.setValue(false);
    }

    public void fastReverse() {
        reverseOutput.setValue(true);
        fastOutput.setValue(true);
        slowOutput.setValue(false);
    }

    public void slowForward() {
        reverseOutput.setValue(false);
        fastOutput.setValue(false);
        slowOutput.setValue(true);
    }

    public void slowReverse() {
        reverseOutput.setValue(true);
        fastOutput.setValue(false);
        slowOutput.setValue(true);
    }

    public void stop() {
        if(isRunning.get()) {
            isRunning.set(false);
            reverseOutput.setValue(false);
            fastOutput.setValue(false);
            slowOutput.setValue(false);
        }
    }

    public void next() {
        isRunning.set(true);
        new Thread(() -> {
            try {
                fastForward();
                awaitSensorState(leftSensorInput, false);
                awaitSensorState(leftSensorInput, true);
                slowForward();
                awaitSensorState(rightSensorInput, false);
                awaitSensorState(rightSensorInput, true);
            } finally {
                stop();
            }
        }).start();
    }

    public void previous() {
        isRunning.set(true);
        new Thread(() -> {
            try {
                fastReverse();
                awaitSensorState(rightSensorInput, false);
                awaitSensorState(rightSensorInput, true);
                slowReverse();
                awaitSensorState(leftSensorInput, false);
                awaitSensorState(leftSensorInput, true);
            } finally {
                stop();
            }
        }).start();
    }
}
