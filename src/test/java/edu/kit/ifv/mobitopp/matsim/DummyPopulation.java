package edu.kit.ifv.mobitopp.matsim;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.utils.objectattributes.ObjectAttributes;

final class DummyPopulation implements Population {

	private final Map<Id<Person>, Person> persons;
	private final PopulationFactory factory;

	DummyPopulation(Map<Id<Person>, Person> persons, PopulationFactory factory) {
		this.persons = persons;
		this.factory = factory;
	}

	@Override
	public void setName(String name) {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public Map<Id<Person>, ? extends Person> getPersons() {
		return persons;
	}

	@Override
	public ObjectAttributes getPersonAttributes() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getName() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public PopulationFactory getFactory() {
		return factory;
	}

	@Override
	public void addPerson(Person p) {
		throw new RuntimeException("Not implemented!");
	}
		
}