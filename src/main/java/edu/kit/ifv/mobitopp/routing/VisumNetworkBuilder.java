package edu.kit.ifv.mobitopp.routing;

import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumTurn;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumNetworkBuilder {

	private final VisumTransportSystems transportSystems;
	private final VisumLinkTypes linkTypes;
	private final Map<Integer, VisumNode> nodes;
	private Map<Integer, VisumLink> links;
	private final Map<Integer, List<VisumTurn>> turns;
	private final Map<Integer, VisumZone> zones;
	private final Map<Integer, VisumSurface> areas;
	private final Map<Integer, VisumTerritory> territories;
	private Map<Integer, List<VisumConnector>> connectors;
	private final Map<Integer, VisumChargingFacility> chargingFacilities;
	private final Map<Integer, VisumChargingPoint> chargingPoints;
	private final Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations;

	public VisumNetworkBuilder(VisumRoadNetwork original) {
		super();
		transportSystems = original.transportSystems;
		linkTypes = original.linkTypes;
		nodes = original.nodes;
		links = original.links.links;
		turns = original.turns;
		zones = original.zones;
		areas = original.areas;
		territories = original.territories;
		connectors = original.connectors;
		chargingFacilities = original.chargingFacilities;
		chargingPoints = original.chargingPoints;
		carSharingStations = original.carSharingStations;
	}

	public VisumRoadNetwork build() {
		return new VisumRoadNetwork(transportSystems, linkTypes, nodes, links, turns, zones, areas, territories,
				connectors, chargingFacilities, chargingPoints, carSharingStations);
	}

	public VisumNetworkBuilder withLinks(Map<Integer, VisumLink> links) {
		this.links = links;
		return this;
	}

	public VisumNetworkBuilder withConnectors(Map<Integer, List<VisumConnector>> connectors) {
		this.connectors = connectors;
		return this;
	}

}
