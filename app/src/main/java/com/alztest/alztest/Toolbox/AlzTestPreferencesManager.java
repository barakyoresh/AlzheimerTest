/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.alztest.alztest.Prefrences.AlzTestUserPrefs;

/**
 * Created by Barak Yoresh on 13/02/2015.
 */
public class AlzTestPreferencesManager {
    final private static String prefsKey = "ALZTEST_PREFS";
    final private static String tempSetKey = "ALZTEST_PREFS_TEMP_SET";
    private SharedPreferences prefs = null;
    private SharedPreferences.Editor editor = null;



    public AlzTestPreferencesManager(Activity activity){
        prefs = activity.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Returns new AlzTestUserPrefs if not found
     * @returns new AlzTestUserPrefs if not found
     */
    public AlzTestUserPrefs getCachedPreferencesSet() {
        return getPreferenceSet(tempSetKey);
    }


    public void setCachedPreferencesSet(AlzTestUserPrefs set) {
        setPreferenceSet(null, set);
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
    public void setPreferenceSet(String setName, AlzTestUserPrefs set) {
        if(setName == null){
            setName = tempSetKey;
        }
        if(set != null) {
            editor.putString(setName, AlzTestSerializeManager.serialize(set));
        }else{
            editor.putString(setName, null);
        }
        editor.commit();
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
