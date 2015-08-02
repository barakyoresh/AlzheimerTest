package com.alztest.alztest.Statistics;

/**
 * Created by Barak Yoresh on 30/07/2015.
 */
public class AlzTestBarGraphData {
    private String name;
    private float value;

    public AlzTestBarGraphData(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}


