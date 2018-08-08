package edu.kit.ifv.mobitopp.routing;

import static edu.kit.ifv.mobitopp.routing.GraphFromVisumNetwork.mivConnectorValidation;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueueBasedPQ;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class ValidateLinks {

	private static final double minimumRequired = 1 - 5e-3;

	public VisumRoadNetwork of(VisumRoadNetwork network) {
		GraphFromVisumNetwork graph = new GraphFromVisumNetwork(network, NodeFromVisumZone::useExternalInRouteSeach);
		List<Link> reachableLinks = filterLinksIn(graph);
		logLinks(reachableLinks, graph);
		Map<Integer, VisumLink> links = reachableLinks(reachableLinks, network);
		Map<Integer, List<VisumConnector>> connectors = reachableConnectors(network);
		return clone(network, links, connectors);
	}

	private VisumRoadNetwork clone(
			VisumRoadNetwork network, Map<Integer, VisumLink> links,
			Map<Integer, List<VisumConnector>> connectors) {
		return new VisumNetworkBuilder(network).withLinks(links).withConnectors(connectors).build();
	}

	private void logLinks(List<Link> reachableLinks, GraphFromVisumNetwork graph) {
		List<Link> links = graph.links();
		int original = links.size();
		int correct = reachableLinks.size();
		int unreachable = original - correct;
		double ratio = (double) correct / (double) original;
		System.out.println(String.format("%01d of %01d MIV links are reachable.", correct, original));
		System.out.println(String.format("%01d links are unreachable.", unreachable));
		System.out.println("Both directions of unreachable links will be removed.");
		Predicate<Link> reachable = reachableLinks::contains;
		logFiltered(links, reachable.negate());
		if (minimumRequired > ratio) {
			throw new IllegalArgumentException(
					String.format("Less than %3d % of MIV links are incorrect.", minimumRequired));
		}
	}

	private void logFiltered(List<Link> links, Predicate<Link> notReachable) {
		links.stream().filter(notReachable).sorted(Comparator.comparing(Link::id)).forEach(
				System.out::println);
	}

	private Map<Integer, VisumLink> reachableLinks(
			List<Link> reachableLinks, VisumRoadNetwork network) {
		Map<Integer, VisumLink> links = new HashMap<>();
		for (Link link : reachableLinks) {
			int parsedId = parseLinkId(link);
			VisumLink visumLink = network.links.links.get(parsedId);
			if (null == visumLink) {
				throw new IllegalArgumentException("Can not find link with id " + parsedId);
			}
			links.put(parsedId, visumLink);
		}
		return links;
	}

	private Map<Integer, List<VisumConnector>> reachableConnectors(VisumRoadNetwork network) {
		VisumTransportSystem carSystem = network.getTransportSystem("P");
		HashMap<Integer, List<VisumConnector>> connectors = new HashMap<>();
		for (Entry<Integer, List<VisumConnector>> entry : network.connectors.entrySet()) {
			connectors.put(entry.getKey(), new ArrayList<>());
			for (VisumConnector connector : entry.getValue()) {
				if (mivConnectorValidation(carSystem).apply(connector)) {
					connectors.get(entry.getKey()).add(connector);
				}
			}
		}
		return connectors;
	}

	private Integer parseLinkId(Link link) {
		return Integer.parseInt(link.id().split(":")[0]);
	}

	private List<Link> filterLinksIn(GraphFromVisumNetwork graph) {
		Set<String> reachableNodes = search(graph);
		Predicate<Link> reachableLinks = link -> reachableNodes.contains(link.from().id())
				&& reachableNodes.contains(link.to().id());
		return graph.links().stream().filter(reachableLinks).collect(toList());
	}

	private Set<String> search(GraphFromVisumNetwork graph) {
		Node node = node(graph);
		Set<String> reachableFrom = allNodesReachableFromZone(graph, node);
		Set<String> headingTo = allNodesHeadingToZone(graph, node);
		HashSet<String> reachableNodeIds = new HashSet<>(reachableFrom);
		reachableNodeIds.retainAll(headingTo);
		return reachableNodeIds;
	}

	private Set<String> allNodesReachableFromZone(GraphFromVisumNetwork graph, Node node) {
		Map<Node, Path> paths = search().shortestPathToAll(graph, node);
		return nodesFrom(paths);
	}

	private Set<String> allNodesHeadingToZone(GraphFromVisumNetwork graph, Node node) {
		Map<Node, Path> paths = search().shortestPathFromAll(graph, node);
		return nodesFrom(paths);
	}

	private Dijkstra search() {
		return new Dijkstra(new PriorityQueueBasedPQ<>());
	}

	private Set<String> nodesFrom(Map<Node, Path> paths) {
		return paths.keySet().stream().map(Node::id).collect(toSet());
	}

	private Node node(GraphFromVisumNetwork graph) {
		TreeMap<Integer, Node> zones = new TreeMap<>(graph.zones());
		return zones.firstEntry().getValue();
	}
}
