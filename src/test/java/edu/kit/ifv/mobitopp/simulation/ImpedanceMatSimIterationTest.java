package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.local.InMemoryMatrices;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceMatSimIterationTest {

	private ImpedanceIfc impedance;
	private InMemoryMatrices travelTime;
	private ImpedanceMatSimIteration matsim;
	private ZoneId origin;
	private ZoneId destination;
	private Time date;
	private EnumSet<StandardMode> carModes;
	private EnumSet<StandardMode> nonCarModes;

	@Before
	public void initialise() {
		impedance = mock(ImpedanceIfc.class);
		travelTime = mock(InMemoryMatrices.class);
		matsim = new ImpedanceMatSimIteration(impedance, travelTime);
		carModes = EnumSet.of(StandardMode.CAR, StandardMode.CARSHARING_FREE, StandardMode.CARSHARING_STATION, StandardMode.PASSENGER);
		nonCarModes = EnumSet.complementOf(carModes);

		origin = new ZoneId("0", 0);
		destination = new ZoneId("0", 0);
		date = new SimpleTime();
	}

	@Test
	public void delegatesCar() {
		for (Mode mode : carModes) {
			usesInMemoryMatricesFor(mode);
		}
		verify(travelTime, times(carModes.size())).getTravelTime(origin.getMatrixColumn(), destination.getMatrixColumn(), date);
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
