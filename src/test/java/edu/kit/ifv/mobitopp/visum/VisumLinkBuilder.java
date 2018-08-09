package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumOrientedLink;

import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class VisumLinkBuilder {

	private static final String downLinkSuffix = ":1";
	private static final String upLinkSuffix = ":2";
	private static final String defaultCode = "D";
	private static final int defaultId = 0;
	private static final VisumOrientedLink defaultLinkA = visumOrientedLink().build();
	private static final VisumOrientedLink defaultLinkB = visumOrientedLink().build();

	private int id;
	private VisumOrientedLink linkA;
	private VisumOrientedLink linkB;
	private VisumNode start;
	private VisumNode end;
	private VisumLinkType type;
	private int walkSpeed;
	private float length;
	private int capacityCar;
	private VisumTransportSystem transportSystem;

	public VisumLinkBuilder() {
		super();
		id = defaultId;
		linkA = defaultLinkA;
		linkB = defaultLinkB;
		transportSystem = defaultTransportSystem();
		type = defaultLinkA.linkType;
		walkSpeed = defaultLinkA.attributes.walkSpeed;
		length = defaultLinkA.length;
		capacityCar = defaultLinkA.attributes.capacityCar;
	}

	private static VisumTransportSystem defaultTransportSystem() {
		return new VisumTransportSystem(defaultCode, defaultCode, defaultCode);
	}

	public VisumLink build() {
		if (start != null && end != null) {
			return fromStartToEnd();
		}
		return new VisumLink(id, linkA, linkB);
	}

	private VisumLink fromStartToEnd() {
		VisumOrientedLink down = visumOrientedLink()
				.from(start)
				.to(end)
				.withId(downLinkId())
				.with(type)
				.withLength(length)
				.withWalkSpeed(walkSpeed)
				.withCarCapacity(capacityCar)
				.with(transportSystem)
				.build();
		VisumOrientedLink up = visumOrientedLink()
				.from(end)
				.to(start)
				.withId(upLinkId())
				.with(type)
				.withLength(length)
				.withWalkSpeed(walkSpeed)
				.withCarCapacity(capacityCar)
				.with(transportSystem)
				.build();
		return new VisumLink(id, down, up);
	}

	private String downLinkId() {
		return id + downLinkSuffix;
	}

	private String upLinkId() {
		return id + upLinkSuffix;
	}

	public VisumLinkBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumLinkBuilder from(VisumNode start) {
		this.start = start;
		return this;
	}

	public VisumLinkBuilder to(VisumNode end) {
		this.end = end;
		return this;
	}

	public VisumLinkBuilder with(VisumLinkType type) {
		this.type = type;
		return this;
	}

	public VisumLinkBuilder withSpeed(int speed) {
		this.walkSpeed = speed;
		return this;
	}

	public VisumLinkBuilder withLength(float length) {
		this.length = length;
		return this;
	}
	
	public VisumLinkBuilder withCapacityCar(int capacity) {
		this.capacityCar = capacity;
		return this;
	}

	public VisumLinkBuilder with(VisumTransportSystem transportSystem) {
		this.transportSystem = transportSystem;
		return this;
	}

	public VisumLinkBuilder withForward(VisumOrientedLink link) {
		this.linkA = link;
		return this;
	}

	public VisumLinkBuilder withBackward(VisumOrientedLink link) {
		this.linkB = link;
		return this;
	}

}
