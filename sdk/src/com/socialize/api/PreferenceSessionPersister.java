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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.socialize.Socialize;
import com.socialize.auth.*;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.entity.User;
import com.socialize.entity.UserFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.profile.UserSettingsFactory;
import com.socialize.util.JSONUtils;
import com.socialize.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Persists session data to private preferences.
 * @author Jason Polites
 */
public class PreferenceSessionPersister implements SocializeSessionPersister {

	private static final String PREFERENCES = "SocializeSession";
	
	private static final String SOCIALIZE_VERSION = "socialize_version";
	private static final String USER_AUTH_DATA = "user_auth_data";
	
	private UserFactory userFactory;
	private UserSettingsFactory userSettingsFactory;
	private SocializeSessionFactory sessionFactory;
	
	private SocializeLogger logger = null;
	private JSONUtils jsonUtils;
	
	public PreferenceSessionPersister() {
		super();
	}

	public PreferenceSessionPersister(UserFactory userFactory, SocializeSessionFactory sessionFactory) {
		super();
		this.userFactory = userFactory;
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public synchronized void saveUser(Context context, User user, UserSettings userSettings) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		saveUser(editor, user, userSettings);
		editor.commit();
	}
	
	@Override
	public void saveUserSettingsAsync(final Context context, final UserSettings settings) {
		new Thread() {
			@Override
			public void run() {
				SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
				Editor editor = prefs.edit();
				try {
					String userJSON = userSettingsFactory.toJSON(settings).toString();
					editor.putString("user-settings", userJSON);
					editor.commit();
				}
				catch (JSONException e) {
					if(logger != null) {
						logger.error("Failed to serialize user settings object", e);
					}
					else {
						SocializeLogger.e("Failed to serialize user settings object", e);
					}
				}
			}
		}.start();
	}

