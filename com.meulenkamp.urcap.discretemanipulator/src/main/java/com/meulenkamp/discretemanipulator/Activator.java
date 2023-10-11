package com.meulenkamp.discretemanipulator;

import com.meulenkamp.discretemanipulator.installation.InstallationService;
import com.meulenkamp.discretemanipulator.program.ProgramService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	@Override
	public void start(final BundleContext bundleContext) {
		bundleContext.registerService(SwingInstallationNodeService.class, new InstallationService(), null);
		bundleContext.registerService(SwingProgramNodeService.class, new ProgramService(), null);
	}

	@Override
	public void stop(final BundleContext bundleContext) {
	}
}
