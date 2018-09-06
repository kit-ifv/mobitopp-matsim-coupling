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
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;

public class MatsimPlanCreator {

	private final Population population;
	private final Network network;
	private final PopulationFactory populationFactory;
	private final ModeConverter modeConverter;
	private final ActivityTypeConverter activityTypeConverter;

	public MatsimPlanCreator(Population population, Network network) {
		super();
		this.population = population;
		this.network = network;
		populationFactory = population.getFactory();
		modeConverter = new ModeConverter();
		activityTypeConverter =  new ActivityTypeConverter();
	}

	public List<Person> createPlansForPersons(
			List<edu.kit.ifv.mobitopp.simulation.Person> mobitoppPersons) {

		List<Person> persons = new ArrayList<Person>();

		for (edu.kit.ifv.mobitopp.simulation.Person mp : mobitoppPersons) {

			if (!mp.activitySchedule().isEmpty()) {

				Integer personId = mp.getOid();

				Id<Person> matsimId = Id.createPersonId(personId);

				Person person = this.population.getPersons().get(matsimId);

				assert person != null : (this.population.getPersons());

				// clear current plans
				for (Plan plan : new ArrayList<Plan>(person.getPlans())) {

					person.removePlan(plan);
				}

				Plan plan = createPlan(mp.activitySchedule());
				person.addPlan(plan);

				persons.add(person);
			}
		}

		return persons;
	}

	private Plan createPlan(ActivitySchedule schedule) {

		assert schedule != null;

		Plan plan = this.populationFactory.createPlan();

		ActivityIfc current = schedule.firstActivity(); 

		if (current == null) return plan;

		assert current != null;


		plan.addActivity(createActivity(current));

		while (schedule.hasNextActivity(current) && schedule.nextActivity(current).isLocationSet())
		{
			current = schedule.nextActivity(current);
			
			if(isModeAllowed(current)) {

				plan.addLeg(createLeg(current));
				plan.addActivity(createActivity(current));
			}
		}

		return plan;
	}
	
	/**
	 * Only allow car as mode
	 * @param current
	 * @return
	 */
	private boolean isModeAllowed(ActivityIfc current) {
		return current.isLocationSet() && current.mode() == Mode.CAR;
	}

	private Activity createActivity(ActivityIfc mobitopp) {
		if (isExternal(mobitopp.zone())) {
			return createActivityForZone(mobitopp);
		}
		return createActivityAtLink(mobitopp);
	}

	private boolean isExternal(Zone zone) {
		String id = zone.getId().replaceFirst(Zone.IDPREFIX, "");
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
		String zoneId = mobitopp.zone().getId();
		return Id.createLinkId("" + zoneId + ":12");
	}
	
	private Leg createLeg(edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc mobitopp) {
		Mode mode = mobitopp.isModeSet() ? mobitopp.mode() : Mode.PEDESTRIAN;
		return populationFactory.createLeg(asMatsimMode(mode));
	}

	private String asMatsimMode(Mode mode) {
		return modeConverter.toMatsim(mode);
	}

	private String activityTypeAsString(ActivityType activityType) {
		return activityTypeConverter.toMatsim(activityType);
	}

}
