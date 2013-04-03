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
package com.socialize.networks.facebook.v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.HttpMethod;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookDialogListener;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.AsyncFacebookRunner.RequestListener;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.ServiceListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.BaseFacebookFacade;
import com.socialize.networks.facebook.FacebookUtilsProxy;
import com.socialize.networks.facebook.OnPermissionResult;
import com.socialize.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Jason Polites
 */
@Deprecated
public class FacebookFacadeV2 extends BaseFacebookFacade {
	
	private FacebookUtilsProxy facebookUtils;
	private IBeanFactory<AsyncFacebookRunner> facebookRunnerFactory;
	private FacebookSessionStore facebookSessionStore;
	
	@Override
	public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
		getFacebook(context).authorizeCallback(requestCode, resultCode, data);
	}
	
	@Override
	public int getSDKMajorVersion() {
		return 2;
	}
	
	@Override
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
		authenticate(context, appId, permissions, sso, listener);
	}

	@Deprecated
	@Override
	public void authenticate(final Activity context, String appId, final String[] permissions, final boolean sso, final AuthProviderListener listener) {
		Facebook facebook = getFacebook(context);
		
		facebookSessionStore.restore(facebook, context);
		
		FacebookDialogListener facebookDialogListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				context.finish();
			}
			
			@Override
			public void handleError(Throwable error) {
				if(listener != null) {
					listener.onError(new SocializeException(error));
				}
				else {
					error.printStackTrace();
				}
			}
		};
		
		if(sso) {
			facebook.authorize(context, permissions, facebookDialogListener);
		}
		else {
			facebook.authorize(context, permissions, Facebook.FORCE_DIALOG_AUTH, facebookDialogListener);
		}		
	}
	
	@Override
	public void onResume(Activity context, SocializeAuthListener listener) {
		extendAccessToken(context, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#extendAccessToken(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void extendAccessToken(final Activity context, final SocializeAuthListener listener) {
		try {
			Facebook facebook = getFacebook(context);
			if(isLinked(context)) {
				if(facebook != null && !facebook.extendAccessTokenIfNeeded(context, new ServiceListener() {
					@Override
					public void onFacebookError(FacebookError e) {
						if(logger != null) {
							logger.warn("An error occurred while attempting to extend a Facebook access token.  The local Facebook account will be cleared.", e);
						}				
						
						// Clear the local session state
						unlink(context, null);
					}
					
					@Override
					public void onError(Error e) {
						if(logger != null) {
							logger.warn("An error occurred while attempting to extend a Facebook access token.  The local Facebook account will be cleared.", e);
						}
						
						// Clear the local session state
						unlink(context, null);
					}
					
					@Override
					public void onComplete(Bundle values) {
						// Update the local session state
						SocializeSession session = getSocialize().getSession();
						
						if(session != null) {
							
							final String newAccessToken = values.getString(Facebook.TOKEN);
							
							if(!StringUtils.isEmpty(newAccessToken)) {
							
								if(logger != null && logger.isDebugEnabled()) {
									logger.debug("Got new Facebook access token [" +
											newAccessToken +
											"]");
								}
								
								// Link the user again
								link(context, newAccessToken, false, new SocializeAuthListener() {
									
									@Override
									public void onError(SocializeException error) {
										if(logger != null) {
											logger.error("An error occurred while attempting to update authentication details", error);
										}
										
										if(listener != null) {
											listener.onError(error);
										}
									}
									
									@Override
									public void onCancel() {
										if(listener != null) {
											listener.onCancel();
										}
									}
									
									@Override
									public void onAuthSuccess(SocializeSession session) {
										
										UserProviderCredentialsMap map = session.getUserProviderCredentials();
										
										UserProviderCredentials creds = map.get(AuthProviderType.FACEBOOK);
										
										DefaultUserProviderCredentials newCreds = new DefaultUserProviderCredentials();
										
										if(creds != null) {
											newCreds.merge(creds);
										}
										
										newCreds.setAccessToken(newAccessToken);
										
										map.put(AuthProviderType.FACEBOOK, newCreds);
										
										getSocialize().setSession(session);
										getSocialize().saveSession(context);		
										
										if(listener != null) {
											listener.onAuthSuccess(session);
										}
									}
									
									@Override
									public void onAuthFail(SocializeException error) {
										if(logger != null) {
											logger.error("An error occurred while attempting to update authentication details", error);
										}
										
										if(listener != null) {
											listener.onAuthFail(error);
										}
									}
								});								
							}
							else {
								if(logger != null) {
									logger.warn("Access token returned from Facebook was empty during request to extend");
								}
							}
						}
					}
				})) {
					if(logger != null) {
						logger.warn("Failed to bind to the Facebook RefreshToken Service");
					}	
				}
			}
			else if(facebook != null) {
				// Ensure the local fb session is cleared
				String accessToken = facebook.getAccessToken();
				if(!StringUtils.isEmpty(accessToken)) {
					new FacebookSessionStore().clear(context);
				}
			}
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onError(SocializeException.wrap(e));
			}
			if(logger != null) {
				logger.error("Failure during Facebook Token Refresh", e);
			}	
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, com.socialize.networks.SocialNetworkListener, com.socialize.networks.PostData)
	 */
	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
		
		boolean okToGo = true;
		
		if(listener != null) {
			okToGo = !listener.onBeforePost(parent, SocialNetwork.FACEBOOK, postData);
		}
		
		if(okToGo) {
			Bundle bundle = new Bundle();
			
			Map<String, Object> postValues = postData.getPostValues();
			
			if(postValues != null) {
				
				Set<Entry<String, Object>> entries = postValues.entrySet();
				
				for (Entry<String, Object> entry : entries) {
					if(entry != null) {
						Object value = entry.getValue();
						String key = entry.getKey();
						
						if(key != null && value != null) {
							if(value instanceof byte[]) {
								bundle.putByteArray(entry.getKey(), (byte[]) value);
							}
							else {
								bundle.putString(entry.getKey(), value.toString());
							}
						}
					}
				}
			}

			Facebook fb = getFacebook(parent);
			
			final FacebookSessionStore store = newFacebookSessionStore();
			
			store.restore(fb, parent);
			
			AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
			
			RequestListener requestListener = newRequestListener(parent, listener);
			
			String path = postData.getPath();
			
			if(StringUtils.isEmpty(path)) {
				path = "me/links";
			}
			
			runner.request(path, bundle, "POST", requestListener, null);
		}
	}	


	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#getCurrentPermissions(android.app.Activity, java.lang.String, com.socialize.networks.facebook.v2.FacebookPermissionCallback)
	 */
	@Override
	public void getCurrentPermissions(final Activity parent, String token, final OnPermissionResult callback) {
		Facebook fb = new Facebook(getFacebookAppId());
		fb.setAccessToken(token);
		AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
		runner.request("me/permissions", new RequestListener() {
			
			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
				handlePermissionError(parent, callback, e);
			}
			
			@Override
			public void onIOException(IOException e, Object state) {
				handlePermissionError(parent, callback, e);
			}
			
			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				handlePermissionError(parent, callback, e);
			}
			
			@Override
			public void onFacebookError(FacebookError e, Object state) {
				handlePermissionError(parent, callback, e);
			}
			
			@Override
			public void onComplete(final String response, final Object state) {
				if(callback != null) {
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								JSONObject json = new JSONObject(response);
								
								if(json.has("data") && !json.isNull("data")) {
									JSONObject data = json.getJSONArray("data").getJSONObject(0);
									
									// Permissions are keys
									JSONArray names = data.names();
									
									ArrayList<String> current = new ArrayList<String>();
									
									for (int i = 0; i < names.length(); i++) {
										
										// If we're ON we're on
										String permission = names.getString(i);
										int status = data.getInt(permission);
										
										if(status == 1) {
											// ON
											current.add(permission);
										}
									}
									
									String[] values = current.toArray(new String[current.size()]);
									
									Arrays.sort(values); // Sort for binary searching
									
									callback.onSuccess(values);
								}
							}
							catch (JSONException e) {
								callback.onError(new SocializeException(e));
							}
						}
					});
				}
			}
		});				
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#logout(android.content.Context)
	 */
	@Override
	public void logout(Context context) {
		// Logout does NOT clear the token.. thanks FB :/
		new FacebookSessionStore().clear(context);
		
		Facebook mFacebook = getFacebook(context);
		
		try {
			if(mFacebook != null) {
				mFacebook.logout(context);
			}
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Failed to log out of Facebook", e);
			}
			else {
				SocializeLogger.e("Failed to log out of Facebook", e);
			}
		}
		finally {
			if(facebookSessionStore != null) {
				facebookSessionStore.clear(context);
			}
		}		
	}

	// So we can mock
	protected Facebook getFacebook(Context context) {
		return facebookUtils.getFacebook(context);
	}
	// So we can mock
	protected AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
		if(facebookRunnerFactory != null) {
			return facebookRunnerFactory.getBean(fb);
		}
		return  new AsyncFacebookRunner(fb);
	}
	// So we can mock
	protected FacebookSessionStore newFacebookSessionStore() {
		return new FacebookSessionStore();
	}
	// So we can mock	
	protected JSONObject newJSONObject(String response) throws JSONException {
		return new JSONObject(response);
	}	
	// So we can mock
	protected RequestListener newRequestListener(final Activity parent, final SocialNetworkPostListener listener) {
		final String defaultErrorMessage = "Facebook Error";
		
		return new RequestListener() {
			public void onMalformedURLException(MalformedURLException e, Object state) {
				handleFacebookError(parent, 0, defaultErrorMessage, e, listener);
			}
			public void onIOException(IOException e, Object state) {
				handleFacebookError(parent, 0, defaultErrorMessage, e, listener);
			}
			public void onFileNotFoundException(final FileNotFoundException e, Object state) {
				handleFacebookError(parent, 0, defaultErrorMessage, e, listener);
			}
			public void onFacebookError(FacebookError e, Object state) {
				handleFacebookError(parent, 0, defaultErrorMessage, e, listener);
			}
			public void onComplete(final String response, Object state) {
				
				JSONObject responseObject = null;
				if(!StringUtils.isEmpty(response)) {
					try {
						responseObject = newJSONObject(response);
						
						if(responseObject.has("error")) {
							
							JSONObject error = responseObject.getJSONObject("error");
							
							int code = 0;
							
							if(error.has("code") && !error.isNull("code")) {
								code = error.getInt("code");
							}
							
							
							if(error.has("message") && !error.isNull("message")) {
								String msg = error.getString("message");
								if(logger != null) {
									logger.error(msg);
								}
								else {
									System.err.println(msg);
								}
								
								handleFacebookError(parent, code, msg, new SocializeException(msg), listener);
							}
							else {
								handleFacebookError(parent, code, defaultErrorMessage, new SocializeException("Facebook Error (Unknown)"), listener);
							}
							
							return;
						}
					}
					catch (JSONException e) {
						onError(parent, defaultErrorMessage, e, listener);
						return;
					}
				}
				
				if(listener != null) {
					final JSONObject fResponse = responseObject;
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							listener.onAfterPost(parent, SocialNetwork.FACEBOOK, fResponse);
						}
					});
				}
			}
		};
	}	
	
	protected void doFacebookCall(Activity parent, Bundle data, String graphPath, HttpMethod method, SocialNetworkPostListener listener) {
		Facebook fb = getFacebook(parent);
		FacebookSessionStore store = newFacebookSessionStore();
		store.restore(fb, parent);
		AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
		RequestListener requestListener = newRequestListener(parent, listener);
		runner.request(graphPath, data, method.toString(), requestListener, null);			
	}
	
	public void setFacebookUtils(FacebookUtilsProxy facebookUtils) {
		this.facebookUtils = facebookUtils;
	}
	public void setFacebookRunnerFactory(IBeanFactory<AsyncFacebookRunner> facebookRunnerFactory) {
		this.facebookRunnerFactory = facebookRunnerFactory;
	}
	public void setFacebookSessionStore(FacebookSessionStore facebookSessionStore) {
		this.facebookSessionStore = facebookSessionStore;
	}
	public void setHolder(ListenerHolder holder) {
		this.holder = holder;
	}
}
