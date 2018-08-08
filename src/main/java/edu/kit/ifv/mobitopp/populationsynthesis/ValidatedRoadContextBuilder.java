package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.routing.ValidateLinks;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

final class ValidatedRoadContextBuilder extends ContextBuilder {

	@Override
	protected SimpleRoadNetwork createRoadNetwork(VisumRoadNetwork network) {
		VisumRoadNetwork visumNetwork = new ValidateLinks().of(network);
		return super.createRoadNetwork(visumNetwork);
	}
}