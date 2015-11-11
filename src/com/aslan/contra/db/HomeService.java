package com.aslan.contra.db;

import com.aslan.contra.entities.Home;

public class HomeService extends GenericService<Home> {

	@Override
	public Class<Home> getEntityType() {
		return Home.class;
	}

}
