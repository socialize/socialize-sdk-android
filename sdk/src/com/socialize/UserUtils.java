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

import android.app.Activity;
import android.content.Context;
import com.socialize.api.action.user.UserUtilsProxy;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.action.OnActionDetailViewListener;
import com.socialize.ui.profile.UserSettings;

import java.lang.reflect.Proxy;


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
	public static SocialNetwork[] getAutoPostSocialNetworks(Context context) throws SocializeException {
		return proxy.getAutoPostSocialNetworks(context);
	}

	/**
	 * Returns the current logged in user.  If no user is currently authenticated this will authenticate synchronously.
	 * @param context The current context.
	 * @return The current logged in user
	 * @throws SocializeException
	 */
	public static User getCurrentUser(Context context) throws SocializeException  {
		return proxy.getCurrentUser(context);
	}

	/**
	 * Returns the current logged in user.  If no user is currently authenticated this will authenticate asynchronously.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 */
	public static void getCurrentUserAsync(Context context, UserGetListener listener)  {
		proxy.getCurrentUserAsync(context, listener);
	}

	/**
	 * Saves the given user.
	 * @param context The current context.
	 * @param user The user to be saved.
	 * @param listener A listener to handle the result.
	 */
	public static void saveUserAsync(Context context, User user, UserSaveListener listener) {
		proxy.saveUserAsync(context, user, listener);
	}

	/**
	 * Returns the settings for the current user.
	 * @param context The current context.
	 * @return The settings for the current user.
	 */
	public static UserSettings getUserSettings(Context context) throws SocializeException {
		return proxy.getUserSettings(context);
	}
	
	/**
	 * Retrieves a User based on the given ID.
	 * @param context The current context.
	 * @param id The id of the user.
	 * @param listener A listener to handle the GET.
	 */
	public static void getUser(Context context, long id, UserGetListener listener) {
		proxy.getUser(context, id, listener);
	}
	
	/**
	 * Shows the user profile UI for the given user.
	 * @param context The current context.
	 * @param user The user for whom the profile will be shown.
	 */
	public static void showUserProfile (Activity context, User user) {
		proxy.showUserProfileView(context, user, null, null);
	}
	
	/**
	 * Shows the user profile UI for the given user.
	 * @param context The current context.
	 * @param user The user for whom the profile will be shown.
	 * @param action The action (comment/share/like) that was performed.
	 */
	public static void showUserProfileWithAction (Activity context, User user, SocializeAction action) {
		proxy.showUserProfileView(context, user, action, null);
	}


	/**
	 * Shows the user profile UI for the given user.
	 * @param context The current context.
	 * @param user The user for whom the profile will be shown.
	 * @param onActionDetailViewListener A listener to handle UI view events.
	 */
	public static void showUserProfile (Activity context, User user, OnActionDetailViewListener onActionDetailViewListener) {
		proxy.showUserProfileView(context, user, null, onActionDetailViewListener);
	}

	/**
	 * Shows the user profile UI for the given user.
	 * @param context The current context.
	 * @param user The user for whom the profile will be shown.
	 * @param action The action (comment/share/like) that was performed.
	 * @param onActionDetailViewListener A listener to handle UI view events.
	 */
	public static void showUserProfileWithAction (Activity context, User user, SocializeAction action, OnActionDetailViewListener onActionDetailViewListener) {
		proxy.showUserProfileView(context, user, action, onActionDetailViewListener);
	}


	/**
	 * Shows the settings UI for the current user.
	 * @param context The current context.
	 */
	public static void showUserSettings (Activity context)  {
		try {
			proxy.showUserSettingsView(context, UserUtils.getCurrentUser(context).getId());
		}
		catch (SocializeException e) {
			SocializeLogger.e("Error displaying user settings", e);
		}		
	}
	
	/**
	 * Shows the settings UI for the current user.
	 * @param context The current context.
	 * @param requestCode (Optional)  Set as the result on the UserSettings activity.
	 */
	public static void showUserSettingsForResult(Activity context, int requestCode) {
		try {
			proxy.showUserSettingsViewForResult(context, UserUtils.getCurrentUser(context).getId(), requestCode);
		}
		catch (SocializeException e) {
			SocializeLogger.e("Error displaying user settings", e);
		}
	}
	
	/**
	 * Saves the profile for the given user.
	 * @param context The current context.
	 * @param userSettings The user settings to be saved.
	 * @param listener A listener to handle the save.
	 */
	public static void saveUserSettings (Context context, UserSettings userSettings, UserSaveListener listener) {
		proxy.saveUserSettings(context, userSettings, listener);
	}
	
	/**
	 * Clears the saved session state for the user.  
	 * WARNING: This will wipe any locally saved preferences for this user.
	 * @param context The current context.
	 */
	public static void clearLocalSessionData(Context context) {
		proxy.clearSession(context);
	}
}
