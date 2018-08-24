package edu.kit.ifv.mobitopp.matsim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class MatsimPersonCreatorTest {

	private MatsimPersonCreator creator;

	@Before
	public void initialise() {
		Population population = mock(Population.class);
		Time simulationStart = Time.start;
		Network network = mock(Network.class);
		creator = new MatsimPersonCreator(population, simulationStart, network);
	}
	
	@Test
	public void convertsModeToMatsim() {
		HashMap<Mode, String> mapping = new HashMap<>();
		Arrays.stream(Mode.values()).forEach(mode -> mapping.put(mode, TransportMode.car));
		mapping.put(Mode.BIKE, TransportMode.bike);
		mapping.put(Mode.CAR, TransportMode.car);
		mapping.put(Mode.PASSENGER, TransportMode.ride);
		mapping.put(Mode.PEDESTRIAN, TransportMode.walk);
		mapping.put(Mode.PUBLICTRANSPORT, TransportMode.pt);
		for (Entry<Mode, String> entry : mapping.entrySet()) {
			evaluate(entry.getKey(), entry.getValue());
		}
	}

	private void evaluate(Mode mobiTopp, String matsim) {
		String matsimMode = creator.asMatsimMode(mobiTopp);

		assertThat(matsimMode, is(equalTo(matsim)));
	}
}
