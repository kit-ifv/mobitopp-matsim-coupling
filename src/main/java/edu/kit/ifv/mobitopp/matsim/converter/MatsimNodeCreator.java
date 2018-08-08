package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimNodeCreator {

	private final Scenario scenario;
	private final Network network;
	private final Map<Integer, Node> nodes;

	public MatsimNodeCreator(Scenario scenario) {
		super();
		this.scenario = scenario;
		this.network = scenario.getNetwork();
		nodes = new HashMap<>();
	}

	public Map<Integer, Node> from(VisumRoadNetwork visum) {
		createFromLinksIn(visum);
		createFromConnectorsIn(visum);
		return Collections.unmodifiableMap(nodes);
	}

	private void createFromLinksIn(VisumRoadNetwork visum) {
		for (VisumLink link : visum.links.links.values()) {
			addNodesOf(link);
		}
	}

	private void addNodesOf(VisumLink link) {
		create(link.linkA.from);
		create(link.linkA.to);
		create(link.linkB.from);
		create(link.linkB.to);
	}

	private void createFromConnectorsIn(VisumRoadNetwork visum) {
		for (List<VisumConnector> connectors : visum.connectors.values()) {
			for (VisumConnector connector : connectors) {
				create(connector.node);
			}
		}
	}

	private void create(VisumNode node) {
		if (nodes.containsKey(node.id())) {
			return;
		}
		Node matsimNode = from("" + node.id(), node.coord.x, node.coord.y);
		nodes.put(node.id(), matsimNode);
	}

	public Node from(String id, float x, float y) {
		Node node = network.getFactory().createNode(nodeIdFrom(id), coordinate(x, y));
		network.addNode(node);
		return node;
	}

	private Id<Node> nodeIdFrom(String id) {
		return Id.createNodeId(id);
	}

	private Coord coordinate(float x, float y) {
		return scenario.createCoord(x, y);
	}

}
