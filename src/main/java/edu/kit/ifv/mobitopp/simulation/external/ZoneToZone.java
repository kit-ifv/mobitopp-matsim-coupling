package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;

public class ZoneToZone extends ExternalTrip {

  private final int from;
  private final int to;

  public ZoneToZone(int id, int from, int to, Time startTime) {
    super(id, startTime);
    this.from = from;
    this.to = to;
  }

  @Override
  public Activity doCreateSource(String suffix, ActivityCreator create) {
    return create.activityForZone(suffix, from);
  }

  @Override
  public Activity doCreateDestination(String suffix, ActivityCreator create) {
    return create.activityForZone(suffix, to);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + from;
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
    ZoneToZone other = (ZoneToZone) obj;
    if (from != other.from)
      return false;
    if (to != other.to)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ZoneToZone [from=" + from + ", to=" + to + ", toString()=" + super.toString() + "]";
  }

}
