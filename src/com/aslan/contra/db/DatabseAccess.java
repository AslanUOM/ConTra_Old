package com.aslan.contra.db;

import java.io.IOException;
import java.util.Date;

import com.aslan.contra.entities.Device;
import com.aslan.contra.entities.Location;
import com.aslan.contra.entities.Person;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class DatabseAccess {
	/**
	 * static Singleton instance.
	 */
	private static DatabseAccess instance;

	/**
	 * Private constructor for singleton.
	 */
	private DatabseAccess() {

	}

	public OObjectDatabaseTx getDatbase() {
		// OObjectDatabaseTx is not a thread safe class
		OObjectDatabaseTx database = new OObjectDatabaseTx("remote:localhost/myTest");
		database.open("root", "annet");
		database.getEntityManager().registerEntityClasses("com.aslan.contra.entities");
		return database;
	}

	/**
	 * Use this method with caution.
	 */
	public void resetDatabase() {
		OServerAdmin serverAdmin;
		try {
			serverAdmin = new OServerAdmin("remote:localhost/myTest").connect("root", "annet");
			serverAdmin.dropDatabase("plocal");
			if (!serverAdmin.existsDatabase("plocal")) {
				serverAdmin.createDatabase("graph", "plocal");
			}
			serverAdmin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a singleton instance of DatabseAccess.
	 */
	public static DatabseAccess getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (DatabseAccess.class) {
				if (instance == null) {
					instance = new DatabseAccess();
				}
			}
		}
		return instance;
	}

	public void savePerson (Long id, String name, String phn_no){
		
		OObjectDatabaseTx database = getDatbase();
		OTransaction transaction = null;
		
		try{
			transaction = database.getTransaction();
			Person person = database.newInstance(Person.class);
			person.setId(id);
			person.setName(name);
			person.setPhoneNumber(phn_no);
			
			database.save(person);
			transaction.commit();
		}
		catch(Exception e){
			if(transaction != null){
				transaction.rollback();	
			}
		}
		 finally {
			database.close();
		}
	}
	
	public void saveDevice(Long deviceId,String token, String name, String serial, boolean active, Date lastSeen){
		
		OObjectDatabaseTx database = getDatbase();

		OTransaction transaction = null;

		try {
			transaction = database.getTransaction();
			Device device = database.newInstance(Device.class);
			device.setId(deviceId);
			device.setToken(token);
			device.setName(name);
			device.setSerial(serial);
			device.setActive(active);
			device.setLastSeen(lastSeen);
			
			database.save(device);
			transaction.commit();
		}
		catch(Exception e){
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			database.close();
		}
	}
	
	
	
	public void saveLocation(String name, long geoFence, double lat, double lon) {
		OObjectDatabaseTx database = getDatbase();

		OTransaction transaction = null;

		try {
			transaction = database.getTransaction();

			Location location = database.newInstance(Location.class);
			location.setName(name);
			location.setGeoFence(geoFence);
			location.setLatitude(lat);
			location.setLongitude(lon);

			database.save(location);

			transaction.commit();
		} catch (Exception ex) {
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			database.close();
		}
	}

	public static void main(String[] args) {
		DatabseAccess databseAccess = DatabseAccess.getInstance();
		// Only if you want to delete the schema
		// databseAccess.resetDatabase();
		databseAccess.saveLocation("Colombo", 100, 6.1256, 73.256);
	}
}
