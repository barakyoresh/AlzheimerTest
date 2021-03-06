/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Session;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestCategoryAdapter;
import com.alztest.alztest.Prefrences.AlzTestUserPrefs;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by Barak Yoresh on 05/04/2015.
 */
public class AlzTestSessionFactory {
    private ArrayList<Stimulus> stimuli = null;

    public AlzTestSessionFactory(Activity context) {
        //get DB handle
        try{
            Log.v(OptionListActivity.APPTAG, "getting stimuli from db");
            stimuli = (ArrayList<Stimulus>) AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Pair<Stimulus, Stimulus>>> buildSessionData(AlzTestUserPrefs userPrefs) {
        int numOfTrials = userPrefs.getNumberOfPairsInTrial(), stimuliSize = stimuli.size();

        //remove un-included categories
        ArrayList<Stimulus> stimuliCopy = removeRedundentStimuli(userPrefs); //specifically chosen stimuli
        stimuliCopy = removeRedundentCategories(userPrefs, stimuliCopy); //specific categories

        //shuffle data
        Collections.shuffle(stimuliCopy);

        //assmble pairs
        ArrayList<Pair<Stimulus, Stimulus>> sessionData = assembleSessionPairs(userPrefs, numOfTrials, stimuliCopy);

        //cluster resulting pairs by category
        return clusterByCategory(sessionData, userPrefs);
    }

    public ArrayList<Pair<Stimulus, Stimulus>> assembleSessionPairs(AlzTestUserPrefs userPrefs, int numOfTrials, ArrayList<Stimulus> stimuliCopy) {
        ArrayList<Pair<Stimulus, Stimulus>> sessionData = new ArrayList<Pair<Stimulus, Stimulus>>();
        Stimulus firstStim;
        for(int i = 0; i < numOfTrials; i++) {
            //extract first stimulus
            if(stimuliCopy.size() > 0) {
                firstStim = stimuliCopy.remove(0);
            } else {
                Log.v(OptionListActivity.APPTAG, "not enough combinations, returning insufficient list");
                break;
            }
            //iterate until a matching partner is found
            for(Stimulus s : stimuliCopy) {
                int diff = Math.abs(s.getValue() - firstStim.getValue());
                if(s.getCategory().equals(firstStim.getCategory())
                        && diff <= userPrefs.getMaximumValueDifference()
                        && diff >= userPrefs.getMinimumValueDifference()) {
                    sessionData.add(new Pair<Stimulus, Stimulus>(firstStim, s));
                    stimuliCopy.remove(s);
                    break;
                }
            }
        }
        return sessionData;
    }

    /**
     * Removes all unincluded stimuli when selected with specific subset
     */
    private ArrayList<Stimulus> removeRedundentStimuli(AlzTestUserPrefs userPrefs) {
        if (!userPrefs.isUsingSpecificStimuliSubset()) {
            return stimuli;
        }

        ArrayList<Stimulus> stimuliCopy = new ArrayList<Stimulus>();
        HashSet<Integer> specificSubset = userPrefs.getSpecificStimuliSubsetIndecies();
        for(Stimulus s : stimuli) {
            if(specificSubset.contains(s.hashCode())) {
                stimuliCopy.add(s);
            }
        }

        return stimuliCopy;
    }

    public ArrayList<Stimulus> removeRedundentCategories(AlzTestUserPrefs userPrefs, ArrayList<Stimulus> stimuli) {
        ArrayList<Stimulus> stimuliCopy = new ArrayList<Stimulus>();
        ArrayList<AlzTestCategoryAdapter.CategoryListItem> categories = userPrefs.getCategoryPreferences();
        for(Stimulus s : stimuli) {
            for(AlzTestCategoryAdapter.CategoryListItem category : categories) {
                if(s.getCategory().equals(category.getCategory()) && category.isIncludeInSession()){
                    stimuliCopy.add(s);
                }
            }
        }
        return stimuliCopy;
    }

    public static ArrayList<ArrayList<Pair<Stimulus, Stimulus>>> clusterByCategory(ArrayList<Pair<Stimulus, Stimulus>> stimuliPairs, final AlzTestUserPrefs userPrefs) {
        ArrayList<ArrayList<Pair<Stimulus, Stimulus>>> stimuliByCategory = new ArrayList<ArrayList<Pair<Stimulus, Stimulus>>>();

        for (Pair<Stimulus, Stimulus> s : stimuliPairs) {
            String category = s.first.getCategory();
            boolean listExists = false;
            //if category exists, add it to correct list
            for (ArrayList<Pair<Stimulus, Stimulus>> categoryStimuli : stimuliByCategory) {
                if ( categoryStimuli.size() > 0 && categoryStimuli.get(0).first.getCategory().equals(category)) {
                    categoryStimuli.add(s);
                    listExists = true;
                    break;
                }
            }
            if (!listExists) {
                ArrayList<Pair<Stimulus, Stimulus>> l = new ArrayList<Pair<Stimulus, Stimulus>>();
                l.add(s);
                stimuliByCategory.add(l);
            }
        }

        ArrayList<AlzTestCategoryAdapter.CategoryListItem> order = userPrefs.getCategoryPreferences();
        final ArrayList<String> stringOrder = new ArrayList<String>();
        for(AlzTestCategoryAdapter.CategoryListItem item : order) {
            stringOrder.add(item.getCategory());
        }

        Collections.sort(stimuliByCategory, new Comparator<ArrayList<Pair<Stimulus, Stimulus>>>() {

            @Override
            public int compare(ArrayList<Pair<Stimulus, Stimulus>> lhs, ArrayList<Pair<Stimulus, Stimulus>> rhs) {
                if(lhs.size() < 1 || lhs.get(0).first == null) { return -1; };
                if(rhs.size() < 1 || rhs.get(0).first == null) { return 1; };

                return stringOrder.indexOf(lhs.get(0).first.getCategory()) - stringOrder.indexOf(rhs.get(0).first.getCategory());
            }
        });
        return stimuliByCategory;
    }
}
