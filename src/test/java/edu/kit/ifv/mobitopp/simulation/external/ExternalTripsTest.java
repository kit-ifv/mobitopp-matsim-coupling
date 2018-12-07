package edu.kit.ifv.mobitopp.simulation.external;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumMatrix;

public class ExternalTripsTest {

  private static final int originId = 1;
  private static final int destinationId = 2;
  private static final int hour = 0;
  private static final int minute = 38;
  private static final Double dummyPoint = new Point2D.Double(0.0d, 0.0d);
  private static final int dummyAccessEdge = -1;
  private static final Location dummyLocation = new Location(dummyPoint, dummyAccessEdge, 0.0d);
  private ExternalTrips trips;
  private Zone origin;
  private Zone destination;
  private OpportunityDataForZone originOpportunities;
  private OpportunityDataForZone destinationOpportunities;
  private Time startTime;

  @Before
  public void initialise() {
    startTime = SimpleTime.ofHours(hour).plusMinutes(minute);
    List<VisumMatrix> totalTrips = asList(new VisumMatrix(new FloatMatrix(emptyList())));
    HourlyTimeProfile timeProfile = mock(HourlyTimeProfile.class);
    ZoneRepository zoneRepository = mock(ZoneRepository.class);
    origin = mock(Zone.class);
    destination = mock(Zone.class);
    when(origin.getId()).thenReturn("Z" + originId);
    when(destination.getId()).thenReturn("Z" + destinationId);
    originOpportunities = mock(OpportunityDataForZone.class);
    destinationOpportunities = mock(OpportunityDataForZone.class);
    when(origin.opportunities()).thenReturn(originOpportunities);
    when(destination.opportunities()).thenReturn(destinationOpportunities);

    trips = new ExternalTrips(totalTrips, timeProfile, zoneRepository);
  }

  @Test
  public void createZoneToZoneTrip() {
    ExternalTrip trip = trips.createTrip(origin, destination, hour);

    ExternalTrip zoneTrip = new ZoneToZone(trip.id(), originId, destinationId, startTime);
    assertThat(trip, is(equalTo(zoneTrip)));
  }

  @Test
  public void createFromLocationTrip() {
    setOriginAvailable();
    int from = dummyAccessEdge;
    int to = 2;
    ExternalTrip trip = trips.createTrip(origin, destination, hour);

    ExternalTrip fromTrip = new FromLocation(trip.id(), from, to, startTime);
    assertThat(trip, is(equalTo(fromTrip)));
  }
  
  @Test
  public void createToLocationTrip() {
    setDestinationAvailable();
    int from = 1;
    int to = dummyAccessEdge;
    ExternalTrip trip = trips.createTrip(origin, destination, hour);
    
    ExternalTrip fromTrip = new ToLocation(trip.id(), from, to, startTime);
    assertThat(trip, is(equalTo(fromTrip)));
  }
  
  @Test
  public void createLocationToLocationTrip() {
    setOriginAvailable();
    setDestinationAvailable();
    int from = dummyAccessEdge;
    int to = dummyAccessEdge;
    ExternalTrip trip = trips.createTrip(origin, destination, hour);
    
    ExternalTrip fromTrip = new LocationToLocation(trip.id(), from, to, startTime);
    assertThat(trip, is(equalTo(fromTrip)));
  }

  private void setOriginAvailable() {
    Location location = dummyLocation;
    when(originOpportunities.locationsAvailable(ActivityType.WORK)).thenReturn(true);
    when(originOpportunities.selectRandomLocation(eq(ActivityType.WORK), anyDouble()))
        .thenReturn(location);
  }

  private void setDestinationAvailable() {
    Location location = dummyLocation;
    when(destinationOpportunities.locationsAvailable(ActivityType.WORK)).thenReturn(true);
    when(destinationOpportunities.selectRandomLocation(eq(ActivityType.WORK), anyDouble()))
        .thenReturn(location);
  }
}
