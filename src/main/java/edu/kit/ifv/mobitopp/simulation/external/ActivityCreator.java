package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;

public interface ActivityCreator {

  Activity activityForLink(String suffix, int onLink);

  Activity activityForZone(String suffix, int zoneId);

}