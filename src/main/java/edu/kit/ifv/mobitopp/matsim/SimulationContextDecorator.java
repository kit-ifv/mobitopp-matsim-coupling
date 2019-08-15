package edu.kit.ifv.mobitopp.matsim;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;
import edu.kit.ifv.mobitopp.simulation.WrittenConfiguration;

public class SimulationContextDecorator implements SimulationContext {

	private final SimulationContext baseContext;

	public SimulationContextDecorator(SimulationContext baseContext) {
		super();
		this.baseContext = baseContext;
	}

	@Override
	public WrittenConfiguration configuration() {
		return baseContext.configuration();
	}

	@Override
	public DynamicParameters experimentalParameters() {
		return baseContext.experimentalParameters();
	}

	@Override
	public long seed() {
		return baseContext.seed();
	}

	@Override
	public float fractionOfPopulation() {
		return baseContext.fractionOfPopulation();
	}

	@Override
	public SimulationDays simulationDays() {
		return baseContext.simulationDays();
	}

	@Override
	public DataRepositoryForSimulation dataRepository() {
		return baseContext.dataRepository();
	}

	@Override
	public ZoneRepository zoneRepository() {
		return baseContext.zoneRepository();
	}

	@Override
	public ImpedanceIfc impedance() {
		return baseContext.impedance();
	}

	@Override
	public VehicleBehaviour vehicleBehaviour() {
		return baseContext.vehicleBehaviour();
	}

	@Override
	public PersonLoader personLoader() {
		return baseContext.personLoader();
	}
	
	@Override
	public DynamicParameters modeChoiceParameters() {
		return baseContext.modeChoiceParameters();
	}

	@Override
	public Results results() {
		return baseContext.results();
	}

	@Override
	public PersonResults personResults() {
		return baseContext.personResults();
	}

	@Override
	public void beforeSimulation() {
		baseContext.beforeSimulation();
	}

	@Override
	public void afterSimulation() {
		baseContext.afterSimulation();
	}

	@Override
	public DynamicParameters destinationChoiceParameters() {
		return baseContext.destinationChoiceParameters();
	}

}