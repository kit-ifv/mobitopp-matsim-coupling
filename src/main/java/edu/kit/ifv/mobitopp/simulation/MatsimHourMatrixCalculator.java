package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.controler.Controler;
import org.matsim.core.router.util.TravelTime;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.routing.MatsimGraph;
import edu.kit.ifv.mobitopp.routing.MatsimVisumTravelTime;
import edu.kit.ifv.mobitopp.routing.Node;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.routing.TimeAwareForwardDijkstra;
import edu.kit.ifv.mobitopp.routing.TravelTimeFromMatsim;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class MatsimHourMatrixCalculator {

  private final Network network;
  private final VisumRoadNetwork visumNetwork;
  private final Map<Integer, Integer> idsToOids;
  private final Controler controler;

  public MatsimHourMatrixCalculator(
      Network network, VisumRoadNetwork visumNetwork, Map<Integer, Integer> idsToOids,
      Controler controler) {
    super();
    this.network = network;
    this.visumNetwork = visumNetwork;
    this.idsToOids = idsToOids;
    this.controler = controler;
  }

  public TravelTimeMatrix calculateMatrix(float timeOfDayInSec) {
    System.out.println("starting Dijkstra: " + ((int) timeOfDayInSec / 60 / 60));
    TravelTimeFromMatsim ttForGraph = createTravelTimeForGraph();
    MatsimGraph graph = createMatsimGraph(timeOfDayInSec, ttForGraph);
    edu.kit.ifv.mobitopp.routing.TravelTime ttFunction = new MatsimVisumTravelTime(ttForGraph);
    return doCalculateMatrix(timeOfDayInSec, graph, ttFunction);
  }

  private MatsimGraph createMatsimGraph(float timeOfDayInSec, TravelTimeFromMatsim ttForGraph) {
    return new MatsimGraph(visumNetwork, ttForGraph, timeOfDayInSec);
  }

  private TravelTimeFromMatsim createTravelTimeForGraph() {
    TravelTime tt = controler.getLinkTravelTimes();
    return new TravelTimeFromMatsim(network, tt);
  }

  private TravelTimeMatrix doCalculateMatrix(
      float timeOfDayInSec, MatsimGraph graph, edu.kit.ifv.mobitopp.routing.TravelTime ttfunction) {
    TravelTimeMatrix ttMatrix = new TravelTimeMatrix(new ArrayList<>(idsToOids.values()),
        Float.POSITIVE_INFINITY);
    for (Entry<Integer, Node> from : graph.zones().entrySet()) {
      Integer fromId = from.getKey();
      Node zone = from.getValue();
      Map<Node, Path> paths = dijkstra()
          .shortestPathToAllZones(graph, ttfunction, zone, timeOfDayInSec);
      for (Node n : paths.keySet()) {
        Path p = paths.get(n);
        Integer toId = Integer.valueOf(n.id().substring(1));
        if (fromId != toId) {
          ttMatrix.set(idsToOids.get(fromId), idsToOids.get(toId), p.travelTime() / 60.0f);
        }
      }
    }
    return ttMatrix;
  }

  private TimeAwareForwardDijkstra dijkstra() {
    return new TimeAwareForwardDijkstra(new PriorityQueueBasedPQ<>());
  }
}
