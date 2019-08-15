package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.matsim.MatsimContext;
import edu.kit.ifv.mobitopp.matsim.MatsimContextBuilder;

public class MobitoppMatsimCoupling {

	private final WrittenConfiguration configuration;

	public MobitoppMatsimCoupling(WrittenConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public void simulate() throws IOException {
		Optional<ImpedanceIfc> impedance = Optional.empty();
		for (int current = 0; current < iterations(); current++) {
			MatsimContext context = loadContext(impedance, current);
			impedance = new MobitoppMatsimIteration(context).simulate();
		}
	}

	private MatsimContext loadContext(Optional<ImpedanceIfc> impedance, int current)
			throws IOException {
		WrittenConfiguration derived = new WrittenConfiguration(configuration);
		String resultFolder = derived.getResultFolder();
		resultFolder += "-" + current;
		derived.setResultFolder(resultFolder);
		MatsimContext context = new MatsimContextBuilder().buildFrom(derived);
		impedance.ifPresent(i -> context.updateImpedance(i));
		return context;
	}

	private int iterations() {
		return new DynamicParameters(configuration.getExperimental()).valueAsInteger("iterations");
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
		ParserBuilder parser = new ParserBuilder();
		SimulationParser format = parser.forSimulation();
		WrittenConfiguration configuration = format.parse(configurationFile);
		new MobitoppMatsimCoupling(configuration).simulate();
	}

}
