/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Session.AlzTestSingleClickStats;
import com.alztest.alztest.Toolbox.AlzTestSerializeManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Barak Yoresh on 10/04/2015.
 */

/**
 * Entire session's stats
 */
@DatabaseTable(tableName = "statistics")
public class AlzTestSessionStatistics {
    @DatabaseField(id = true)
    public Date sessionStartTime;
    @DatabaseField
    public Date sessionEndTime;
    @DatabaseField
    public String subjectName;
    @DatabaseField
    public long subjectId;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> statistics;

    public AlzTestSessionStatistics(){}

    public AlzTestSessionStatistics(String subjectName, long subjectId) {
        this.sessionStartTime = new Date();
        statistics = new ArrayList<String>();
        this.subjectName = subjectName;
        this.subjectId = subjectId;
    }

    public Date getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(Date sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public Date getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(Date sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public ArrayList<AlzTestSingleClickStats> getStatistics() {
        ArrayList<AlzTestSingleClickStats> DeserializedStatistics = new ArrayList<AlzTestSingleClickStats>();
        for(String clickStatsJson : this.statistics) {
            //TODO: add cast safety
            DeserializedStatistics.add((AlzTestSingleClickStats)AlzTestSerializeManager.deSerialize(clickStatsJson, AlzTestSingleClickStats.class));
        }
        return DeserializedStatistics;
    }

    public void setStatistics(ArrayList<AlzTestSingleClickStats> statistics) {
        ArrayList<String> jsonStatistics = new ArrayList<String>();
        for(AlzTestSingleClickStats clickStats : statistics) {
            jsonStatistics.add(AlzTestSerializeManager.serialize(clickStats));
        }
        this.statistics = jsonStatistics;
    }

    public void addClickStat(AlzTestSingleClickStats clickStat) {
        String clickStatJson = AlzTestSerializeManager.serialize(clickStat);
        this.statistics.add(clickStatJson);
        Log.v(OptionListActivity.APPTAG, "Added clickstat:\n" + clickStat);
        return;
    }

    public ArrayList<AlzTestSingleClickStats> getCorrectStatistics() {
        ArrayList<AlzTestSingleClickStats> correct = new ArrayList<AlzTestSingleClickStats>();

        for(AlzTestSingleClickStats click : getStatistics()) {
            if(click.correctResponse) {
                correct.add(click);
            }
        }
        return correct;
    }
}
