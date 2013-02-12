package com.makingiants.tellit.model.calls;

import java.util.ArrayList;

import android.content.Context;

import com.makingiants.tellit.model.dao.ContactsDAO;

/**
 * Manage the CallLog list
 */
public class ContactManager {
	
	// ****************************************************************
	// Attributes
	// ****************************************************************
	
	private ArrayList<Contact> contacts;
	private int actualContact;
	
	// ****************************************************************
	// Constructor
	// ****************************************************************
	
	public ContactManager(final Context context) {
		
		this.contacts = ContactsDAO.getStarredContacts(context);
		
		this.actualContact = 0;
		
	}
	
	/**
	 * Update the actual list of calls
	 * @param context
	 */
	public void updateContacts(final Context context) {
		this.contacts = ContactsDAO.getStarredContacts(context);
		this.actualContact = 0;
	}
	
	// ****************************************************************
	// Accessor Methods
	// ****************************************************************
	
	/**
	 * Get the actual call
	 * @return actual call if it exist, else return null
	 */
	public Contact getActualContact() {
		if (contacts.size() != 0) {
			return contacts.get(actualContact);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get the next call and update the actual call with it
	 * @return next call if it exist, else return null
	 */
	public Contact getNextContact() {
		if (contacts.size() != 0) {
			if (++actualContact >= contacts.size()) {
				actualContact = 0;
			}
			return contacts.get(actualContact);
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * Get the previous call and update the actual call with it
	 * @return previous call if it exist, else return null
	 */
	public Contact getPreviousContact() {
		if (contacts.size() != 0) {
			if (--actualContact < 0) {
				actualContact = contacts.size() - 1;
			}
			return contacts.get(actualContact);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Return the number of calls
	 * @return
	 */
	public int getContactsLength() {
		return contacts.size();
	}
}
