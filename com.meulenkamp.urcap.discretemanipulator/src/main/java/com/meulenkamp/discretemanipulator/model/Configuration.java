package com.meulenkamp.discretemanipulator.model;

public class Configuration {
    private String leftSensorInput = "";
    private String rightSensorInput = "";
    private String fastOutput = "";
    private String slowOutput = "";
    private String reverseOutput = "";

    public String getLeftSensorInput() {
        return leftSensorInput;
    }

    public void setLeftSensorInput(final String leftSensorInput) {
        this.leftSensorInput = leftSensorInput;
    }

    public String getRightSensorInput() {
        return rightSensorInput;
    }

    public void setRightSensorInput(final String rightSensorInput) {
        this.rightSensorInput = rightSensorInput;
    }

    public String getFastOutput() {
        return fastOutput;
    }

    public void setFastOutput(final String fastOutput) {
        this.fastOutput = fastOutput;
    }

    public String getSlowOutput() {
        return slowOutput;
    }

    public void setSlowOutput(final String slowOutput) {
        this.slowOutput = slowOutput;
    }

    public String getReverseOutput() {
        return reverseOutput;
    }

    public void setReverseOutput(final String reverseOutput) {
        this.reverseOutput = reverseOutput;
    }
}
