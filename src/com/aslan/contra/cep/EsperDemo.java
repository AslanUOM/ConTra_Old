package com.aslan.contra.cep;

import java.util.Date;

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

public class EsperDemo {
	private static final long CURRENT_TIME = System.currentTimeMillis();

	public static class CEPListener implements UpdateListener {

		public void update(EventBean[] newData, EventBean[] oldData) {
			for (EventBean eb : newData) {
				LocationEvent event = (LocationEvent) eb.getUnderlying();
				System.out.println("Event received: " + event + "\t@: " + new Date(event.getTime()).toString());
			}

			System.out.println();
		}
	}

	public static void main(String[] args) {

		// The Configuration is meant only as an initialization-time object.
		Configuration cepConfig = new Configuration();
		cepConfig.addEventType("LocationEvent", LocationEvent.class.getName());
		EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		EPRuntime cepRT = cep.getEPRuntime();

		EPAdministrator admin = cep.getEPAdministrator();
		EPStatement pattern = admin.createPattern(
				"every A=LocationEvent -> B=LocationEvent(geoFence=A.geoFence) where timer:within (100)");
				// EPStatement cepStatement = admin.createEPL("select * from "
				// + "StockTick(symbol='AAPL').win:ext_timed_batch(timeStamp, 2 second) " + "having avg(price) > 2.0");

		// cepStatement.addListener(new CEPListener());
		pattern.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] arg1) {
				// for (EventBean eb : newEvents) {
				// LocationEvent event = (LocationEvent) eb.getUnderlying();
				// System.out.println("Event received: " + event + "\t@: " + new
				// Date(event.getTime()).toString());
				// }
				//
				// System.out.println();
				LocationEvent spike = (LocationEvent) newEvents[0].get("A");
				LocationEvent error = (LocationEvent) newEvents[0].get("B");
				System.out.println("A:" + spike);
				System.out.println("B: " + error);
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
		// generateData(cepRT, 6.87812017, 79.857593);
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
			generateData(cepRT, 6.87812017, 79.857593);
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
		event.setGeoFence(LocationGrid.toGridNumber(lat, lon, levelOfDetail));

		cepRT.sendEvent(event);
	}
}