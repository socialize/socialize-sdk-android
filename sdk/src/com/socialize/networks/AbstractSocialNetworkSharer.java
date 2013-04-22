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
package com.socialize.networks;

import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public abstract class AbstractSocialNetworkSharer implements SocialNetworkSharer {
	
	private SocializeConfig config;
	private SocializeLogger logger;
	private AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory;
		
	@Override
	public void share(final Activity context, final Entity entity, final PropagationInfo urlSet, final String comment, boolean autoAuth, final ActionType type, final SocialNetworkListener listener) {

		try {
			AuthProviderType authProviderType = AuthProviderType.valueOf(getNetwork());

			if(getSocialize().isSupported(context, authProviderType)) {

				if(getSocialize().isAuthenticatedForWrite(authProviderType)) {
					doShare(context, entity, urlSet, comment, listener, type);
				}
				else if(autoAuth) {

					String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
					String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);

					AuthProviderInfo authProviderInfo = authProviderInfoFactory.getInstanceForWrite();

					getSocialize().authenticate(context, consumerKey, consumerSecret, authProviderInfo, new SocializeAuthListener() {

						@Override
						public void onError(SocializeException error) {
							doError(error, context, listener);
						}

						@Override
						public void onAuthSuccess(SocializeSession session) {
							try {
								doShare(context, entity, urlSet, comment, listener, type);
							}
							catch (SocializeException e) {
								doError(SocializeException.wrap(e), context, listener);
							}
						}

						@Override
						public void onAuthFail(SocializeException error) {
							doError(error, context, listener);
						}

						@Override
						public void onCancel() {
							// Do nothing
						}
					});
				}
				else {
					SocializeException error = new SocializeException("User is not authenticated with [" +
							getNetwork().name() +
							"]");

					if (logger != null) {
						logger.error("Error during share", error);
					}
					else {
						SocializeLogger.e(error.getMessage(), error);
					}

					// Fail
					if(listener != null) {
						listener.onNetworkError(context, getNetwork(), error);
					}
				}
			}
		}
		catch (Throwable e) {
			doError(SocializeException.wrap(e), context, listener);
		}
	}
	
	protected void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
		String msg = "Error sharing to " + getNetwork().name();
		
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(msg, e);
		}
		
		if(listener != null) {
			listener.onNetworkError(parent, getNetwork(), e);
		}
	}
	
	protected abstract SocialNetwork getNetwork();
	
	protected abstract void doShare(final Activity context, Entity entity, PropagationInfo urlSet, String comment, final SocialNetworkListener listener, ActionType type) throws SocializeException;
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setAuthProviderInfoFactory(AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory) {
		this.authProviderInfoFactory = authProviderInfoFactory;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
