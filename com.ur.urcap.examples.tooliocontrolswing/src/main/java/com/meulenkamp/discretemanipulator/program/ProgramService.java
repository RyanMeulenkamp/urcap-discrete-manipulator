package com.meulenkamp.discretemanipulator.program;

import com.meulenkamp.discretemanipulator.general.V3Style;
import com.meulenkamp.discretemanipulator.model.Configuration;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;
import com.meulenkamp.discretemanipulator.general.V5Style;

import java.util.Locale;

public class ProgramService
		implements SwingProgramNodeService<ProgramContribution, ProgramView> {

	@Override
	public String getId() {
		return "DiscreteManipulator";
	}

	@Override
	public void configureContribution(
			final ContributionConfiguration configuration
	) {
		configuration.setChildrenAllowed(true);
	}

	@Override
	public String getTitle(final Locale locale) {
		return "Discrete manipulator";
	}

	@Override
	public ProgramView createView(final ViewAPIProvider apiProvider) {
		return new ProgramView(
				apiProvider,
				apiProvider
						.getSystemAPI()
						.getSoftwareVersion()
						.getMajorVersion() >= 5 ? new V5Style() : new V3Style()
		);
	}

	@Override
	public ProgramContribution createNode(
			final ProgramAPIProvider apiProvider,
			final ProgramView view, final DataModel model,
			final CreationContext context
	) {
		return new ProgramContribution(apiProvider, view, model);
	}
}
