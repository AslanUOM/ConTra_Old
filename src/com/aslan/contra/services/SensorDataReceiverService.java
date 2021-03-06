package com.aslan.contra.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.aslan.contra.cep.CEPProcessor;
import com.aslan.contra.model.SensorResponse;

@Path("/sensordatareceiver")
public class SensorDataReceiverService {
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public Response save(SensorResponse response) {
		// Get the CEPProcessor
		CEPProcessor processor = CEPProcessor.getInstance();
		// Add the event to the processor
		processor.addEvent(response);
		// Return a successful response
		return Response.status(201).entity("Accepted").build();
	}
}
