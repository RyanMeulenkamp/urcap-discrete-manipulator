package com.meulenkamp.discretemanipulator.installation;

import com.meulenkamp.discretemanipulator.general.V3Style;
import com.meulenkamp.discretemanipulator.general.V5Style;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;

public class InstallationService implements SwingInstallationNodeService<InstallationContribution, InstallationView> {


	public InstallationService() {
	}

	@Override
	public void configureContribution(final ContributionConfiguration configuration) {
	}

	@Override
	public String getTitle(final Locale locale) {
		return "Discrete Manipulator";
	}

	@Override
	public InstallationView createView(final ViewAPIProvider apiProvider) {
		return new InstallationView(
				apiProvider
						.getSystemAPI()
						.getSoftwareVersion()
						.getMajorVersion() >= 5 ? new V5Style() : new V3Style()
		);
	}

	@Override
	public InstallationContribution createInstallationNode(
			final InstallationAPIProvider apiProvider,
			final InstallationView view,
			final DataModel model,
			final CreationContext context
	) {
		return new InstallationContribution(apiProvider, view, model);
	}
}
