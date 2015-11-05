package com.aslan.contra.services;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.aslan.contra.cep.CEPProcessor;
import com.aslan.contra.cep.query.Context;
import com.aslan.contra.cep.query.Context.Type;
import com.aslan.contra.cep.query.ContextFactory;
import com.aslan.contra.db.LocationService;
import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Location;
import com.aslan.contra.entities.Person;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.LocationGrid;
import com.aslan.contra.util.Utility;
import com.google.i18n.phonenumbers.NumberParseException;

/**
 * This service is accessed by sensor plug-in to send the sensed information to
 * the middleware.
 * 
 * @author gobinath
 *
 */
@Path("/sensordatareceiver")
public class SensorDataReceiverService {
	private static CEPProcessor processor = CEPProcessor.getProcessor();

	static {
		ContextFactory factory = ContextFactory.getInstance();
		Context context = factory.getContext(Type.LOCATION, "Origin");
		processor.addContext(context);
	}

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(SensorResponse res) {
		// Handle the response
		Iterable<Person> friends = handleSensorResponse(res);
		Response response = Response.status(201).entity(friends).build();
		// Return a successful response
		return response;
	}

	private Iterable<Person> handleSensorResponse(SensorResponse response) {
		// LocationEvent event = new LocationEvent();
		// event.setUserID(response.getUserID());
		// event.setDeviceID(response.getDeviceID());
		String userId = response.getUserID();

		for (SensorData data : response) {
			String[] info = data.getData();
			switch (data.getType()) {
			case Constants.Type.LOCATION:
				double latitude = Double.parseDouble(info[0]);
				double longitude = Double.parseDouble(info[1]);
				long geoFence = LocationGrid.toGridNumber(latitude, longitude);

				Location location = saveLocation(latitude, longitude, geoFence);
				Iterable<Person> friends = bind(userId, location);

				return friends;
			// event.setLatitude(latitude);
			// event.setLongitude(longitude);
			// event.setGeoFence(geoFence);
			// event.setTime(data.getTime());
			// break;

			// case Constants.Type.AVAILABLE_WIFI:
			// List<String> wifiNetworks = Arrays.asList(info);
			// event.setWifiNetworks(wifiNetworks);

			case Constants.Type.CONTACTS:
				String[] numbers = data.getData();
				addFriend(userId, numbers);
			}
		}
		return new ArrayList<>();
		// System.out.println("@" + event);
		// processor.addEvent(event);
	}

	private Location saveLocation(double latitude, double longitude, long geoFence) {
		LocationService service = new LocationService();
		Location location = service.findUsingGeoFence(geoFence);
		if (location == null) {
			// This location is not in the database
			location = new Location();
			location.setGeoFence(geoFence);
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			service.createOrUpdate(location);
		}

		return location;
	}

	private Iterable<Person> bind(String userId, Location location) {
		PersonService service = new PersonService();
		Person person = service.find(userId);
		if (person != null) {
			person.setCurrentLocation(location);
		}
		Iterable<Person> friends = service.nearByFriends(userId);
		return friends;
	}

	private void addFriend(String userId, String[] numbers) {
		PersonService service = new PersonService();
		Person person = service.find(userId);
		if (person != null) {
			// Add all friends to the person
			for (String number : numbers) {
				try {
					String friendId = Utility.formatPhoneNumber(number);
					Person friend = service.find(friendId);
					if (friend != null) {
						person.getFriends().add(friend);
					}
				} catch (NumberParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			service.createOrUpdate(person);
		}
	}
}
