package com.aslan.contra.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSensorDataReceiverService {

	@Test
	public void registerPerson() {

	}

	@Test
	public void sendContacts() {
		SensorResponse sensorResponse = new SensorResponse();
		sensorResponse.setDeviceID("bc28793448172e9c");
		sensorResponse.setUserID("+94770780210");

		SensorData data = new SensorData();
		data.setType(Constants.Type.CONTACTS);
		data.setData(new String[] { "0778709767", "758266336", "+94771199331" });

		sensorResponse.addSensorData(data);

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080/ConTra/sensordatareceiver/save");
		Response response = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.<SensorResponse> json(sensorResponse));

		assertEquals("Failed to update the contacts", 201, response.getStatus(), 0);
	}

	@Test
	public void sendLocation() {
		try {
			SensorResponse sensorResponse = new SensorResponse();
			sensorResponse.setDeviceID("bc28793448172e9c");
			sensorResponse.setUserID("+94770780210");

			SensorData data = new SensorData();
			data.setType(Constants.Type.LOCATION);
			data.setData(new String[] { "6.8321335", "79.7507096" });

			sensorResponse.addSensorData(data);

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://localhost:8080/ConTra/sensordatareceiver/save");
			List response = target.request(MediaType.APPLICATION_JSON)
					.post(Entity.<SensorResponse> json(sensorResponse), List.class);

			assertTrue("No one in the same location of +94770780210", response.contains("+94778709767"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
