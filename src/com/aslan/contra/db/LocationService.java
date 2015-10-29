package com.aslan.contra.db;

import com.aslan.contra.entities.Location;

public class LocationService extends GenericService<Location> {

	@Override
	public Class<Location> getEntityType() {
		return Location.class;
	}

}
