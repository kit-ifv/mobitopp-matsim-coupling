package edu.kit.ifv.mobitopp.simulation;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.controler.Controler;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimMatrixGenerator {

  private final Network network;
  private final VisumRoadNetwork visumNetwork;
  private final Map<Integer, Integer> idsToOids;

  public MatsimMatrixGenerator(
      Network network, VisumRoadNetwork visumNetwork, Map<Integer, Integer> idsToOids) {
    super();
    this.network = network;
    this.visumNetwork = visumNetwork;
    this.idsToOids = idsToOids;
  }

  public TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices(
      Controler controler) {
    TreeMap<Integer, TravelTimeMatrix> ttMatrix = new TreeMap<>();
    calculateTravelTimes(controler, ttMatrix);
    return ttMatrix;
  }

  private void calculateTravelTimes(Controler matsim, TreeMap<Integer, TravelTimeMatrix> ttMatrix) {
    MatsimHourMatrixCalculator matrixCalculator = new MatsimHourMatrixCalculator(network, visumNetwork, idsToOids, matsim);
    IntConsumer createAndAdd = hour -> createAndAddMatrix(ttMatrix, matrixCalculator, hour);
    runInParallel(() -> IntStream.range(0, 24).parallel().forEach(createAndAdd));
  }

  private void createAndAddMatrix(
      TreeMap<Integer, TravelTimeMatrix> ttMatrix, MatsimHourMatrixCalculator matrixCalculator,
      int hour) {
    float midHourInSeconds = (0.5f + hour) * 60 * 60;
    TravelTimeMatrix matrix = matrixCalculator.calculateMatrix(midHourInSeconds);
    ttMatrix.put(hour, matrix);
  }

  private void runInParallel(Runnable task) {
    ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfThreads());
    try {
      forkJoinPool.submit(task).get();
    } catch (InterruptedException | ExecutionException cause) {
      throw new RuntimeException(cause);
    }
  }

  private int numberOfThreads() {
    return Runtime.getRuntime().availableProcessors();
  }

}
