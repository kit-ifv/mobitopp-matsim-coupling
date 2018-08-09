package edu.kit.ifv.mobitopp.matsim.converter;

import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNetwork;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumBuilder;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class MatsimLinkCreatorTest {

	private NetworkFactory factory;
	private Network matsim;
	private Map<Integer, Node> nodes;

	@Before
	public void initialise() {
		matsim = mock(Network.class);
		factory = mock(NetworkFactory.class);
		when(matsim.getFactory()).thenReturn(factory);
		nodes = new HashMap<>();
	}

	@Test
	public void convert() {
		VisumNetworkBuilder visumNetwork = visumNetwork();
		Stream
				.of("P", "Lkw", "I", "NVBW", "R", "S", "U", "Rad", "Fuss", "F")
				.map(this::transportSystem)
				.forEach(visumNetwork::with);
		VisumNode first = VisumBuilder.visumNode().withId(1).build();
		VisumNode second = VisumBuilder.visumNode().withId(2).build();
		int linkId = 1;
		VisumLink visumLink = VisumBuilder
				.visumLink()
				.withId(linkId)
				.withCapacityCar(1)
				.from(first)
				.with(transportSystem("P"))
				.to(second)
				.build();
		VisumRoadNetwork visum = visumNetwork.with(first).with(second).with(visumLink).build();
		Node firstNode = configureNode(first);
		Node secondNode = configureNode(second);
		Link linkA = configureLink(visumLink.linkA, firstNode, secondNode);
		Link linkB = configureLink(visumLink.linkB, secondNode, firstNode);

		MatsimLinkCreator creator = new MatsimLinkCreator(visum, matsim);

		creator.createLinks(nodes);

		verify(matsim).addLink(linkA);
		verify(matsim).addLink(linkB);
	}

	private VisumTransportSystem transportSystem(String name) {
		return new VisumTransportSystem(name, name, name);
	}

	private Link configureLink(VisumOrientedLink visumLink, Node fromNode, Node toNode) {
		Link link = mock(Link.class);
		when(link.getFromNode()).thenReturn(fromNode);
		when(link.getToNode()).thenReturn(toNode);
		Id<Link> id = Id.createLinkId(visumLink.id);
		when(factory.createLink(id, fromNode, toNode)).thenReturn(link);
		return link;
	}

	private Node configureNode(VisumNode first) {
		Node node = mock(Node.class);
		nodes.put(first.id(), node);
		return node;
	}
}
