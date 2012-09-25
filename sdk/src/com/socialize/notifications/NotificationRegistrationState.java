/*
 * Copyright (c) 2012 Socialize Inc.
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
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class NotificationRegistrationState {
	
	private static final String PREFERENCES = "SocializeNotificationState";
	
	private String c2DMRegistrationId;
	private long registeredUserId;
	private long pendingC2DMRequestTime = 0L;
	private long pendingSocializeRequestTime = 0L;
	private long lastC2DMRegistrationTime = 0L;
	
	private SocializeLogger logger;
	
	public boolean isRegisteredSocialize(User user) {
		return registeredUserId == user.getId();
	}

	public boolean isRegisteredC2DM() {
		// TODO: configure timeout.
		return !StringUtils.isEmpty(c2DMRegistrationId) && (System.currentTimeMillis() - lastC2DMRegistrationTime) < 60000;
	}
	
	public void setRegisteredSocialize(User user) {
		this.registeredUserId = user.getId();
		this.pendingSocializeRequestTime = 0;
	}

	public String getC2DMRegistrationId() {
		return c2DMRegistrationId;
	}

	public void setC2DMRegistrationId(String c2dmRegistrationId) {
		this.c2DMRegistrationId = c2dmRegistrationId;
		this.lastC2DMRegistrationTime = System.currentTimeMillis();
		this.pendingC2DMRequestTime = 0;
	}
	
	public boolean isSocializeRegistrationPending() {
		return (System.currentTimeMillis() - pendingSocializeRequestTime) < 30000; // Allow 30 seconds
	}

	public boolean isC2dmPending() {
		return (System.currentTimeMillis() - pendingC2DMRequestTime) < 30000; // Allow 30 seconds
	}

	public void setC2dmPendingRequestTime(long time) {
		this.pendingC2DMRequestTime = time;
	}
	
	public void setPendingSocializeRequestTime(long pendingSocializeRequestTime) {
		this.pendingSocializeRequestTime = pendingSocializeRequestTime;
	}

	public void load(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		c2DMRegistrationId = prefs.getString("c2DMRegistrationId", null);
		registeredUserId = prefs.getLong("registeredUserId", -1L);
		pendingC2DMRequestTime = prefs.getLong("pendingC2DMRequestTime", 0);
		pendingSocializeRequestTime = prefs.getLong("pendingSocializeRequestTime", 0);
		lastC2DMRegistrationTime = prefs.getLong("lastC2DMRegistrationTime", 0);
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Loaded notification state with registration id [" +
					c2DMRegistrationId +
					"], user id [" +
					registeredUserId +
					"]");
		}		
	}
	
	public void save(Context context) {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Saving notification state with registration id [" +
					c2DMRegistrationId +
					"], user id [" +
					registeredUserId +
					"]");
		}
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("c2DMRegistrationId", c2DMRegistrationId);
		editor.putLong("registeredUserId", registeredUserId);
		editor.putLong("pendingC2DMRequestTime", pendingC2DMRequestTime);
		editor.putLong("pendingSocializeRequestTime", pendingSocializeRequestTime);
		editor.putLong("lastC2DMRegistrationTime", lastC2DMRegistrationTime);
		editor.commit();
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
