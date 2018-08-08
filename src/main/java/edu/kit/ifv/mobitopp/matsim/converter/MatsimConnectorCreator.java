package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class MatsimConnectorCreator extends BaseLinkCreator {

	public static final int   CONNECTOR_LANES = 4;
	public static final float CONNECTOR_FREESPEED = 50.0f;	// km/h
	public static final float CONNECTOR_CAPACITY = 100000;

	private final VisumRoadNetwork visum;

	public MatsimConnectorCreator(VisumRoadNetwork visum, Network network) {
		super(visum, network);
		this.visum = visum;
	}

	public void createConnectors(Map<Integer, Node> nodes, Map<String, Node> zones) {
		for (Integer zoneId : visum.connectors.keySet()) {
			for (VisumConnector connector : visum.connectors.get(zoneId)) {
				if (isCar(connector.transportSystems.transportSystems)) {
					Node matsimNode = nodes.get(connector.node.id());
					if (connector.direction == VisumConnector.Direction.ORIGIN) {
						makeLink(connector.id,  
											zones.get("Z" + connector.zone.id + ":1"),
											matsimNode,
											linkLengthFor(connector),
											CONNECTOR_CAPACITY,
											CONNECTOR_FREESPEED,
											CONNECTOR_LANES,
											modesFromTS(connector.transportSystems)
										);
					} else {
						makeLink(connector.id,  
											matsimNode,
											zones.get("Z" + connector.zone.id + ":2"),
											linkLengthFor(connector),
											CONNECTOR_CAPACITY,
											CONNECTOR_FREESPEED,
											CONNECTOR_LANES,
											modesFromTS(connector.transportSystems)
										);
					}
				}
			}
		}
	}

	private float linkLengthFor(VisumConnector connector) {
		return CONNECTOR_FREESPEED * connector.travelTimeInSeconds;
	}
	
	private boolean isCar(Set<VisumTransportSystem> transport) {
		return toMode().isCar(transport);
	}

}
