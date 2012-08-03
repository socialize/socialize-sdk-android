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

import com.socialize.auth.AuthProviderData;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;

/**
 * @author Jason Polites
 *
 */
public interface SocializeSessionFactory {

	/**
	 * Creates a new session.
	 * @param key The socialize consumer key.
	 * @param secret The socialize consumer secret.
	 * @param userProviderCredentials
	 * @return A new writable session object.
	 */
	public WritableSession create(String key, String secret, UserProviderCredentials userProviderCredentials);
	
	/**
	 * Creates a new session.
	 * @param key The socialize consumer key.
	 * @param secret The socialize consumer secret.
	 * @param userProviderCredentialsMap Data pertaining to the auth provider used when creating the session.
	 * @return A new writable session object.
	 */
	public WritableSession create(String key, String secret, UserProviderCredentialsMap userProviderCredentialsMap);
	
	/**
	 * Creates a new session.
	 * @param key The socialize consumer key.
	 * @param secret The socialize consumer secret.
	 * @param data Data pertaining to the auth provider used when creating the session.
	 * @return A new writable session object.
	 */
	public WritableSession create(String key, String secret, AuthProviderData data);
	
}
