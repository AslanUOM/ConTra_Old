package com.aslan.contra.cep.query;

import java.util.Map;

public interface OnUpdateListener {

	public static final OnUpdateListener NULL_OBJECT = new OnUpdateListener() {
		@Override
		public void onUpdate(Map<String, Object> properties) {

		}
	};

	public abstract void onUpdate(Map<String, Object> properties);
}
