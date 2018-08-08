package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.HashSet;
import java.util.Set;

import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class MatsimModeConverter {

	private final VisumRoadNetwork visum;

	public MatsimModeConverter(VisumRoadNetwork visum) {
		super();
		this.visum = visum;
	}

	public Set<String> from(VisumTransportSystemSet tss) {
		Set<VisumTransportSystem> transport = tss.transportSystems;

		Set<String> result = new HashSet<String>();

		if (isCar(transport)) {
			result.add("car");
		}
		if (isPublicTransport(transport)) {
			result.add("pt");
		}
		if (transport.contains(visum.getTransportSystem("Rad"))) {
			result.add("bike");
		}
		if (transport.contains(visum.getTransportSystem("Fuss"))) {
			result.add("walk");
		}
		if (transport.contains(visum.getTransportSystem("F"))) {
			result.add("transit_walk");
		}
		return result;
	}

	public boolean isCar(Set<VisumTransportSystem> transport) {
		return transport.contains(visum.getTransportSystem("P"))
				||	transport.contains(visum.getTransportSystem("Lkw") );
	}

	private boolean isPublicTransport(Set<VisumTransportSystem> transport) {
		return transport.contains(visum.getTransportSystem("I"))
				|| transport.contains(visum.getTransportSystem("NVBW"))
				|| transport.contains(visum.getTransportSystem("R"))
				|| transport.contains(visum.getTransportSystem("S"))
				|| transport.contains(visum.getTransportSystem("U"));
	}
}
