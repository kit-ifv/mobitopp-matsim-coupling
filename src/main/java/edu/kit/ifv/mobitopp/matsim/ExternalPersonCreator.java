package edu.kit.ifv.mobitopp.matsim;

import java.util.Collection;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;

import edu.kit.ifv.mobitopp.simulation.external.ExternalTrip;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ExternalPersonCreator {

	static final String OUTFLOW = "OUTFLOW";
	static final String INFLOW = "INFLOW";
	static final RelativeTime externalTripDuration = RelativeTime.ofHours(2);
	private final Population population;
	private final PopulationFactory populationFactory;
	private int i;

	public ExternalPersonCreator(Population population) {
		super();
		this.population = population;
		this.populationFactory = population.getFactory();
	}

	public void createPersonsWithPlansFor(Collection<ExternalTrip> trips) {
		i = 0;
		for (ExternalTrip trip : trips) {
			Person person = createPerson();
			Plan plan = createPlan(trip);
			person.addPlan(plan);
		}
	}

	private Person createPerson() {
		String personId = "T" + (++i);
		Person person = this.populationFactory.createPerson(Id.createPersonId(personId));
		assert this.population != null;
		assert person != null;
		this.population.addPerson(person);
		return person;
	}

	private Plan createPlan(ExternalTrip trip) {
		Plan plan = this.populationFactory.createPlan();
		Activity source = createSource(trip);
		Leg leg = createLeg();
		Activity destination = createDestination(trip);
		plan.addActivity(source);
		plan.addLeg(leg);
		plan.addActivity(destination);
		return plan;
	}

	private Leg createLeg() {
		return populationFactory.createLeg(TransportMode.car);
	}

	private Activity createDestination(ExternalTrip trip) {
		int onLink = trip.to;
		Activity destination = createActivityFor(OUTFLOW, onLink);
		long startTime = trip.startTime().toSeconds();
		destination.setStartTime(startTime);
		return destination;
	}

	private Activity createActivityFor(String activityType, int link) {
		Id<Link> linkId = createLinkId(activityType, link);
		return populationFactory.createActivityFromLinkId(activityType, linkId);
	}

	static Id<Link> createLinkId(String activityType, int link) {
		return Id.createLinkId("Z" + link + "-" + activityType);
	}

	private Activity createSource(ExternalTrip trip) {
		int onLink = trip.from;
		Activity source = createActivityFor(INFLOW, onLink);
		source.setStartTime(Time.start.toSeconds());
		long endTime = trip.startTime().minus(externalTripDuration).toSeconds();
		source.setEndTime(endTime);
		return source;
	}

	public int personsCreated() {
		return i;
	}

}
