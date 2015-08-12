package com.aslan.contra.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This service class will be accessed by end user library to retrieve the
 * context through a pull request.
 * 
 * @author gobinath
 *
 */
@Path("/contextprovider")
public class ContextProviderService {

	@GET
	@Path("/current/{type}")
	public Response getCurrentContext(@PathParam("type") String type) {
		// Currently it does nothing
		return Response.status(200).entity("context").build();
	}
}
