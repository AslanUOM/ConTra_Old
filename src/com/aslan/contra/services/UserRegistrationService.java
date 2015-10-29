package com.aslan.contra.services;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@Path("/register")
public class UserRegistrationService {
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserRegistrationService.class);

	@POST
	@Path("/user")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response registerUser(@FormParam("name") String name, @FormParam("country") String country,
			@FormParam("phone") String phone, @FormParam("deviceName") String deviceName,
			@FormParam("deviceSerial") String deviceSerial, @FormParam("deviceToken") String deviceToken) {

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
				person.setName(name);
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
}
