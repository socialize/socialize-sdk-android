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
package com.socialize.api.action.activity;

import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.listener.activity.ActionListListener;


/**
 * @author Jason Polites
 *
 */
public class SocializeActionUtils implements ActionUtilsProxy {
	
	private ActivitySystem activitySystem;

	/* (non-Javadoc)
	 * @see com.socialize.api.action.activity.ActivityUtilsProxy#getActivityByApplication(android.app.Activity, int, int, com.socialize.listener.activity.UserActivityListListener)
	 */
	@Override
	public void getActionsByApplication(Activity context, int start, int end, ActionListListener listener) {
		activitySystem.getActivityByApplication(Socialize.getSocialize().getSession(), start, end, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.activity.ActivityUtilsProxy#getActivityByUser(android.app.Activity, com.socialize.entity.User, int, int, com.socialize.listener.activity.UserActivityListListener)
	 */
	@Override
	public void getActionsByUser(Activity context, long userId, int start, int end, ActionListListener listener) {
		activitySystem.getActivityByUser(Socialize.getSocialize().getSession(), userId, start, end, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.activity.ActivityUtilsProxy#getActivityByEntity(android.app.Activity, com.socialize.entity.Entity, int, int, com.socialize.listener.activity.UserActivityListListener)
	 */
	@Override
	public void getActionsByEntity(Activity context, String entityKey, int start, int end, ActionListListener listener) {
		activitySystem.getActivityByEntity(Socialize.getSocialize().getSession(), entityKey, start, end, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.activity.ActivityUtilsProxy#getActivityByUserAndEntity(android.app.Activity, com.socialize.entity.User, com.socialize.entity.Entity, int, int, com.socialize.listener.activity.UserActivityListListener)
	 */
	@Override
	public void getActionsByUserAndEntity(Activity context, long userId, String entityKey, int start, int end, ActionListListener listener) {
		activitySystem.getActivityByUserAndEntity(Socialize.getSocialize().getSession(), userId, entityKey, start, end, listener);
	}
	
	public void setActivitySystem(ActivitySystem activitySystem) {
		this.activitySystem = activitySystem;
	}
}
