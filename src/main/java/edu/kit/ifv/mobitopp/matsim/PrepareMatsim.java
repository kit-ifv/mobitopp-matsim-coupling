package edu.kit.ifv.mobitopp.matsim;

import java.io.File;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import edu.kit.ifv.mobitopp.routing.ValidateLinks;
import edu.kit.ifv.mobitopp.simulation.Matsim;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class PrepareMatsim {

	static final String resultFolder = "matsim";
	private final MatsimContext context;

	public PrepareMatsim(MatsimContext context) {
		this.context = context;
	}
	
	public static Matsim from(MatsimContext context) {
		return from(context, new CarOnly());
	}

	public static Matsim from(MatsimContext context, ActivityFilter filter) {
		return new PrepareMatsim(context).create(filter);
	}

	private Matsim create(ActivityFilter filter) {
		Scenario scenario = createScenario();
		loadNetwork(scenario);
		return new Matsim(context, scenario, filter);
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
		updateResultFolder(config);
		return config;
	}

	void updateResultFolder(Config config) {
		String baseFolder = context.configuration().getResultFolder();
		String matsimResults = new File(baseFolder, resultFolder).getAbsolutePath();
		config.controler().setOutputDirectory(matsimResults);
	}

}
