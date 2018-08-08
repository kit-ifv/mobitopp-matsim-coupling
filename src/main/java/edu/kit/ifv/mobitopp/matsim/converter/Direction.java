package edu.kit.ifv.mobitopp.matsim.converter;

public enum Direction {

	up(":1"), down(":2");

	private final String suffix;

	private Direction(String suffix) {
		this.suffix = suffix;
	}

	public String suffix() {
		return suffix;
	}
}
