package com.meulenkamp.discretemanipulator.general;

public enum RunState {
    PRESERVE_STATE(0),
    LOW_WHEN_STOPPED(1),
    HIGH_WHEN_STOPPED(2),
    HIGH_WHEN_RUNNING_LOW_WHEN_STOPPED(3);

    private final int value;

    private RunState(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
