package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;

public interface ActivityCreator {

  Activity activityForLink(String suffix, Id<Link> linkId);

  Activity activityForZone(String suffix, int zoneId);

}