package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.Time;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

}
