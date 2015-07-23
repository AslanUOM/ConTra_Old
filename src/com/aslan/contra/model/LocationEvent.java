package com.aslan.contra.model;

public class LocationEvent extends Event {
	/**
	 * Latitude of the location.
	 */
	private double latitude;

	/**
	 * Longitude of the location.
	 */
	private double longitude;

	/**
	 * GeoFence number of the location.
	 */
	private int geoFence;

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the geoFence
	 */
	public int getGeoFence() {
		return geoFence;
	}

	/**
	 * @param geoFence
	 *            the geoFence to set
	 */
	public void setGeoFence(int geoFence) {
		this.geoFence = geoFence;
	}

	@Override
	public String toString() {
		return String.format("[%.8f, %.8f] = %d", latitude, longitude, geoFence);
	}
}
