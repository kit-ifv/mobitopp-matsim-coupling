package edu.kit.ifv.mobitopp.routing;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

public class TravelTimeFromMatsim implements TravelTime {

  private org.matsim.core.router.util.TravelTime travelTime;
  private Network network;

  public TravelTimeFromMatsim(Network network, org.matsim.core.router.util.TravelTime travelTime) {
    this.network = network;
    this.travelTime = travelTime;
  }

  public float travelTime(Edge edge, float currentTime) {
    String edgeId = edge.id();
    return getTravelTime(edgeId, currentTime);
  }

  private void verifyLinkExists(Link link, String edgeId) {
    if (link == null) {
      throw new IllegalArgumentException("Can not find link for edge: " + edgeId);
    }
  }

  private float travelTimeInSeconds(float currentTime, Link link) {
    return (float) this.travelTime.getLinkTravelTime(link, currentTime, null, null);
  }

  public float getTravelTime(String edgeId, float currentTime) {
    Id<Link> id = Id.createLinkId(edgeId);
    Link link = this.network.getLinks().get(id);
    verifyLinkExists(link, edgeId);
    return travelTimeInSeconds(currentTime, link);
  }

}
