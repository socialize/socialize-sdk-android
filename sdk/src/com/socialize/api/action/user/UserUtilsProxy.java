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
package com.socialize.api.action.user;

import android.content.Context;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.User;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.profile.UserSettings;


/**
 * @author Jason Polites
 *
 */
public interface UserUtilsProxy {

	/**
	 * Synchronously returns the current user.  
	 * If the Socialize instance has not been initialized it will be initialized synchronously here.
	 * If the Socialize instance has not authenticated it will be authenticated synchronously here.
	 * @param context The current context.
	 * @return The current user.
	 */
	@Synchronous
	public User getCurrentUser(Context context);
	
	/**
	 * Returns the settings for the current user.
	 * @param context The current context.
	 * @return The settings for the current user.
	 */
	@Synchronous
	public UserSettings getUserSettings(Context context);
	
	/**
	 * Gets the Social Networks the user has elected to auto-post to.
	 * @param context
	 * @return
	 */
	@Synchronous
	public SocialNetwork[] getAutoPostSocialNetworks(Context context);
	
	/**
	 * 
	 * @param context
	 * @param id
	 * @param listener
	 */
	public void getUser(Context context, long id, UserGetListener listener);
	
	/**
	 * Saves the current user's settings;
	 * @param context
	 * @param settings
	 * @param listener
	 */
	public void saveUserSettings (Context context, UserSettings settings, UserSaveListener listener);
	
	public void clearSession(Context context);
}
