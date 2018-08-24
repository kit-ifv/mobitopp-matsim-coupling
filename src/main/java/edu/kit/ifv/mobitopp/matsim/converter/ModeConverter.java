package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.TransportMode;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class ModeConverter {

	private final Map<Mode, String> toMatsim;

	public ModeConverter() {
		super();
		toMatsim = new HashMap<>();
		toMatsim.put(Mode.BIKE, TransportMode.bike);
		toMatsim.put(Mode.CAR, TransportMode.car);
		toMatsim.put(Mode.PASSENGER, TransportMode.ride);
		toMatsim.put(Mode.PEDESTRIAN, TransportMode.walk);
		toMatsim.put(Mode.PUBLICTRANSPORT, TransportMode.pt);
	}

	public String toMatsim(Mode mode) {
		return toMatsim.getOrDefault(mode, TransportMode.car);
	}

}
