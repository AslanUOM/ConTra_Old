package com.aslan.contra.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/contextprovider")
public class ContextProviderService {

	@GET
	@Path("/current/{type}")
	public Response getCurrentContext(@PathParam("type") String type) {
		return Response.status(200).entity("context").build();
	}
}
