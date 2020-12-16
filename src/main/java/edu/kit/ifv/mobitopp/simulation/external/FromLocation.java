package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

}
