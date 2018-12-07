package edu.kit.ifv.mobitopp.simulation.external;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumMatrix;

public class ExternalTrips {

  private final List<VisumMatrix> totalTrips;
  private final HourlyTimeProfile timeProfile;
  private final ZoneRepository zoneRepository;
  private final Random rnd_trips = new Random(1234);
  private final Random rnd_time = new Random(1234);
  private final Random rnd_location = new Random(1234);
  private int id_seq = 0;

  public ExternalTrips(
      List<VisumMatrix> totalTrips, HourlyTimeProfile timeProfile, ZoneRepository zoneRepository) {

    assert totalTrips != null;
    assert totalTrips.size() > 0;

    this.totalTrips = totalTrips;
    this.timeProfile = timeProfile;
    this.zoneRepository = zoneRepository;

    totalTrips.size();
  }

  public Collection<ExternalTrip> trips(float fraction) {

    assert fraction > 0.0f;
    assert fraction <= 1.0f;

    Collection<ExternalTrip> trips = new TreeSet<>();

    for (VisumMatrix tripMatrix : totalTrips) {

      for (Zone origin : zoneRepository.getZones()) {
        for (Zone destination : zoneRepository.getZones()) {

          int numberOfTrips = calculateNumberOfTrips(origin, destination, tripMatrix, fraction);

          for (int k = 0; k < numberOfTrips; k++) {

            int hour = estimateStartHour(timeProfile);

            ExternalTrip t = createTrip(origin, destination, hour);

            trips.add(t);
          }
        }
      }

    }

    return trips;
  }

  protected int calculateNumberOfTrips(
      Zone origin, Zone destination, VisumMatrix tripProbabilityMatrix, float fraction) {

    float probability = tripProbabilityMatrix.get(origin, destination) * fraction;

    if (probability < 0) {
      return 0;
    }

    int cnt = (int) Math.floor(probability);

    probability -= cnt;

    float random = rnd_trips.nextFloat();

    if (random < probability) {
      cnt++;
    }

    return cnt;
  }

  protected int estimateStartHour(HourlyTimeProfile timeProfile) {

    float random = rnd_time.nextFloat();

    return timeProfile.randomHour(random);
  }

  protected ExternalTrip createTrip(Zone origin, Zone destination, int hour) {
    int minute = (int) Math.floor(60.0f * rnd_time.nextFloat());
    Time startTime = SimpleTime.ofHours(hour).plusMinutes(minute);
    int originId = zoneId(origin);
    int destinationId = zoneId(destination);
    if (hasOpportunities(origin) && hasOpportunities(destination)) {
      Location originLocation = selectLocationIn(origin);
      Location destinationLocation = selectLocationIn(destination);
      int from = originLocation.roadAccessEdgeId;
      int to = destinationLocation.roadAccessEdgeId;
      return new LocationToLocation(id_seq++, from, to, startTime);
    }
    if (hasOpportunities(origin)) {
      Location location = selectLocationIn(origin);
      int from = location.roadAccessEdgeId;
      return new FromLocation(id_seq++, from, destinationId, startTime);
    }
    if (hasOpportunities(destination)) {
      Location location = selectLocationIn(destination);
      int to = location.roadAccessEdgeId;
      return new ToLocation(id_seq++, originId, to, startTime);
    }
    return new ZoneToZone(id_seq++, originId, destinationId, startTime);
  }

  private Location selectLocationIn(Zone origin) {
    return origin
        .opportunities()
        .selectRandomLocation(ActivityType.WORK, rnd_location.nextDouble());
  }

  private boolean hasOpportunities(Zone origin) {
    return origin.opportunities().locationsAvailable(ActivityType.WORK);
  }

  private int zoneId(Zone origin) {
    return Integer.parseInt(origin.getId().substring(1));
  }

}
