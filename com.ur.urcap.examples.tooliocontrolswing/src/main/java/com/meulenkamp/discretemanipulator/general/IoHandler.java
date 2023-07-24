package com.meulenkamp.discretemanipulator.general;

import com.ur.urcap.api.domain.io.DigitalIO;
import com.ur.urcap.api.domain.io.IOModel;

import java.util.Collection;

public class IoHandler {

    private final IOModel ioModel;

    public IoHandler(IOModel ioModel) {
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
    public DigitalIO getDigitalIO(final String defaultName){
        final Collection<DigitalIO> IOcollection = ioModel.getIOs(DigitalIO.class);
        int IO_count = IOcollection.size();
        if(IO_count > 0){
            for (final DigitalIO thisIO : IOcollection) {
                final String thisDefaultName = thisIO.getDefaultName();
                if (thisDefaultName.equals(defaultName)) {
                    return thisIO;
                }
            }
        }
        return null;
    }
}
