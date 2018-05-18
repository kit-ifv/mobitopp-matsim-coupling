package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.CombinedUtilityFunctions;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.DestinationAndModeChoiceSchaufenster;
import edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice.DestinationAndModeChoiceUtility;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModel;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceModelChoiceSet;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.DestinationChoiceWithFixedLocations;
import edu.kit.ifv.mobitopp.simulation.destinationChoice.SimpleRepeatedDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModelAddingCarsharing;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceModel;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;

public class SimulationMatsim extends Simulation {

	public SimulationMatsim(SimulationContext context) {
		super(context);
	}

	@Override
	protected DemandSimulator simulator() {
		ModeAvailabilityModel modeAvailabilityModel = new ModeAvailabilityModelAddingCarsharing(
				impedance());
		DestinationAndModeChoiceSchaufenster destinationAndModeChoice = createDestinationAndModeChoiceModel(
				modeAvailabilityModel);
		DestinationChoiceModel destinationSelector = destinationChoiceModel(destinationAndModeChoice);
		ModeChoiceModel modeSelector = destinationAndModeChoice;
		ZoneBasedRouteChoice routeChoice = new NoRouteChoice();
		ReschedulingStrategy rescheduling = new ReschedulingSkipTillHome(context().simulationDays());
		System.out.println("Initializing simulator...");
		return new DemandSimulatorPassenger(destinationSelector, modeSelector, routeChoice,
				new DefaultActivityDurationRandomizer(context().seed()), rescheduling,
				PersonStateSimple.UNINITIALIZED, context());
	}

	private DestinationAndModeChoiceSchaufenster createDestinationAndModeChoiceModel(
			ModeAvailabilityModel modeAvailabilityModel) {
		CombinedUtilityFunctions combinedUtilityFunctions = new CombinedUtilityFunctions(context());
		Map<ActivityType, DestinationAndModeChoiceUtility> utilityFunctions = combinedUtilityFunctions
				.load();
		return new DestinationAndModeChoiceSchaufenster(zoneRepository().zones(), modeAvailabilityModel,
				utilityFunctions);
	}

	private DestinationChoiceModel destinationChoiceModel(
			DestinationChoiceModelChoiceSet destinationModeModel) {
		return new DestinationChoiceWithFixedLocations(zoneRepository().zones(),
				new SimpleRepeatedDestinationChoice(zoneRepository().zones(), destinationModeModel,
						getDestinationChoiceFileFor("repetition")));
	}

	private String getDestinationChoiceFileFor(String name) {
		return context().configuration().getDestinationChoice().get(name);
	}

	public static void main(String... args) throws IOException {
		if (1 > args.length) {
			System.out.println("Usage: ... <configuration file>");
			System.exit(-1);
		}

		File configurationFile = new File(args[0]);
		LocalDateTime start = LocalDateTime.now();
		startSimulation(configurationFile);
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		System.out.println("Simulation took " + runtime);
	}

	private static void startSimulation(File configurationFile) throws IOException {
		SimulationContext context = new ContextBuilder().buildFrom(configurationFile);
		startSimulation(context);
	}
		
	public static void startSimulation(SimulationContext context) {
		new SimulationMatsim(context).simulate();
	}

}
