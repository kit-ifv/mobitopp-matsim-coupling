package edu.kit.ifv.mobitopp.matsim;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.matsim.converter.ActivityTypeConverter;
import edu.kit.ifv.mobitopp.matsim.converter.ModeConverter;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;

public class MatsimPlanCreator {

	private final Population population;
	private final Network network;
	private final PopulationFactory populationFactory;
	private final ModeConverter modeConverter;
	private final ActivityTypeConverter activityTypeConverter;
	private final ActivityFilter activityFilter;

	public MatsimPlanCreator(Population population, Network network, ActivityFilter filter) {
		super();
		this.population = population;
		this.network = network;
		populationFactory = population.getFactory();
		modeConverter = new ModeConverter();
		activityTypeConverter = new ActivityTypeConverter();
		activityFilter = filter;
	}

	public MatsimPlanCreator(Population population, Network network) {
		this(population, network, new CarOnly());
	}

	public void createPlansForPersons(List<edu.kit.ifv.mobitopp.simulation.Person> mobitoppPersons) {
		for (edu.kit.ifv.mobitopp.simulation.Person mp : mobitoppPersons) {
			if (!mp.activitySchedule().isEmpty()) {
				Person person = findPersonFor(mp);
				clearCurrentPlans(person);
				Plan plan = createPlan(mp.activitySchedule());
				person.addPlan(plan);
			}
		}
	}

	private Person findPersonFor(edu.kit.ifv.mobitopp.simulation.Person mp) {
		int personId = mp.getOid();
		Id<Person> matsimId = Id.createPersonId(personId);
		if (population.getPersons().containsKey(matsimId)) {
			return this.population.getPersons().get(matsimId);
		}
		throw new IllegalArgumentException(
				"No person in MATSim available for person in mobiTopp: " + personId);
	}

	private void clearCurrentPlans(Person person) {
		for (Plan plan : new ArrayList<Plan>(person.getPlans())) {
			person.removePlan(plan);
		}
	}

	private Plan createPlan(ActivitySchedule schedule) {
		assert schedule != null;
		Plan toPlan = this.populationFactory.createPlan();
		ActivityIfc current = schedule.firstActivity();
		if (current == null) {
			return toPlan;
		}

		toPlan.addActivity(createActivity(current));
		current = addActivitiesFrom(schedule, toPlan, current);
		return toPlan;
	}

	private ActivityIfc addActivitiesFrom(ActivitySchedule schedule, Plan plan, ActivityIfc current) {
		while (schedule.hasNextActivity(current) && schedule.nextActivity(current).isLocationSet()) {
			current = schedule.nextActivity(current);
			if (isModeAllowed(current)) {
				plan.addLeg(createLeg(current));
				plan.addActivity(createActivity(current));
			}
		}
		return current;
	}

	/**
	 * Only allow car as mode
	 * 
	 * @param current
	 * @return
	 */
	private boolean isModeAllowed(ActivityIfc current) {
		return activityFilter.isAllowed(current);
	}

	private Activity createActivity(ActivityIfc mobitopp) {
		if (isExternal(mobitopp.zone())) {
			return createActivityForZone(mobitopp);
		}
		return createActivityAtLink(mobitopp);
	}

	private boolean isExternal(Zone zone) {
		String id = zone.getId().getExternalId();
		int idAsInt = Integer.parseInt(id);
		return 700000 < idAsInt;
	}

	private Activity createActivityAtLink(ActivityIfc mobitopp) {
		Id<Link> linkId = linkId(mobitopp);
		return createActivity(mobitopp, linkId);
	}

	private Id<Link> linkId(ActivityIfc mobitopp) {
		int edgeId = mobitopp.location().roadAccessEdgeId;
		int linkId = Math.abs(edgeId);
		Id<Link> forwardLink = Id.createLinkId(linkId + ":1");
		if (network.getLinks().containsKey(forwardLink)) {
			return forwardLink;
		}
		Id<Link> backwardLink = Id.createLinkId(linkId + ":2");
		if (network.getLinks().containsKey(backwardLink)) {
			return backwardLink;
		}
		return zoneLink(mobitopp);
	}

	private Activity createActivityForZone(ActivityIfc mobitopp) {
		Id<Link> linkId = zoneLink(mobitopp);
		return createActivity(mobitopp, linkId);
	}

	private Activity createActivity(ActivityIfc mobitopp, Id<Link> linkId) {
		String activity = activityTypeAsString(mobitopp.activityType());
		Activity matsim = populationFactory.createActivityFromLinkId(activity, linkId);
		matsim.setStartTime(mobitopp.startDate().toSeconds());
		matsim.setEndTime(mobitopp.calculatePlannedEndDate().toSeconds());
		return matsim;
	}

	private Id<Link> zoneLink(ActivityIfc mobitopp) {
		String zoneId = mobitopp.zone().getId().getExternalId();
		return Id.createLinkId("Z" + zoneId + ":12");
	}

	private Leg createLeg(edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc mobitopp) {
		Mode mode = mobitopp.isModeSet() ? mobitopp.mode() : StandardMode.PEDESTRIAN;
		return populationFactory.createLeg(asMatsimMode(mode));
	}

	private String asMatsimMode(Mode mode) {
		return modeConverter.toMatsim(mode);
	}

	private String activityTypeAsString(ActivityType activityType) {
		return activityTypeConverter.toMatsim(activityType);
	}

}
