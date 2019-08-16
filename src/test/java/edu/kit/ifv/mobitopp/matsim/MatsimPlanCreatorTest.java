package edu.kit.ifv.mobitopp.matsim;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
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
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;
import edu.kit.ifv.mobitopp.time.Time;

public class MatsimPlanCreatorTest {

	private static final int personId = 1;
	private static final ZoneId workZoneId = new ZoneId("1", 0);
	private static final ZoneId homeZoneId = new ZoneId("2", 1);

	private Location workLocation;
	private Location homeLocation;
	private edu.kit.ifv.mobitopp.simulation.Person mobiToppPerson;
	private Plan plan;
	private Person matsimPerson;
	private PopulationFactory factory;
	private Network network;
	private ActivityScheduleWithState schedule;

	@Before
	public void initialise() {
		workLocation = new Location(new Point2D.Double(1.0d, 2.0d), 1, 0.5d);
		homeLocation = new Location(new Point2D.Double(3.0d, 4.0d), 2, 0.5d);
		factory = mock(PopulationFactory.class);
		network = mock(Network.class);
		mobiToppPerson = mock(edu.kit.ifv.mobitopp.simulation.Person.class);
		matsimPerson = mock(Person.class);
		plan = mock(Plan.class);
		schedule = mock(ActivityScheduleWithState.class);

		when(factory.createPlan()).thenReturn(plan);
		when(mobiToppPerson.activitySchedule()).thenReturn(schedule);
		when(mobiToppPerson.getOid()).thenReturn(personId);

	}

	private Activity createSingleActivity() {
		ActivityType type = ActivityType.WORK;
		Time simulationStart = Time.start;
		Time start = simulationStart.plusHours(1);
		ActivityIfc activity = createActivity(type, workZoneId, workLocation, start);
		when(schedule.isEmpty()).thenReturn(false);
		when(schedule.firstActivity()).thenReturn(activity);
		return createMatsimActivity(type, workZoneId);
	}

	private DummyPopulation createPopulationWith(Person matsimPerson2) {
		Id<Person> matsimId = Id.createPersonId(personId);
		Map<Id<Person>, Person> persons = singletonMap(matsimId, matsimPerson2);
		return new DummyPopulation(persons, factory);
	}

	@Test
	public void createsNothingForEmptySchedule() {
		configureEmptyActivitySchedule();

		DummyPopulation population = createPopulationWith(matsimPerson);
		MatsimPlanCreator creator = new MatsimPlanCreator(population, network);

		creator.createPlansForPersons(asList(mobiToppPerson));

		verifyZeroInteractions(matsimPerson);
	}

	private void configureEmptyActivitySchedule() {
		when(schedule.isEmpty()).thenReturn(true);
	}

	@Test
	public void clearsOldPlans() {
		createSingleActivity();
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
		Activity matsimWork = createSingleActivity();
		DummyPopulation population = createPopulationWith(matsimPerson);
		MatsimPlanCreator creator = new MatsimPlanCreator(population, network);

		creator.createPlansForPersons(asList(mobiToppPerson));

		verify(matsimPerson).addPlan(plan);
		verify(plan).addActivity(matsimWork);
	}

	@Test
	public void createsPlansForSeveralActivities() {
		List<Activity> matsimActivities = configureSeveralActivities();
		DummyPopulation population = createPopulationWith(matsimPerson);
		MatsimPlanCreator creator = new MatsimPlanCreator(population, network);

		creator.createPlansForPersons(asList(mobiToppPerson));

		verify(matsimPerson).addPlan(plan);
		matsimActivities.forEach(a -> verify(plan).addActivity(a));
	}

	private List<Activity> configureSeveralActivities() {
		ActivityType workType = ActivityType.WORK;
		Time simulationStart = Time.start;
		Time workStart = simulationStart.plusHours(1);
		ActivityIfc workActivity = createActivity(workType, workZoneId, workLocation, workStart);
		when(schedule.isEmpty()).thenReturn(false);
		when(schedule.firstActivity()).thenReturn(workActivity);
		Activity matsimWork = createMatsimActivity(workType, workZoneId);

		ActivityType homeType = ActivityType.HOME;
		ZoneId zoneId = homeZoneId;
		Time homeStart = workActivity.calculatePlannedEndDate().plusHours(1);
		ActivityIfc homeActivity = createActivity(homeType, zoneId, homeLocation, homeStart);
		when(schedule.hasNextActivity(workActivity)).thenReturn(true);
		when(schedule.nextActivity(workActivity)).thenReturn(homeActivity);
		Activity matsimHome = createMatsimActivity(homeType, zoneId);

		return asList(matsimWork, matsimHome);
	}

	private Activity createMatsimActivity(ActivityType type, ZoneId zoneId) {
		Id<Link> linkId = Id.createLinkId("Z" + zoneId.getExternalId() + ":12");
		Activity matsimHome = mock(Activity.class);
		when(factory.createActivityFromLinkId(type.getTypeAsString(), linkId)).thenReturn(matsimHome);
		return matsimHome;
	}

	private ActivityIfc createActivity(
			ActivityType type, ZoneId zoneId, Location location, Time start) {
		Time end = start.plusHours(1);
		Zone zone = mock(Zone.class);
		ActivityIfc activity = mock(ActivityIfc.class);
		when(zone.getId()).thenReturn(zoneId);
		when(activity.zone()).thenReturn(zone);
		when(activity.location()).thenReturn(location);
		when(activity.startDate()).thenReturn(start);
		when(activity.activityType()).thenReturn(type);
		when(activity.calculatePlannedEndDate()).thenReturn(end);
		when(activity.isLocationSet()).thenReturn(true);
		when(activity.mode()).thenReturn(Mode.CAR);
		return activity;
	}
}
