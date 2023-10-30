package com.meulenkamp.discretemanipulator.general;

import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.io.IO;
import com.ur.urcap.api.domain.io.IOModel;

public class IOHandler {

    private final IOModel ioModel;

    public IOHandler(final IOModel ioModel) {
        this.ioModel = ioModel;
    }

    /*
     * Returns a DigitalIO object found by its default name
     * Default names are:
     * 	digital_in[0]
     *  digital_in[1]
     *  ...
     *  digital_in[7]
     *  digital_out[0]
     *  digital_out[1]
     *  ...
     *  digital_out[7]
     *  tool_in[0]
     *  tool_in[1]
     *  tool_out[0]
     *  tool_out[1]
     *  config_in[0]
     *  config_in[1]
     *  ...
     *  config_in[7]
     *  config_out[0]
     *  config_out[1]
     *  ...
     *  config_out[7]
     */
    public DigitalIO getDigitalIO(final String defaultName) {
        return (DigitalIO) ioModel.getIOs(
                element -> element.getType() == IO.IOType.DIGITAL
                        && element.getDefaultName().equals(defaultName))
                .iterator()
                .next();
    }
}
