package com.meulenkamp.discretemanipulator.general;

import javax.swing.*;
import java.awt.*;

public abstract class Style {
	private static final int HORIZONTAL_SPACING = 10;
	private static final int VERTICAL_SPACING = 10;
	private static final int LARGE_VERTICAL_SPACING = 25;
	private static final int XLARGE_VERTICAL_SPACING = 50;

	public abstract Dimension getInputfieldSize();

	private Component createVerticalSpacing(final int height) {
		return Box.createRigidArea(new Dimension(0, height));
	}

	public Component createVerticalSpacing() {
		return createVerticalSpacing(VERTICAL_SPACING);
	}

	public Component createLargeVerticalSpacing() {
		return createVerticalSpacing(LARGE_VERTICAL_SPACING);
	}

	public Component createExtraLargeVerticalSpacing() {
		return createVerticalSpacing(XLARGE_VERTICAL_SPACING);
	}

	public Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(HORIZONTAL_SPACING, 0));
	}
}
