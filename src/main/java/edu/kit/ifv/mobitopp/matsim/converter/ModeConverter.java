package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.TransportMode;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class ModeConverter {

	private final Map<Mode, String> toMatsim;

	public ModeConverter() {
		super();
		toMatsim = new HashMap<>();
		toMatsim.put(StandardMode.BIKE, TransportMode.bike);
		toMatsim.put(StandardMode.CAR, TransportMode.car);
		toMatsim.put(StandardMode.PASSENGER, TransportMode.ride);
		toMatsim.put(StandardMode.PEDESTRIAN, TransportMode.walk);
		toMatsim.put(StandardMode.PUBLICTRANSPORT, TransportMode.pt);
	}

	public String toMatsim(Mode mode) {
		return toMatsim.getOrDefault(mode, TransportMode.car);
	}

}
