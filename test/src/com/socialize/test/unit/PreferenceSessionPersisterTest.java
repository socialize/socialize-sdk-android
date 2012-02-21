/*
 * Copyright (c) 2011 Socialize Inc. 
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
package com.socialize.test.unit;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.api.PreferenceSessionPersister;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.api.SocializeSessionImpl;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.entity.User;
import com.socialize.entity.UserFactory;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockEditor;
import com.socialize.util.JSONUtils;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ MockContext.class, SharedPreferences.class, MockEditor.class, SocializeSession.class, User.class, UserFactory.class, SocializeSessionFactory.class, SocializeSessionImpl.class, JSONUtils.class })
public class PreferenceSessionPersisterTest extends SocializeActivityTest {

	MockContext context;
	SharedPreferences prefs;
	MockEditor editor;
	SocializeSessionImpl session;
	User user = null;
	UserFactory userFactory;
	JSONObject jsonObject;
	SocializeSessionFactory socializeSessionFactory;
	JSONUtils jsonUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		context = AndroidMock.createMock(MockContext.class);
		prefs = AndroidMock.createMock(SharedPreferences.class);
		editor = AndroidMock.createMock(MockEditor.class);
		session = AndroidMock.createMock(SocializeSessionImpl.class);
		user = AndroidMock.createMock(User.class);
		userFactory = AndroidMock.createMock(UserFactory.class);
		jsonUtils = AndroidMock.createMock(JSONUtils.class);
		socializeSessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
	}

	@UsesMocks ({UserProviderCredentialsMap.class})
	public void testPreferencePersistLoad() throws JSONException {

		final UserProviderCredentialsMap userProviderCredentialsMap = AndroidMock.createMock(UserProviderCredentialsMap.class);
		
		final String key = "foo", secret = "bar", token = "sna", tokenSecret = "fu", json = "{foo:bar}";
		
		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.getString("consumer_key", null)).andReturn(key);
		AndroidMock.expect(prefs.getString("consumer_secret", null)).andReturn(secret);
		AndroidMock.expect(prefs.getString("consumer_token", null)).andReturn(token);
		AndroidMock.expect(prefs.getString("consumer_token_secret", null)).andReturn(tokenSecret);
		AndroidMock.expect(prefs.getString("user", null)).andReturn(json);
		
		AndroidMock.expect(userFactory.fromJSON((JSONObject) AndroidMock.anyObject())).andReturn(user);
		
		AndroidMock.expect(socializeSessionFactory.create(key, secret, userProviderCredentialsMap)).andReturn(session);

		session.setConsumerToken(token);
		session.setConsumerTokenSecret(tokenSecret);
		session.setUser(user);

		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(session);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(socializeSessionFactory);

		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory) {

			@Override
			protected UserProviderCredentialsMap loadUserProviderCredentials(SharedPreferences prefs) {
				return userProviderCredentialsMap;
			}
			
			
		};

		SocializeSession loaded = persister.load(context);

		assertSame(session, loaded);

		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(session);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(socializeSessionFactory);
	}

	@UsesMocks ({UserProviderCredentialsMap.class})
	public void testPreferencePersistSave() throws JSONException {
		final UserProviderCredentialsMap userProviderCredentialsMap = AndroidMock.createMock(UserProviderCredentialsMap.class);
		final String key = "foo", secret = "bar", token = "sna", tokenSecret = "fu", json = "{foo:bar}", credsJson = "{creds:foobar}";

		JSONObject jsonObject = new JSONObject() {
			@Override
			public String toString() {
				return json;
			}
		};

		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(session.getConsumerKey()).andReturn(key);
		AndroidMock.expect(session.getConsumerSecret()).andReturn(secret);
		AndroidMock.expect(session.getConsumerToken()).andReturn(token);
		AndroidMock.expect(session.getConsumerTokenSecret()).andReturn(tokenSecret);
		AndroidMock.expect(session.getUserProviderCredentials()).andReturn(userProviderCredentialsMap);
		AndroidMock.expect(jsonUtils.toJSON(userProviderCredentialsMap)).andReturn(credsJson);
		
		AndroidMock.expect(editor.putString("user_auth_data", credsJson)).andReturn(editor);
		AndroidMock.expect(editor.putString("socialize_version", Socialize.VERSION)).andReturn(editor);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(userFactory.toJSON(user)).andReturn(jsonObject);
		AndroidMock.expect(editor.putString("consumer_key", key)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_secret", secret)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_token", token)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_token_secret", tokenSecret)).andReturn(editor);
		AndroidMock.expect(editor.putString("user", json)).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);

		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(session);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(editor);
		AndroidMock.replay(jsonUtils);

		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory);
		persister.setJsonUtils(jsonUtils);

		persister.save(context, session);

		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(session);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(editor);
		AndroidMock.verify(jsonUtils);
	}

	public void testPreferencePersistDelete() throws JSONException {

		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(editor.clear()).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);

		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(editor);

		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory);

		persister.delete(context);

		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(editor);
	}
}
