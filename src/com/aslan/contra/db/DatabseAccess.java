package com.aslan.contra.db;

import java.io.IOException;

import com.aslan.contra.model.LocationEvent;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType.OrientVertexProperty;

public class DatabseAccess {
	/**
	 * OrientDB graph factory.
	 */
	private final OrientGraphFactory factory;

	/**
	 * static Singleton instance.
	 */
	private static DatabseAccess instance;

	/**
	 * Private constructor for singleton.
	 */
	private DatabseAccess() {
		this.factory = new OrientGraphFactory("remote:localhost/test").setupPool(1, 10);

		OServerAdmin serverAdmin;
		try {
			serverAdmin = new OServerAdmin("remote:localhost/test").connect("root", "root");
			if (!serverAdmin.existsDatabase("plocal")) {
				// Creating new database
				serverAdmin.createDatabase("graph", "plocal");
				init();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	private void init() {

		OrientGraph graph = null;
		try {
			graph = factory.getTx();
			graph.begin();

			OrientVertexType wifi = graph.createVertexType("Wifi");
			wifi.createProperty("bssid", OType.STRING);
			wifi.createProperty("name", OType.STRING);

			OrientVertexType person = graph.createVertexType("Person");
			person.createProperty("id", OType.STRING);
			person.createProperty("name", OType.STRING);

			OrientVertexType location = graph.createVertexType("Location");
			location.createProperty("id", OType.LONG);
			location.createProperty("latitude", OType.DOUBLE);
			location.createProperty("longitude", OType.DOUBLE);
			location.createProperty("type", OType.EMBEDDEDSET);

			OrientEdgeType locatedIn = graph.createEdgeType("locatedIn");
			locatedIn.createProperty("from", OType.DATETIME);
			locatedIn.createProperty("accuracy", OType.FLOAT);

			OrientEdgeType home = graph.createEdgeType("home", "locatedIn");
			OrientEdgeType work = graph.createEdgeType("work", "locatedIn");
			OrientEdgeType current = graph.createEdgeType("current", "locatedIn");

			OrientEdgeType hasWifi = graph.createEdgeType("hasWifi");
			hasWifi.createProperty("strength", OType.FLOAT);

			// person.createEdgeProperty(Direction.OUT, "locatedIn");
			// location.createEdgeProperty(Direction.IN, "locatedIn");
			// location.createEdgeProperty(Direction.OUT, "hasWifi");
			// wifi.createEdgeProperty(Direction.IN, "hasWifi");

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
	}

	public Object saveLocation() {
		OrientGraph graph = null;
		Object id = null;
		try {
			graph = factory.getTx();
			graph.begin();

			OrientVertex mc = graph.addVertex("class:Location");
			mc.setProperty("id", 100);
			mc.setProperty("latitude", 7.2646);
			mc.setProperty("longitude", 80.646);
			mc.setProperty("type", "MULTIPLEX");

			id = mc.getId();

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
		return id;
	}

	public Object savePerson() {
		OrientGraph graph = null;
		Object id = null;
		try {
			graph = factory.getTx();
			graph.begin();

			OrientVertex gobi = graph.addVertex("class:Person");
			gobi.setProperty("id", "P001");
			gobi.setProperty("name", "Gobinath");

			id = gobi.getId();

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
		return id;
	}

	public Object addWifiToLocation(Object locationId, Object wifiId) {
		OrientGraph graph = null;
		Object id = null;
		try {
			graph = factory.getTx();
			graph.begin();

			OrientVertex location = graph.getVertex(locationId);
			OrientVertex wifi = graph.getVertex(wifiId);

			OrientEdge hasWifi = graph.addEdge("class:hasWifi", location, wifi, "wifi");
			hasWifi.setProperty("strength", 80.0f);

			id = hasWifi.getId();

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
		return id;
	}

	public Object saveWifi() {
		OrientGraph graph = null;
		Object id = null;
		try {
			graph = factory.getTx();
			graph.begin();

			OrientVertex wifi = graph.addVertex("class:Wifi");
			wifi.setProperty("bssid", "A1:001:12");
			wifi.setProperty("name", "MC_WIFI");

			id = wifi.getId();

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
		return id;
	}

	public void readAll() {
		OrientGraph graph = null;
		try {
			graph = factory.getTx();
			graph.begin();

			GraphQuery query = graph.query();
			query.has("bssid", "A1:001:12");
			for (Vertex v : query.vertices()) {
				System.out.println(v.getId());
			}

			graph.commit();
		} catch (Exception ex) {
			if (graph != null) {
				graph.rollback();
			}
		}
	}

	public Object getWifiID(String bssid) {
		OrientGraphNoTx graph = null;
		Object id = null;
		try {
			graph = factory.getNoTx();
			GraphQuery query = graph.query();
			query.has("bssid", bssid);
			for (Vertex v : query.vertices()) {
				id = v.getId();
			}
		} catch (Exception ex) {
		}
		return id;
	}

	public Object getLocationID(long geoFence) {
		OrientGraphNoTx graph = null;
		Object id = null;
		try {
			graph = factory.getNoTx();
			GraphQuery query = graph.query();
			query.has("id", geoFence);
			for (Vertex v : query.vertices()) {
				id = v.getId();
			}
		} catch (Exception ex) {
		}
		return id;
	}

	public void process() {

	}

	public static void main(String[] args) {
		DatabseAccess db = DatabseAccess.getInstance();
		db.savePerson();
		Object locId = db.saveLocation();
		Object wifiId = db.saveWifi();
		db.addWifiToLocation(locId, wifiId);
		db.readAll();
	}
}
