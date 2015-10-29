package com.aslan.contra.db;

import com.aslan.contra.entities.Device;

public class DeviceService extends GenericService<Device>{

	@Override
	public Class<Device> getEntityType() {
		return Device.class;
	}

}
