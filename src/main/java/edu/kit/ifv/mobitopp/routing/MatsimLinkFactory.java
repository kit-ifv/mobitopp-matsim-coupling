package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

public final class MatsimLinkFactory implements VisumLinkFactory {

  private final float currentTime;
  private final TravelTimeFromMatsim travelTime;

  public MatsimLinkFactory(float currentTime, TravelTimeFromMatsim travelTime) {
    this.currentTime = currentTime;
    this.travelTime = travelTime;
  }

  @Override
  public Link create(VisumOrientedLink link, NodeFromVisumNode from, NodeFromVisumNode to) {
  	float time = travelTime.getTravelTime(link.id, currentTime);
  	return new LinkFromMatsim(link, from, to, time);
  }
}