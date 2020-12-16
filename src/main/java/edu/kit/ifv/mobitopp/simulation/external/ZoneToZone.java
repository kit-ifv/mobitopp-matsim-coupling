package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

}
