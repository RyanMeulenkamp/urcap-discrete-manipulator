package com.meulenkamp.discretemanipulator.model;

public class Configuration {
    private String sensor1Input = "";
    private String sensor2Input = "";
    private String fastOutput = "";
    private String slowOutput = "";
    private String reverseOutput = "";

    public String getSensor1Input() {
        return sensor1Input;
    }

    public void setSensor1Input(final String sensor1Input) {
        this.sensor1Input = sensor1Input;
    }

    public String getSensor2Input() {
        return sensor2Input;
    }

    public void setSensor2Input(final String sensor2Input) {
        this.sensor2Input = sensor2Input;
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
