package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.PopulationFactory;

public class DefaultActivityCreator implements ActivityCreator {

  private final PopulationFactory populationFactory;

  public DefaultActivityCreator(PopulationFactory populationFactory) {
    super();
    this.populationFactory = populationFactory;
  }

  @Override
  public Activity activityForLink(String suffix, int onLink) {
    Id<Link> linkId = createLinkId(onLink);
    return populationFactory.createActivityFromLinkId(suffix, linkId);
  }

  private Id<Link> createLinkId(int onLink) {
    return Id.createLinkId(onLink);
  }

  @Override
  public Activity activityForZone(String suffix, int zoneId) {
    Id<Link> linkId = createZoneLinkId(suffix, zoneId);
    return populationFactory.createActivityFromLinkId(suffix, linkId);
  }
  
  public static Id<Link> createZoneLinkId(String activityType, int link) {
    return Id.createLinkId("Z" + link + "-" + activityType);
  }

}
