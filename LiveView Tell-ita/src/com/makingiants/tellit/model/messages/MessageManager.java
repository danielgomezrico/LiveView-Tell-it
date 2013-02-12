package com.makingiants.tellit.model.messages;

import android.content.Context;

import com.makingiants.tellit.model.dao.MessagesDAO;

/**
 * Manage the messages list
 */
public class MessageManager {
	
	// ****************************************************************
	// Attributes
	// ****************************************************************
	
	private String[] messages;
	private int actualMessage;
	
	// ****************************************************************
	// Constructor
	// ****************************************************************
	
	public MessageManager(Context context, int numberOfMessages) {
		
		this.actualMessage = 0;
		this.messages = MessagesDAO.getMessages(context, numberOfMessages);
	}
	
	// ****************************************************************
	// Accessor methods
	// ****************************************************************
	
	/**
	 * Add a message to the list if there is no message in the indexed position
	 * @param index must be > 0
	 * @param message
	 */
	public void addMessage(int index, String message) {
		messages[index] = message;
	}
	
	/**
	 * Get the actual message
	 * @return
	 */
	public String getActualMessage() {
		if (messages.length != 0) {
			return messages[actualMessage];
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get the next message and update the actual message with it
	 */
	public String getNextMessage() {
		if (messages.length != 0) {
			String message = null;
			
			do {
				if (++actualMessage >= messages.length) {
					actualMessage = 0;
				}
				message = messages[actualMessage];
				
			} while (message == null || message.equals(""));
			
			return messages[actualMessage];
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get the previous message and update the actual message with it
	 */
	public String getPreviousMessage() {
		if (messages.length != 0) {
			String message = null;
			do {
				if (--actualMessage < 0) {
					actualMessage = messages.length - 1;
				}
				message = messages[actualMessage];
				
			} while (message == null || message.equals(""));
			
			return messages[actualMessage];
		}
		else {
			return null;
		}
	}
	
}
