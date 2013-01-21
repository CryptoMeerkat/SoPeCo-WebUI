package org.sopeco.frontend.pap;

import java.util.Map;

import org.sopeco.frontend.server.chartconnector.IChartConnection;
import org.sopeco.frontend.server.chartconnector.IChartConnectionExtension;

public class PAPChartConnectionExtension implements IChartConnectionExtension {
	public static final String NAME = "PAP Connection";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public Map<String, String> getConfigParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IChartConnection createExtensionArtifact() {
		// TODO Auto-generated method stub
		return new PAPChartConnection(this);
	}

}
