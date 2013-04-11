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
import com.socialize.config.SocializeConfig;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class NotificationRegistrationState {
	
	private static final String PREFERENCES = "SocializeNotificationState";
	
	public static final long DEFAULT_SOCIALIZE_NOTIFICATION_TIMEOUT = 24 * 60 * 60 * 1000; // 1 day
	public static final long DEFAULT_GCM_TIMEOUT = -1; // Don't check again
	
	private String c2DMRegistrationId;
	private long registeredUserId;
	private long pendingC2DMRequestTime = 0L;
	private long pendingSocializeRequestTime = 0L;
	private long lastC2DMRegistrationTime = 0L;
	private long lastSocializeRegistrationTime = 0L;

	private String lastGCMSenderID = null;
	private String lastCustomGCMSenderId = null;
	
	private SocializeLogger logger;
	private SocializeConfig config;
	private boolean loaded = false;
	
	public boolean isRegisteredSocialize(Context context, User user) {
		
		load(context);
		
		long timeout = config.getLongProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_INTERVAL, DEFAULT_SOCIALIZE_NOTIFICATION_TIMEOUT);
		long timeSinceLast = (System.currentTimeMillis() - lastSocializeRegistrationTime);
		
		if(registeredUserId != user.getId() || (timeSinceLast > timeout && timeout >= 0)) {
			return false;
		}
		
		return true;
	}

	public boolean isRegisteredC2DM(Context context) {
		
		load(context);

		if(StringUtils.isEmpty(lastGCMSenderID)) {
			return false;
		}
		else if(!lastGCMSenderID.equals(config.getProperty(SocializeConfig.SOCIALIZE_GCM_SENDER_ID, ""))) {
			return false;
		}

		if(lastCustomGCMSenderId != null && !lastCustomGCMSenderId.equals(config.getProperty(SocializeConfig.SOCIALIZE_CUSTOM_GCM_SENDER_ID, ""))) {
			// Does not match ID
			return false;
		}

		long timeout = config.getLongProperty(SocializeConfig.GCM_REGISTRATION_INTERVAL, DEFAULT_GCM_TIMEOUT);
		long timeSinceLast = (System.currentTimeMillis() - lastC2DMRegistrationTime);

		return !(StringUtils.isEmpty(c2DMRegistrationId) || (timeSinceLast > timeout && timeout >= 0));

	}
	
	public void setRegisteredSocialize(User user) {
		this.registeredUserId = user.getId();
		this.pendingSocializeRequestTime = 0;
		this.lastSocializeRegistrationTime = System.currentTimeMillis();
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

	public void reload(Context context) {
		loaded = false;
		load(context);
	}
	
	public void load(Context context) {
		if(!loaded) {
			SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			c2DMRegistrationId = prefs.getString("c2DMRegistrationId", null);
			registeredUserId = prefs.getLong("registeredUserId", -1L);
			pendingC2DMRequestTime = prefs.getLong("pendingC2DMRequestTime", 0);
			pendingSocializeRequestTime = prefs.getLong("pendingSocializeRequestTime", 0);
			lastC2DMRegistrationTime = prefs.getLong("lastC2DMRegistrationTime", 0);
			lastSocializeRegistrationTime = prefs.getLong("lastSocializeRegistrationTime", 0);

			lastGCMSenderID = prefs.getString("lastGCMSenderID", "");
			lastCustomGCMSenderId = prefs.getString("lastCustomGCMSenderId", "");

			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Loaded notification state with registration id [" +
						c2DMRegistrationId +
						"], user id [" +
						registeredUserId +
						"]");
			}		
		}

		loaded = true;
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
		editor.putLong("lastSocializeRegistrationTime", lastSocializeRegistrationTime);
		editor.putString("lastGCMSenderID", config.getProperty(SocializeConfig.SOCIALIZE_GCM_SENDER_ID, ""));
		editor.putString("lastCustomGCMSenderId", config.getProperty(SocializeConfig.SOCIALIZE_CUSTOM_GCM_SENDER_ID, ""));

		editor.commit();
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
