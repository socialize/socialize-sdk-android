/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
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
package com.socialize.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Jason Polites
 *
 */
public class NotificationRegistrationState {
	
	private static final String PREFERENCES = "SocializeNotificationState";
	
	private boolean registeredSocialize;
	private String c2DMRegistrationId;
	
	public boolean isRegisteredSocialize() {
		return registeredSocialize;
	}

	public void setRegisteredSocialize(boolean registeredSocialize) {
		this.registeredSocialize = registeredSocialize;
	}

	public String getC2DMRegistrationId() {
		return c2DMRegistrationId;
	}

	public void setC2DMRegistrationId(String c2dmRegistrationId) {
		c2DMRegistrationId = c2dmRegistrationId;
	}

	public void load(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		c2DMRegistrationId = prefs.getString("c2DMRegistrationId", null);
		registeredSocialize = prefs.getBoolean("registeredSocialize", false);
	}
	
	public void save(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("c2DMRegistrationId", c2DMRegistrationId);
		editor.putBoolean("registeredSocialize", registeredSocialize);
		editor.commit();
	}
}
