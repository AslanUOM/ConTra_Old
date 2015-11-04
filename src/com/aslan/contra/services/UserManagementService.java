package com.aslan.contra.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.aslan.contra.db.DeviceService;
import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Device;
import com.aslan.contra.entities.Person;
import com.aslan.contra.util.Utility;
import com.google.i18n.phonenumbers.NumberParseException;

/**
 * This service class is used to create/register a new user along with his/her
 * 
 * @author gobinath
 *
 */
@Path("/user")
public class UserManagementService {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserManagementService.class);

	/**
	 * This service method receives the minimal user information and create a
	 * new Person and Device object using those information. If the Person
	 * already exists (the same phone number), the device information will be
	 * either updated or added if not exists.
	 * 
	 * @param country
	 *            the code representing country. For example, SriLanka: LK. (Not
	 *            case sensitive).
	 * @param phone
	 *            the phone number of the user. It can include the country code
	 *            or not.
	 * @param deviceName
	 *            name of the user's mobile device. It is used to give a
	 *            readable name for the device.
	 * @param deviceSerial
	 *            a unique key to identify the device.
	 * @param deviceToken
	 *            the Google push notification token. It will be used later to
	 *            send push notifications to the device.
	 * @return the userID (Formatted phone number) of the person.
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response registerUser(@FormParam("country") String country, @FormParam("phone") String phone,
			@FormParam("deviceName") String deviceName, @FormParam("deviceSerial") String deviceSerial,
			@FormParam("deviceToken") String deviceToken) {

		// Utility.formatPhoneNumber countryCode must be in upper case
		country = country.toUpperCase();

		// Start with a default Response
		Response response = Response.status(500).entity("Unknown error.").build();

		// Format the phone number to international format
		String formattedPhoneNumber = null;
		try {
			// Format the phone number
			formattedPhoneNumber = Utility.formatPhoneNumber(country, phone);
		} catch (NumberParseException e) {
			// Invalid phone number
			response = Response.status(406).entity("Invalid phone number: " + phone).build();
			LOGGER.error("Invalid phone number", e);
		}

		if (formattedPhoneNumber != null) {
			// TODO: Validate the phone number by sending an SMS

			// If the phone number is valid
			PersonService personService = new PersonService();
			Person person = personService.find(formattedPhoneNumber);
			if (person == null) {
				// New registration
				person = new Person();
				person.setPhoneNumber(formattedPhoneNumber);

				// Create a new device
				Device device = new Device();
				device.setName(deviceName);
				device.setToken(deviceToken);
				device.setSerial(deviceSerial);
				device.setActive(true);
				device.setLastSeen(new Date());

				person.getDevice().add(device);

				// Save the person
				personService.createOrUpdate(person);

				// Return the formattedPhoneNumber for the reference of the
				// device
				response = Response.status(201).entity(formattedPhoneNumber).build();
			} else {
				// Given phone number is already registered - Login

				DeviceService deviceService = new DeviceService();
				Device device = deviceService.find(deviceSerial);

				if (device == null) {
					// Create a new device
					device = new Device();
					device.setName(deviceName);
					device.setToken(deviceToken);
					device.setActive(true);
					device.setLastSeen(new Date());

					// Add the device
					person.getDevice().add(device);
					// Update the person
					personService.createOrUpdate(person);
				} else {
					// Update the device
					device.setActive(true);
					device.setName(deviceName);
					device.setToken(deviceToken);
					device.setLastSeen(new Date());

					// Update the device
					deviceService.createOrUpdate(device);
				}

				response = Response.status(201).entity(formattedPhoneNumber).build();
			}

		}
		return response;
	}

	/**
	 * This method is used to update the profile information of a Person.
	 * 
	 * @param userId
	 *            the formatted phone number of the user.
	 * @param name
	 *            first name of the user.
	 * @param email
	 *            email address of the user.
	 * @return a text explaining the result. Do not use it to check the result.
	 *         Instead use the status code to check the success or failure of
	 *         the request.
	 */
	@POST
	@Path("/updateprofile")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateProfile(@FormParam("userId") String userId, @FormParam("name") String name,
			@FormParam("email") String email) {

		// Start with a default Response
		Response response = Response.status(500).entity("Unknown error.").build();

		PersonService personService = new PersonService();
		Person person = personService.find(userId);

		if (person != null) {
			person.setName(name);
			person.setEmail(email);
			// Save the person
			personService.createOrUpdate(person);
			response = Response.status(201).entity("Profile is updated successfully").build();
		} else {
			response = Response.status(406).entity("Invalid user id").build();
		}

		return response;
	}

	/**
	 * This method returns a java.util.Map<String, String> which contains the
	 * properties of Person. Currently it returns 'name' and 'email'. If the
	 * given userID is not valid, an empty map will be returned.
	 * 
	 * @param userId
	 *            the formatted phone number of the user.
	 * @return the attributes of user as a Map.
	 */
	@GET
	@Path("/profile/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@PathParam("userId") String userId) {
		// Create an empty Map
		Map<String, String> result = new HashMap<>();

		// Retrieve the person
		PersonService personService = new PersonService();
		Person person = personService.find(userId);
		Response response;
		// Create the response
		if (person != null) {
			result.put("name", person.getName());
			result.put("email", person.getEmail());
			response = Response.status(201).entity(result).build();
		} else {
			response = Response.status(404).entity(result).build();
		}
		return response;
	}
}