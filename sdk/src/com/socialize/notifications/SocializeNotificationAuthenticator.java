package com.socialize.notifications;

import android.content.Context;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.user.UserSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.util.StringUtils;

public class SocializeNotificationAuthenticator implements NotificationAuthenticator {
	private SocializeConfig config;
	private UserSystem userSystem;

	@Override
	public SocializeSession authenticate(Context context) throws SocializeException {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);		
		if(checkKeys(consumerKey, consumerSecret)) {
			return userSystem.authenticateSynchronous(context, consumerKey, consumerSecret);
		}
		else {
			throw new SocializeException("Consumer key and/or secret not provided");
		}
	}
	
	@Override
	public void authenticateAsync(Context context, SocializeAuthListener listener) throws SocializeException {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);		
		if(checkKeys(consumerKey, consumerSecret)) {
			userSystem.authenticate(context, consumerKey, consumerSecret, listener, Socialize.getSocialize());
		}
	}

	protected boolean checkKeys(String consumerKey, String consumerSecret) {
		return !StringUtils.isEmpty(consumerKey) && !StringUtils.isEmpty(consumerSecret);
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
}
