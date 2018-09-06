package edu.kit.ifv.mobitopp.matsim;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;

public class MatsimPersonCreator {

	private final Population population;
	private final PopulationFactory populationFactory;

	public MatsimPersonCreator(Population population) {
		super();
		this.population = population;
		this.populationFactory = population.getFactory();
	}

	public List<Person> createPersons(List<edu.kit.ifv.mobitopp.simulation.Person> mobitoppPersons) {
		List<Person> persons = new ArrayList<Person>();
		for (edu.kit.ifv.mobitopp.simulation.Person mp : mobitoppPersons) {
			if (!mp.activitySchedule().isEmpty()) {
				Person person = createPerson(mp);
				persons.add(person);
			}
		}
		return persons;
	}

	private Person createPerson(edu.kit.ifv.mobitopp.simulation.Person mobitoppPerson) {
		Id<Person> id = idOf(mobitoppPerson);
		Person person = this.populationFactory.createPerson(id);
		assert this.population != null;
		assert person != null;
		this.population.addPerson(person);
		return person;
	}

	private Id<Person> idOf(edu.kit.ifv.mobitopp.simulation.Person mobitoppPerson) {
		int personId = mobitoppPerson.getOid();
		return Id.createPersonId(personId);
	}

}
