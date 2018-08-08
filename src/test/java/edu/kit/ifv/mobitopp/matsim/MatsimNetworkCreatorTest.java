package edu.kit.ifv.mobitopp.matsim;

import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNode;
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
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimNetworkCreatorTest {

	private Network network;
	private NetworkFactory factory;
	private Scenario scenario;

	@Before
	public void initialise() {
		scenario = mock(Scenario.class);
		network = mock(Network.class);
		factory = mock(NetworkFactory.class);

		when(scenario.getNetwork()).thenReturn(network);
		when(network.getFactory()).thenReturn(factory);
	}

	@Test
	public void createsNodes() {
		Node someMatsimNode = configureMatsimNode(someNode());
		Node anotherMatsimNode = configureMatsimNode(anotherNode());
		Node otherMatsimNode = configureMatsimNode(otherNode());
		VisumRoadNetwork visum = visumFrom(someNode(), anotherNode(), otherNode());

		MatsimNetworkCreator creator = new MatsimNetworkCreator(scenario, visum);

		Map<Integer, Node> nodes = creator.createNodes();

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

	private VisumRoadNetwork visumFrom(VisumNode... nodes) {
		VisumNetworkBuilder toVisumNetwork = visumNetwork();
		add(asList(nodes), toVisumNetwork);
		addLinksFrom(asList(nodes), toVisumNetwork);
		return toVisumNetwork.build();
	}

	private void addLinksFrom(List<VisumNode> nodes, VisumNetworkBuilder visumNetwork) {
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

	private void add(List<VisumNode> nodes, VisumNetworkBuilder visumNetwork) {
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
