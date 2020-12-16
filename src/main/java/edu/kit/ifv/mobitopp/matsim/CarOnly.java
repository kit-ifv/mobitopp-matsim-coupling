package edu.kit.ifv.mobitopp.matsim;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class CarOnly implements ActivityFilter {

	@Override
	public boolean isAllowed(ActivityIfc current) {
		return current.isLocationSet() && current.mode() == StandardMode.CAR;
	}

}
