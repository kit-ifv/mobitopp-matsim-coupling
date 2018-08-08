package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimLinkCreator extends BaseLinkCreator {

	private final VisumRoadNetwork visum;

	public MatsimLinkCreator(VisumRoadNetwork visum, Network network) {
		super(visum, network);
		this.visum = visum;
	}

	public void createLinks(Map<Integer, Node> nodes) {
		for (VisumLink link : visum.links.links.values()) {

			VisumOrientedLink linkA = link.linkA;
			VisumOrientedLink linkB = link.linkB;

			Set<String> allowedModesA = modesFromTS(linkA.transportSystems);
			Set<String> allowedModesB = modesFromTS(linkB.transportSystems);
	
			if (allowedModesA.contains("car") && linkA.attributes.capacityCar > 0.0f)		{
				String directionSuffix = ":1";
				String id = link.id + directionSuffix;
				makeLink(nodes, linkA, id);
			} 

			if (allowedModesB.contains("car") && linkB.attributes.capacityCar > 0.0f)		{
				String directionSuffix = ":2";
				String id = link.id + directionSuffix;
				makeLink(nodes, linkB, id);
			}
		}
	}

	private void makeLink(Map<Integer, Node> nodes, VisumOrientedLink link, String id) {
		Node fromNode = nodes.get(link.from.id());
		Node toNode = nodes.get(link.to.id());
		float length = link.length;
		int capacity = fixCapacityOf(link);
		int freeFlowSpeedCar = link.attributes.freeFlowSpeedCar;
		int numberOfLanes = link.attributes.numberOfLanes;
		Set<String> modes = modesFromTS(link.transportSystems);
		makeLink(id, fromNode, toNode, length, capacity, freeFlowSpeedCar, numberOfLanes, modes);
	}
	
	private int fixCapacityOf(VisumOrientedLink link) {
		if (link.id.startsWith("2072748551")) {
			return 10000;
		}
		return link.attributes.capacityCar;
	}
	
}
