package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.util.dataexport.MatrixPrinter;

public class MatrixWriter {

  private final MatrixPrinter matrixPrinter;
  private File travelTimeMatrixDir;

  public MatrixWriter(MatrixPrinter matrixPrinter) {
    super();
    this.matrixPrinter = matrixPrinter;
  }

  public void setTravelTimeMatrixFolder(SimulationContext context) {
    File baseFolder = Convert.asFile(context.configuration().getResultFolder());
    String travelTimeFolderName = context
        .experimentalParameters()
        .value("travelTimeMatrixFolderName");
    travelTimeMatrixDir = new File(baseFolder, travelTimeFolderName);
    travelTimeMatrixDir.mkdirs();
    if (!travelTimeMatrixDir.exists() || !travelTimeMatrixDir.isDirectory()) {
      throw new IllegalStateException(
          "Could not create travel time folder at " + travelTimeMatrixDir);
    }
  }

  public void writeTravelTimeMatrices(TreeMap<Integer, TravelTimeMatrix> matrices, int iteration) {
    for (Integer hour : matrices.keySet()) {
      TravelTimeMatrix matrix = matrices.get(hour);
      File file = matrixFileFor(iteration, hour);
      System.out.println("Writing travel time matrix for " + hour);
      matrixPrinter.writeMatrixToFile(matrix, hour.toString(), hour.toString(), file);
    }
  }

  private File matrixFileFor(int iteration, Integer hour) {
    return new File(travelTimeMatrixDir, "TravelTime_" + "it" + iteration + "_" + hour + ".mtx");
  }
}
