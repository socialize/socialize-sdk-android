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

import android.app.Activity;
import android.content.Context;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.action.OnActionDetailViewListener;
import com.socialize.ui.profile.UserSettings;


/**
 * @author Jason Polites
 */
public interface UserUtilsProxy {

	@Synchronous
	public User getCurrentUser(Context context) throws SocializeException;

	public void getCurrentUserAsync(Context context, UserGetListener listener);
	
	@Synchronous
	public UserSettings getUserSettings(Context context) throws SocializeException;
	
	@Synchronous
	public SocialNetwork[] getAutoPostSocialNetworks(Context context) throws SocializeException;
	
	public void getUser(Context context, long id, UserGetListener listener);
	
	public void saveUserSettings (Context context, UserSettings settings, UserSaveListener listener);

	public void saveUserAsync (Context context, User user, UserSaveListener listener);
	
	@Synchronous
	public void showUserSettingsView(Activity context, Long userId);
	
	@Synchronous
	public void showUserSettingsViewForResult(Activity context, Long userId, int requestCode);
	
	@Synchronous
	public void showUserProfileView(Activity context, User user, SocializeAction action, OnActionDetailViewListener onActionDetailViewListener);
	
	@Synchronous
	public void clearSession(Context context);
}
