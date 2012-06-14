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
package com.socialize.api;

import java.io.Serializable;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.entity.User;
import com.socialize.oauth.OAuthAuthorizer;
import com.socialize.ui.profile.UserSettings;

/**
 * @author Jason Polites
 *
 */
public interface SocializeSession extends Serializable, OAuthAuthorizer {

	public User getUser();
	
	public UserSettings getUserSettings();

	public String getConsumerKey();

	public String getConsumerSecret();
	
	public String getConsumerToken();
	
	public String getConsumerTokenSecret();
	
	public String getHost();
	
	/**
	 * Returns the set of session data for any 3rd party providers.
	 * @return
	 */
	public UserProviderCredentialsMap getUserProviderCredentials();
	
	public UserProviderCredentials getUserProviderCredentials(AuthProviderType type);
	
	/**
	 * @deprecated use getUserProviderCredentials
	 * @return
	 */
	@Deprecated
	public String get3rdPartyUserId();
	
	/**
	 * @deprecated use getUserProviderCredentials
	 * @return
	 */
	@Deprecated
	public String get3rdPartyToken();
	
	/**
	 * @deprecated use getUserProviderCredentials
	 * @return
	 */
	@Deprecated
	public String get3rdPartyAppId();

	@Deprecated
	public AuthProviderType getAuthProviderType();
	
	@Deprecated
	public AuthProvider<?> getAuthProvider();
	
	public void clear(AuthProviderType type);
	
	/**
	 * Returns true if this session was restored from a previously saved session
	 * @return
	 */
	public boolean isRestored();

}
