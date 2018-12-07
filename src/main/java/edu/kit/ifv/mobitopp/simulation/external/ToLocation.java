package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;

public class ToLocation extends ExternalTrip {

  private final int from;
  private final int toLink;

  public ToLocation(int id, int from, int toLink, Time startTime) {
    super(id, startTime);
    this.from = from;
    this.toLink = toLink;
  }

  @Override
  public Activity doCreateSource(String suffix, ActivityCreator create) {
    return create.activityForZone(suffix, from);
  }

  @Override
  public Activity doCreateDestination(String suffix, ActivityCreator create) {
    return create.activityForLink(suffix, toLink);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + from;
    result = prime * result + toLink;
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
    ToLocation other = (ToLocation) obj;
    if (from != other.from)
      return false;
    if (toLink != other.toLink)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ToLocation [from=" + from + ", toLink=" + toLink + ", toString()=" + super.toString()
        + "]";
  }

}
