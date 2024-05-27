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

    private void innerFastForward() {
        isRunning.set(true);
        reverseOutput.setValue(false);
        fastOutput.setValue(true);
        slowOutput.setValue(false);
    }

    public void fastForward() {
        if (!isRunning.get()) {
            innerFastForward();
        }
    }

    private void innerFastReverse() {
        isRunning.set(true);
        reverseOutput.setValue(true);
        fastOutput.setValue(true);
        slowOutput.setValue(false);
    }

    public void fastReverse() {
        if (!isRunning.get()) {
            innerFastReverse();
        }
    }

    private void innerSlowForward() {
        isRunning.set(true);
        reverseOutput.setValue(false);
        fastOutput.setValue(false);
        slowOutput.setValue(true);
    }

    public void slowForward() {
        if (!isRunning.get()) {
            innerSlowForward();
        }
    }

    private void innerSlowReverse() {
        isRunning.set(true);
        reverseOutput.setValue(true);
        fastOutput.setValue(false);
        slowOutput.setValue(true);
    }

    public void slowReverse() {
        if (!isRunning.get()) {
            innerSlowReverse();
        }
    }

    public void stop() {
        if(isRunning.get()) {
            reverseOutput.setValue(false);
            fastOutput.setValue(false);
            slowOutput.setValue(false);
            isRunning.set(false);
        }
    }

    public void next() {
        if (!isRunning.get()) {
            isRunning.set(true);
            new Thread(() -> {
                try {
                    innerFastForward();
                    awaitSensorState(leftSensorInput, false);
                    awaitSensorState(leftSensorInput, true);
                    innerSlowForward();
                    awaitSensorState(rightSensorInput, false);
                    awaitSensorState(rightSensorInput, true);
                } finally {
                    stop();
                }
            }).start();
        }
    }

    public void previous() {
        if (!isRunning.get()) {
            isRunning.set(true);
            new Thread(() -> {
                try {
                    innerFastReverse();
                    awaitSensorState(rightSensorInput, false);
                    awaitSensorState(rightSensorInput, true);
                    innerSlowReverse();
                    awaitSensorState(leftSensorInput, false);
                    awaitSensorState(leftSensorInput, true);
                } finally {
                    stop();
                }
            }).start();
        }
    }
}
