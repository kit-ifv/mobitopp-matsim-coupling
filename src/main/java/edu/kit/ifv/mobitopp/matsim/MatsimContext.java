package edu.kit.ifv.mobitopp.matsim;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class MatsimContext extends SimulationContextDecorator implements SimulationContext {

	private final Network network;

	public MatsimContext(SimulationContext baseContext, Network network) {
		super(baseContext);
		this.network = network;
	}

	public VisumNetwork network() {
		return network.visumNetwork;
	}

}
