package edu.kit.ifv.mobitopp.matsim.converter;

import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNetwork;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimLinkCreatorTest {

	@Test
	public void convert() {
		VisumRoadNetwork visum = visumNetwork().build();
		Network matsim = mock(Network.class);
		Map<Integer, Node> nodes = new HashMap<>();
		MatsimLinkCreator creator = new MatsimLinkCreator(visum, matsim);
		
		creator.createLinks(nodes);
	}
}
