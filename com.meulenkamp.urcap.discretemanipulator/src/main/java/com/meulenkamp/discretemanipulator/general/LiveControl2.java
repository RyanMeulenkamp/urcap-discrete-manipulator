package com.meulenkamp.discretemanipulator.general;

import com.meulenkamp.discretemanipulator.installation.InstallationContribution;
import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.io.IOModel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class LiveControl {
    private final IoHandler ioHandler;
    private final Supplier<InstallationContribution> installationSupplier;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public LiveControl(final IOModel ioModel, final Supplier<InstallationContribution> installationSupplier) {
        this.ioHandler = new IoHandler(ioModel);
        this.installationSupplier = installationSupplier;
    }

    public DigitalIO sensorInput(final int sensor) {
        if (sensor == 1) {
            return ioHandler.getDigitalIO(
                    installationSupplier.get().getSensor1Input()
            );
        }
        return ioHandler.getDigitalIO(
                installationSupplier.get().getSensor2Input()
        );
    }

    public DigitalIO fastOut() {
        return ioHandler.getDigitalIO(
                installationSupplier.get().getFastOutput()
        );
    }

    public DigitalIO slowOut() {
        return ioHandler.getDigitalIO(
                installationSupplier.get().getSlowOutput()
        );
    }

    public DigitalIO reverseOut() {
        return ioHandler.getDigitalIO(
                installationSupplier.get().getReverseOutput()
        );
    }

    public boolean isSensorActive(final int sensor) {
        return sensorInput(sensor).getValue();
    }

    public void awaitSensorState(final int sensor, final boolean state) {
        while (isSensorActive(sensor) != state && isRunning.get());
    }

    public void setOutputs(
            final boolean reverse, final boolean fast, final boolean slow
    ) {
        reverseOut().setValue(reverse);
        fastOut().setValue(fast);
        slowOut().setValue(slow);
    }

    public void fastForward() {
        setOutputs(false, true, false);
    }

    public void fastReverse() {
        setOutputs(true, true, false);
    }

    public void slowForward() {
        setOutputs(false, false, true);
    }

    public void slowReverse() {
        setOutputs(true, false, true);
    }

    private void stopMovement() {
        setOutputs(false, false, false);
    }

    public void stop() {
        cancel();
        stopMovement();
    }

    public void cancel() {
        if (isRunning.get()) {
            isRunning.set(false);
        }
    }

    public void next() {
        cancel();
        new Thread(() -> {
            isRunning.set(true);
            fastForward();
//            awaitSensorState(1, true);
            awaitSensorState(1, false);
            awaitSensorState(1, true);
            slowForward();
//            awaitSensorState(2, true);
            awaitSensorState(2, false);
            awaitSensorState(2, true);
            stop();
            isRunning.set(false);
        }).start();
    }

    public void previous() {
        cancel();
        new Thread(() -> {
            isRunning.set(true);
            fastReverse();
//            awaitSensorState(2, true);
            awaitSensorState(2, false);
            awaitSensorState(2, true);
            slowReverse();
//            awaitSensorState(1, true);
            awaitSensorState(1, false);
            awaitSensorState(1, true);
            stop();
            isRunning.set(false);
        }).start();
    }
}
