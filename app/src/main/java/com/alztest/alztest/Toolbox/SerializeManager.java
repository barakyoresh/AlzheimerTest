/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Wrapper for the Gson Library for simple serializing and deserializing of data
 * @author Barak Yoresh
 *
 */
public class SerializeManager {
	private static Gson gson = null;
	
	/**
	 * Serialize an object into a String
	 * @param obj object to serialize
	 * @return String representation of given object
	 */
	public static String serialize(Object obj){
		if (gson == null){
			gson = new GsonBuilder().create();
		}
		return gson.toJson(obj);
	}
	
	/**
	 * De-Serialize a String into an Object, this is an Un-Checked method, and the correct use of it 
	 * is the responsibility of the user.
	 * @param str Serialized string to retrieve object from
	 * @param ObjectType Type of object to de-serialize
	 * @return Object serialized by the given String
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object deSerialize(String str, Class ObjectType){
		if (gson == null){
			gson = new GsonBuilder().create();
		}
		return gson.fromJson(str, ObjectType);
	}
}
