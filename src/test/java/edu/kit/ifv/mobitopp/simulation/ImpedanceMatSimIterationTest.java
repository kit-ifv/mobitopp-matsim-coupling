package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.local.InMemoryMatrices;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.ImpedanceMatSimIteration;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceMatSimIterationTest {

	private ImpedanceIfc impedance;
	private InMemoryMatrices travelTime;
	private ImpedanceMatSimIteration matsim;
	private int origin;
	private int destination;
	private Time date;
	private EnumSet<Mode> carModes;
	private EnumSet<Mode> nonCarModes;

	@Before
	public void initialise() {
		impedance = mock(ImpedanceIfc.class);
		travelTime = mock(InMemoryMatrices.class);
		matsim = new ImpedanceMatSimIteration(impedance, travelTime);
		carModes = EnumSet.of(Mode.CAR, Mode.CARSHARING_FREE, Mode.CARSHARING_STATION, Mode.PASSENGER);
		nonCarModes = EnumSet.complementOf(carModes);

		origin = 0;
		destination = 0;
		date = new SimpleTime();
	}

	@Test
	public void delegatesCar() {
		for (Mode mode : carModes) {
			usesInMemoryMatricesFor(mode);
		}
		verify(travelTime, times(carModes.size())).getTravelTime(origin, destination, date);
	}

	private void usesInMemoryMatricesFor(Mode mode) {
		matsim.getTravelTime(origin, destination, mode, date);
	}

	@Test
	public void delegatesNonCar() {
		for (Mode mode : nonCarModes) {
			usesImpedanceFor(mode);
		}
	}

	private void usesImpedanceFor(Mode mode) {
		matsim.getTravelTime(origin, destination, mode, date);

		verify(impedance).getTravelTime(origin, destination, mode, date);
	}
}
