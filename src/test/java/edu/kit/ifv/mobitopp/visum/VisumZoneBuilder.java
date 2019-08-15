package edu.kit.ifv.mobitopp.visum;

import java.util.Collections;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumZoneBuilder {

	private static final int defaultId = 0;
	private static final String defaultName = "zone";
	private static final int defaultMainZoneId = 0;
	private static final int defaultType = 1;
	private static final int defaultParkingPlaces = 1;
	private static final float defaultCoordX = 2.0f;
	private static final float defaultCoordY = 3.0f;
	private static final int defaultAreaId = 4;
	private static final int defaultChargingFacilities = 5;
	private static final String defaultFreeFloatingCarSharingCompany = "free-floating";
	private static final int defaultFreeFloatingCarSharingArea = 0;
	private static final int defaultFreeFloatingCarSharingCars = 7;
	private static final double defaultPrivateChargingProbability = 1.0;
	private static final Map<String, Float> defaultCarsharingDensities = Collections.emptyMap();
	private static final float defaultInnerZonePublicTransport = 8.0f;
	
	private int id;
	private String name;
	private int mainZoneId;
	private int type;
	private int parkingPlaces;
	private float coord_x;
	private float coord_y;
	private int areaId;
	private int chargingFacilities;
	private String freeFloatingCarSharingCompany;
	private int freeFloatingCarSharingArea;
	private int freeFloatingCarSharingCars;
	private double privateChargingProbability;
	private Map<String, Float> carsharingDensities;
	private float innerZonePublicTransport;

	public VisumZoneBuilder() {
		super();
		id = defaultId;
		name = defaultName;
		mainZoneId = defaultMainZoneId;
		type = defaultType;
		parkingPlaces = defaultParkingPlaces;
		coord_x = defaultCoordX;
		coord_y = defaultCoordY;
		areaId = defaultAreaId;
		chargingFacilities = defaultChargingFacilities;
		freeFloatingCarSharingCompany = defaultFreeFloatingCarSharingCompany;
		freeFloatingCarSharingArea = defaultFreeFloatingCarSharingArea;
		freeFloatingCarSharingCars = defaultFreeFloatingCarSharingCars;
		privateChargingProbability = defaultPrivateChargingProbability;
		carsharingDensities = defaultCarsharingDensities;
		innerZonePublicTransport = defaultInnerZonePublicTransport;
	}

	public VisumZone build() {
		return new VisumZone(id, name, mainZoneId, type, parkingPlaces, coord_x, coord_y, areaId, chargingFacilities,
				freeFloatingCarSharingCompany, freeFloatingCarSharingArea, freeFloatingCarSharingCars,
				privateChargingProbability, carsharingDensities, innerZonePublicTransport);
	}
	
	public VisumZoneBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public VisumZoneBuilder external() {
		this.type = 5;
		return this;
	}

	public VisumZoneBuilder internal() {
		this.type = 1;
		return this;
	}

}
