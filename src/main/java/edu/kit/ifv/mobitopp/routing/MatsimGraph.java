package edu.kit.ifv.mobitopp.routing;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class MatsimGraph extends GraphFromVisumNetwork {

  public MatsimGraph(VisumRoadNetwork visum, TravelTimeFromMatsim travelTime, float currentTime) {
    super(visum, mivLinkValidation(visum), mivConnectorValidation(visum),
        createMatsimLinks(travelTime, currentTime), NodeFromVisumNode::new);
  }

  private static VisumLinkFactory createMatsimLinks(
      TravelTimeFromMatsim travelTime, float currentTime) {
    return new MatsimLinkFactory(currentTime, travelTime);
  }

  private static Function<VisumOrientedLink, Boolean> mivLinkValidation(VisumRoadNetwork visum) {
    VisumTransportSystem carSystem = carSystemFrom(visum);
    return link -> link.transportSystems.contains(carSystem) && link.attributes.capacityCar > 0.0f;
  }

  static Function<VisumConnector, Boolean> mivConnectorValidation(VisumRoadNetwork visum) {
    VisumTransportSystem carSystem = carSystemFrom(visum);
    return connector -> connector.transportSystems.contains(carSystem);
  }
}
