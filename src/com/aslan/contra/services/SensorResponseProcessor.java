package com.aslan.contra.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.aslan.contra.db.DeviceService;
import com.aslan.contra.db.LocationService;
import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Device;
import com.aslan.contra.entities.Location;
import com.aslan.contra.entities.Person;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants.Type;
import com.aslan.contra.util.GCMNotification;
import com.aslan.contra.util.LocationGrid;
import com.aslan.contra.util.Utility;
import com.google.i18n.phonenumbers.NumberParseException;

/**
 * This class handles the SensorResponse received from devices. It saves the
 * information into the database and feed the necessary details to the CEP.
 * 
 * @author gobinath
 *
 */
public class SensorResponseProcessor {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(SensorResponseProcessor.class);

	/**
	 * Sensor response received form the device.
	 */
	private SensorResponse response;

	/**
	 * Owner of the device which sent the response.
	 */
	private Person person;

	/**
	 * Formatted phone number of the owner.
	 */
	private String userId;

	/**
	 * PersonService to perform Person related database operations.
	 */
	private final PersonService personService;

	/**
	 * LocationService to perform Location related database operations.
	 */
	private final LocationService locationService;

	/**
	 * Construct a SensorResponseProcessor for the given SensorResponse.
	 * 
	 * @param response
	 *            the response received from the device.
	 */
	public SensorResponseProcessor(SensorResponse response) {
		// Create database services
		this.personService = new PersonService();
		this.locationService = new LocationService();

		this.response = response;
		this.userId = response.getUserID();
		this.person = personService.find(userId);

		LOGGER.info("Sensor data received from " + person);
	}

	public boolean isValidUser() {
		return this.person != null;
	}

	public void process() {
		if (isValidUser()) {
			// Update the last known property of the device
			String deviceId = response.getDeviceID();
			DeviceService deviceService = new DeviceService();
			Device device = deviceService.find(deviceId);

			// TODO: Include the process part inside this if block.
			if (device != null) {
				device.setLastSeen(new Date());
				deviceService.createOrUpdate(device);
			}
			LOGGER.info("Device which sent the data is: " + device);

			List<SensorData> sensorDatas = response.getSensorDatas();
			sensorDatas.forEach(this::process);
		}
	}

	public void process(SensorData data) {
		String type = data.getType();

		if (Type.LOCATION.equals(type)) {
			processLocation(data);
		} else if (Type.CONTACTS.equals(type)) {
			processContacts(data);
		}
	}

	private void processLocation(SensorData data) {
		// Get all the data = {latitude, longitude, geoFence}
		String[] info = data.getData();

		double latitude = Double.parseDouble(info[0]);
		double longitude = Double.parseDouble(info[1]);
		long geoFence = LocationGrid.toGridNumber(latitude, longitude);

		Location location = locationService.findUsingGeoFence(geoFence);

		if (location == null) {
			// This location is not in the database
			location = new Location();
			location.setGeoFence(geoFence);
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			locationService.createOrUpdate(location);
			LOGGER.info("New location: " + geoFence + " is saved");
		}

		// Update the current location
		personService.updateCurrentLocation(person, location);
		LOGGER.info("Current location of " + person + " is updated.");

		// Send notification to all the devices
		Iterable<Person> friends = personService.nearByFriends(userId);
		List<String> phoneNumbers = new ArrayList<>();
		friends.forEach(x -> phoneNumbers.add(x.getPhoneNumber()));
		if (!phoneNumbers.isEmpty()) {
			Iterable<String> allDeviceTokens = personService.allDeviceTokens(userId);
			GCMNotification notification = new GCMNotification(phoneNumbers.toString(), allDeviceTokens);
			notification.executeHTTPSConnectionBuilder();
		}
	}

	private void processContacts(SensorData data) {
		// Get all the data = {phone numbers}
		String[] contactNumbers = data.getData();

		// List of friends who do not have this person in their friends list
		List<String> friendsNotHavingPerson = new ArrayList<>();

		for (String number : contactNumbers) {
			try {
				String friendId = Utility.formatPhoneNumber(number);
				// Sometimes the person may have his phone number in his
				// contacts
				// list
				if (!userId.equals(friendId)) {
					Person friend = personService.find(friendId);
					if (friend != null) {
						// Friend is detected

						person.getFriends().add(friend);

						boolean friendHasPersonAlready = personService.isFriendWith(friend, person);
						if (!friendHasPersonAlready) {
							// Retrive all the device tokens
							Iterable<String> gcmTokens = personService.allDeviceTokens(friendId);
							// Add them to the list
							gcmTokens.forEach(friendsNotHavingPerson::add);
						}
					}
				}
			} catch (NumberParseException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		// Update the person for all the changes
		personService.createOrUpdate(person);
		LOGGER.info("Contacts have been processes successfully.");

		if (!friendsNotHavingPerson.isEmpty()) {
			LOGGER.info("Sending notification to the friends.");
			// Send a notification to the friends
			// TODO: Change the empty String to a menaingful command later
			GCMNotification notification = new GCMNotification("", friendsNotHavingPerson);
			notification.executeHTTPSConnectionBuilder();
		}
	}
}
