package com.aslan.contra.cep;

import org.apache.log4j.Logger;

import com.aslan.contra.cep.event.Event;
import com.aslan.contra.cep.event.LocationEvent;
import com.aslan.contra.cep.query.Context;
import com.aslan.contra.cep.query.OnUpdateListener;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

/**
 * 
 * @author Annet
 *
 */
class EsperProcessor extends CEPProcessor {
    /**
     * Logger to log the events.
     */
    private static final Logger LOGGER = Logger.getLogger(CEPProcessor.class);

    /**
     * ESPER CEP Configuration.
     */
    private static final Configuration CONFIGURATION = new Configuration();

    /**
     * ESPER Service provider.
     */
    private static final EPServiceProvider SERVICE_PROVIDER;

    /**
     * ESPER CEP runtime engine.
     */
    private static final EPRuntime CEP_RUNTIME;

    /**
     * ESPER administrator.
     */
    private static final EPAdministrator ADMIN;

    /**
     * Singleton instance of EsperProcessor.
     */
    private static EsperProcessor instance;

    /**
     * Initialize the variables.
     */
    static {
        // Define a stream named Origin.
        CONFIGURATION.addEventType("Origin", LocationEvent.class.getName());
        // Create the service provider.
        SERVICE_PROVIDER = EPServiceProviderManager.getProvider("CoreEngine",
                CONFIGURATION);
        // Get the Esper runtime.
        CEP_RUNTIME = SERVICE_PROVIDER.getEPRuntime();
        // Get the administrator of Esper runtime.
        ADMIN = SERVICE_PROVIDER.getEPAdministrator();
    }

    /**
     * Private constructor to enforce singleton behavior.
     */
    public EsperProcessor() {
        // // Query definition
        // ADMIN.createEPL("insert into LocationEvent select * from Origin.std:groupwin(userID).win:ext_timed_batch(time, 24 hours)");
        // // Current limitation 2 hours
        // EPStatement statement = ADMIN
        // .createEPL("select beginevent.userID as userID, beginevent.geoFence as geoFence, beginevent.time as beginTime, endevent.time as endTime, beginevent.wifiNetworks as wifiNetworks"
        // +
        // " from pattern [every (beginevent=LocationEvent -> middleevent=LocationEvent(geoFence=beginevent.geoFence AND (time - beginevent.time < 7200000))"
        // +
        // " until endevent=LocationEvent(geoFence!=beginevent.geoFence OR (time - beginevent.time >= 7200000)))] group by beginevent.userID");

    }

    /**
     * Singleton factory method to return a singleton object of EsperProcessor.
     * 
     * @return a singleton instance of EsperProcessor
     */
    public static EsperProcessor getInstance() {
        if (instance == null) {
            synchronized (CEPProcessor.class) {
                if (instance == null) {
                    instance = new EsperProcessor();
                }
            }
        }
        return instance;
    }

    @Override
    public void addContext(Context context) {
        int size = context.size();
        for (int i = 0; i < size; i++) {
            final String query = context.getQuery(i);
            final OnUpdateListener listener = context.getOnUpdateListener(i);

            LOGGER.error("Context: " + context);
            
            EPStatement statement = ADMIN.createEPL(query);
            if (listener != OnUpdateListener.NULL_OBJECT) {
                statement.addListener(new UpdateListener() {

                    @Override
                    public void update(EventBean[] newEvents,
                            EventBean[] oldEvents) {
                        for (EventBean e : newEvents) {
                            MapEventBean bean = (MapEventBean) e;
                            listener.onUpdate(bean.getProperties());
                        }
                    }
                });
            } else {
                statement.addListener(new UpdateListener() {

                    @Override
                    public void update(EventBean[] newEvents,
                            EventBean[] oldEvents) {
                        // Do nothing
                    }
                });
            }
        }
    }

    @Override
    public void addEvent(Event event) {
        CEP_RUNTIME.sendEvent(event);

    }

}
