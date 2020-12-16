package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.visum.VisumBuilder.visumZone;
import static java.util.Collections.singletonMap;

import edu.kit.ifv.mobitopp.visum.VisumConnector.Direction;

public class VisumConnectorBuilder {

	private static final VisumZone defaultZone = visumZone().build();
	private static final VisumNode defaultNode = visumNode().build();
	private static final Direction sourceDirection = Direction.ORIGIN;
	private static final Direction destinationDirection = Direction.DESTINATION;
	private static final String defaultCode = "P";
	private static final float defaultLength = 1.0f;
	private static final int defaultTravelTimeInSeconds = 1;
	
	private VisumZone zone;
	private VisumNode node;
	private Direction direction;
	private VisumTransportSystemSet transportSystems;
	private float length;
	private int travelTimeInSeconds;
	
	public VisumConnectorBuilder() {
		super();
		zone = defaultZone;
		node = defaultNode;
		direction = sourceDirection;
		transportSystems = defaultTransportSystemSet();
		length = defaultLength;
		travelTimeInSeconds = defaultTravelTimeInSeconds;
	}
	
	private static VisumTransportSystemSet defaultTransportSystemSet() {
		return VisumTransportSystemSet.getByCode(defaultCode, systems());
	}

	private static VisumTransportSystems systems() {
		return new VisumTransportSystems(
				singletonMap(defaultCode, new VisumTransportSystem(defaultCode, defaultCode, defaultCode)));
	}

	public VisumConnector build() {
		return new VisumConnector(zone, node, direction, transportSystems, length, travelTimeInSeconds);
	}

	public VisumConnectorBuilder with(VisumZone zone) {
		this.zone = zone;
		return this;
	}

	public VisumConnectorBuilder with(VisumNode node) {
		this.node = node;
		return this;
	}
	
	public VisumConnectorBuilder origin() {
		direction = sourceDirection;
		return this;
	}
	
	public VisumConnectorBuilder destination() {
		direction = destinationDirection;
		return this;
	}

}
