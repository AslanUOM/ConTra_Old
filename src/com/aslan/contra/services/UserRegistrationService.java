package com.aslan.contra.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.aslan.contra.cep.CEPProcessor;
import com.aslan.contra.db.PersonService;
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
			@FormParam("phone") String phone) {
		// Start with a default Response
		Response response = Response.status(500).entity("Unknown error.").build();

		String formattedPhoneNumber = null;
		try {
			// Format the phone number
			formattedPhoneNumber = Utility.formatPhoneNumber(country, phone);
		} catch (NumberParseException e) {
			response = Response.status(406).entity("Invalid phone number: " + phone).build();
			LOGGER.error("Invalid phone number", e);
		}

		if (formattedPhoneNumber != null) {
			PersonService service = new PersonService();
			Person person = service.find(formattedPhoneNumber);
			if (person == null) {
				// TODO: Validate the phone number by sending an SMS
				person = new Person();
				person.setName(name);
				person.setPhoneNumber(formattedPhoneNumber);

				// Save the person
				service.createOrUpdate(person);
				response = Response.status(201).entity("User is registered successfully").build();
			} else {
				// Given phone number is already registered
				response = Response.status(406).entity("Phone number is already taken: " + formattedPhoneNumber)
						.build();
			}

		}
		return response;
	}
}
