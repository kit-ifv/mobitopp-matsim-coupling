package edu.kit.ifv.mobitopp.visum;


public class VisumBuilder {

	public static VisumNodeBuilder visumNode() {
		return new VisumNodeBuilder();
	}

	public static VisumLinkBuilder visumLink() {
		return new VisumLinkBuilder();
	}

	public static VisumOrientedLinkBuilder visumOrientedLink() {
		return new VisumOrientedLinkBuilder();
	}
	
	public static VisumConnectorBuilder visumConnector() {
		return new VisumConnectorBuilder();
	}

	public static VisumZoneBuilder visumZone() {
		return new VisumZoneBuilder();
	}

	public static VisumNetworkBuilder visumNetwork() {
		return new VisumNetworkBuilder();
	}
}
