package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.matsim.PrepareMatsim;

public class MobitoppMatsimCoupling {

	private SimulationContext context;

	public MobitoppMatsimCoupling(SimulationContext context) {
		super();
		this.context = context;
	}

	public void simulate() {
		Mobitopp mobitopp = new Mobitopp(context);
		Matsim matsim = PrepareMatsim.from(context);
		mobitopp.simulate();
		matsim.createPersons();
		matsim.createPlans();
		matsim.simulate();
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
		new MobitoppMatsimCoupling(context).simulate();
	}

}
