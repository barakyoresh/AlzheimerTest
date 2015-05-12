/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Session;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestUserPrefs;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

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
    public ArrayList<Pair<Stimulus, Stimulus>> buildSessionData(AlzTestUserPrefs userPrefs) {
        int numOfTrials = userPrefs.getNumberOfPairsInTrial(), stimuliSize = stimuli.size();

        //remove un-included categories
        ArrayList<Stimulus> stimuliCopy = removeRedundentCategories(userPrefs, stimuliSize);
        Collections.shuffle(stimuliCopy);

        Stimulus firstStim;

        ArrayList<Pair<Stimulus, Stimulus>> sessionData = new ArrayList<Pair<Stimulus, Stimulus>>();

        for(int i = 0; i < numOfTrials; i++) {
            //extract first stimulus
            if(stimuliCopy.size() > 0) {
                firstStim = stimuliCopy.remove(0);
            } else {
                Log.v(OptionListActivity.APPTAG, "not enough combinations, returning insufficient list");
                break;
            }
            //iterate untill a matching partner is found
            for(Stimulus s : stimuliCopy) {
                int diff = Math.abs(s.getValue() - firstStim.getValue());
                if(s.getCategory().equals(firstStim.getCategory())
                        && diff <= userPrefs.getMaximumValueDifference()
                        && diff >= userPrefs.getMinimumValueDifference()) {
                    sessionData.add(new Pair<Stimulus, Stimulus>(firstStim, s));
                    stimuliCopy.remove(s);
                    //TODO: make sure this pythonish thing doesnt break the planet
                    break;
                }
            }
        }
        return sessionData;
    }

    public ArrayList<Stimulus> removeRedundentCategories(AlzTestUserPrefs userPrefs, int stimuliSize) {
        ArrayList<Stimulus> stimuliCopy = new ArrayList<Stimulus>(stimuli);
        for(int i = stimuliSize - 1; i >= 0; i--) {
            if((userPrefs.getSelectedCategories().contains(stimuliCopy.get(i).getCategory()))) {
                if(userPrefs.getOperationSelection() == 0/*TODO: some hardcoded poop right here, change to enum*/){
                    stimuliCopy.remove(i);
                }
            } else {
                if(userPrefs.getOperationSelection() == 1/*TODO: some hardcoded poop right here, change to enum*/){
                    stimuliCopy.remove(i);
                }
            }
        }
        return stimuliCopy;
    }
}
