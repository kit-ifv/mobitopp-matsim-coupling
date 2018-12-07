package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;

public class FromLocation extends ExternalTrip {

  private final Id<Link> from;
  private final int to;

  public FromLocation(int id, Id<Link> from, int to, Time startTime) {
    super(id, startTime);
    this.from = from;
    this.to = to;
  }

  @Override
  public Activity doCreateSource(String suffix, ActivityCreator create) {
    return create.activityForLink(suffix, from);
  }

  @Override
  public Activity doCreateDestination(String suffix, ActivityCreator create) {
    return create.activityForZone(suffix, to);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + to;
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
    FromLocation other = (FromLocation) obj;
    if (from == null) {
      if (other.from != null)
        return false;
    } else if (!from.equals(other.from))
      return false;
    if (to != other.to)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "FromLocation [from=" + from + ", to=" + to + ", toString()=" + super.toString() + "]";
  }

}
