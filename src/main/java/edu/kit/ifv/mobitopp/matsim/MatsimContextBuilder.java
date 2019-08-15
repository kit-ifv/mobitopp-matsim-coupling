package edu.kit.ifv.mobitopp.matsim;

import java.io.File;
import java.io.IOException;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.routing.ValidateLinks;
import edu.kit.ifv.mobitopp.simulation.ContextBuilder;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.WrittenConfiguration;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimContextBuilder extends ContextBuilder {

	@Override
	public MatsimContext buildFrom(File configurationFile) throws IOException {
		SimulationContext baseContext = super.buildFrom(configurationFile);
		return new MatsimContext(baseContext, network(), createRoadNetwork());
	}

	@Override
	public MatsimContext buildFrom(WrittenConfiguration configuration) throws IOException {
		SimulationContext baseContext = super.buildFrom(configuration);
		return new MatsimContext(baseContext, network(), createRoadNetwork());
	}

	private VisumRoadNetwork createRoadNetwork() {
		Network network = super.network();
		return new ValidateLinks().of(network.visumNetwork);
	}
}