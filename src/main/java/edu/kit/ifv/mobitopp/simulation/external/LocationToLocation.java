package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;

public class LocationToLocation extends ExternalTrip {

  private final int originLink;
  private final int destinationLink;

  public LocationToLocation(int id, int originLink, int destinationLink, Time startTime) {
    super(id, startTime);
    this.originLink = originLink;
    this.destinationLink = destinationLink;
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
    result = prime * result + destinationLink;
    result = prime * result + originLink;
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
    if (destinationLink != other.destinationLink)
      return false;
    if (originLink != other.originLink)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LocationToLocation [originLink=" + originLink + ", destinationLink=" + destinationLink
        + ", toString()=" + super.toString() + "]";
  }

}
