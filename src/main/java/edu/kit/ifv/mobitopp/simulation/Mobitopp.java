package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.CarRangeReachableZonesFilter;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceForFlexibleActivity;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceWithFixedLocations;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.SimpleRepeatedDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModelAddingCarsharing;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeChoiceStuttgart;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterFirstTrip;
import edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart.ModeSelectorParameterOtherTrip;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;

public class Mobitopp extends Simulation {

	public Mobitopp(SimulationContext context) {
		super(context);
	}

	@Override
	protected DemandSimulator simulator() {
		ModeAvailabilityModel modeAvailabilityModel = new ModeAvailabilityModelAddingCarsharing(
				impedance());
		DestinationChoiceModel destinationSelector = destinationChoiceModel(modeAvailabilityModel);
		ModeChoiceModel modeSelector = modeSelector(modeAvailabilityModel);
		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();
		ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(context().simulationDays());
		System.out.println("Initializing simulator...");
		return new DemandSimulatorPassenger(destinationSelector, modeSelector, routeChoice,
				new DefaultActivityDurationRandomizer(context().seed()), rescheduling,
				PersonStateSimple.UNINITIALIZED, context());
	}

	private ModeChoiceModel modeSelector(ModeAvailabilityModel modeAvailabilityModel) {
		ModeChoiceModel modeSelectorFirst = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterFirstTrip());
		ModeChoiceModel modeSelectorOther = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterOtherTrip());
		return new ModeSelectorFirstOther(modeAvailabilityModel, modeSelectorFirst, modeSelectorOther);
	}

	private DestinationChoiceModel destinationChoiceModel(
			ModeAvailabilityModel modeAvailabilityModel) {
		return new DestinationChoiceWithFixedLocations(zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(zoneRepository().zones(),
						new DestinationChoiceForFlexibleActivity(modeAvailabilityModel,
								new CarRangeReachableZonesFilter(impedance()),
								new AttractivityCalculatorCostNextPole(zoneRepository().zones(), impedance(),
										getDestinationChoiceFileFor("cost"), 0.5f)),
						getDestinationChoiceFileFor("repetition")));
	}

	private String getDestinationChoiceFileFor(String name) {
		return context().configuration().getDestinationChoice().get(name);
	}
}
