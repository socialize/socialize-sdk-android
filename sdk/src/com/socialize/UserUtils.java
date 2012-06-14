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
package com.socialize;

import java.lang.reflect.Proxy;
import android.app.Activity;
import android.content.Context;
import com.socialize.api.action.user.UserUtilsProxy;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.profile.UserSettings;


/**
 * @author Jason Polites
 *
 */
public class UserUtils {
	
	static UserUtilsProxy proxy;
	
	static {
		proxy = (UserUtilsProxy) Proxy.newProxyInstance(
				UserUtilsProxy.class.getClassLoader(),
				new Class[]{UserUtilsProxy.class},
				new SocializeActionProxy("userUtils"));	// Bean name
	}
	
	/**
	 * Gets the Social Networks the user has elected to auto-post to.
	 * @param context The current context.
	 * @return An array of SocialNetworks, or null if the user has not elected to auto post to any.
	 */
	public static SocialNetwork[] getAutoPostSocialNetworks(Context context) {
		return proxy.getAutoPostSocialNetworks(context);
	}

	/**
	 * Returns the current logged in user.  If no user is currently authenticated this will authenticate synchronously.
	 * @param context The current context.
	 * @return The current logged in user.
	 * @throws SocializeException
	 */
	public static User getCurrentUser(Context context)  {
		return proxy.getCurrentUser(context);
	}
	
	/**
	 * Returns the settings for the current user.
	 * @param context
	 * @return
	 */
	public static UserSettings getUserSettings(Context context) {
		return proxy.getUserSettings(context);
	}
	
	/**
	 * Retrieves a User based on the given ID.
	 * @param context
	 * @param id
	 * @param listener
	 */
	public static void getUser(Context context, long id, UserGetListener listener) {
		proxy.getUser(context, id, listener);
	}
	
	/**
	 * Shows the user profile UI for the given user.
	 * @param context
	 * @param user
	 */
	public static void showUserProfile (Activity context, User user) {
		Socialize.getSocialize().showActionDetailView(context, user, null);
	}
	
	/**
	 * Shows the settings UI for the current user.
	 * @param context
	 * @throws SocializeException If the current user could not be found or authenticated.
	 */
	public static void showUserSettings (Activity context)  {
		Socialize.getSocialize().showUserProfileView(context, UserUtils.getCurrentUser(context).getId());
	}
	
	/**
	 * Saves the profile for the given user.
	 * @param context
	 * @param user
	 * @param listener
	 */
	public static void saveUserSettings (Context context, UserSettings userSettings, UserSaveListener listener) {
		proxy.saveUserSettings(context, userSettings, listener);
	}
	
	/**
	 * Clears the saved session state for the user.  
	 * WARNING: This will wipe any locally saved preferences for this user.
	 * @param context
	 */
	public static void clearCache(Context context) {
		if(!Socialize.getSocialize().isInitialized(context)) {
			Socialize.getSocialize().init(context);
		}
		Socialize.getSocialize().clearSessionCache(context);
	}
}
