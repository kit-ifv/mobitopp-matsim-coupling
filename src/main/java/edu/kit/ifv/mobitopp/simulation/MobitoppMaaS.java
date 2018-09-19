package edu.kit.ifv.mobitopp.simulation;

import java.io.File;

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
import edu.kit.ifv.mobitopp.simulation.modeChoice.maas.ModeSelectorParameterFirstTrip;
import edu.kit.ifv.mobitopp.simulation.modeChoice.maas.ModeSelectorParameterOtherTrip;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.tour.TourBasedModeChoiceModelDummy;

public class MobitoppMaaS extends Simulation {

	public MobitoppMaaS(SimulationContext context) {
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
		return new DemandSimulatorPassenger(destinationSelector,
				new TourBasedModeChoiceModelDummy(modeSelector), routeChoice,
				new DefaultActivityDurationRandomizer(context().seed()), rescheduling,
				PersonStateSimple.UNINITIALIZED, context());
	}

	private ModeChoiceModel modeSelector(ModeAvailabilityModel modeAvailabilityModel) {
		File firstTripFile = getModeChoiceFile("firstTrip");
		File otherTripFile = getModeChoiceFile("otherTrip");
		ModeChoiceModel modeSelectorFirst = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterFirstTrip(firstTripFile));
		ModeChoiceModel modeSelectorOther = new ModeChoiceStuttgart(impedance(),
				new ModeSelectorParameterOtherTrip(otherTripFile));
		return new ModeSelectorFirstOther(modeAvailabilityModel, modeSelectorFirst, modeSelectorOther);
	}

	private File getModeChoiceFile(String fileName) {
		return context().modeChoiceParameters().valueAsFile(fileName);
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
