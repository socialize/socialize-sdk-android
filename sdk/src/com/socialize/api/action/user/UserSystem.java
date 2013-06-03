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
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.user.UserListener;
import com.socialize.ui.profile.UserSettings;

/**
 * @author Jason Polites
 *
 */
public interface UserSystem {

	public static final String ENDPOINT = "/user/";
	
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);

	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth);
	
	public void authenticate(Context context, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);

	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);
	
	public void authenticateKnownUser(Context context, UserProviderCredentials userProviderCredentials, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);

	public void authenticate(Context context, AuthProviderType authProviderType, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);

	public SocializeSession authenticateSynchronous(Context context, String consumerKey, String consumerSecret) throws SocializeException;

	public SocializeSession authenticateSynchronous(Context context);	
	
	public void clearSession();
	
	public void saveSession(Context context, SocializeSession session);
	
	public void clearSession(AuthProviderType type);

	public void getUser(SocializeSession session, long id, UserListener listener);

	public void saveUserAsync(Context context, SocializeSession session, User user, UserListener listener);

	public void saveUserSettings(Context context, SocializeSession session, UserSettings profile, UserListener listener);
}