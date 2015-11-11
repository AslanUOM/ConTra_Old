package com.aslan.contra.services;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Person;

/**
 * This service class will be accessed by end user library to retrieve the
 * context through a pull request.
 * 
 * @author gobinath
 *
 */
@Path("/contextprovider")
public class ContextProviderService {
	private static final Logger LOGGER = Logger.getLogger(ContextProviderService.class);

	@GET
	@Path("/nearbyfriends/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentContext(@NotNull @PathParam("userId") String userId) {
		LOGGER.debug("Request for near by friends from: " + userId);
		// String[] nearByFriends = new String[0];

		// Create database service
		PersonService personService = new PersonService();
		Iterable<Person> friends = personService.nearByFriends(userId);

		// List<String> phoneNumbers = new ArrayList<>();
		// friends.forEach(x -> phoneNumbers.add(x.getPhoneNumber()));
		// nearByFriends = phoneNumbers.toArray(nearByFriends);
		// Return all the near by friends
		Response response = Response.status(201).entity(friends).build();
		return response;

	}
}
