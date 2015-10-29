package com.aslan.contra.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/register")
public class UserRegistrationService {
	@POST
	@Path("/user")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response registerUser(@FormParam("name") String name, @FormParam("phone") String phoneNumber) {
		
		// Return a successful response
		return Response.status(201).entity("Accepted").build();
	}
}
