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
package com.socialize.test;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.socialize.api.PreferenceSessionPersister;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.entity.User;
import com.socialize.entity.UserFactory;

/**
 * @author Jason Polites
 *
 */
public class PublicPreferenceSessionPersister extends PreferenceSessionPersister {

	public PublicPreferenceSessionPersister() {
		super();
	}

	public PublicPreferenceSessionPersister(UserFactory userFactory, SocializeSessionFactory sessionFactory) {
		super(userFactory, sessionFactory);
	}

	@Override
	public void saveUser(Editor editor, User user) {
		super.saveUser(editor, user);
	}

	@Override
	public UserProviderCredentials makeDefaultUserProviderCredentials() {
		return super.makeDefaultUserProviderCredentials();
	}

	@Override
	public UserProviderCredentialsMap loadUserProviderCredentials(SharedPreferences prefs) {
		return super.loadUserProviderCredentials(prefs);
	}

	@Override
	public DefaultUserProviderCredentials newDefaultUserProviderCredentials() {
		return super.newDefaultUserProviderCredentials();
	}

	@Override
	public DefaultUserProviderCredentialsMap newDefaultUserProviderCredentialsMap() {
		return super.newDefaultUserProviderCredentialsMap();
	}
}
