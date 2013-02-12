package com.makingiants.tellit.model.dao;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.makingiants.tellit.model.calls.Contact;

/**
 * Know how to collect the contacts from phone
 */
public class ContactsDAO {
	
	/**
	 * Get the starred (favorites) contacts of the device
	 * 
	 * @param context 
	 * 
	 * @return list of contacts founded
	 */
	public static ArrayList<Contact> getStarredContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		ContentResolver resolver = context.getContentResolver();
		try {
			
			// Define Wich data query
			String[] projection = new String[] {
					ContactsContract.Contacts.STARRED,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER,
					ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED };
			
			// Where will be the query
			Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			
			Cursor cursor = resolver.query(contactsUri, projection,
					ContactsContract.CommonDataKinds.Phone.STARRED + "=?",
					new String[] { "1" },
					ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED
							+ " DESC");
			
			try {
				if (cursor.moveToFirst()) {
					String name = null;
					String number = null;
					
					do {
						name = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						
						number = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						contacts.add(new Contact(name, number));
						
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		} catch (NullPointerException e) {
			Log.e("Tell-it", "ContactsDAO NullPointerException 1", e);
		} catch (Exception e) {
			Log.e("Tell-it", "ContactsDAO Exception 2", e);
		}
		
		return contacts;
	}
}
