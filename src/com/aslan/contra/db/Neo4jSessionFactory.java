package com.aslan.contra.db;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4jSessionFactory {
	private static final String ENTITY_PACKAGE = "com.aslan.contra.entities";
	private static final String NEO4J_URL = "http://localhost:7474";
	private static final String USERNAME = "neo4j";
	private static final String PASSWORD = "admin";

	private final SessionFactory sessionFactory;

	/**
	 * static Singleton instance.
	 */
	private static Neo4jSessionFactory instance;

	/**
	 * Private constructor for singleton.
	 */
	private Neo4jSessionFactory() {
		this.sessionFactory = new SessionFactory(ENTITY_PACKAGE);
	}

	/**
	 * Return a singleton instance of Neo4jSessionFactory.
	 */
	public static Neo4jSessionFactory getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Neo4jSessionFactory.class) {
				if (instance == null) {
					instance = new Neo4jSessionFactory();
				}
			}
		}
		return instance;
	}

	public Session getNeo4jSession() {
		return sessionFactory.openSession(NEO4J_URL, USERNAME, PASSWORD);
	}
}
