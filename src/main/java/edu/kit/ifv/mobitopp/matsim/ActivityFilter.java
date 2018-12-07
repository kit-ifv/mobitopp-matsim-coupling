package edu.kit.ifv.mobitopp.matsim;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface ActivityFilter {

	boolean isAllowed(ActivityIfc current);

}
