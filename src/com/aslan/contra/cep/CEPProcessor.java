package com.aslan.contra.cep;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.aslan.contra.model.LocationEvent;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.LocationGrid;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

/**
 * This class receives the events and process them using CEP engine.
 * 
 * @author gobinath
 *
 */
public class CEPProcessor {
	/**
	 * ESPER CEP Configuration.
	 */
	private static final Configuration CONFIGURATION = new Configuration();

	/**
	 * ESPER Service provider.
	 */
	private static final EPServiceProvider SERVICE_PROVIDER;

	/**
	 * ESPER CEP runtime engine.
	 */
	private static final EPRuntime CEP_RUNTIME;

	/**
	 * ESPER administrator.
	 */
	private static final EPAdministrator ADMIN;

	/**
	 * Singleton instance of CEPProcessor.
	 */
	private static CEPProcessor instance;

	static {
		CONFIGURATION.addEventType("Origin", LocationEvent.class.getName());
		SERVICE_PROVIDER = EPServiceProviderManager.getProvider("CoreEngine",
				CONFIGURATION);
		CEP_RUNTIME = SERVICE_PROVIDER.getEPRuntime();
		ADMIN = SERVICE_PROVIDER.getEPAdministrator();
	}

	/**
	 * Private constructor to enforce singleton behavior.
	 */
	private CEPProcessor() {
		// Query definition
		ADMIN.createEPL("insert into LocationEvent select * from Origin.std:groupwin(userID).win:ext_timed_batch(time, 24 hours)");
		EPStatement statement = ADMIN
				.createEPL("select beginevent.userID as userID, beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime, beginevent.wifiNetworks as wifiNetworks"
						+ " from pattern [every (beginevent=LocationEvent -> middleevent=LocationEvent(geoFence=beginevent.geoFence AND (time - beginevent.time < 7200000))"
						+ " until endevent=LocationEvent(geoFence!=beginevent.geoFence OR (time - beginevent.time >= 7200000)))] group by beginevent.userID");

		// Add listener
		// s.addListener(new UpdateListener() {
		// @Override
		// public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// for (EventBean e : newEvents) {
		// System.out.println(e);
		// }
		// }
		// });

		statement.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				for (EventBean e : newEvents) {
					onLocationFound((MapEventBean) e);
				}
			}
		});

	}

	/**
	 * Add a SensorResponse to the CEP stream.
	 * 
	 * @param response
	 */
	public void addEvent(SensorResponse response) {
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
		System.out.println(event);
		CEP_RUNTIME.sendEvent(event);
	}

	public void onLocationFound(MapEventBean bean) {
		String userID = (String) bean.get("userID");
		Long geoFence = (Long) bean.get("geoFence");

		Calendar start = Calendar.getInstance();
		start.setTimeInMillis((Long) bean.get("beginTime"));

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis((Long) bean.get("endTime"));

		// System.out.println(bean.get("wifiNetworks"));
		@SuppressWarnings("unchecked")
		List<String> wifiNetworks = (List<String>) bean.get("wifiNetworks");

		long interval = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60;

		String status = "NA";
		if (interval >= 5) {
			if (isDayTime(start)) {
				// Work
				status = "In work";
			} else {
				// Home
				status = "In Home";
			}
		}

		System.out.println("User ID: " + userID);
		System.out.println("GEO Fence: " + geoFence);
		System.out.println("From: " + start.getTime());
		System.out.println("To: " + end.getTime());
		System.out.println("Interval: " + interval + " mins");
		System.out.println("Status: " + status);
		System.out.println("WIFI: " + wifiNetworks);
		System.out.println();
	}

	private static boolean isDayTime(Calendar startCalendar) {
		final int MORNING = 6;
		final int EVENING = 16;

		int start = startCalendar.get(Calendar.HOUR_OF_DAY);

		if (start >= MORNING && start <= EVENING) { // Day time
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Singleton factory method to return a singleton object of CEPProcessor.
	 * 
	 * @return a singleton instance of CEPProcessor
	 */
	public static CEPProcessor getInstance() {
		if (instance == null) {
			synchronized (CEPProcessor.class) {
				if (instance == null) {
					instance = new CEPProcessor();
				}
			}
		}

		return instance;
	}

}
