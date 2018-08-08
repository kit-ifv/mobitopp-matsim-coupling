package edu.kit.ifv.mobitopp.matsim;

import java.util.Map;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.matsim.converter.MatsimConnectorCreator;
import edu.kit.ifv.mobitopp.matsim.converter.MatsimLinkCreator;
import edu.kit.ifv.mobitopp.matsim.converter.MatsimNodeCreator;
import edu.kit.ifv.mobitopp.matsim.converter.MatsimZoneLinkCreator;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimNetworkCreator {

	private final Scenario scenario;
	private final Network network;
	private final VisumRoadNetwork visum;

	public MatsimNetworkCreator(Scenario scenario, VisumRoadNetwork visumNetwork) {
		this.scenario = scenario;
		this.network = scenario.getNetwork();
		this.visum = visumNetwork;
	}

	public void createFromVisumNetwork() {
		MatsimNodeCreator createNode = new MatsimNodeCreator(scenario);
		Map<Integer, Node> nodes = createNode.from(visum);
		createLinks(nodes);
		Map<String, Node> zones = createZoneLinks(createNode);
		createConnectors(nodes, zones);
	}

	private void createLinks(Map<Integer, Node> nodes) {
		new MatsimLinkCreator(visum, network).createLinks(nodes);
	}

	private Map<String, Node> createZoneLinks(MatsimNodeCreator createNode) {
		return new MatsimZoneLinkCreator(visum, network).createZoneLinks(createNode);
	}

	private void createConnectors(Map<Integer, Node> nodes, Map<String, Node> zones) {
		new MatsimConnectorCreator(visum, network).createConnectors(nodes, zones);
	}

	Map<Integer, Node> createNodes() {
		return new MatsimNodeCreator(scenario).from(visum);
	}

}
