package com.aslan.contra.cep.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class represents an event of location detection. It consists the geo
 * location related information and available WIFI addresses.
 * 
 * @author gobinath
 *
 */
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
	private long geoFence;

	/**
	 * Available WIFI locations at the current location.
	 */
	private List<String> wifiNetworks;

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
	public long getGeoFence() {
		return geoFence;
	}

	/**
	 * @param geoFence
	 *            the geoFence to set
	 */
	public void setGeoFence(long geoFence) {
		this.geoFence = geoFence;
	}

	/**
	 * @return the wifiNetworks
	 */
	public List<String> getWifiNetworks() {
		return wifiNetworks;
	}

	/**
	 * @param wifiNetworks
	 *            the wifiNetworks to set
	 */
	public void setWifiNetworks(List<String> wifiNetworks) {
		this.wifiNetworks = wifiNetworks;
	}

	public void addAll(String... wifiNetworks) {
		if (this.wifiNetworks == null) {
			this.wifiNetworks = new ArrayList<>();
		}
		this.wifiNetworks.addAll(Arrays.asList(wifiNetworks));
	}

	@Override
	public String toString() {
		return String.format("%s -> [%.8f, %.8f] = %d \t @%s:", getUserID(),
				latitude, longitude, geoFence, new Date(getTime()).toString());
	}
}
