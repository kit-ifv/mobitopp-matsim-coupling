package edu.kit.ifv.mobitopp.matsim.converter;

import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumConnector;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumZone;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class MatsimNodeCreatorTest {

	private Network network;
	private NetworkFactory factory;
	private Scenario scenario;
	private VisumNetworkBuilder visumNetwork;

	@Before
	public void initialise() {
		scenario = mock(Scenario.class);
		network = mock(Network.class);
		factory = mock(NetworkFactory.class);

		when(scenario.getNetwork()).thenReturn(network);
		when(network.getFactory()).thenReturn(factory);
		visumNetwork = visumNetwork();
	}

	@Test
	public void nodesFromLinks() {
		MatsimNodeCreator creator = new MatsimNodeCreator(scenario);

		Node someMatsimNode = configureMatsimNode(someNode());
		Node anotherMatsimNode = configureMatsimNode(anotherNode());
		Node otherMatsimNode = configureMatsimNode(otherNode());
		addLinksFrom(someNode(), anotherNode(), otherNode());
		VisumRoadNetwork visum = buildNetwork();

		Map<Integer, Node> nodes = creator.from(visum);

		assertThat(nodes, hasEntry(someNode().id(), someMatsimNode));
		assertThat(nodes, hasEntry(anotherNode().id(), anotherMatsimNode));
		assertThat(nodes, hasEntry(otherNode().id(), otherMatsimNode));

		verify(scenario).createCoord(someNode().coord.x, someNode().coord.y);
		verify(scenario).createCoord(anotherNode().coord.x, anotherNode().coord.y);
		verify(scenario).createCoord(otherNode().coord.x, otherNode().coord.y);
		verify(network).addNode(someMatsimNode);
		verify(network).addNode(anotherMatsimNode);
		verify(network).addNode(otherMatsimNode);
	}

	@Test
	public void nodesFromConnectors() {
		MatsimNodeCreator creator = new MatsimNodeCreator(scenario);

		Node someMatsimNode = configureMatsimNode(someNode());
		Node anotherMatsimNode = configureMatsimNode(anotherNode());
		addConnectorsFrom(someNode(), anotherNode());
		VisumNetwork visum = buildNetwork();

		Map<Integer, Node> nodes = creator.from(visum);

		assertThat(nodes, hasEntry(someNode().id(), someMatsimNode));
		assertThat(nodes, hasEntry(anotherNode().id(), anotherMatsimNode));
	}

	private void addConnectorsFrom(VisumNode... nodes) {
		VisumZone someZone = addZone();
		add(asList(nodes));
		addConnectors(someZone, nodes);
	}

	private void addConnectors(VisumZone someZone, VisumNode... nodes) {
		for (VisumNode node : nodes) {
			VisumConnector connector = visumConnector().with(node).build();
			visumNetwork.addConnector(someZone, connector);
		}
	}

	private VisumZone addZone() {
		VisumZone someZone = visumZone().withId(1).build();
		visumNetwork.with(someZone);
		return someZone;
	}

	private VisumNetwork buildNetwork() {
		return visumNetwork.build();
	}

	private void addLinksFrom(VisumNode... nodes) {
		add(asList(nodes));
		addLinksFrom(asList(nodes));
	}

	private void addLinksFrom(List<VisumNode> nodes) {
		int id = 0;
		ArrayList<VisumNode> endNodes = new ArrayList<>(nodes);
		for (VisumNode start : nodes) {
			endNodes.remove(start);
			for (VisumNode end : endNodes) {
				id++;
				VisumLink link = visumLink().withId(id).from(start).to(end).build();
				visumNetwork.with(link);
			}
		}
	}

	private void add(List<VisumNode> nodes) {
		for (VisumNode node : nodes) {
			visumNetwork.with(node);
		}
	}

	private Node configureMatsimNode(VisumNode node) {
		Coord coordinate = mock(Coord.class);
		when(scenario.createCoord(node.coord.x, node.coord.y)).thenReturn(coordinate);
		Node matsimNode = mock(Node.class);
		when(factory.createNode(any(), eq(coordinate))).thenReturn(matsimNode);
		return matsimNode;
	}

	private VisumNode someNode() {
		int id = 1;
		return nodeFor(id);
	}

	private VisumNode anotherNode() {
		int id = 2;
		return nodeFor(id);
	}

	private VisumNode otherNode() {
		int id = 3;
		return nodeFor(id);
	}

	private VisumNode nodeFor(int id) {
		float x = id;
		float y = id;
		return visumNode().withId(id).at(x, y).build();
	}
}
