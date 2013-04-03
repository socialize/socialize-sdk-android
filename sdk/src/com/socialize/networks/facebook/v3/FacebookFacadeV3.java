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
package com.socialize.networks.facebook.v3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.*;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.BaseFacebookFacade;
import com.socialize.networks.facebook.OnPermissionResult;
import com.socialize.util.StringUtils;

import java.util.Arrays;


/**
 * @author Jason Polites
 */
public class FacebookFacadeV3 extends BaseFacebookFacade {
	
	@Override
	public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
		Session activeSession = getActiveSession(context);
		if(activeSession != null) {
			activeSession.onActivityResult(context, requestCode, resultCode, data);
		}		
	}
	
	@Override
	public int getSDKMajorVersion() {
		return 3;
	}
	
	@Deprecated
	@Override
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, final AuthProviderListener listener) {
		// Clear current session
		logout(context);
		login(context, appId, permissions, sso, true, listener);
	}
	
	@Override
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
		login(context, appId, permissions, sso, read, listener);
	}

	// Protected so we can mock
	protected void login(Activity context, boolean read, final AuthProviderListener listener) {
		login(context, config.getProperty(SocializeConfig.FACEBOOK_APP_ID), (read) ? READ_PERMISSIONS : WRITE_PERMISSIONS, config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true), read, listener);
	}
	
	@Override
	public void onResume(Activity context, SocializeAuthListener listener) {
		Session session = Session.getActiveSession();
	    if (session == null) {
	        session = createNewSession(context, config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
			String strToken = getAccessToken(context);
			if(!StringUtils.isEmpty(strToken)) {
				AccessToken accessToken = AccessToken.createFromExistingAccessToken(
						strToken,
	                    null,
						null,
						null,
						null);

				openSessionWithToken(session, accessToken);

				Session.setActiveSession(session);
			}
	    }
	}

	// So we can mock
	protected Session createNewSession(Activity context, String appId) {
		return new Session.Builder(context).setApplicationId(appId).build();
	}

	// So we can mock
	protected void openSessionWithToken(Session session, AccessToken token) {
		session.open(token, null);
	}

	// So we can mock
	protected void openSessionForRead(Session session, Session.OpenRequest auth) {
		session.openForRead(auth);
	}

	// So we can mock
	protected void openSessionForPublish(Session session, Session.OpenRequest auth) {
		session.openForPublish(auth);
	}

	// Protected so we can mock
	protected void login(Activity context, String appId, String[] permissions, boolean sso, boolean read, final AuthProviderListener listener) {

		Session.OpenRequest auth = new Session.OpenRequest(context);
		
		if(permissions != null) {
			auth.setPermissions(Arrays.asList(permissions));
		}
		
		if(sso) {
			auth.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		}
		else {
			auth.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		}
	
		auth.setCallback(createLoginCallback(listener));
		
		Session session = createNewSession(context, appId);
		Session.setActiveSession(session);
		
		if(read) {
			openSessionForRead(session ,auth);
		}
		else {
			openSessionForPublish(session ,auth);
		}
	}

	// So we can mock
	protected Session.StatusCallback createLoginCallback(final AuthProviderListener listener) {

		return new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(final Session session, SessionState state, Exception exception) {

				if(exception != null && exception instanceof FacebookOperationCanceledException) {
					if(logger != null) {
						logger.error("Facebook operation failed", exception);
					}
					handleCancel(listener);
					return;
				}

				switch(state) {

					case OPENED:
						if (isSessionOpen(session)) {
							// make request to the /me API
							getUser(session, listener);
						}

						break;

					case CLOSED:
						if(exception != null) {
							handleError(exception, listener);
						}
						break;

					case CLOSED_LOGIN_FAILED:
						if(exception != null) {
							handleAuthFail(exception, listener);
						}
						break;
				}
			}
		};
	}

	protected boolean isSessionOpen(Session session) {
		return session.isOpened();
	}

	// so we can mock
	protected void getUser(final Session session, final AuthProviderListener listener) {
		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
			// callback after Graph API response with user object
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if(response.getError() != null) {
					handleError(response.getError().getException(), listener);
				}
				else if (user != null) {
					handleResult(session, user, listener);
				}
			}
		});
	}

	// Protected so we can mock
	protected void handleError(Exception exception, SocializeListener listener) {
		if(listener != null) {
			listener.onError(SocializeException.wrap(exception));
		}
	}

	// Protected so we can mock
	protected void handleAuthFail(Exception exception, AuthProviderListener listener) {
		if(listener != null) {
			listener.onAuthFail(SocializeException.wrap(exception));
		}
	}

	// Protected so we can mock
	protected void handleCancel(AuthProviderListener listener) {
		if(listener != null) {
			listener.onCancel();
		}
	}

	// Protected so we can mock
	protected void handleResult(Session session, GraphUser user, AuthProviderListener listener) {
		if(listener != null) {
			AuthProviderResponse response = new AuthProviderResponse();
			response.setUserId(user.getId());
			response.setToken(session.getAccessToken());
			listener.onAuthSuccess(response);
		}
	}	

	@Override
	public void extendAccessToken(Activity context, SocializeAuthListener listener) {
		// Do Nothing.  Session refreshes this automatically
	}
	
	@Override
	public void getCurrentPermissions(Activity context, String token, final OnPermissionResult callback) {
		Session activeSession = getActiveSession(context);
		
		if(activeSession != null) {
			if(activeSession.getAccessToken().equals(token)) {
				callback.onSuccess((String[])activeSession.getPermissions().toArray(new String[activeSession.getPermissions().size()]));
			}
			else {
				AccessToken accessToken = AccessToken.createFromExistingAccessToken(
						token,
	                    null, null, null, null);
				
				// We must close the current session
				if(activeSession.isOpened()) {
					activeSession.closeAndClearTokenInformation();
					activeSession = new Session.Builder(context).setApplicationId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).build();
				}
				
				activeSession.open(accessToken, new StatusCallback() {
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if(exception != null) {
							if(callback != null) {
								callback.onError(SocializeException.wrap(exception));
							}
							else if(logger != null) {
								logger.error("Error accessing permissions for alternate token", exception);
							}
							else {
								Log.e("Socialize", "Error accessing permissions for alternate token", exception);
							}
						}
						else {
							Session.setActiveSession(session);
							if(callback != null) {
								callback.onSuccess((String[])session.getPermissions().toArray(new String[session.getPermissions().size()]));
							}
						}
					}
				});
			}			
		}
		else {
			handleNotSignedIn(context, callback);
		}
	}

	@Override
	public void logout(Context context) {
		Session activeSession = getActiveSession(context);
		if(activeSession != null) {
			activeSession.closeAndClearTokenInformation();
		}
	}
	
	@Deprecated
	@Override
	public boolean isLinked(Context context) {
		return super.isLinked(context) && getActiveSession(context) != null;
	}

	@Override
	protected void doFacebookCall(final Activity context, final Bundle data, final String graphPath, final HttpMethod method, final SocialNetworkPostListener listener) {
		Session activeSession = getActiveSession(context);
		
		boolean read = method.equals(HttpMethod.GET);
		
		if(activeSession != null) {
			if(activeSession.isOpened()) {
				Request request = new Request(activeSession, graphPath, data, method, new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						handleFBResponse(context, response, listener);
					}
				});
				
				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();		
			}
			else {
				login(context, read, new AuthProviderListener() {
					
					@Override
					public void onError(SocializeException error) {
						if(listener != null) {
							listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
						}
							
						handleNonListenerError("", error);
					}
					
					@Override
					public void onCancel() {
						if(listener != null) {
							listener.onCancel();
						}
					}
					
					@Override
					public void onAuthSuccess(AuthProviderResponse response) {
						doFacebookCall(context, data, graphPath, method, listener);
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						if(listener != null) {
							listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
						}
							
						handleNonListenerError("", error);
						
						unlink(context, null);
					}
				});
			}
		}
		else {
			handleNotSignedIn(context, listener);
		}
	}

	// Protected so we can mock
	protected Session getActiveSession(Context context) {
		Session activeSession = Session.getActiveSession();
		if(activeSession == null) {
			activeSession = new Session.Builder(context).setApplicationId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}

	// Protected so we can mock
	protected void handleFBResponse(final Activity context, Response response, final SocialNetworkPostListener listener) {
		FacebookRequestError error = response.getError();
		
		if(error != null) {
			if(listener != null) {
				listener.onNetworkError(context, SocialNetwork.FACEBOOK, error.getException());
			}
			 
			if(logger != null) {
				logger.error(error.getErrorMessage(), error.getException());
			}
			else {
				Log.e("Socialize", error.getErrorMessage(), error.getException());
			}
			
			switch(error.getCategory()) {
			
				case AUTHENTICATION_REOPEN_SESSION:
					unlink(context, null);
					break;
					
				case AUTHENTICATION_RETRY:
					unlink(context, null);
					break;
					
				case PERMISSION:
					unlink(context, null);
					break;
			}
			
		}
		else if(listener != null) {
			listener.onAfterPost(context, SocialNetwork.FACEBOOK, response
                    .getGraphObject()
                    .getInnerJSONObject());
		}
	}

	// Protected so we can mock
	protected void handleNotSignedIn(final Activity context, SocializeListener listener) {
		String msg = "Not signed into Facebook";
		if(listener != null) {
			listener.onError(new SocializeException(msg));
		}
		else {
			handleNonListenerError(msg, new SocializeException(msg));
		}
	}

	// Protected so we can mock
	protected void handleNotSignedIn(final Activity context, SocialNetworkPostListener listener) {
		String msg = "Not signed into Facebook";
		if(listener != null) {
			listener.onNetworkError(context, SocialNetwork.FACEBOOK, new SocializeException(msg));
		}
		else {
			handleNonListenerError(msg, new SocializeException(msg));
		}
	}

	// Protected so we can mock
	protected void handleNonListenerError(String msg, Exception error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			Log.e("Socialize", msg, error);
		}
	}
}
