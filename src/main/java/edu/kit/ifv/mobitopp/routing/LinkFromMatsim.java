package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;

public final class LinkFromMatsim extends LinkFromVisumLink {

	private final float time;

	public LinkFromMatsim(
			VisumOrientedLink visumLink, NodeFromVisumNode from, NodeFromVisumNode to, float time) {
		super(visumLink, from, to);
    this.time = time;
	}

	@Override
	public float travelTime() {
		return time;
	}

}