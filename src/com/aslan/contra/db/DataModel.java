package com.aslan.contra.db;

public class DataModel {
	/**
	 * static Singleton instance.
	 */
	private static DataModel instance;

	/**
	 * Neo4jSessionFactory to create Sessions.
	 */
	private Neo4jSessionFactory sessionFactory;
	
	/**
	 * Private constructor for singleton.
	 */
	private DataModel() {
		this.sessionFactory = Neo4jSessionFactory.getInstance();
	}

	/**
	 * Return a singleton instance of DataModel.
	 */
	public static DataModel getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (DataModel.class) {
				if (instance == null) {
					instance = new DataModel();
				}
			}
		}
		return instance;
	}
	
	
}
