package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.controler.Controler;

import edu.kit.ifv.mobitopp.matsim.MatsimPersonCreator;
import edu.kit.ifv.mobitopp.time.Time;

public class Matsim {

	private final SimulationContext context;
	private final Scenario scenario;
	private final Network network;
	private final Population population;

	public Matsim(SimulationContext context, Scenario scenario) {
		this.context = context;
		this.scenario = scenario;
		this.network = scenario.getNetwork();
		this.population = scenario.getPopulation();
	}

	public void createPersons() {
		System.out.println("createMatsimPersons");
		List<Person> persons = persons();
		System.out.println(persons.size() + " persons simulated");
		MatsimPersonCreator creator = new MatsimPersonCreator(population, simulationStart(), network);
		List<org.matsim.api.core.v01.population.Person> matsim = creator.createPersons(persons);
		System.out.println(matsim.size() + " matsim persons");
	}

	private Time simulationStart() {
		return context.simulationDays().startDate();
	}

	private List<Person> persons() {
		List<Person> persons = new ArrayList<Person>();
		Collection<Integer> ids = context.personLoader().getPersonOids();
		for (Integer id : ids) {
			Person p = context.personLoader().getPersonByOid(id);
			persons.add(p);
		}
		return persons;
	}

	public void createPlans() {
		List<Person> persons = persons();
		MatsimPersonCreator creator = new MatsimPersonCreator(population, simulationStart(), network);
		creator.createPlansForPersons(persons);
	}

	public void simulate() {
		new Controler(scenario).run();
	}

}
