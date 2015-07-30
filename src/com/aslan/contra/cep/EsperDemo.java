package com.aslan.contra.cep;

import java.util.Calendar;
import java.util.List;

import com.aslan.contra.model.LocationEvent;
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

public class EsperDemo {
	private static final long CURRENT_TIME = System.currentTimeMillis();

	public static void main(String[] args) {

		// The Configuration is meant only as an initialization-time object.
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("LocationEvent", LocationEvent.class.getName());
		EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		EPRuntime cepRT = cep.getEPRuntime();

		EPAdministrator admin = cep.getEPAdministrator();

		EPStatement smt = admin.createEPL(
				"select beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime, middleevent[0].wifiNetworks as wifiNetworks from pattern ["
						+ "beginevent=LocationEvent" + "-> middleevent=LocationEvent(geoFence=beginevent.geoFence)"
						+ "until endevent=LocationEvent(geoFence!=beginevent.geoFence) where timer:within(10 second)]");

		smt.addListener(new UpdateListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(EventBean[] newEvents, EventBean[] arg1) {
				MapEventBean bean = (MapEventBean) newEvents[0];

				Integer geoFence = (Integer) bean.get("geoFence");

				Calendar start = Calendar.getInstance();
				start.setTimeInMillis((Long) bean.get("beginTime"));

				Calendar end = Calendar.getInstance();
				end.setTimeInMillis((Long) bean.get("endTime"));

				// System.out.println(bean.get("wifiNetworks"));
				List<String> wifiNetworks = (List<String>) bean.get("wifiNetworks");

				Calendar c = Calendar.getInstance();
				int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

				if (timeOfDay >= 6 && timeOfDay <= 18) {
					// Work
				} else {
					// Home
				}

				System.out.println("GEO Fence: " + geoFence);
				System.out.println("From: " + start);
				System.out.println("To: " + end);
				System.out.println(wifiNetworks);
				System.out.println();
			}
		});

		// We generate a few ticks...

		// generateData(cepRT, 6.87809544, 79.8575395);
		// generateData(cepRT, 6.878472, 79.8573639);
		// generateData(cepRT, 6.87824868, 79.85762096);
		// generateData(cepRT, 6.87825261, 79.85742666);
		// generateData(cepRT, 6.87814489, 79.8577096);
		// generateData(cepRT, 6.87813571, 79.85746346);
		// generateData(cepRT, 6.8783003, 79.8576553);
		// generateData(cepRT, 6.87812017, 75.857593);
		// generateData(cepRT, 6.87822805, 79.85757823);
		// generateData(cepRT, 6.87811312, 79.85744618);

		try {
			generateData(cepRT, 6.87809544, 79.8575395);
			Thread.sleep(500);
			generateData(cepRT, 6.878472, 79.8573639);
			Thread.sleep(500);
			generateData(cepRT, 6.87824868, 79.85762096);
			Thread.sleep(500);
			generateData(cepRT, 6.87825261, 79.85742666);
			Thread.sleep(1000);
			generateData(cepRT, 6.87814489, 79.8577096);
			Thread.sleep(500);
			generateData(cepRT, 6.87813571, 79.85746346);
			Thread.sleep(500);
			generateData(cepRT, 6.8783003, 79.8576553);
			Thread.sleep(500);
			generateData(cepRT, 6.87812017, 75.857593);
			Thread.sleep(500);
			generateData(cepRT, 6.87822805, 79.85757823);
			Thread.sleep(500);
			generateData(cepRT, 6.87811312, 79.85744618);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	static int count = 20;

	public static void generateData(EPRuntime cepRT, double lat, double lon) {
		int levelOfDetail = 100;
		LocationEvent event = new LocationEvent();
		event.setAccuracy(99.00);
		event.setDeviceID("D001");
		event.setLatitude(lat);
		event.setLongitude(lon);
		event.setSource("gps");
		event.setTime(CURRENT_TIME - (60000 * count--));
		event.setUserID("U001");
		event.addAll("UOMWireless", "CSE Smart");
		event.setGeoFence(LocationGrid.toGridNumber(lat, lon, levelOfDetail));
		System.out.println("Insert: " + event.getGeoFence());
		cepRT.sendEvent(event);
	}
}