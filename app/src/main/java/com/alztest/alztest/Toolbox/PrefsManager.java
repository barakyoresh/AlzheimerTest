/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;
import java.util.HashMap;
import java.util.Map;

import android.content.*;
import android.preference.PreferenceManager;

/**
 * Wrapper for a singleton sharedPrefrences data map
 * @author Barak Yoresh
 *
 */
public class PrefsManager {

	static boolean init = false;
	static SharedPreferences sharedPrefs = null;
	static SharedPreferences.Editor editor = null;
	static private HashMap<String, String> prefsMap;
	/**
	 * Initialize the preference manager
	 * @param context required context for Android.getDefaultSharedPreferences
	 */
	static public void initPrefsManager(Context context){
		if (init){
			return;
		}
		init = true;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sharedPrefs.edit();
		prefsMap = loadPrefs();
		return;
	}
	
	/**
	 * loads and returns the preferences map from the devices memory
	 * @return the the preferences map from the devices memory
	 */
	@SuppressWarnings("unchecked")
	static private HashMap<String, String> loadPrefs(){
		HashMap<String, String> newMap = new HashMap<String, String>();
		if (sharedPrefs != null){
			newMap.putAll((Map<? extends String, ? extends String>) sharedPrefs.getAll());
			return newMap;
		}
		return newMap;
	}


	/**
	 * Saves the current preferences to device
	 */
	static private void savePrefs(){
		if(editor != null){
			for (String key : prefsMap.keySet()){
				editor.putString(key, prefsMap.get(key));
			}
			editor.commit();
		}
	}
	
	/**
	 * This method is used to store a system wide preference
	 * Add Set an java.lang.Object value in the preferences editor. This method updates the entire prefsManager, so use it as rarely as possible.
	 * @param key The name of the preference to modify. 
	 * @param value The new value for the preference. Supplying null as the value is equivalent to calling remove(String) with this key.
	 */
	static public void setGlobalValue(String key, Object value){
		if(prefsMap != null){
			String gsonString = SerializeManager.serialize(value);
			prefsMap.put(key, gsonString);
			savePrefs();
		}
	}

	/**
	 * Returns the global value matching the given key.
	 */
	static public Object getGlobalValueForKey(String key){
		if (prefsMap != null && prefsMap.containsKey(key)){
			return SerializeManager.deSerialize(prefsMap.get(key), Object.class);
		}
		return null;
	}
}