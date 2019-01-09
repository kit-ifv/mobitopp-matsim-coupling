package edu.kit.ifv.mobitopp.routing;

public final class MatsimVisumTravelTime implements TravelTime {

  private static final float secondsPerMinute = 60.0f;
  private final TravelTimeFromMatsim travelTime;

  public MatsimVisumTravelTime(TravelTimeFromMatsim travelTime) {
    this.travelTime = travelTime;
  }

  @Override
  public float travelTime(Edge edge, float currentTime) {
    if (edge instanceof LinkFromMatsim) {
      return ((LinkFromMatsim) edge).travelTime();
    }
    if (edge instanceof LinkFromVisumConnector) {
      return travelTimeInSeconds((LinkFromVisumConnector) edge);
    }
    return travelTime.travelTime(edge, currentTime);
  }

  private float travelTimeInSeconds(LinkFromVisumConnector fromConnector) {
    return fromConnector.travelTime() * secondsPerMinute;
  }
}