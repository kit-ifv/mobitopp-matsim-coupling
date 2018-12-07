package edu.kit.ifv.mobitopp.simulation.external;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.matsim.api.core.v01.population.Activity;

import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ExternalTripTestData {

  final String suffix = "suffix";
  final int tripId = 0;
  final int from = 1;
  final int to = 2;
  final int hour = 3;
  final int minute = 4;
  final Activity zoneActivity;
  final Activity linkActivity;
  final ActivityCreator activityCreator;
  final Time startTime;

  private ExternalTripTestData(
      Activity zoneActivity, Activity linkActivity, ActivityCreator activityCreator) {
    super();
    this.zoneActivity = zoneActivity;
    this.linkActivity = linkActivity;
    this.activityCreator = activityCreator;
    startTime = SimpleTime.ofHours(hour).plusMinutes(minute);
  }

  public void initialise() {
  }

  static ExternalTripTestData create() {
    Activity zoneActivity = mock(Activity.class);
    Activity linkActivity = mock(Activity.class);
    ActivityCreator activityCreator = mock(ActivityCreator.class);
    when(activityCreator.activityForZone(anyString(), anyInt())).thenReturn(zoneActivity);
    when(activityCreator.activityForLink(anyString(), anyInt())).thenReturn(linkActivity);
    return new ExternalTripTestData(zoneActivity, linkActivity, activityCreator);
  }
}
