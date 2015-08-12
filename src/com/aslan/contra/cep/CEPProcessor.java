package com.aslan.contra.cep;

import com.aslan.contra.cep.query.Context;
import com.aslan.contra.model.Event;
import com.aslan.contra.model.SensorResponse;

/**
 * This class receives the events and process them using CEP engine.
 * 
 * @author Annet
 *
 */
public abstract class CEPProcessor {
    /**
     * Type of CEPProcessor.
     * 
     * @author annet
     *
     */
    public enum Engine {
        ESPER, SIDDHI
    }

    public abstract void addEvent(Event event);

    public abstract void addContext(Context context);

    /**
     * Returns the default CEPProcessor.
     * 
     * @return the singleton object of default CEPProcessor
     */
    public static CEPProcessor getProcessor() {
        return EsperProcessor.getInstance();
    }

    /**
     * Returns the CEPProcessor of desired type.
     * 
     * @param type
     *            the CEP engine type.
     * @return the singleton object of CEPProcessor
     */
    public static CEPProcessor getProcessor(Engine engine) {
        return EsperProcessor.getInstance();
    }

}
