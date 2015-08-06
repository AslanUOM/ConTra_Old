package com.aslan.contra.cep;

import java.util.Calendar;
import java.util.Date;
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
		Configuration configuration = new Configuration();
		configuration.addEventType("Origin", LocationEvent.class.getName());

		EPServiceProvider serviceProvider = EPServiceProviderManager
				.getProvider("CoreEngine", configuration);
		EPRuntime cepRuntime = serviceProvider.getEPRuntime();

		EPAdministrator admin = serviceProvider.getEPAdministrator();

		EPStatement s2 =admin.createEPL("insert into LocationEvent select * from Origin.std:groupwin(userID).win:ext_timed_batch(time, 10 minutes)");
		EPStatement statement = admin
				.createEPL("select beginevent.userID as userID, beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime, beginevent.wifiNetworks as wifiNetworks"
						+ " from pattern [every (beginevent=LocationEvent -> middleevent=LocationEvent(geoFence=beginevent.geoFence AND (time - beginevent.time < 420000))"
						+ " until endevent=LocationEvent(geoFence!=beginevent.geoFence OR (time - beginevent.time >= 420000)))] group by beginevent.userID");

		s2.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				for (EventBean e : newEvents) {
					System.out.println(e);
				}
				System.out
						.println("--------------------------------------------");
			}
		});
		
		statement.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				for (EventBean e : newEvents) {
					System.out.println(e);
					try {
						MapEventBean bean = (MapEventBean) e;
						print(bean);
						System.out.println();
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
				}

				// for (EventBean e : oldEvents) {
				// MapEventBean bean = (MapEventBean) e;
				// print(bean);
				// System.out.println();
				// }
				System.out
						.println("--------------------------------------------");
			}

			public void print(MapEventBean bean) {
				String userID = (String) bean.get("userID");
				Long geoFence = (Long) bean.get("geoFence");

				Calendar start = Calendar.getInstance();
				start.setTimeInMillis((Long) bean.get("beginTime"));

				Calendar end = Calendar.getInstance();
				end.setTimeInMillis((Long) bean.get("endTime"));

				long interval = (end.getTimeInMillis() - start
						.getTimeInMillis()) / 1000 / 60;

				// System.out.println(bean.get("wifiNetworks"));
				@SuppressWarnings("unchecked")
				List<String> wifiNetworks = (List<String>) bean
						.get("wifiNetworks");

				Calendar c = Calendar.getInstance();
				int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

				if (timeOfDay >= 6 && timeOfDay <= 18) { // Work
					System.out.println("In Work");
				} else { // Home
					System.out.println("In Home");
				}
				System.out.println("User ID: " + userID);
				System.out.println("GEO Fence: " + geoFence);
				System.out.println("From: " + start.getTime());
				System.out.println("To: " + end.getTime());
				System.out.println("Interval: " + interval + " mins");
				System.out.println(wifiNetworks);
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
			generateData(cepRuntime, 6.87809544, 79.8575395);
			Thread.sleep(500);
			generateData(cepRuntime, 6.878472, 79.8573639);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87824868, 79.85762096);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87825261, 79.85742666);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87814489, 79.8577096);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87813571, 79.85746346);
			Thread.sleep(500);
			generateData(cepRuntime, 6.8783003, 79.8576553);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87812017, 75.857593);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87822805, 79.85757823);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			Thread.sleep(500);
			generateData(cepRuntime, 6.87811312, 79.85744618);
			// Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Total: " + c);

	}

	static int count = 20;
	static int c = 0;

	public static void generateData(EPRuntime cepRT, double lat, double lon) {
		int levelOfDetail = 100;
		LocationEvent event = new LocationEvent();
		event.setAccuracy(99.00);
		event.setDeviceID("D001");
		event.setLatitude(lat);
		event.setLongitude(lon);
		event.setSource("gps");
		event.setTime(CURRENT_TIME - (60000 * count--));
		if (count % 2 == 0) {
			event.setUserID("U001");
		} else {
			event.setUserID("U002");
		}
		event.addAll("UOMWireless", "CSE Smart");
		event.setGeoFence(LocationGrid.toGridNumber(lat, lon, levelOfDetail));
		System.out.println("Insert: " + event.getGeoFence() + "\t"
				+ event.getUserID() + "\t@ "
				+ new Date(event.getTime()).toString());
		cepRT.sendEvent(event);
		c++;
	}
}