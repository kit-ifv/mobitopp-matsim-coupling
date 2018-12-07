package edu.kit.ifv.mobitopp.matsim;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.SimulationContext;
import edu.kit.ifv.mobitopp.simulation.external.ExternalTrip;
import edu.kit.ifv.mobitopp.simulation.external.ExternalTrips;
import edu.kit.ifv.mobitopp.simulation.external.HourlyTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumMatrix;

public class ExternalDemandCreator {

	private final SimulationContext context;

	public ExternalDemandCreator(SimulationContext context) {
		super();
		this.context = context;
	}

	public void addDemandTo(Population population, Network network) {
		System.out.println("Create external demand");
		List<VisumMatrix> matrices = externalTraffic();
		Collection<ExternalTrip> trips = createExternalTrips(matrices, network);
		ExternalPersonCreator externalCreator = new ExternalPersonCreator(population);
		externalCreator.createPersonsWithPlansFor(trips);
		System.out.println(externalCreator.personsCreated() + " matsim persons created.");
	}

	private Collection<ExternalTrip> createExternalTrips(List<VisumMatrix> matrices, Network network) {
		ExternalTrips etm = new ExternalTrips(matrices, HourlyTimeProfile.DEFAULT, zoneRepository(), network);
		Collection<ExternalTrip> trips = etm.trips(fraction());
		return trips;
	}

	private List<VisumMatrix> externalTraffic() {
		VisumMatrix matrix1 = VisumMatrix.loadFrom(externalMatrixFileFor("externalTrafficW"));
		VisumMatrix matrix2 = VisumMatrix.loadFrom(externalMatrixFileFor("externalTrafficEx"));
		return Arrays.asList(matrix1, matrix2);
	}

	private File externalMatrixFileFor(String fileName) {
		return context.experimentalParameters().valueAsFile(fileName);
	}

	private float fraction() {
		return context.fractionOfPopulation();
	}

	private ZoneRepository zoneRepository() {
		return context.zoneRepository();
	}

}
