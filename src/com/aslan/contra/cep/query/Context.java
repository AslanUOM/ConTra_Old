package com.aslan.contra.cep.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aslan.contra.cep.CEPProcessor;
import com.aslan.contra.model.LocationEvent;
import com.aslan.contra.model.SensorData;
import com.aslan.contra.model.SensorResponse;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.LocationGrid;

public abstract class Context {
    /**
     * Query Type.
     * 
     * @author Annet
     *
     */
    public enum Type {
        LOCATION
    };

    protected final CEPProcessor CEP_PROCESSOR;
    private List<String> queries = new ArrayList<String>();

    private List<OnUpdateListener> listeners = new ArrayList<OnUpdateListener>();

    /**
     * Name of the input stream.
     */
    protected final String INPUT_STREAM;

    public Context(String inputStream) {
        this.CEP_PROCESSOR = CEPProcessor.getProcessor();
        this.INPUT_STREAM = inputStream;
    }

    public abstract Type getType();

    protected void add(String query, OnUpdateListener listener) {
        this.queries.add(query);
        this.listeners.add(listener);
    }

    protected void remove(String query) {
        int index = queries.indexOf(query);
        this.queries.remove(index);
        this.listeners.remove(index);
    }

    public int size() {
        return this.queries.size();
    }

    public String getQuery(int index) {
        return this.queries.get(index);
    }

    public OnUpdateListener getOnUpdateListener(int index) {
        return this.listeners.get(index);
    }
}
