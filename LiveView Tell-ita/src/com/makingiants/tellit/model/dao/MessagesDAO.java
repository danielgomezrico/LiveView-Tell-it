package com.makingiants.tellit.model.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
 * Know how and where to get the messages
 */
public class MessagesDAO {
	
	/**
	 * Get the messages list
	 * 
	 * @param context
	 * @param numberOfMessages number of messages to get
	 * @return
	 */
	public static String[] getMessages(Context context, int numberOfMessages) {
		String[] messages = new String[numberOfMessages];
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		// Key for prefs are "message_0" "message_1"
		for (int i = 0; i < numberOfMessages; i++) {
			messages[i] = prefs.getString("message_" + i, "").trim();
		}
		
		return messages;
	}
	
}
