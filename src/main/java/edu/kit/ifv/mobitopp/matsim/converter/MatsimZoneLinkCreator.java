package edu.kit.ifv.mobitopp.matsim.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class MatsimZoneLinkCreator extends BaseLinkCreator {

	private static final float ZONE_CAPACITY = 100000;
	private static final float ZONE_FREESPEED = 30.0f;	// km/h
	private static final float ZONE_LENGTH = 1; // km
	private static final int ZONE_LANES = 2;
	private static final Set<String> ZONE_MODES;
	static {
		Set<String> tmp = new HashSet<String>();
		tmp.add("car");
		tmp.add("pt");
		tmp.add("bike");
		tmp.add("walk");

		ZONE_MODES = Collections.unmodifiableSet(tmp);
	}

	private final VisumRoadNetwork visum;

	public MatsimZoneLinkCreator(VisumRoadNetwork visum, Network network) {
		super(visum, network);
		this.visum = visum;
	}

	public Map<String, Node> createZoneLinks(MatsimNodeCreator createNode) {
		Map<String, Node> zones = new HashMap<String, Node>();

		for (VisumZone zone : visum.zones.values()) {
			String id1 = "Z" + zone.id + ":1";
			Node matsimNode1 = createNode.from(id1, zone.coord.x, zone.coord.y);

			zones.put(id1, matsimNode1);
			String id1Inflow = "Z" + zone.id + ":1-INFLOW";
			Node matsimNode1Inflow = createNode.from(id1Inflow, zone.coord.x, zone.coord.y);
			
			zones.put(id1Inflow, matsimNode1Inflow);
			String inflowLink = "Z" + zone.id + "-INFLOW";
			makeLink(inflowLink, 
					matsimNode1Inflow, 
					matsimNode1, 
					ZONE_LENGTH, 
					MatsimConnectorCreator.CONNECTOR_CAPACITY, 
					MatsimConnectorCreator.CONNECTOR_FREESPEED, 
					MatsimConnectorCreator.CONNECTOR_LANES, 
					ZONE_MODES);
			
			String id2 = "Z" + zone.id + ":2";
			Node matsimNode2 = createNode.from(id2, zone.coord.x, zone.coord.y);

			zones.put(id2, matsimNode2);
			String id2Outflow = "Z" + zone.id + ":2-OUTFLOW";
			Node matsimNode2Outflow = createNode.from(id2Outflow, zone.coord.x, zone.coord.y);
			
			zones.put(id2Outflow, matsimNode2Outflow);
			String outflowLink = "Z" + zone.id + "-OUTFLOW";
			makeLink(outflowLink, 
					matsimNode2, 
					matsimNode2Outflow, 
					ZONE_LENGTH, 
					MatsimConnectorCreator.CONNECTOR_CAPACITY, 
					MatsimConnectorCreator.CONNECTOR_FREESPEED, 
					MatsimConnectorCreator.CONNECTOR_LANES, 
					ZONE_MODES);
			
			if (isExternal(zone)) {
				makeLink("Z" + zone.id + ":12", 
						matsimNode1, 
						matsimNode2, 
						ZONE_LENGTH, 
						ZONE_CAPACITY,
						ZONE_FREESPEED, 
						ZONE_LANES, 
						ZONE_MODES);

				makeLink("Z" + zone.id + ":21", 
						matsimNode2, 
						matsimNode1, 
						ZONE_LENGTH, 
						ZONE_CAPACITY,
						ZONE_FREESPEED, 
						ZONE_LANES, 
						ZONE_MODES);
			}
		}
		return zones;
	}

	private boolean isExternal(VisumZone zone) {
		return 5 == zone.type;
	}
}
