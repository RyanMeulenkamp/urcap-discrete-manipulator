package com.meulenkamp.discretemanipulator.general;

import com.ur.urcap.api.domain.io.DigitalIO;

import java.util.concurrent.atomic.AtomicBoolean;

public class LiveControl {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final DigitalIO sensor1Input;
    private final DigitalIO sensor2Input;

    private final DigitalIO fastOutput;

    private final DigitalIO slowOutput;

    private final DigitalIO reverseOutput;

    public LiveControl(
            DigitalIO sensor1Input,
            DigitalIO sensor2Input,
            DigitalIO fastOutput,
            DigitalIO slowOutput,
            DigitalIO reverseOutput
    ) {
        this.sensor1Input = sensor1Input;
        this.sensor2Input = sensor2Input;
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
                awaitSensorState(sensor1Input, false);
                awaitSensorState(sensor1Input, true);
                slowForward();
//            awaitSensorState(2, true);
                awaitSensorState(sensor2Input, false);
                awaitSensorState(sensor2Input, true);
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
                awaitSensorState(sensor2Input, false);
                awaitSensorState(sensor2Input, true);
                slowReverse();
                //            awaitSensorState(1, true);
                awaitSensorState(sensor1Input, false);
                awaitSensorState(sensor1Input, true);
            } finally {
                stop();
            }
        }).start();
    }
}
