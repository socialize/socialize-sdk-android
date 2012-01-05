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
package com.socialize.ui.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class exists because Android doesn't work.  Well, that's not really fair, 
 * but the onNewIntent() method of Activities is rarely called when it's supposed to, 
 * see here: http://code.google.com/p/android/issues/detail?id=17137.
 * 
 * So.. if an activity is already running, and you want to send new data to it in its
 * Intent.. you can't.  The getIntent() method will return the Intent used to create 
 * the Activity the first time.. weird.
 * 
 * @author Jason Polites
 *
 */
public class ActionIdPersister {

	private static final String PREFERENCES = "ActionData";
	
	private String actionId;
	private String userId;
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void save(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("userId", userId);
		editor.putString("actionId", actionId);
		editor.commit();
	}
	
	public void load(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		userId = prefs.getString("userId", null);
		actionId = prefs.getString("actionId", null);
	}
	
	public void reset(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		prefs.edit().clear().commit();
	}
}
