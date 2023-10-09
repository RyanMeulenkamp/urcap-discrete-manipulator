package com.meulenkamp.discretemanipulator.general;

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
            DigitalIO leftSensorInput,
            DigitalIO rightSensorInput,
            DigitalIO fastOutput,
            DigitalIO slowOutput,
            DigitalIO reverseOutput
    ) {
        this.leftSensorInput = leftSensorInput;
        this.rightSensorInput = rightSensorInput;
        this.fastOutput = fastOutput;
        this.slowOutput = slowOutput;
        this.reverseOutput = reverseOutput;
    }

    public void awaitSensorState(final DigitalIO sensor, final boolean state) {
        while (sensor.getValue() != state && isRunning.get()) ;
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
        isRunning.set(false);
        reverseOutput.setValue(false);
        fastOutput.setValue(false);
        slowOutput.setValue(false);
    }

    public void next() {
        isRunning.set(true);
        new Thread(() -> {
            try {
                isRunning.set(true);
                fastForward();
//            awaitSensorState(1, true);
                awaitSensorState(leftSensorInput, false);
                awaitSensorState(leftSensorInput, true);
                slowForward();
//            awaitSensorState(2, true);
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
                //            awaitSensorState(2, true);
                awaitSensorState(rightSensorInput, false);
                awaitSensorState(rightSensorInput, true);
                slowReverse();
                //            awaitSensorState(1, true);
                awaitSensorState(leftSensorInput, false);
                awaitSensorState(leftSensorInput, true);
            } finally {
                stop();
            }
        }).start();
    }
}
