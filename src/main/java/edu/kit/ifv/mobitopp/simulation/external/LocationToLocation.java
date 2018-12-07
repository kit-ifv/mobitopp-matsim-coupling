package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;

public class LocationToLocation extends ExternalTrip {

  private final Id<Link> originLink;
  private final Id<Link> destinationLink;

  public LocationToLocation(int id, Id<Link> from, Id<Link> to, Time startTime) {
    super(id, startTime);
    this.originLink = from;
    this.destinationLink = to;
  }

  @Override
  public Activity doCreateSource(String suffix, ActivityCreator create) {
    return create.activityForLink(suffix, originLink);
  }

  @Override
  public Activity doCreateDestination(String suffix, ActivityCreator create) {
    return create.activityForLink(suffix, destinationLink);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((destinationLink == null) ? 0 : destinationLink.hashCode());
    result = prime * result + ((originLink == null) ? 0 : originLink.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LocationToLocation other = (LocationToLocation) obj;
    if (destinationLink == null) {
      if (other.destinationLink != null)
        return false;
    } else if (!destinationLink.equals(other.destinationLink))
      return false;
    if (originLink == null) {
      if (other.originLink != null)
        return false;
    } else if (!originLink.equals(other.originLink))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LocationToLocation [originLink=" + originLink + ", destinationLink=" + destinationLink
        + ", toString()=" + super.toString() + "]";
  }

}
