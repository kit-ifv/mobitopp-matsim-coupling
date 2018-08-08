package edu.kit.ifv.mobitopp.matsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import edu.kit.ifv.mobitopp.routing.ValidateLinks;
import edu.kit.ifv.mobitopp.simulation.Matsim;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class PrepareMatsim {

	private final SimulationContext context;

	public PrepareMatsim(SimulationContext context) {
		this.context = context;
	}

	public static Matsim from(SimulationContext context) {
		return new PrepareMatsim(context).create();
	}

	private Matsim create() {
		Scenario scenario = createScenario();
		loadNetwork(scenario);
		return new Matsim(context, scenario);
	}

	private void loadNetwork(Scenario scenario) {
		VisumRoadNetwork visumNetwork = new ValidateLinks().of(context.network());
		MatsimNetworkCreator creator = new MatsimNetworkCreator(scenario, visumNetwork);
		creator.createFromVisumNetwork();
	}

	private Scenario createScenario() {
		return ScenarioUtils.createScenario(fromConfig());
	}

	private Config fromConfig() {
		float fractionOfPopulation = context.fractionOfPopulation();
		String matsimConfig = context.experimentalParameters().value("matsimConfig");
		Config config = ConfigUtils.loadConfig(matsimConfig);
		config.qsim().setFlowCapFactor(fractionOfPopulation);
		config.qsim().setStorageCapFactor(fractionOfPopulation);
		config
				.controler()
				.setOverwriteFileSetting(
						OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		return config;
	}

}
