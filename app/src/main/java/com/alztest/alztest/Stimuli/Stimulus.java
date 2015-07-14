/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */


@DatabaseTable(tableName = "stimuli")
public class Stimulus {
    @DatabaseField(id = true)
    private int hashCode;
    @DatabaseField
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
        hashCode = hashCode();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        hashCode = hashCode();
    }

    public String getName() {return name; }

    public void setName(String name) {
        this.name = name;
        hashCode = hashCode();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        hashCode = hashCode();
    }

    @Override
    public String toString(){
        return (this.name + ", " + this.category + ", " + this.value);
    }
    
    @Override
    public int hashCode(){
        int hash = 7;
        hash = 71 * hash + this.name.hashCode();
        hash = 71 * hash + this.category.hashCode();
        hash = 71 * hash + this.value;
        return hash;
    }
}
