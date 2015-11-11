package com.aslan.contra.cep.query;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aslan.contra.db.LocationService;
import com.aslan.contra.db.PersonService;
import com.aslan.contra.entities.Home;
import com.aslan.contra.entities.Location;
import com.aslan.contra.entities.Person;
import com.aslan.contra.entities.Work;
import com.aslan.contra.util.TimeUtility;

public class LocationIdentificationContext extends Context {
	boolean sent = false;
	/**
	 * Logger to log the events.
	 */
	private static final Logger LOGGER = Logger.getLogger(LocationIdentificationContext.class);

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
		String smt1 = String.format(
				"insert into LocationEvent select * from %s.std:groupwin(userID).win:ext_timed_batch(time, %d hours)",
				INPUT_STREAM, BATCH_TIME);
		// String smt2 = String.format(
		// "select beginevent.userID as userID, beginevent.geoFence as geoFence,
		// beginevent.time as beginTime, endevent.time as endTime,
		// beginevent.wifiNetworks as wifiNetworks"
		// + " from pattern [every (beginevent=LocationEvent ->
		// middleevent=LocationEvent(geoFence=beginevent.geoFence AND (time -
		// beginevent.time < %d))"
		// + " until endevent=LocationEvent(geoFence!=beginevent.geoFence OR
		// (time - beginevent.time >= %d)))] group by beginevent.userID",
		// MAX_TIME_INTERVAL, MAX_TIME_INTERVAL);
		String smt2 = String.format(
				"select beginevent.userID as userID, beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime"
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
				// @SuppressWarnings("unchecked")
				// List<String> wifiNetworks = (List<String>)
				// properties.get("wifiNetworks");

				long interval = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60;

				LOGGER.info("CEP notification: " + userID + " @ " + geoFence + " Interval: " + interval);
				// String status = "NA";
				if (interval >= 5) {
					if (TimeUtility.isDayTime(start)) {
						// Work
						// status = "Work";
						updateWork(userID, geoFence);
						LOGGER.info("Work location of " + userID + " is updated.");

					} else {
						// Home
						// status = "Home";
						updateHome(userID, geoFence);
						LOGGER.info("Home location of " + userID + " is updated.");
					}
				}
				// Print the information
				// LOGGER.info("User ID: " + userID);
				// LOGGER.info("GEO Fence: " + geoFence);
				// LOGGER.info("From: " + start.getTime());
				// LOGGER.info("To: " + end.getTime());
				// LOGGER.info("Interval: " + interval + " mins");
				// LOGGER.info("Status: " + status);
				// LOGGER.info("WIFI: " + wifiNetworks);
			}
		});
	}

	/**
	 * Add the information to the database.
	 * 
	 * @param userId
	 * @param geoFence
	 */
	private void updateHome(String userId, long geoFence) {
		PersonService personService = new PersonService();
		LocationService locationService = new LocationService();

		Person person = personService.find(userId);
		Location location = locationService.findUsingGeoFence(geoFence);

		LOGGER.info("Person: " + person);
		LOGGER.info("Location: " + location);

		if (person != null && location != null) {
			Home home = personService.home(userId);

			if (home == null) {
				home = new Home();
				home.setPerson(person);
				home.setFrom(new Date());
				home.setLocation(location);
				home.setConfidence(1);

				person.setHome(home);
			} else {
				Location existingLocation = home.getLocation();
				if (existingLocation.getGeoFence() == geoFence) {
					// Increase the confidence
					int confidence = home.getConfidence() + 1;
					if (confidence > 100) {
						confidence = 100;
					}

					home.setConfidence(confidence);
				} else {
					// Different home
					// Reduce the confidence
					int confidence = home.getConfidence() - 1;
					if (confidence < 0) {
						// Already confidence in 0. Update this location
						home.setPerson(person);
						home.setLocation(location);
						home.setConfidence(1);
						home.setFrom(new Date());
					} else {
						home.setConfidence(confidence);
					}
				}
			}

			personService.createOrUpdate(person);
		}
	}

	/**
	 * Add the information to the database.
	 * 
	 * @param userId
	 * @param geoFence
	 */
	private void updateWork(String userId, long geoFence) {
		PersonService personService = new PersonService();
		LocationService locationService = new LocationService();

		Person person = personService.find(userId);
		Location location = locationService.findUsingGeoFence(geoFence);

		LOGGER.info("Person: " + person);
		LOGGER.info("Location: " + location);

		if (person != null && location != null) {
			Work work = personService.work(userId);

			if (work == null) {
				work = new Work();
				work.setPerson(person);
				work.setFrom(new Date());
				work.setLocation(location);
				work.setConfidence(1);

				person.setWork(work);
			} else {
				Location existingLocation = work.getLocation();
				if (existingLocation.getGeoFence() == geoFence) {
					// Increase the confidence
					int confidence = work.getConfidence() + 1;
					if (confidence > 100) {
						confidence = 100;
					}

					work.setConfidence(confidence);
				} else {
					// Different work location
					// Reduce the confidence
					int confidence = work.getConfidence() - 1;
					if (confidence < 0) {
						// Already confidence in 0. Update this location
						work.setLocation(location);
						work.setConfidence(1);
						work.setFrom(new Date());
					} else {
						work.setConfidence(confidence);
					}
				}
			}

			personService.createOrUpdate(person);
		}
	}

	@Override
	public Type getType() {
		return Type.LOCATION;
	}
}
