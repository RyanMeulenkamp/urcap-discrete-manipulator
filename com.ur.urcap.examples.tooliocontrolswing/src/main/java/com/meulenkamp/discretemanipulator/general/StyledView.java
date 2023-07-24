package com.meulenkamp.discretemanipulator.general;

import javax.swing.*;
import java.awt.*;

public abstract class StyledView {
    protected final Style style;

    protected StyledView(final Style style) {
        this.style = style;
    }
}
