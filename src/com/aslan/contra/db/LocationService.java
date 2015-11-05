package com.aslan.contra.db;

import java.util.HashMap;
import java.util.Map;

import com.aslan.contra.entities.Location;

public class LocationService extends GenericService<Location> {

	@Override
	public Class<Location> getEntityType() {
		return Location.class;
	}

	/**
	 * Find the location using geo_fence value.
	 * 
	 * @param geoFence
	 * @return
	 */
	public Location findUsingGeoFence(long geoFence) {
		Map<String, Long> parameters = new HashMap<>();
		parameters.put("geo_fence", geoFence);
		String cypher = "MATCH (location:Location) WHERE location.geoFence = {geo_fence} RETURN location";
		Location location = session.queryForObject(getEntityType(), cypher, parameters);
		return location;
	}
}
