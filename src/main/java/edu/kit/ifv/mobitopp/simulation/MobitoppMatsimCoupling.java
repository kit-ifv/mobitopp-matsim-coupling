package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeMap;

import org.matsim.core.controler.Controler;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.matsim.MatsimContext;
import edu.kit.ifv.mobitopp.matsim.MatsimContextBuilder;
import edu.kit.ifv.mobitopp.matsim.PrepareMatsim;
import edu.kit.ifv.mobitopp.util.dataexport.MatrixPrinter;

public class MobitoppMatsimCoupling {

	private MatsimContext context;

	public MobitoppMatsimCoupling(MatsimContext context) {
		super();
		this.context = context;
	}

	public void simulate() {
		Mobitopp mobitopp = new Mobitopp(context);
		Matsim matsim = PrepareMatsim.from(context);
		mobitopp.simulate();
		matsim.createPersons();
		matsim.createPlans();
		Controler lastRun = matsim.simulate();
		processTravelTimeMatrices(matsim, lastRun);
	}

  private void processTravelTimeMatrices(Matsim matsim, Controler lastRun) {
    MatrixPrinter matrixPrinter = MatrixPrinter.fromZones(context.zoneRepository().zones());
		TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices = matsim.createTravelTimeMatrices(lastRun);
		matsim.updateTravelTime(travelTimeMatrices);
		MatrixWriter matrixWriter = new MatrixWriter(matrixPrinter);
    matrixWriter.writeTravelTimeMatrices(travelTimeMatrices);
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
		MatsimContext context = new MatsimContextBuilder().buildFrom(configurationFile);
		new MobitoppMatsimCoupling(context).simulate();
	}

}
