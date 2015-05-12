/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.alztest.alztest.Prefrences.AlzTestUserPrefs;

import java.util.ArrayList;

/**
 * Created by Barak Yoresh on 13/02/2015.
 */
public class AlzTestPreferencesManager {
    final private static String prefsKey = "ALZTEST_PREFS";
    final private static String savedPreferencesNamesKey = "ALZTEST_PREFS_SAVED";
    final private static String lastSavedKey = "ALZTEST_PREFS_LAST_SAVED";
    final private static String tempSetKey = "ALZTEST_PREFS_TEMP_SET";
    private SharedPreferences prefs = null;
    private SharedPreferences.Editor editor = null;



    public AlzTestPreferencesManager(Activity activity){
        prefs = activity.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public ArrayList<String> getSavedPreferencesSetNames() {
        String jsonStr = prefs.getString(savedPreferencesNamesKey, "");
        if(!jsonStr.equals("")) {
            Object jsonObj = AlzTestSerializeManager.deSerialize(jsonStr, ArrayList.class);
            if (jsonObj != null) {
                return (ArrayList<String>) jsonObj;
            }
        }
        return null;
    }

    /**
     * Returns new AlzTestUserPrefs if not found
     * @returns new AlzTestUserPrefs if not found
     */
    public AlzTestUserPrefs getLastSavedPreferencesSet() {
        return getPreferenceSet(prefs.getString(lastSavedKey, ""));
    }


    /**
     * Returns new set if not existing
     */
    public AlzTestUserPrefs getPreferenceSet(String setName) {
        if(prefs.contains(setName)){
            String jsonStr = prefs.getString(setName, "");
            if(!jsonStr.equals("")){
                Object jsonObj = AlzTestSerializeManager.deSerialize(jsonStr, AlzTestUserPrefs.class);
                return (AlzTestUserPrefs) jsonObj;
            }
        }
        return new AlzTestUserPrefs();
    }

    /**
     * passing null set is equivelant to remove(setName)
     * passing null name creates a temporary cahced name
     */
    public  void setPreferenceSet(String setName, AlzTestUserPrefs set) {
        if(setName == null){
            setName = tempSetKey;
        }
        if(set != null) {
            editor.putString(setName, AlzTestSerializeManager.serialize(set));
            editor.putString(lastSavedKey, setName);
            addNameToPrefNames(setName);
        }else{
            editor.putString(setName, null);
        }
        editor.commit();
    }

    private void addNameToPrefNames(String setName) {
        if(!setName.equals(tempSetKey)) {
            ArrayList<String> prefNames;
            String jsonStr = prefs.getString(savedPreferencesNamesKey, "");
            if (!jsonStr.equals("")) {
                Object jsonObj = AlzTestSerializeManager.deSerialize(jsonStr, ArrayList.class);
                if (jsonObj != null) {
                    prefNames = (ArrayList<String>) jsonObj;
                } else {
                    prefNames = new ArrayList<String>();
                }
            } else {
                prefNames = new ArrayList<String>();
            }

            if (!prefNames.contains(setName)) {
                prefNames.add(setName);
                editor.putString(savedPreferencesNamesKey, AlzTestSerializeManager.serialize(prefNames));
                editor.commit();
            }
        }
    }

    /**
     * returns null if non existing
     * requires casting outside of method
     */
    public Object getGlobalValue(String key) {
        if(prefs.contains(key)){
            String jsonStr = prefs.getString(key, "");
            if(!jsonStr.equals("")){
                return AlzTestSerializeManager.deSerialize(jsonStr, Object.class);
            }
        }
        return null;
    }

    public void setGlobalValue(String key, Object value) {
        editor.putString(key, AlzTestSerializeManager.serialize(value));
        editor.commit();
    }

}
