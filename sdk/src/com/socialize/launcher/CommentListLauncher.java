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
package com.socialize.launcher;

import android.app.Activity;
import android.os.Bundle;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.activity.ActivitySystem;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.NotificationAuthenticator;
import com.socialize.util.EntityLoaderUtils;
import com.socialize.util.StringUtils;


/**
 * Loads the comment list.
 * @author Jason Polites
 */
public class CommentListLauncher extends BaseLauncher {
	
	private EntityLoaderUtils entityLoaderUtils;
	private SocializeLogger logger;
	private ActivitySystem activitySystem;
	private NotificationAuthenticator notificationAuthenticator;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(Activity context, Bundle data) {
		
		if(entityLoaderUtils != null) {
			entityLoaderUtils.initEntityLoader();
		}
		
		// Expect an action id
		Object idObj = data.get(Socialize.ACTION_ID);
		String actionType = data.getString(Socialize.ACTION_TYPE);
		
		if(idObj != null && !StringUtils.isEmpty(actionType)) {
			
			long id = Long.parseLong(idObj.toString());
			
			try {
				ActionType type = ActionType.valueOf(actionType);
				
				SocializeSession session = notificationAuthenticator.authenticate(context);
				SocializeAction action = activitySystem.getAction(session, id, type);
				
				if(action != null) {
					CommentUtils.showCommentView(context, action.getEntity());
					return true;
				}
				else {
					handleWarn("No action found for id [" +
							id +
							"].");
				}
			}
			catch (SocializeException e) {
				handleError("Failed to load entity", e);
			}
		}
		else {
			handleWarn("No action id found.  Action based notification cannot be handled");
		}		
		
		return false;
	}
	
	protected void handleError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}		
	
	protected void handleWarn(String msg) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
		}
	}

	public void setEntityLoaderUtils(EntityLoaderUtils entityLoaderUtils) {
		this.entityLoaderUtils = entityLoaderUtils;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setActivitySystem(ActivitySystem activitySystem) {
		this.activitySystem = activitySystem;
	}
	
	public void setNotificationAuthenticator(NotificationAuthenticator notificationAuthenticator) {
		this.notificationAuthenticator = notificationAuthenticator;
	}
}
