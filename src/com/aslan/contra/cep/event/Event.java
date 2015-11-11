package com.aslan.contra.cep.event;

/**
 * This class is the abstract super class of all Events which is used to feed
 * the events to CEP streams.
 * 
 * @author gobinath
 *
 */
public abstract class Event {
	/**
	 * ID of the user.
	 */
	private String userID;

	/**
	 * ID of the device.
	 */
	private String deviceID;

	/**
	 * Accuracy or reliability of the event.
	 */
	private double accuracy;

	/**
	 * Source used to capture the event.
	 */
	private String source;

	/**
	 * Time of the event in milliseconds from 1970 Jan 1st.
	 */
	private long time;

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * @param deviceID
	 *            the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
}
