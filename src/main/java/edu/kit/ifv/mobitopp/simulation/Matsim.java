package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.controler.Controler;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.local.InMemoryMatrices;
import edu.kit.ifv.mobitopp.matsim.ActivityFilter;
import edu.kit.ifv.mobitopp.matsim.ExternalDemandCreator;
import edu.kit.ifv.mobitopp.matsim.MatsimContext;
import edu.kit.ifv.mobitopp.matsim.MatsimPersonCreator;
import edu.kit.ifv.mobitopp.matsim.MatsimPlanCreator;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class Matsim {

  private final MatsimContext context;
  private final Scenario scenario;
  private final Network network;
  private final Population population;
  private final ActivityFilter filter;

  public Matsim(MatsimContext context, Scenario scenario, ActivityFilter filter) {
    this.context = context;
    this.scenario = scenario;
    this.filter = filter;
    this.network = scenario.getNetwork();
    this.population = scenario.getPopulation();
  }

  public void createPersons() {
    System.out.println("Create demand for matsim");
    createInternalDemand();
    createExternalDemand();
  }

  private void createInternalDemand() {
    System.out.println("Create internal demand for matsim");
    List<Person> persons = persons();
    System.out.println(persons.size() + " persons simulated in mobiTopp");
    MatsimPersonCreator creator = new MatsimPersonCreator(population);
    List<org.matsim.api.core.v01.population.Person> matsim = creator.createPersons(persons);
    System.out.println(matsim.size() + " matsim persons created.");
  }

  private void createExternalDemand() {
    ExternalDemandCreator demandCreator = new ExternalDemandCreator(context);
    demandCreator.addDemandTo(population, network);
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
    System.out.println("Create plans for matsim persons");
    List<Person> persons = persons();
    MatsimPlanCreator creator = planCreator(population, network);
    creator.createPlansForPersons(persons);
  }

  private MatsimPlanCreator planCreator(Population population, Network network) {
    return new MatsimPlanCreator(population, network, filter);
  }

  public Controler simulate() {
    Controler controler = new Controler(scenario);
    controler.run();
    return controler;
  }

  public void updateTravelTime(TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices) {
    InMemoryMatrices carMatrices = createMatrices(travelTimeMatrices);
    ImpedanceIfc newImpedance = new ImpedanceMatSimIteration(context.impedance(), carMatrices);
    context.updateImpedance(newImpedance);
  }

  private InMemoryMatrices createMatrices(TreeMap<Integer, TravelTimeMatrix> travelTimeMatrices) {
    Map<DayOfWeek, TreeMap<Integer, TravelTimeMatrix>> dayMatrices = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      dayMatrices.put(day, travelTimeMatrices);
    }
    return new InMemoryMatrices(dayMatrices);
  }

  public TreeMap<Integer, TravelTimeMatrix> createTravelTimeMatrices(
      Controler controler) {
    MatsimMatrixGenerator generate = new MatsimMatrixGenerator(network, context.network(),
        idToOidMapping());
    return generate.travelTimeMatrices(controler);
  }

  private Map<Integer, Integer> idToOidMapping() {
    Map<Integer, Zone> zones = context.zoneRepository().zones();
    Map<Integer, Integer> id2OidMapping = new LinkedHashMap<Integer, Integer>();
    for (Zone zone : zones.values()) {
      Integer id = Integer.valueOf(zone.getId().substring(1));
      id2OidMapping.put(id, zone.getOid());
    }
    return id2OidMapping;
  }

}
