package com.aslan.contra.entities;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Location extends Entity {
	/**
	 * Name of the location.
	 */
	private String name;

	/**
	 * GeoFence number of the location.
	 */
	private long geoFence;

	/**
	 * Latitude of the location.
	 */
	private double latitude;

	/**
	 * Longitude of the location.
	 */
	private double longitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getGeoFence() {
		return geoFence;
	}

	public void setGeoFence(long geoFence) {
		this.geoFence = geoFence;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
