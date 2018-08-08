package edu.kit.ifv.mobitopp.routing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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

public class VisumNetworkBuilderTest {

	private VisumTransportSystems transportSystems;
	private VisumLinkTypes linkTypes;
	private Map<Integer, VisumNode> nodes;
	private Map<Integer, VisumLink> links;
	private Map<Integer, List<VisumTurn>> turns;
	private Map<Integer, VisumZone> zones;
	private Map<Integer, VisumSurface> areas;
	private Map<Integer, VisumTerritory> territories;
	private Map<Integer, List<VisumConnector>> connectors;
	private Map<Integer, VisumChargingFacility> chargingFacilities;
	private Map<Integer, VisumChargingPoint> chargingPoints;
	private Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations;
	private VisumRoadNetwork original;
	
	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		transportSystems = mock(VisumTransportSystems.class);
		linkTypes = mock(VisumLinkTypes.class);
		nodes = mock(Map.class);
		links = mock(Map.class);
		turns = mock(Map.class);
		zones = mock(Map.class);
		areas = mock(Map.class);
		territories = mock(Map.class);
		connectors = mock(Map.class);
		chargingFacilities = mock(Map.class);
		chargingPoints = mock(Map.class);
		carSharingStations = mock(Map.class);
		original = new VisumRoadNetwork(transportSystems, linkTypes, nodes, links, turns, zones, areas, territories, connectors,
				chargingFacilities, chargingPoints, carSharingStations);
	}

	@Test
	public void cloneCompleteNetwork() {
		VisumRoadNetwork roadNetwork = builder().build();
		
		assertThat(roadNetwork, is(not(sameInstance(original))));
		assertThat(roadNetwork.areas, is(equalTo(original.areas)));
		assertThat(roadNetwork.carSharingStations, is(equalTo(original.carSharingStations)));
		assertThat(roadNetwork.chargingFacilities, is(equalTo(original.chargingFacilities)));
		assertThat(roadNetwork.chargingPoints, is(equalTo(original.chargingPoints)));
		assertThat(roadNetwork.connectors, is(equalTo(original.connectors)));
		assertThat(roadNetwork.links.links, is(equalTo(original.links.links)));
		assertThat(roadNetwork.turns, is(equalTo(original.turns)));
		assertThat(roadNetwork.nodes, is(equalTo(original.nodes)));
		assertThat(roadNetwork.territories, is(equalTo(original.territories)));
		assertThat(roadNetwork.zones, is(equalTo(original.zones)));
	}
	
	@Test
	public void usesOtherLinks() {
		@SuppressWarnings("unchecked")
		Map<Integer, VisumLink> otherLinks = mock(Map.class);
		VisumRoadNetwork network = builder().withLinks(otherLinks).build();
		
		assertThat(network.links.links, is(equalTo(otherLinks)));
	}
	
	@Test
	public void usesOtherConnectors() {
		@SuppressWarnings("unchecked")
		Map<Integer, List<VisumConnector>> otherConnectors = mock(Map.class);
		VisumRoadNetwork network = builder().withConnectors(otherConnectors).build();
		
		assertThat(network.connectors, is(equalTo(otherConnectors)));
	}
	
	private VisumNetworkBuilder builder() {
		return new VisumNetworkBuilder(original);
	}
}