	public synchronized void saveUser(Editor editor, User user, UserSettings userSettings) {
		if(user != null) {
			try {
				String userJSON = userFactory.toJSON(user).toString();
				editor.putString("user", userJSON);
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to serialize user object", e);
				}
				else {
					SocializeLogger.e("Failed to serialize user object", e);
				}
			}
			
			if(userSettings == null) {
				// Legacy
				userSettings = createSettingsLegacy(user);
			}
			
			try {
				String userJSON = userSettingsFactory.toJSON(userSettings).toString();
				editor.putString("user-settings", userJSON);
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to serialize user settings object", e);
				}
				else {
					SocializeLogger.e("Failed to serialize user settings object", e);
				}
			}
		}			
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#delete(android.content.Context, com.socialize.auth.AuthProviderType)
	 */
	@Override
	public synchronized void delete(Context context, AuthProviderType type) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		String authData = prefs.getString(USER_AUTH_DATA, null);
		
		Editor editor = prefs.edit();
		
		if(!StringUtils.isEmpty(authData)) {
			 UserProviderCredentialsMap map = jsonUtils.fromJSON(authData, UserProviderCredentialsMap.class);
			 
			 map.remove(type);
			 
			 editor.putString(USER_AUTH_DATA, jsonUtils.toJSON(map));
		}
		else {
			editor.putString(USER_AUTH_DATA, jsonUtils.toJSON(makeDefaultUserProviderCredentials()));
		}
		
		editor.commit();
	}
	
	protected UserProviderCredentials makeDefaultUserProviderCredentials() {
		DefaultUserProviderCredentials userProviderCredentials = newDefaultUserProviderCredentials();
		SocializeAuthProviderInfo info = new SocializeAuthProviderInfo();
		userProviderCredentials.setAuthProviderInfo(info);
		return userProviderCredentials;
	}
	

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#save(android.content.Context, com.socialize.api.SocializeSession)
	 */
	@Override
	public synchronized void save(Context context, SocializeSession session) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		Editor editor = prefs.edit();
		
		editor.putString("consumer_key", session.getConsumerKey());
		editor.putString("consumer_secret", session.getConsumerSecret());
		editor.putString("consumer_token", session.getConsumerToken());
		editor.putString("consumer_token_secret", session.getConsumerTokenSecret());

		editor.putString(SOCIALIZE_VERSION, Socialize.VERSION);
		
		String userProviderCredentials = jsonUtils.toJSON(session.getUserProviderCredentials());
		
		editor.putString(USER_AUTH_DATA, userProviderCredentials);	
		
		User user = session.getUser();
		UserSettings settings = session.getUserSettings();
		
		saveUser(editor, user, settings);

		editor.commit();
	}
	

	@SuppressWarnings("deprecation")
	protected UserSettings createSettingsLegacy(User user) {
		UserSettings userSettings = new UserSettings();
		userSettings.setAutoPostFacebook(user.isAutoPostToFacebook());
		userSettings.setAutoPostTwitter(user.isAutoPostToTwitter());
		userSettings.setFirstName(user.getFirstName());
		userSettings.setLastName(user.getLastName());
		userSettings.setLocationEnabled(user.isShareLocation());
		userSettings.setNotificationsEnabled(user.isNotificationsEnabled());	
		return userSettings;
	}
	
	protected UserProviderCredentialsMap loadUserProviderCredentials(SharedPreferences prefs) {
		String authData = prefs.getString(USER_AUTH_DATA, null);
		
		UserProviderCredentialsMap map = null;
		
		if(StringUtils.isEmpty(authData)) {
			
			String userId3rdParty = prefs.getString("3rd_party_userid", null);
			String token3rdParty = prefs.getString("3rd_party_token", null);
			
			int iProviderType = prefs.getInt("3rd_party_type", AuthProviderType.SOCIALIZE.getId());
			
			AuthProviderType type = AuthProviderType.valueOf(iProviderType);
			
			map = newDefaultUserProviderCredentialsMap();
			
			// Legacy
			if(type.equals(AuthProviderType.FACEBOOK)) {
				
				// Legacy, must only be FB
				DefaultUserProviderCredentials data = newDefaultUserProviderCredentials();
				
				String appId3rdParty = prefs.getString("3rd_party_app_id", null);
				
				data.setAccessToken(token3rdParty);
				data.setUserId(userId3rdParty);
				
				FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
				info.setAppId(appId3rdParty);
				
				data.setAuthProviderInfo(info);
				
				map.put(type, data);
			}
			else {
				if(!type.equals(AuthProviderType.SOCIALIZE)) {
					if(logger != null) {
						logger.warn("Unexpected auth type [" +
								type +
								"].  Legacy session loading only supports [" +
								AuthProviderType.FACEBOOK +
								"] or [" +
								AuthProviderType.SOCIALIZE +
								"].");
					}
					type = AuthProviderType.SOCIALIZE;
				}
			}
			
			// Always add Socialize
			SocializeAuthProviderInfo info = new SocializeAuthProviderInfo();
			DefaultUserProviderCredentials data = newDefaultUserProviderCredentials();
			data.setAuthProviderInfo(info);
			map.put(AuthProviderType.SOCIALIZE, data);
		}
		else {
			map = jsonUtils.fromJSON(authData, UserProviderCredentialsMap.class);
		}
		
		return map;
	}
	
	protected DefaultUserProviderCredentials newDefaultUserProviderCredentials() {
		return new DefaultUserProviderCredentials();
	}
	
	protected DefaultUserProviderCredentialsMap newDefaultUserProviderCredentialsMap() {
		return new DefaultUserProviderCredentialsMap();
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#load(android.content.Context)
	 */
	@Override
	public synchronized WritableSession load(Context context) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		String key = prefs.getString("consumer_key", null);
		String secret = prefs.getString("consumer_secret", null);
		
		UserProviderCredentialsMap userProviderCredentials = loadUserProviderCredentials(prefs);
		
		WritableSession session = sessionFactory.create(key, secret, userProviderCredentials);
			
		session.setRestored(true);
		
		String oauth_token = prefs.getString("consumer_token", null);
		String oauth_token_secret = prefs.getString("consumer_token_secret", null);
		
		if(StringUtils.isEmpty(oauth_token) || StringUtils.isEmpty(oauth_token_secret)) {
			return null;
		}

		session.setConsumerToken(oauth_token);
		session.setConsumerTokenSecret(oauth_token_secret);
		
		String userJson = prefs.getString("user", null);
		
		User user = null;
		
		if(userJson != null) {
			try {
				JSONObject json = new JSONObject(userJson);
				user = userFactory.fromJSON(json);
				session.setUser(user);
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to deserialize user object", e);
				}
				else {
					SocializeLogger.e("Failed to deserialize user object", e);
				}
			}
		}
		
		String userSettingsJson = prefs.getString("user-settings", null);
		
		if(userSettingsJson != null) {
			try {
				JSONObject json = new JSONObject(userSettingsJson);
				UserSettings userSettings = userSettingsFactory.fromJSON(json);
				session.setUserSettings(userSettings);
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to deserialize user settings object", e);
				}
				else {
					SocializeLogger.e("Failed to deserialize user settings object", e);
				}
			}
		}
		else if(user != null) {
			session.setUserSettings(createSettingsLegacy(user));
		}
		
		return session;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#delete(android.content.Context)
	 */
	@Override
	public synchronized void delete(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		prefs.edit().clear().commit();
	}

	public void setUserFactory(UserFactory userFactory) {
		this.userFactory = userFactory;
	}

	public void setSessionFactory(SocializeSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setJsonUtils(JSONUtils jsonUtils) {
		this.jsonUtils = jsonUtils;
	}
	
	public void setUserSettingsFactory(UserSettingsFactory userSettingsFactory) {
		this.userSettingsFactory = userSettingsFactory;
	}
}
