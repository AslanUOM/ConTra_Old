package com.aslan.contra.cep.query;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aslan.contra.util.TimeUtility;

public class LocationIdentificationContext extends Context {
    /**
     * Logger to log the events.
     */
    private static final Logger LOGGER = Logger
            .getLogger(LocationIdentificationContext.class);

    /**
     * External batch time window interval in hours.
     */
    private final long BATCH_TIME = 24;

    /**
     * Wait until this time if there are no changes in GEO location.<br>
     * Time in milliseconds.
     */
    private final long MAX_TIME_INTERVAL = 7200000;

    /**
     * Construct a LocationIdentificationQuery.
     * 
     * @param sourceStream
     */
    public LocationIdentificationContext(String inputStream) {
        super(inputStream);
        buildQuery();
    }

    /**
     * Generate queries for location identification.
     */
    private void buildQuery() {
        String smt1 = String
                .format("insert into LocationEvent select * from %s.std:groupwin(userID).win:ext_timed_batch(time, %d hours)",
                        INPUT_STREAM, BATCH_TIME);
        String smt2 = String
                .format("select beginevent.userID as userID, beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime, beginevent.wifiNetworks as wifiNetworks"
                        + " from pattern [every (beginevent=LocationEvent -> middleevent=LocationEvent(geoFence=beginevent.geoFence AND (time - beginevent.time < %d))"
                        + " until endevent=LocationEvent(geoFence!=beginevent.geoFence OR (time - beginevent.time >= %d)))] group by beginevent.userID",
                        MAX_TIME_INTERVAL, MAX_TIME_INTERVAL);

        super.add(smt1, OnUpdateListener.NULL_OBJECT);
        super.add(smt2, new OnUpdateListener() {

            @Override
            public void onUpdate(Map<String, Object> properties) {
                String userID = (String) properties.get("userID");
                Long geoFence = (Long) properties.get("geoFence");

                Calendar start = Calendar.getInstance();
                start.setTimeInMillis((Long) properties.get("beginTime"));

                Calendar end = Calendar.getInstance();
                end.setTimeInMillis((Long) properties.get("endTime"));

                // System.out.println(bean.get("wifiNetworks"));
                @SuppressWarnings("unchecked")
                List<String> wifiNetworks = (List<String>) properties
                        .get("wifiNetworks");

                long interval = (end.getTimeInMillis() - start
                        .getTimeInMillis()) / 1000 / 60;

                String status = "NA";
                if (interval >= 5) {
                    if (TimeUtility.isDayTime(start)) {
                        // Work
                        status = "In work";
                    } else {
                        // Home
                        status = "In Home";
                    }
                }
                // Print the information
                LOGGER.info("User ID: " + userID);
                LOGGER.info("GEO Fence: " + geoFence);
                LOGGER.info("From: " + start.getTime());
                LOGGER.info("To: " + end.getTime());
                LOGGER.info("Interval: " + interval + " mins");
                LOGGER.info("Status: " + status);
                LOGGER.info("WIFI: " + wifiNetworks);
            }
        });
    }

    @Override
    public Type getType() {
        return Type.LOCATION;
    }
}
