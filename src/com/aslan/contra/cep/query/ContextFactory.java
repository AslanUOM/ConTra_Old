package com.aslan.contra.cep.query;

import com.aslan.contra.cep.CEPProcessor;
import com.aslan.contra.cep.query.Context.Type;

/**
 * 
 * @author Annet
 *
 */
public class ContextFactory {
	private static ContextFactory instance;

	private ContextFactory() {

	}

	/**
	 * Singleton factory method to return a singleton object of EsperProcessor.
	 * 
	 * @return a singleton instance of EsperProcessor
	 */
	public static ContextFactory getInstance() {
		if (instance == null) {
			synchronized (CEPProcessor.class) {
				if (instance == null) {
					instance = new ContextFactory();
				}
			}
		}
		return instance;
	}

	public Context getContext(Type type, String inputStream) {
		Context query = null;
		switch (type) {
		case LOCATION:
			query = new LocationIdentificationContext(inputStream);
			break;
		}

		return query;
	}
}
