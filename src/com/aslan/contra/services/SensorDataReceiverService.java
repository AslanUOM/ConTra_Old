package com.aslan.contra.services;

import java.util.Arrays;
import java.util.List;

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
import com.aslan.contra.model.LocationEvent;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.LocationGrid;

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
    @Produces(MediaType.TEXT_HTML)
    public Response save(SensorResponse response) {

        LocationEvent event = new LocationEvent();
        event.setUserID(response.getUserID());
        event.setDeviceID(response.getDeviceID());

        for (SensorData data : response) {
            String[] info = data.getData();
            switch (data.getType()) {
            case Constants.Type.LOCATION:
                double latitude = Double.parseDouble(info[0]);
                double longitude = Double.parseDouble(info[1]);
                long geoFence = LocationGrid.toGridNumber(latitude, longitude);

                event.setLatitude(latitude);
                event.setLongitude(longitude);
                event.setGeoFence(geoFence);
                event.setTime(data.getTime());
                break;

            case Constants.Type.AVAILABLE_WIFI:
                List<String> wifiNetworks = Arrays.asList(info);
                event.setWifiNetworks(wifiNetworks);
            }
        }
        System.out.println("@" + event);
        processor.addEvent(event);

        // Return a successful response
        return Response.status(201).entity("Accepted").build();
    }
}
