package com.aslan.contra.db;

import java.util.HashMap;
import java.util.Map;

import com.aslan.contra.entities.Location;

public class LocationService extends GenericService<Location> {

	@Override
	public Class<Location> getEntityType() {
		return Location.class;
	}

	public Location findUsingGeoFence(long geoFence) {
		Map<String, Long> parameters = new HashMap<>();
		parameters.put("geo_fence", geoFence);
		String cypher = "MATCH (location:Location) WHERE location.geoFence = {geo_fence} RETURN person";
		Location location = session.queryForObject(getEntityType(), cypher, parameters);
		return location;
	}
}
