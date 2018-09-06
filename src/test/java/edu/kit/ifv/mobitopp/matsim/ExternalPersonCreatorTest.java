package edu.kit.ifv.mobitopp.matsim;

import static edu.kit.ifv.mobitopp.matsim.ExternalPersonCreator.INFLOW;
import static edu.kit.ifv.mobitopp.matsim.ExternalPersonCreator.OUTFLOW;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.simulation.external.ExternalTrip;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ExternalPersonCreatorTest {

	private PopulationFactory factory;

	@Test
	public void addExternalTripsToPopulation() {
		int from = 1;
		int to = 2;
		int hour = 3;
		int minute = 4;
		double sourceStart = Time.start.toSeconds();
		Time sourceEndTime = SimpleTime
				.ofHours(hour)
				.minus(ExternalPersonCreator.externalTripDuration)
				.plusMinutes(minute);
		double sourceEnd = sourceEndTime.toSeconds();
		double destinationStart = sourceEndTime
				.plus(ExternalPersonCreator.externalTripDuration)
				.toSeconds();
		Id<Person> personId = Id.createPersonId("T1");
		Population population = mock(Population.class);
		factory = mock(PopulationFactory.class);
		Person person = mock(Person.class);
		Plan plan = mock(Plan.class);
		InOrder orderedPlan = inOrder(plan);
		when(population.getFactory()).thenReturn(factory);
		when(factory.createPerson(personId)).thenReturn(person);
		when(factory.createPlan()).thenReturn(plan);
		Activity source = configureActivity(INFLOW, from);
		Leg leg = configureLeg();
		Activity destination = configureActivity(OUTFLOW, to);

		ExternalTrip externalTrip = new ExternalTrip(from, to, hour, minute);
		Collection<ExternalTrip> trips = asList(externalTrip);
		ExternalPersonCreator creator = new ExternalPersonCreator(population);

		creator.createPersonsWithPlansFor(trips);

		assertThat(creator.personsCreated(), is(equalTo(trips.size())));

		verify(factory).createPerson(personId);
		verify(population).addPerson(person);
		verify(source).setStartTime(sourceStart);
		verify(source).setEndTime(sourceEnd);
		verify(destination).setStartTime(destinationStart);
		orderedPlan.verify(plan).addActivity(source);
		orderedPlan.verify(plan).addLeg(leg);
		orderedPlan.verify(plan).addActivity(destination);
	}

	private Leg configureLeg() {
		Leg leg = mock(Leg.class);
		String mode = TransportMode.car;
		when(factory.createLeg(mode)).thenReturn(leg);
		return leg;
	}

	private Activity configureActivity(String outflow, int to) {
		Id<Link> toLink = ExternalPersonCreator.createLinkId(outflow, to);
		Activity destination = mock(Activity.class);
		when(factory.createActivityFromLinkId(outflow, toLink)).thenReturn(destination);
		return destination;
	}
}
