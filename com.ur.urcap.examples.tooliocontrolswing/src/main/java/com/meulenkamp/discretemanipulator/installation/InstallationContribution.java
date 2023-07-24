package com.meulenkamp.discretemanipulator.installation;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.io.IO;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class InstallationContribution implements InstallationNodeContribution {
    public static final String SENSOR_1_INPUT_KEY = "sensor_1_input";
    public static final String SENSOR_2_INPUT_KEY = "sensor_2_input";
    public static final String FAST_OUTPUT_KEY = "fast_output";
    public static final String SLOW_OUTPUT_KEY = "slow_output";
    public static final String REVERSE_OUTPUT_KEY = "reverse_output";
    private final InstallationView view;
    private final DataModel model;
    private final InstallationAPIProvider apiProvider;
    volatile boolean active = false;

    public InstallationContribution(
            final InstallationAPIProvider apiProvider,
            final InstallationView view,
            final DataModel model
    ) {
        this.apiProvider = apiProvider;
        this.view = view;
        this.model = model;
    }

    private IO[] getIos(boolean input) {
        return apiProvider
                .getInstallationAPI()
                .getIOModel()
                .getIOs()
                .stream()
                .filter(io -> io.getType() == IO.IOType.DIGITAL)
                .filter(io -> io.isInput() == input)
                .toArray(IO[]::new);
    }

    @Override
    public void openView() {
        view.setInputs(getIos(true));
        view.setOutputs(getIos(false));

        view.setSensor1Input(model.get(SENSOR_1_INPUT_KEY, ""));
        view.setSensor2Input(model.get(SENSOR_2_INPUT_KEY, ""));
        view.setFastOutput(model.get(FAST_OUTPUT_KEY, ""));
        view.setSlowOutput(model.get(SLOW_OUTPUT_KEY, ""));
        view.setReverseOutput(model.get(REVERSE_OUTPUT_KEY, ""));
        active = true;
    }

    @Override
    public void closeView() {
        active = false;
    }

    @Override
    public void generateScript(final ScriptWriter writer) {
        writer.assign(SENSOR_1_INPUT_KEY, "\"" + getSensor1Input() + "\"");
        writer.assign(SENSOR_2_INPUT_KEY, "\"" + getSensor2Input() + "\"");
        writer.assign(FAST_OUTPUT_KEY, "\"" + getFastOutput() + "\"");
        writer.assign(SLOW_OUTPUT_KEY, "\"" + getSlowOutput() + "\"");
        writer.assign(REVERSE_OUTPUT_KEY, "\"" + getReverseOutput() + "\"");
    }

    public void ioChanged(final String ioKey, final String ioPin) {
        System.out.println("ioChanged: " + ioKey + " = " + ioPin);
        model.set(ioKey, ioPin);
    }

    public String getSensor1Input() {
        return model.get(SENSOR_1_INPUT_KEY, "");
    }

    public String getSensor2Input() {
        return model.get(SENSOR_2_INPUT_KEY, "");
    }

    public String getFastOutput() {
        return model.get(FAST_OUTPUT_KEY, "");
    }

    public String getSlowOutput() {
        return model.get(SLOW_OUTPUT_KEY, "");
    }

    public String getReverseOutput() {
        return model.get(REVERSE_OUTPUT_KEY, "");
    }
}
