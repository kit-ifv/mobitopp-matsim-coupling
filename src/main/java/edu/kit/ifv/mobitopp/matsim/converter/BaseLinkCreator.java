package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class BaseLinkCreator {

	private final MatsimModeConverter toMode;
	private Network network;

	public BaseLinkCreator(VisumRoadNetwork visum, Network network) {
		super();
		this.network = network;
		this.toMode = new MatsimModeConverter(visum);
	}

	public Link makeLink(
			String id, Node fromNode, Node toNode, float length, float capacity, float freespeed,
			float lanes, Set<String> allowedModes) {
		if (length == 0.0f) {
			length = 1.0f;
		}

		if (lanes == 0) {
			lanes = 1;
		}

		Link link = network.getFactory().createLink(Id.createLinkId(id), fromNode, toNode);

		link.setLength(1000f * length);
		link.setCapacity(capacity / 10.0f);
		link.setFreespeed(freespeed / 3.6f);
		link.setNumberOfLanes(lanes);

		link.setAllowedModes(allowedModes);

		network.addLink(link);

		return link;
	}

	protected Set<String> modesFromTS(VisumTransportSystemSet transportSystems) {
		return toMode.from(transportSystems);
	}
	
	protected MatsimModeConverter toMode() {
		return toMode;
	}

}