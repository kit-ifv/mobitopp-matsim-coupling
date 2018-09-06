package edu.kit.ifv.mobitopp.matsim;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityScheduleWithState;

public class MatsimPersonCreatorTest {

	private static final int personId = 1;
	private MatsimPersonCreator creator;
	private edu.kit.ifv.mobitopp.simulation.Person mobiToppPerson;
	private Person matsimPerson;
	private Population population;
	private PopulationFactory factory;

	@Before
	public void initialise() {
		factory = mock(PopulationFactory.class);
		mobiToppPerson = mock(edu.kit.ifv.mobitopp.simulation.Person.class);
		matsimPerson = mock(Person.class);
		Id<Person> matsimId = Id.createPersonId(personId);
		population = mock(Population.class);

		when(population.getFactory()).thenReturn(factory);
		when(factory.createPerson(matsimId)).thenReturn(matsimPerson);
		when(mobiToppPerson.getOid()).thenReturn(personId);

		creator = new MatsimPersonCreator(population);
	}

	@Test
	public void createsAgents() {
		configureScheduleWithActivities();

		List<Person> matsimPersons = creator.createPersons(asList(mobiToppPerson));

		Person matsimPerson = matsimPersons.iterator().next();

		assertThat(matsimPerson, is(equalTo(this.matsimPerson)));

		verify(population).addPerson(matsimPerson);
	}

	private void configureScheduleWithActivities() {
		ActivityScheduleWithState schedule = mock(ActivityScheduleWithState.class);
		when(mobiToppPerson.activitySchedule()).thenReturn(schedule);
		when(schedule.isEmpty()).thenReturn(false);
	}
	
	@Test
	public void createsNoAgentsWithEmptySchedule() {
		configureEmptySchedule();
		
		List<Person> matsimPersons = creator.createPersons(asList(mobiToppPerson));
		
		assertThat(matsimPersons, is(empty()));
		
		verify(population).getFactory();
		verifyNoMoreInteractions(population);
		verifyZeroInteractions(factory);
	}

	private void configureEmptySchedule() {
		ActivityScheduleWithState schedule = mock(ActivityScheduleWithState.class);
		when(mobiToppPerson.activitySchedule()).thenReturn(schedule);
		when(schedule.isEmpty()).thenReturn(true);
	}
}
