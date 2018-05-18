package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleEdge;

public class CarVelocityFilterTest {

	private static final int maxVelocity = 1;
	private static final float lowerThanMaxVelocity = maxVelocity - 1.0f;
	private static final float higherThanMaxVelocity = maxVelocity + 1.0f;

	private CarVelocityFilter filter;
	private SimpleEdge allowedEdge;
	private float higherThanFallbackVelocity;

	@Before
	public void initialise() {
		filter = newFilter();
		allowedEdge = allowedEdge();
		higherThanFallbackVelocity = filter.maximumVelocityFallback + 1.0f;
	}

	@Test
	public void filtersNonCarEdges() {
		SimpleEdge rejectedEdge = mock(SimpleEdge.class);
		carNotAllowedOn(rejectedEdge);
		Collection<Edge> edges = asList(allowedEdge, rejectedEdge);

		List<Edge> filterEdges = filter.filter(edges);

		assertThat(filterEdges, contains(allowedEdge));
	}

	@Test
	public void filtersTooFastEdges() {
		SimpleEdge rejectedEdge = mock(SimpleEdge.class);
		carAllowedOn(rejectedEdge);
		carsTooFastOn(rejectedEdge);
		Collection<Edge> edges = asList(allowedEdge, rejectedEdge);

		List<Edge> filterEdges = filter.filter(edges);

		assertThat(filterEdges, contains(allowedEdge));
	}

	@Test
	public void filtersNothing() {
		SimpleEdge anotherAllowedOne = allowedEdge();
		Collection<Edge> edges = asList(allowedEdge, anotherAllowedOne);

		List<Edge> filterEdges = filter.filter(edges);

		assertThat(filterEdges, containsInAnyOrder(allowedEdge, anotherAllowedOne));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnEmptyList() {
		filter.filter(emptyList());
	}

	@Test
	public void filtersToFallback() {
		SimpleEdge rejectedEdge = mock(SimpleEdge.class);
		carAllowedOn(rejectedEdge);
		carsTooFastOn(rejectedEdge);
		Collection<Edge> edges = asList(rejectedEdge);

		List<Edge> filterEdges = filter.filter(edges);

		assertThat(filterEdges, contains(rejectedEdge));
	}

	@Test
	public void tooFastForFallback() {
		SimpleEdge edge = mock(SimpleEdge.class);
		carAllowedOn(edge);
		when(edge.allowedVelocityInKm()).thenReturn(higherThanFallbackVelocity);
		Collection<Edge> edges = asList(edge);

		List<Edge> filterEdges = filter.filter(edges);

		assertThat(filterEdges, containsInAnyOrder(edge));
	}

	private void carsTooFastOn(SimpleEdge edge) {
		when(edge.allowedVelocityInKm()).thenReturn(higherThanMaxVelocity);
	}

	private void carNotAllowedOn(SimpleEdge rejectedEdge) {
		when(rejectedEdge.carAllowed()).thenReturn(false);
	}

	private SimpleEdge allowedEdge() {
		SimpleEdge allowedEdge = mock(SimpleEdge.class);
		carAllowedOn(allowedEdge);
		when(allowedEdge.allowedVelocityInKm()).thenReturn(lowerThanMaxVelocity);
		return allowedEdge;
	}

	private void carAllowedOn(SimpleEdge edge) {
		when(edge.carAllowed()).thenReturn(true);
	}

	private CarVelocityFilter newFilter() {
		return new CarVelocityFilter(maxVelocity);
	}
}
