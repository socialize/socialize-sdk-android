/*
 * Copyright (c) 2011 SocializeService Inc.
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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.socialize.entity.User;
import com.socialize.entity.factory.UserFactory;
import com.socialize.log.SocializeLogger;

/**
 * Persists session data to private preferences.
 * @author Jason Polites
 */
public class PreferenceSessionPersister implements SocializeSessionPersister {

	private static final String PREFERENCES = "SocializeSession";
	
	private UserFactory userFactory;
	private SocializeSessionFactory sessionFactory;
	
	private SocializeLogger logger = null;
	
	public PreferenceSessionPersister(UserFactory userFactory, SocializeSessionFactory sessionFactory) {
		super();
		this.userFactory = userFactory;
		this.sessionFactory = sessionFactory;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#save(android.content.Context, com.socialize.api.SocializeSession)
	 */
	@Override
	public void save(Context context, SocializeSession session) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		Editor editor = prefs.edit();
		
		editor.putString("consumer_key", session.getConsumerKey());
		editor.putString("consumer_secret", session.getConsumerSecret());
		editor.putString("consumer_token", session.getConsumerToken());
		editor.putString("consumer_token_secret", session.getConsumerTokenSecret());
		
		User user = session.getUser();
		
		if(user != null) {
			try {
				editor.putString("user", userFactory.toJSON(user).toString());
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to serialize user object", e);
				}
				else {
					e.printStackTrace();
				}
			}
		}
		
		editor.commit();
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#load(android.content.Context)
	 */
	@Override
	public SocializeSession load(Context context) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		String key = prefs.getString("consumer_key", null);
		String secret = prefs.getString("consumer_secret", null);
		
		WritableSession session = sessionFactory.create(key, secret);
			
		session.setConsumerToken(prefs.getString("consumer_token", null));
		session.setConsumerTokenSecret(prefs.getString("consumer_token_secret", null));
		
		String userJson = prefs.getString("user", null);
		
		if(userJson != null) {
			try {
				JSONObject json = new JSONObject(userJson);
				User user = userFactory.fromJSON(json);
				session.setUser(user);
			}
			catch (JSONException e) {
				if(logger != null) {
					logger.error("Failed to deserialize user object", e);
				}
				else {
					e.printStackTrace();
				}
			}
		}
		
		return session;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionPersister#delete(android.content.Context)
	 */
	@Override
	public void delete(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		prefs.edit().clear().commit();
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
