/*
 * Copyright (c) 2010 Sony Ericsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.makingiants.tellit.liveview.plugins;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.makingiants.tellit.InAppBuyActivity;
import com.makingiants.tellit.R;
import com.makingiants.tellit.model.calls.Contact;
import com.makingiants.tellit.model.calls.ContactManager;
import com.makingiants.tellit.model.messages.MessageManager;
import com.sonyericsson.extras.liveview.plugins.AbstractPluginService;
import com.sonyericsson.extras.liveview.plugins.PluginConstants;
import com.sonyericsson.extras.liveview.plugins.PluginUtils;

public class SandboxPlugin extends AbstractPluginService {

	/**
	 * How many messages are in preferences
	 */
	private static int numberOfMessages;

	// ****************************************************************
	// Attributes
	// ****************************************************************

	// Used to update the LiveView screen
	private Handler handler;

	// Model managers
	private MessageManager messageManager;
	private ContactManager callManager;

	// Streams for background image in LiveView
	private Bitmap bitmapBackground;

	// Send image attributes
	private Bitmap bitmapSend;// Used to show send image while sending
	private boolean showingSendImage;// Used to disable any interaction while
										// bitmapSend is showed

	// Paint used for text
	private Paint bigTextPaint, littleTextPaint;

	// ****************************************************************
	// Service Overrides
	// ****************************************************************

	@Override
	public void onStart(final Intent intent, final int startId) {
		super.onStart(intent, startId);

		if (handler == null) {

			// Init main handler
			handler = new Handler();

			// Init backgrounds
			bitmapBackground = BitmapFactory.decodeStream(this.getResources()
					.openRawResource(R.drawable.background));

			bitmapSend = BitmapFactory.decodeStream(this.getResources()
					.openRawResource(R.drawable.background_sent));

			// Init paints
			bigTextPaint = new Paint();
			bigTextPaint.setColor(Color.WHITE);
			bigTextPaint.setTextSize(15); // Text Size
			bigTextPaint.setTypeface(Typeface.SANS_SERIF);
			bigTextPaint.setAntiAlias(true);
			bigTextPaint.setTextAlign(Paint.Align.CENTER);

			littleTextPaint = new Paint();
			littleTextPaint.setColor(Color.WHITE);
			littleTextPaint.setTextSize(11); // Text Size
			littleTextPaint.setTypeface(Typeface.SANS_SERIF);
			littleTextPaint.setAntiAlias(true);
			littleTextPaint.setTextAlign(Paint.Align.CENTER);

			// Init Messages values
			numberOfMessages = getResources().getInteger(
					R.integer.number_default_messages);
			messageManager = new MessageManager(this, numberOfMessages);

			callManager = new ContactManager(this.getApplicationContext());

		} else {

			callManager.updateContacts(this.getApplicationContext());
		}

		showingSendImage = false;

	}

	@Override
	public void onCreate() {
		super.onCreate();

		// ...
		// Do plugin specifics.
		// ...
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// ...
		// Do plugin specifics.
		// ...
	}

	/**
	 * Plugin is just sending notifications.
	 */
	@Override
	protected boolean isSandboxPlugin() {
		return true;
	}

	/**
	 * Must be implemented. Starts plugin work, if any.
	 */
	@Override
	protected void startWork() {

		// Check if plugin is enabled.
		if (mSharedPreferences.getBoolean(
				PluginConstants.PREFERENCES_PLUGIN_ENABLED, false)) {
			// Track plugin started

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (callManager.getContactsLength() != 0) {
						final Contact call = callManager.getActualContact();
						String message = messageManager.getActualMessage();

						if (message == null) {
							message = getString(R.string.plugin_message_no_messages);
						}

						PluginUtils.sendScaledImage(mLiveViewAdapter,
								mPluginId,
								getBackgroundBitmapWithCall(call, message));

					} else {
						// Show no calls message
						PluginUtils.sendTextBitmap(mLiveViewAdapter, mPluginId,
								getString(R.string.plugin_message_no_call_log),
								PluginConstants.LIVEVIEW_SCREEN_X, 15);
					}
				}

			}, 1000);
		}

	}

	/**
	 * Must be implemented. Stops plugin work, if any.
	 */
	@Override
	protected void stopWork() {

	}

	/**
	 * Must be implemented.
	 * 
	 * PluginService has done connection and registering to the LiveView
	 * Service.
	 * 
	 * If needed, do additional actions here, e.g. starting any worker that is
	 * needed.
	 */
	@Override
	protected void onServiceConnectedExtended(final ComponentName className,
			final IBinder service) {

	}

	/**
	 * Must be implemented.
	 * 
	 * PluginService has done disconnection from LiveView and service has been
	 * stopped.
	 * 
	 * Do any additional actions here.
	 */
	@Override
	protected void onServiceDisconnectedExtended(final ComponentName className) {

	}

	/**
	 * Must be implemented.
	 * 
	 * PluginService has checked if plugin has been enabled/disabled.
	 * 
	 * The shared preferences has been changed. Take actions needed.
	 */
	@Override
	protected void onSharedPreferenceChangedExtended(
			final SharedPreferences prefs, final String key) {
		if (!key.equals("pluginEnabled") && !key.equals("in_app")) {
			final String message = prefs.getString(key, "");

			// Message key values are: message_#
			final int messageNumber = Integer.parseInt(key.substring(key
					.length() - 1));

			messageManager.addMessage(messageNumber, message);
		}

		if (key.equals("in_app")) {

			String sku = prefs.getString(key, "");

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(key, "-1");
			// editor.remove(key);
			editor.commit();

			Intent inAppIntent = new Intent(this.getApplicationContext(),
					InAppBuyActivity.class);

			// prefs.getString(key, "");

			inAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			inAppIntent.putExtra(InAppBuyActivity.EXTRA_KEY_SKU, sku);

			startActivity(inAppIntent);

		}
	}

	/**
	 * This method is called by the LiveView application to start the plugin.
	 * For sandbox plugins, this means when the user has pressed the action
	 * button to start the plugin.
	 */
	@Override
	protected void startPlugin() {
		// // Log.d(PluginConstants.LOG_TAG, "startPlugin");

		startWork();
	}

	/**
	 * This method is called by the LiveView application to stop the plugin. For
	 * sandbox plugins, this means when the user has long-pressed the action
	 * button to stop the plugin.
	 */
	@Override
	protected void stopPlugin() {
		// // Log.d(PluginConstants.LOG_TAG, "stopPlugin");
		stopWork();
	}

	// ****************************************************************
	// Events
	// ****************************************************************

	/**
	 * Sandbox mode only. When a user presses any buttons on the LiveView
	 * device, this method will be called.
	 */
	@Override
	protected void button(final String buttonType, final boolean doublepress,
			final boolean longpress) {

		if (mSharedPreferences.getBoolean(
				PluginConstants.PREFERENCES_PLUGIN_ENABLED, false)) {

			if (callManager.getContactsLength() != 0) {
				if (!showingSendImage) {
					if (buttonType.equalsIgnoreCase(PluginConstants.BUTTON_UP)) {

						final Contact call = callManager.getPreviousContact();
						String message = messageManager.getActualMessage();

						if (message == null) {
							message = getString(R.string.plugin_message_no_messages);
						}

						PluginUtils.sendScaledImage(mLiveViewAdapter,
								mPluginId,
								getBackgroundBitmapWithCall(call, message));

					} else if (buttonType
							.equalsIgnoreCase(PluginConstants.BUTTON_DOWN)) {

						final Contact call = callManager.getNextContact();
						String message = messageManager.getActualMessage();

						if (message == null) {
							message = getString(R.string.plugin_message_no_messages);
						}

						PluginUtils.sendScaledImage(mLiveViewAdapter,
								mPluginId,
								getBackgroundBitmapWithCall(call, message));

					} else if (buttonType
							.equalsIgnoreCase(PluginConstants.BUTTON_LEFT)) {

						final Contact call = callManager.getActualContact();
						String message = messageManager.getPreviousMessage();

						if (message == null) {
							message = getString(R.string.plugin_message_no_messages);
						}

						PluginUtils.sendScaledImage(mLiveViewAdapter,
								mPluginId,
								getBackgroundBitmapWithCall(call, message));

					} else if (buttonType
							.equalsIgnoreCase(PluginConstants.BUTTON_RIGHT)) {

						final Contact call = callManager.getActualContact();
						String message = messageManager.getNextMessage();

						if (message == null) {
							message = getString(R.string.plugin_message_no_messages);
						}

						PluginUtils.sendScaledImage(mLiveViewAdapter,
								mPluginId,
								getBackgroundBitmapWithCall(call, message));

					} else if (buttonType
							.equalsIgnoreCase(PluginConstants.BUTTON_SELECT)) {

						if (!showingSendImage) {

							// Send message
							final Contact call = callManager.getActualContact();
							final String message = messageManager
									.getActualMessage();

							if (call != null && message != null) {
								try {
									final SmsManager shortMessageManager = SmsManager
											.getDefault();

									shortMessageManager.sendTextMessage(
											call.getNumber(), null, message,
											null, null);

									showingSendImage = true;
									mLiveViewAdapter.vibrateControl(mPluginId,
											0, 200);
									// Show send image
									PluginUtils.sendScaledImage(
											mLiveViewAdapter, mPluginId,
											bitmapSend);

									// Set the schedule to allow sending again
									// and show send image for a while
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {
											final Contact call = callManager
													.getActualContact();
											final String message = messageManager
													.getActualMessage();

											PluginUtils
													.sendScaledImage(
															mLiveViewAdapter,
															mPluginId,
															getBackgroundBitmapWithCall(
																	call,
																	message));

											showingSendImage = false;
										}
									}, 1000);
								} catch (IllegalArgumentException e) {
									Log.e("LiveView Tell-it",
											"IllegalArgumentException 1", e);
								}
							}
						}
					}
				}
			} else {
				// Show no calls message
				PluginUtils.sendTextBitmap(mLiveViewAdapter, mPluginId,
						getString(R.string.plugin_message_no_call_log),
						PluginConstants.LIVEVIEW_SCREEN_X, 15);

			}
		}

	}

	/**
	 * Called by the LiveView application to indicate the capabilites of the
	 * LiveView device.
	 */
	@Override
	protected void displayCaps(final int displayWidthPx,
			final int displayHeigthPx) {
		// // Log.d(PluginConstants.LOG_TAG, "displayCaps - width " +
		// displayWidthPx + ", height "
		// + displayHeigthPx);
	}

	/**
	 * Called by the LiveView application when the plugin has been kicked out by
	 * the framework.
	 */
	@Override
	protected void onUnregistered() {
		// // Log.d(PluginConstants.LOG_TAG, "onUnregistered");
		stopWork();
	}

	/**
	 * When a user presses the "open in phone" button on the LiveView device,
	 * this method is called. You could e.g. open a browser and go to a specific
	 * URL, or open the music player.
	 */
	@Override
	protected void openInPhone(final String openInPhoneAction) {
		// // Log.d(PluginConstants.LOG_TAG, "openInPhone: " +
		// openInPhoneAction);
	}

	/**
	 * Sandbox mode only. Called by the LiveView application when the screen
	 * mode has changed. 0 = screen is off, 1 = screen is on
	 */
	@Override
	protected void screenMode(final int mode) {
	}

	// ****************************************************************
	// GUI Changes
	// ****************************************************************

	private Bitmap getBackgroundBitmapWithCall(final Contact call,
			final String message) {

		final Bitmap background = bitmapBackground.copy(Bitmap.Config.RGB_565,
				true);

		final Canvas canvas = new Canvas(background);

		// Draw the name and number
		final String number = call.getNumber();
		final String[] name = trimText(call.getName(), 14);
		if (name.length == 2) {

			canvas.drawText(name[0], PluginConstants.LIVEVIEW_SCREEN_X / 2, 35,
					bigTextPaint);
			canvas.drawText(name[1], PluginConstants.LIVEVIEW_SCREEN_X / 2, 50,
					bigTextPaint);
			canvas.drawText(number,
					(PluginConstants.LIVEVIEW_SCREEN_X - number.length()) / 2,
					65, littleTextPaint);
		} else {

			canvas.drawText(name[0], PluginConstants.LIVEVIEW_SCREEN_X / 2, 40,
					bigTextPaint);
			canvas.drawText(number,
					(PluginConstants.LIVEVIEW_SCREEN_X - number.length()) / 2,
					55, littleTextPaint);

		}
		// Draw message
		final String[] messageTrimed = trimText(message, 18);
		canvas.drawText(messageTrimed[0],
				PluginConstants.LIVEVIEW_SCREEN_X / 2, 100, littleTextPaint);
		if (messageTrimed.length == 2) {
			canvas.drawText(messageTrimed[1],
					PluginConstants.LIVEVIEW_SCREEN_X / 2, 110, littleTextPaint);
		}

		return background;
	}

	/**
	 * Trim a message in two lines. Each line must have max length <= maxLength
	 * and second line will contain '...' at the end.
	 * 
	 * @param message
	 * @param maxLength
	 * @return String array with length = 1 if there message length is <=
	 *         maxLength otherwise String array with length = 2 with each line
	 *         and last one with '...' chars in the end
	 */
	private String[] trimText(final String message, final int maxLength) {
		if (message.length() <= maxLength) {

			return new String[] { message };

		} else {

			final String message1 = message.substring(0, maxLength);
			String message2 = message.substring(maxLength, message.length());

			if (message2.length() > maxLength) {
				message2 = message2.substring(0, maxLength - 3) + "...";
			}

			return new String[] { message1, message2 };

		}
	}
}