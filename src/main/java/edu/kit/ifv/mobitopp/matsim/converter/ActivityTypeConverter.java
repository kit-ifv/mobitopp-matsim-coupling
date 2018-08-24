package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class ActivityTypeConverter {

	private final Map<ActivityType, String> toMatsim;

	public ActivityTypeConverter() {
		super();
		toMatsim = new HashMap<>();
		toMatsim.put(ActivityType.WORK, "WORK");
		toMatsim.put(ActivityType.BUSINESS, "BUSINESS");
		toMatsim.put(ActivityType.EDUCATION, "EDUCATION");
		toMatsim.put(ActivityType.SHOPPING, "SHOPPING");
		toMatsim.put(ActivityType.LEISURE, "LEISURE");
		toMatsim.put(ActivityType.SERVICE, "SERVICE");
		toMatsim.put(ActivityType.HOME, "HOME");
		toMatsim.put(ActivityType.PRIVATE_BUSINESS, "PRIVATE_BUSINESS");
		toMatsim.put(ActivityType.PRIVATE_VISIT, "LEISURE");
		toMatsim.put(ActivityType.SHOPPING_DAILY, "SHOPPING");
		toMatsim.put(ActivityType.SHOPPING_OTHER, "SHOPPING");
		toMatsim.put(ActivityType.LEISURE_INDOOR, "LEISURE");
		toMatsim.put(ActivityType.LEISURE_OUTDOOR, "LEISURE");
		toMatsim.put(ActivityType.LEISURE_OTHER, "LEISURE");
		toMatsim.put(ActivityType.LEISURE_WALK, "LEISURE");
		toMatsim.put(ActivityType.EDUCATION_PRIMARY, "EDUCATION");
		toMatsim.put(ActivityType.EDUCATION_SECONDARY, "EDUCATION");
		toMatsim.put(ActivityType.EDUCATION_TERTIARY, "EDUCATION");
		toMatsim.put(ActivityType.EDUCATION_OCCUP, "EDUCATION");
	}

	public String toMatsim(ActivityType activityType) {
		return toMatsim.getOrDefault(activityType, "UNKNOWN");
	}

}
