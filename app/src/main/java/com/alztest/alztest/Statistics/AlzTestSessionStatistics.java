/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestCategoryAdapter;
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
    @DatabaseField
    public int MMSEOrientationSpace;
    @DatabaseField
    public int MMSEOrientationTime;
    @DatabaseField
    public int MMSETotal;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> statistics;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> categoryPreferences;

    public AlzTestSessionStatistics(){}

    public AlzTestSessionStatistics(String subjectName, long subjectId) {
        this.sessionStartTime = new Date();
        statistics = new ArrayList<String>();
        this.subjectName = subjectName;
        this.subjectId = subjectId;
    }


    public int getMMSETotal() {
        return MMSETotal;
    }

    public void setMMSETotal(int MMSETotal) {
        this.MMSETotal = MMSETotal;
    }

    public int getMMSEOrientationSpace() {
        return MMSEOrientationSpace;
    }

    public void setMMSEOrientationSpace(int MMSEOrientationSpace) {
        this.MMSEOrientationSpace = MMSEOrientationSpace;
    }

    public int getMMSEOrientationTime() {
        return MMSEOrientationTime;
    }

    public void setMMSEOrientationTime(int MMSEOrientationTime) {
        this.MMSEOrientationTime = MMSEOrientationTime;
    }

    public void setMMSEScore(int orientationSpace, int orientationTime, int total) {
        this.MMSEOrientationSpace = orientationSpace;
        this.MMSEOrientationTime = orientationTime;
        this.MMSETotal = total;
    }

    public static ArrayList<Integer> getMMSEOrientationSpaceValues(){
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (int i = 1 ; i <= 5 ; i++) {
            values.add(i);
        }
        return values;
    }

    public static ArrayList<Integer> getMMSEOrientationTimeValues(){
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (int i = 1 ; i <= 5 ; i++) {
            values.add(i);
        }
        return values;
    }

    public static ArrayList<Integer> getMMSETotalValues(){
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (int i = 1 ; i <= 30 ; i++) {
            values.add(i);
        }
        return values;
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


    public ArrayList<AlzTestCategoryAdapter.CategoryListItem> getCategoryPreferences() {
        ArrayList<AlzTestCategoryAdapter.CategoryListItem> DeserializedCategoryPreferences = new ArrayList<AlzTestCategoryAdapter.CategoryListItem>();
        for(String clickStatsJson : this.categoryPreferences) {
            DeserializedCategoryPreferences.add((AlzTestCategoryAdapter.CategoryListItem)AlzTestSerializeManager.deSerialize(clickStatsJson, AlzTestCategoryAdapter.CategoryListItem.class));
        }
        return DeserializedCategoryPreferences;
    }

    public void setCategoryPreferences(ArrayList<AlzTestCategoryAdapter.CategoryListItem> categoryPreferences) {
        ArrayList<String> jsonCategoryPreferences = new ArrayList<String>();
        for(AlzTestCategoryAdapter.CategoryListItem category : categoryPreferences) {
            jsonCategoryPreferences.add(AlzTestSerializeManager.serialize(category));
        }
        this.categoryPreferences = jsonCategoryPreferences;
    }

}
