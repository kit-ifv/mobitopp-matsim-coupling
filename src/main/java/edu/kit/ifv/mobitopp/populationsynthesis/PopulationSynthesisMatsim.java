package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ElectricCarOwnershipBasedOnSociodemographic;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.GenericElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.ProbabilityForElectricCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.CarVelocityFilter;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.EdgeFilter;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.RoadBasedHouseholdLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.RoadBasedOpportunitySelector;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPersonCreator;

public class PopulationSynthesisMatsim extends PopulationSynthesis {

	private static final int maxVelocity = 50;
	private static final double maxDistance = 1.0d;

	public PopulationSynthesisMatsim(
			CarOwnershipModel carOwnershipModel, HouseholdLocationSelector householdLocationSelector,
			ChargePrivatelySelector chargePrivatelySelector, PersonCreator personCreator,
			SynthesisContext context) {

		super(carOwnershipModel, householdLocationSelector, chargePrivatelySelector, personCreator,
				context);
	}

	public static void main(String... args) throws Exception {
		if (1 > args.length) {
			System.out.println("Usage: ... <configuration file>");
			System.exit(-1);
		}

		File configurationFile = new File(args[0]);
		LocalDateTime start = LocalDateTime.now();
		startSynthesis(configurationFile);
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		System.out.println("Population synthesis took " + runtime);
	}

	public static PopulationSynthesisMatsim startSynthesis(File configurationFile) throws Exception {
		SynthesisContext context = new ContextBuilder().buildFrom(configurationFile);
		PopulationSynthesisMatsim synthesizer = populationSynthesis(context);
		createLocations(context, synthesizer);
		synthesizer.createPopulation();
		return synthesizer;
	}

	private static HouseholdLocationSelector householdLocations(SynthesisContext context) {
		return new RoadBasedHouseholdLocationSelector(context, maxDistance, edgeFilter());
	}

	private static EdgeFilter edgeFilter() {
		return new CarVelocityFilter(maxVelocity);
	}

	private static PopulationSynthesisMatsim populationSynthesis(SynthesisContext context) {
		HouseholdLocationSelector householdLocationSelector = householdLocations(context);
		CommutationTicketModelIfc commuterTicketModel = commuterTickets(context);
		CarOwnershipModel carOwnershipModel = carOwnership(context);
		ActivityScheduleCreator activityScheduleCreator = context.activityScheduleCreator();
		ChargePrivatelySelector chargePrivatelySelector = chargePrivately(context);
		PersonCreator personCreator = personCreator(context, commuterTicketModel,
				activityScheduleCreator);
		return populationSynthesis(householdLocationSelector, carOwnershipModel,
				chargePrivatelySelector, personCreator, context);
	}

	private static EmobilityPersonCreator personCreator(
			SynthesisContext configuration, CommutationTicketModelIfc commuterTicketModel,
			ActivityScheduleCreator activityScheduleCreator) {
		Map<String, CarSharingCustomerModel> carSharing = configuration.carSharing();
		return new EmobilityPersonCreator(activityScheduleCreator, commuterTicketModel, carSharing,
				configuration.seed());
	}

	private static CommutationTicketModelIfc commuterTickets(SynthesisContext context) {
		String commuterTicketFile = context.configuration().getCommuterTicket();
		return new CommutationTicketModelStuttgart(commuterTicketFile, context.seed());
	}

	private static CarOwnershipModel carOwnership(SynthesisContext context) {
		IdSequence carIDs = new IdSequence();
		long seed = context.seed();
		File carOwnershipFile = context.carEngineFile();
		String segmentFile = context.configuration().getCarOwnership().getSegment();
		String ownershipFile = context.configuration().getCarOwnership().getOwnership();
		ImpedanceIfc impedance = context.impedance();
		CarSegmentModel segmentModel = new LogitBasedCarSegmentModel(impedance, seed, segmentFile);
		ProbabilityForElectricCarOwnershipModel calculator = new ElectricCarOwnershipBasedOnSociodemographic(
				impedance, carOwnershipFile.getAbsolutePath());
		return new GenericElectricCarOwnershipModel(carIDs, segmentModel, seed, calculator,
				ownershipFile);
	}

	private static AllowChargingProbabilityBased chargePrivately(SynthesisContext configuration) {
		return new AllowChargingProbabilityBased(configuration.seed());
	}

	private static PopulationSynthesisMatsim populationSynthesis(
			HouseholdLocationSelector householdLocationSelector, CarOwnershipModel carOwnershipModel,
			ChargePrivatelySelector chargePrivatelySelector, PersonCreator personCreator,
			SynthesisContext context) {
		return new PopulationSynthesisMatsim(carOwnershipModel, householdLocationSelector,
				chargePrivatelySelector, personCreator, context);
	}

	private static void createLocations(SynthesisContext context, PopulationSynthesis synthesizer) {
		System.out.println("creating destinations...");
		OpportunityLocationSelector opportunityLocationSelector = new RoadBasedOpportunitySelector(
				context, edgeFilter(), maxDistance);
		synthesizer.createLocations(opportunityLocationSelector);
		System.out.println("creating DONE.");
	}

}
