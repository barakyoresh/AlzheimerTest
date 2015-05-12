/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */


@DatabaseTable(tableName = "stimuli")
public class Stimulus {
    @DatabaseField(id = true)
    private String name;
    @DatabaseField()
    private String category;
    @DatabaseField()
    private int value;

    public Stimulus(){}

    public Stimulus(String name, String category, int value){
        this.name = name;
        this.category = category;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString(){
        return (this.name + ", " + this.category + ", " + this.value);
    }
}
