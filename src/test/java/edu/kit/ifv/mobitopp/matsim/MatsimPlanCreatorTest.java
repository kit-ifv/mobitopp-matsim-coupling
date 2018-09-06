package edu.kit.ifv.mobitopp.matsim;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationFactory;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.time.Time;

public class MatsimPlanCreatorTest {

	private static final int personId = 1;
	private static final String zoneId = "Z1";
	private static final Location zoneCenter = Example.location;

	private edu.kit.ifv.mobitopp.simulation.Person mobiToppPerson;
	private Plan plan;
	private Person matsimPerson;
	private Activity matsimActivity;
	private PopulationFactory factory;
	private Network network;

	@Before
	public void initialise() {
		Time simulationStart = Time.start;
		Time activityStart = simulationStart.plusHours(1);
		Time activityEnd = activityStart.plusHours(1);
		factory = mock(PopulationFactory.class);
		network = mock(Network.class);
		mobiToppPerson = mock(edu.kit.ifv.mobitopp.simulation.Person.class);
		ActivityType activityType = ActivityType.WORK;
		ActivityIfc activity = mock(ActivityIfc.class);
		Zone zone = mock(Zone.class);
		matsimPerson = mock(Person.class);
		plan = mock(Plan.class);
		ActivityScheduleWithState schedule = mock(ActivityScheduleWithState.class);
		Id<Link> linkId = Id.createLinkId(zoneId + ":12");
		matsimActivity = mock(Activity.class);

		when(factory.createPlan()).thenReturn(plan);
		when(factory.createActivityFromLinkId("WORK", linkId)).thenReturn(matsimActivity);
		when(mobiToppPerson.activitySchedule()).thenReturn(schedule);
		when(mobiToppPerson.getOid()).thenReturn(personId);
		when(schedule.firstActivity()).thenReturn(activity);
		when(zone.getId()).thenReturn(zoneId);
		when(activity.zone()).thenReturn(zone);
		when(activity.location()).thenReturn(zoneCenter);
		when(activity.startDate()).thenReturn(activityStart);
		when(activity.activityType()).thenReturn(activityType);
		when(activity.calculatePlannedEndDate()).thenReturn(activityEnd);

	}

	private DummyPopulation createPopulationWith(Person matsimPerson2) {
		Id<Person> matsimId = Id.createPersonId(personId);
		Map<Id<Person>, Person> persons = singletonMap(matsimId, matsimPerson2);
		return new DummyPopulation(persons, factory);
	}

	@Test
	public void clearsOldPlans() {
		Plan oldPlan = mock(Plan.class);
		DummyPerson dummyPerson = new DummyPerson(oldPlan);
		DummyPopulation population = createPopulationWith(dummyPerson);
		MatsimPlanCreator creator = new MatsimPlanCreator(population, network);

		creator.createPlansForPersons(asList(mobiToppPerson));

		assertThat(dummyPerson.addedPlans(), contains(plan));
		assertThat(dummyPerson.removedPlans(), contains(oldPlan));
	}

	@Test
	public void createsPlanFromActivity() {
		DummyPopulation population = createPopulationWith(matsimPerson);
		MatsimPlanCreator creator = new MatsimPlanCreator(population, network);

		List<Person> matsimPersons = creator.createPlansForPersons(asList(mobiToppPerson));

		Person matsimPerson = matsimPersons.iterator().next();

		verify(matsimPerson).addPlan(plan);
		verify(plan).addActivity(matsimActivity);
	}
}
