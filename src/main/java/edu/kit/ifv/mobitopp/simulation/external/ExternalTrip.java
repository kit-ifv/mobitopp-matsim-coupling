package edu.kit.ifv.mobitopp.simulation.external;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.PopulationFactory;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public abstract class ExternalTrip implements Comparable<ExternalTrip> {

  protected static final RelativeTime externalTripDuration = RelativeTime.ofHours(2);
  private final int id;
  private final Time startTime;

  public ExternalTrip(int id, Time startTime) {
    super();
    this.id = id;
    this.startTime = startTime;
  }

  public Activity createSource(String suffix, PopulationFactory populationFactory) {
    ActivityCreator activityCreator = new DefaultActivityCreator(populationFactory);
    Activity source = doCreateSource(suffix, activityCreator);
    source.setStartTime(Time.start.toSeconds());
    long endTime = startTime().minus(externalTripDuration).toSeconds();
    source.setEndTime(endTime);
    return source;
  }

  protected abstract Activity doCreateSource(String suffix, ActivityCreator activityCreator);

  public Activity createDestination(String suffix, PopulationFactory populationFactory) {
    ActivityCreator activityCreator = new DefaultActivityCreator(populationFactory);
    Activity activity = doCreateDestination(suffix, activityCreator);
    activity.setStartTime(startTime().toSeconds());
    return activity;
  }

  protected abstract Activity doCreateDestination(String suffix, ActivityCreator activityCreator);

  public int id() {
    return id;
  }

  public Time startTime() {
    return startTime;
  }

  public int compareTo(ExternalTrip o) {
    if (this.startTime.compareTo(o.startTime) != 0) {
      return this.startTime.compareTo(o.startTime);
    }

    if (this.id < o.id) {
      return -1;
    } else if (this.id > o.id) {
      return 1;
    }

    return 0;
  }

}