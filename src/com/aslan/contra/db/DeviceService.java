package com.aslan.contra.db;

import java.util.HashMap;
import java.util.Map;

import com.aslan.contra.entities.Device;

public class DeviceService extends GenericService<Device> {

	@Override
	public Class<Device> getEntityType() {
		return Device.class;
	}

	public Iterable<Device> devices(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		String cypher = "MATCH (person:Person)-[:OWNS]->(devices) WHERE person.phoneNumber = {phone_number} RETURN devices";
		Iterable<Device> friends = session.query(getEntityType(), cypher, parameters);
		return friends;
	}

	public Device find(String serial) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("serial", serial);
		String cypher = "MATCH (device:Device) WHERE device.serial = {serial} RETURN device";
		Device device = session.queryForObject(getEntityType(), cypher, parameters);
		return device;
	}
}
