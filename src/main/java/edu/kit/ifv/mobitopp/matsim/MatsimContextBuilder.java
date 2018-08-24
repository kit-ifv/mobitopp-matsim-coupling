package edu.kit.ifv.mobitopp.matsim;

import java.io.File;
import java.io.IOException;

import edu.kit.ifv.mobitopp.simulation.ContextBuilder;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;

public class MatsimContextBuilder extends ContextBuilder {

	@Override
	public MatsimContext buildFrom(File configurationFile) throws IOException {
		SimulationContext baseContext = super.buildFrom(configurationFile);
		return new MatsimContext(baseContext, network());
	}
	
}