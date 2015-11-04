package com.aslan.contra.config;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class is required by Jersey 2.0 to define the package which contains web
 * service classes.
 * 
 * @author gobinath
 *
 */
public class WebServiceConfiguration extends ResourceConfig {
	public WebServiceConfiguration() {
		// Define the package which contains the service classes.
		packages("com.aslan.contra.services");
	}
}