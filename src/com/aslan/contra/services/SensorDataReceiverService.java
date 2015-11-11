package com.aslan.contra.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.aslan.contra.cep.event.SensorResponse;
import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Person;

/**
 * This service is accessed by sensor plug-in to send the sensed information to
 * the middleware.
 * 
 * @author gobinath
 *
 */
@Path("/sensordatareceiver")
public class SensorDataReceiverService {
	private static final Logger LOGGER = Logger.getLogger(SensorDataReceiverService.class);

	// private static CEPProcessor processor = CEPProcessor.getProcessor();

	// static {
	// ContextFactory factory = ContextFactory.getInstance();
	// Context context = factory.getContext(Type.LOCATION, "Origin");
	// processor.addContext(context);
	// }

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(SensorResponse res) {
		// // Handle the response
		// String[] friends = handleSensorResponse(res);
		//
		// Response response = Response.status(201).entity(friends).build();
		// // Return a successful response
		// return response;

		String[] nearByFriends = new String[0];
		SensorResponseProcessor processor = new SensorResponseProcessor(res);
		boolean validUser = processor.isValidUser();
		if (validUser) {
			processor.process();

			PersonService personService = new PersonService();
			Iterable<Person> friends = personService.nearByFriends(res.getUserID());
			List<String> phoneNumbers = new ArrayList<>();
			friends.forEach(x -> phoneNumbers.add(x.getPhoneNumber()));

			nearByFriends = phoneNumbers.toArray(nearByFriends);
		} else {
			LOGGER.warn("Response from invalid user: " + res.getUserID());
		}

		Response response = Response.status(201).entity(nearByFriends).build();
		return response;
	}

	// private String[] handleSensorResponse(SensorResponse response) {
	// // LocationEvent event = new LocationEvent();
	// // event.setUserID(response.getUserID());
	// // event.setDeviceID(response.getDeviceID());
	// String userId = response.getUserID();
	//
	// for (SensorData data : response) {
	// String[] info = data.getData();
	// switch (data.getType()) {
	// case Constants.Type.LOCATION:
	// double latitude = Double.parseDouble(info[0]);
	// double longitude = Double.parseDouble(info[1]);
	// long geoFence = LocationGrid.toGridNumber(latitude, longitude);
	//
	// Location location = saveLocation(latitude, longitude, geoFence);
	// String[] friends = bind(userId, geoFence);
	//
	// return friends;
	// // event.setLatitude(latitude);
	// // event.setLongitude(longitude);
	// // event.setGeoFence(geoFence);
	// // event.setTime(data.getTime());
	// // break;
	//
	// // case Constants.Type.AVAILABLE_WIFI:
	// // List<String> wifiNetworks = Arrays.asList(info);
	// // event.setWifiNetworks(wifiNetworks);
	//
	// case Constants.Type.CONTACTS:
	// String[] numbers = data.getData();
	// addFriend(userId, numbers);
	// }
	// }
	// return new String[0];
	// // processor.addEvent(event);
	// }

	// private Location saveLocation(double latitude, double longitude, long
	// geoFence) {
	// LocationService service = new LocationService();
	// Location location = service.findUsingGeoFence(geoFence);
	// if (location == null) {
	// // This location is not in the database
	// location = new Location();
	// location.setGeoFence(geoFence);
	// location.setLatitude(latitude);
	// location.setLongitude(longitude);
	//
	// service.createOrUpdate(location);
	// }
	//
	// return location;
	// }

	// private String[] bind(String userId, long geoFence) {
	// LocationService locationService = new LocationService();
	// PersonService personService = new PersonService();
	//
	// Location location = locationService.findUsingGeoFence(geoFence);
	// Person person = personService.find(userId);
	// if (person != null && location != null) {
	// LOGGER.info("Current location of person " + person.getName() + " is " +
	// location.getId());
	// person.setCurrentLocation(location);
	// }
	// personService.createOrUpdate(person);
	//
	// Iterable<Person> friends = personService.nearByFriends(userId);
	// List<String> phoneNumbers = new ArrayList<>();
	// friends.forEach(x -> phoneNumbers.add(x.getPhoneNumber()));
	// return phoneNumbers.toArray(new String[0]);
	// }

	// private void addFriend(String userId, String[] numbers) {
	// PersonService service = new PersonService();
	// Person person = service.find(userId);
	// if (person != null) {
	// LOGGER.info("Person: " + person.getName());
	// // Add all friends to the person
	// for (String number : numbers) {
	// try {
	// String friendId = Utility.formatPhoneNumber(number);
	// Person friend = service.find(friendId);
	// if (friend != null) {
	// LOGGER.info("Friend: " + friend.getName());
	// person.getFriends().add(friend);
	// }
	// } catch (NumberParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// service.createOrUpdate(person);
	// }
	// }
}
