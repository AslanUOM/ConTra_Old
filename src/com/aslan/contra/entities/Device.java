package com.aslan.contra.entities;

import java.util.Date;

public class Device extends Entity {
	/**
	 * Google push notification token.
	 */
	private String token;

	/**
	 * A user friendly name of the device.
	 */
	private String name;

	/**
	 * Unique serial number to identify the device.
	 */
	private String serial;

	/**
	 * Indicates whether the device is active right now.
	 */
	private boolean active;

	/**
	 * The lat seen time of this device.
	 */
	private Date lastSeen;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@Override
	public String toString() {
		return name + " - " + serial;
	}

}
