package edu.kit.ifv.mobitopp.simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.matsim.core.controler.Controler;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.InMemoryMatrices;
import edu.kit.ifv.mobitopp.matsim.MatsimContext;
import edu.kit.ifv.mobitopp.matsim.PrepareMatsim;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.dataexport.MatrixPrinter;

public class MobitoppMatsimIteration {

	private final MatsimContext context;

	public MobitoppMatsimIteration(MatsimContext context) {
		this.context = context;
	}

	public Optional<ImpedanceIfc> simulate() {
		Mobitopp mobitopp = new Mobitopp(context);
		Matsim matsim = PrepareMatsim.from(context);
		mobitopp.simulate();
		matsim.createPersons();
		matsim.createPlans();
		Controler lastRun = matsim.simulate();
		ImpedanceIfc impedance = finishIteration(matsim, lastRun, context);
		return Optional.of(impedance);
	}

	private ImpedanceIfc finishIteration(Matsim matsim, Controler lastRun, MatsimContext context) {
		MatrixPrinter matrixPrinter = new MatrixPrinter();
		TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices = matsim
				.createTravelTimeMatrices(lastRun);
		MatrixWriter matrixWriter = new MatrixWriter(matrixPrinter);
		matrixWriter.setTravelTimeMatrixFolder(context);
		matrixWriter.writeTravelTimeMatrices(travelTimeMatrices);
		return createNewImpedance(travelTimeMatrices);
	}

	private ImpedanceIfc createNewImpedance(TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices) {
    InMemoryMatrices carMatrices = createMatrices(travelTimeMatrices);
    return new ImpedanceMatSimIteration(context.impedance(), carMatrices);
  }

  private InMemoryMatrices createMatrices(TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices) {
    Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> dayMatrices = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      dayMatrices.put(day, travelTimeMatrices);
    }
    return new InMemoryMatrices(dayMatrices);
  }
}
