package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleEdge;

public class CarVelocityFilter implements EdgeFilter {

	final int maximumVelocityFallback = 50;
	private final int maximumVelocity;

	public CarVelocityFilter(int maximumVelocity) {
		super();
		this.maximumVelocity = maximumVelocity;
	}

	@Override
	public List<Edge> filter(Collection<Edge> edges) {
		verify(edges);
		List<Edge> filtered = filterEdges(edges, maximumVelocity);
		if (filtered.size() > 0) {
			return filtered;
		}
		List<Edge> filteredByFallback = filterEdges(edges, maximumVelocityFallback);
		if (filteredByFallback.size() > 0) {
			return filteredByFallback;
		}
		return new ArrayList<>(edges);
	}

	private void verify(Collection<Edge> edges) {
		if (edges.isEmpty()) {
			throw new IllegalArgumentException("No edges to filter!");
		}
	}

	private List<Edge> filterEdges(Collection<Edge> edges, int maximumVelocity) {
		return edges
				.stream()
				.flatMap(this::toSimpleEdge)
				.filter(SimpleEdge::carAllowed)
				.filter(slowerThan(maximumVelocity))
				.collect(toList());
	}

	private Predicate<SimpleEdge> slowerThan(int maximumVelocity) {
		return edge -> maximumVelocity > edge.allowedVelocityInKm();
	}

	private Stream<SimpleEdge> toSimpleEdge(Edge edge) {
		if (edge instanceof SimpleEdge) {
			return Stream.of((SimpleEdge)edge);
		}
		return Stream.empty();
	}
}
