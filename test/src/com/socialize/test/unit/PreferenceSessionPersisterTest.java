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
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.entity.User;
import com.socialize.entity.UserFactory;
import com.socialize.test.PublicPreferenceSessionPersister;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockEditor;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.profile.UserSettingsFactory;
import com.socialize.util.JSONUtils;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ 
	MockContext.class, 
	SharedPreferences.class, 
	MockEditor.class, 
	SocializeSession.class, 
	User.class, 
	UserSettings.class,
	UserFactory.class, 
	UserSettingsFactory.class, 
	SocializeSessionFactory.class, 
	SocializeSessionImpl.class, 
	JSONUtils.class })
public class PreferenceSessionPersisterTest extends SocializeActivityTest {

	MockContext context;
	SharedPreferences prefs;
	MockEditor editor;
	SocializeSessionImpl session;
	User user = null;
	UserSettings userSettings = null;
	UserFactory userFactory;
	UserSettingsFactory userSettingsFactory;
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
		userSettings = AndroidMock.createMock(UserSettings.class);
		userFactory = AndroidMock.createMock(UserFactory.class);
		userSettingsFactory = AndroidMock.createMock(UserSettingsFactory.class);
		jsonUtils = AndroidMock.createMock(JSONUtils.class);
		socializeSessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
	}

	@UsesMocks ({UserProviderCredentialsMap.class})
	public void testPreferencePersistLoad() throws JSONException {

		final UserProviderCredentialsMap userProviderCredentialsMap = AndroidMock.createMock(UserProviderCredentialsMap.class);
		
		final String key = "foo", secret = "bar", token = "sna", tokenSecret = "fu", json = "{foo:bar}", settings = "{settings:here}";
		
		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.getString("consumer_key", null)).andReturn(key);
		AndroidMock.expect(prefs.getString("consumer_secret", null)).andReturn(secret);
		AndroidMock.expect(prefs.getString("consumer_token", null)).andReturn(token);
		AndroidMock.expect(prefs.getString("consumer_token_secret", null)).andReturn(tokenSecret);
		AndroidMock.expect(prefs.getString("user", null)).andReturn(json);
		AndroidMock.expect(prefs.getString("user-settings", null)).andReturn(settings);
		
		AndroidMock.expect(userFactory.fromJSON((JSONObject) AndroidMock.anyObject())).andReturn(user);
		AndroidMock.expect(userSettingsFactory.fromJSON((JSONObject) AndroidMock.anyObject())).andReturn(userSettings);
		
		AndroidMock.expect(socializeSessionFactory.create(key, secret, userProviderCredentialsMap)).andReturn(session);

		session.setConsumerToken(token);
		session.setConsumerTokenSecret(tokenSecret);
		session.setUser(user);
		session.setUserSettings(userSettings);
		session.setRestored(true);

		AndroidMock.replay(context,prefs,session,userFactory,userSettingsFactory,socializeSessionFactory);

		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory) {
			@Override
			protected UserProviderCredentialsMap loadUserProviderCredentials(SharedPreferences prefs) {
				return userProviderCredentialsMap;
			}
		};

		persister.setUserSettingsFactory(userSettingsFactory);
		
		SocializeSession loaded = persister.load(context);

		assertSame(session, loaded);

		AndroidMock.verify(context,prefs,session,userFactory,userSettingsFactory,socializeSessionFactory);
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
		AndroidMock.expect(session.getUserSettings()).andReturn(userSettings);
		
		AndroidMock.expect(userFactory.toJSON(user)).andReturn(jsonObject);
		AndroidMock.expect(userSettingsFactory.toJSON(userSettings)).andReturn(jsonObject);
		
		AndroidMock.expect(editor.putString("consumer_key", key)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_secret", secret)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_token", token)).andReturn(editor);
		AndroidMock.expect(editor.putString("consumer_token_secret", tokenSecret)).andReturn(editor);
		AndroidMock.expect(editor.putString("user", json)).andReturn(editor);
		AndroidMock.expect(editor.putString("user-settings", json)).andReturn(editor);
		
		AndroidMock.expect(editor.commit()).andReturn(true);

		AndroidMock.replay(context,prefs,session,userFactory,userSettingsFactory,editor,jsonUtils);

		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory);
		persister.setJsonUtils(jsonUtils);
		persister.setUserSettingsFactory(userSettingsFactory);

		persister.save(context, session);

		AndroidMock.verify(context,prefs,session,userFactory,userSettingsFactory,editor,jsonUtils);
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
	
	@UsesMocks ({SharedPreferences.class, MockContext.class, MockEditor.class, JSONUtils.class})
	public void testPreferencePersistDeleteByType() throws JSONException {
		
		MockContext context = AndroidMock.createMock(MockContext.class);
		SharedPreferences prefs = AndroidMock.createMock(SharedPreferences.class);
		JSONUtils jsonUtils = AndroidMock.createMock(JSONUtils.class);
		MockEditor editor = AndroidMock.createMock(MockEditor.class);
		String authData = "foobar";
		String authData_after = "foobar_after";
		
		// Use a real UserProviderCredentialsMap to make the test behavioral
		DefaultUserProviderCredentialsMap map = new DefaultUserProviderCredentialsMap();
		DefaultUserProviderCredentials facebook = new DefaultUserProviderCredentials();
		DefaultUserProviderCredentials twitter = new DefaultUserProviderCredentials();
		
		map.put(AuthProviderType.FACEBOOK, facebook);
		map.put(AuthProviderType.TWITTER, twitter);
		
		assertNotNull(map.get(AuthProviderType.FACEBOOK));
		assertNotNull(map.get(AuthProviderType.TWITTER));
		
		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.getString("user_auth_data", null)).andReturn(authData);
		
		AndroidMock.expect(jsonUtils.fromJSON(authData, UserProviderCredentialsMap.class)).andReturn(map);
		AndroidMock.expect(jsonUtils.toJSON(map)).andReturn(authData_after);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(editor.putString("user_auth_data", authData_after)).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);
		
		AndroidMock.replay(context, jsonUtils, prefs, editor);
		
		PreferenceSessionPersister perister = new PreferenceSessionPersister();
		perister.setJsonUtils(jsonUtils);
		
		perister.delete(context, AuthProviderType.FACEBOOK);
		
		AndroidMock.verify(context, jsonUtils, prefs, editor);
		
		assertNull(map.get(AuthProviderType.FACEBOOK));
		assertNotNull(map.get(AuthProviderType.TWITTER));
		
	}
	
	@UsesMocks ({SharedPreferences.class})
	public void testLegacySessionMigrateWithoutFB() {
		SharedPreferences prefs = AndroidMock.createMock(SharedPreferences.class);
		
		AndroidMock.expect(prefs.getString("user_auth_data", null)).andReturn(null);
		AndroidMock.expect(prefs.getString("3rd_party_userid", null)).andReturn(null);
		AndroidMock.expect(prefs.getString("3rd_party_token", null)).andReturn(null);
		AndroidMock.expect(prefs.getInt("3rd_party_type", AuthProviderType.SOCIALIZE.getId())).andReturn(AuthProviderType.SOCIALIZE.getId());
		
		AndroidMock.replay(prefs);
		
		PublicPreferenceSessionPersister persister = new PublicPreferenceSessionPersister();
		UserProviderCredentialsMap map = persister.loadUserProviderCredentials(prefs);
		
		AndroidMock.verify(prefs);
		
		assertNotNull(map);
		
		UserProviderCredentials credsFB = map.get(AuthProviderType.FACEBOOK);
		
		assertNull(credsFB);
		
		UserProviderCredentials credsS = map.get(AuthProviderType.SOCIALIZE);
		
		assertNotNull(credsS);
		
		AuthProviderInfo authProviderInfoS = credsS.getAuthProviderInfo();
		
		assertNotNull(authProviderInfoS);
		assertTrue((authProviderInfoS instanceof SocializeAuthProviderInfo));
	}
	
	@UsesMocks ({SharedPreferences.class})
	public void testLegacySessionMigrateWithFB() {
		
		String userId3rdParty = "fb_user_id";
		String token3rdParty = "fb_token";
		String appId3rdParty = "fb_appid";
		
		SharedPreferences prefs = AndroidMock.createMock(SharedPreferences.class);
		
		AndroidMock.expect(prefs.getString("user_auth_data", null)).andReturn(null);
		AndroidMock.expect(prefs.getString("3rd_party_userid", null)).andReturn(userId3rdParty);
		AndroidMock.expect(prefs.getString("3rd_party_token", null)).andReturn(token3rdParty);
		AndroidMock.expect(prefs.getString("3rd_party_app_id", null)).andReturn(appId3rdParty);
		AndroidMock.expect(prefs.getInt("3rd_party_type", AuthProviderType.SOCIALIZE.getId())).andReturn(AuthProviderType.FACEBOOK.getId());
		
		AndroidMock.replay(prefs);
		
		PublicPreferenceSessionPersister persister = new PublicPreferenceSessionPersister();
		UserProviderCredentialsMap map = persister.loadUserProviderCredentials(prefs);
		
		AndroidMock.verify(prefs);
		
		assertNotNull(map);
		
		UserProviderCredentials credsFB = map.get(AuthProviderType.FACEBOOK);
		
		assertNotNull(credsFB);
		assertEquals(token3rdParty, credsFB.getAccessToken());
		assertEquals(userId3rdParty, credsFB.getUserId());
		
		AuthProviderInfo authProviderInfo = credsFB.getAuthProviderInfo();
		
		assertNotNull(authProviderInfo);
		assertTrue((authProviderInfo instanceof FacebookAuthProviderInfo));
		assertEquals(appId3rdParty, ((FacebookAuthProviderInfo)authProviderInfo).getAppId());
		
		UserProviderCredentials credsS = map.get(AuthProviderType.SOCIALIZE);
		
		assertNotNull(credsS);
		
		AuthProviderInfo authProviderInfoS = credsS.getAuthProviderInfo();
		
		assertNotNull(authProviderInfoS);
		assertTrue((authProviderInfoS instanceof SocializeAuthProviderInfo));
		
	}
}
