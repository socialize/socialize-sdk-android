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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.Share;
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
import com.socialize.networks.DefaultPostData;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.networks.facebook.FacebookUtilsProxy;
import com.socialize.networks.facebook.OnPermissionResult;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.ImageUtils;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
@SuppressWarnings("deprecation")
public class FacebookFacadeV2 implements FacebookFacade {
	
	private SocializeLogger logger;
	private ListenerHolder holder; // This is a singleton
	private ImageUtils imageUtils;
	private FacebookUtilsProxy facebookUtils;
	private IBeanFactory<AsyncFacebookRunner> facebookRunnerFactory;
	private SocializeConfig config;	
	private FacebookSessionStore facebookSessionStore;
	private UserSystem userSystem;
	
	

	@Override
	public void authenticate(Context context, FacebookAuthProviderInfo info, final AuthProviderListener listener) {

		final String listenerKey = "auth";
		
		holder.push(listenerKey, new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				holder.remove(listenerKey);
				listener.onError(error);
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				holder.remove(listenerKey);
				listener.onAuthSuccess(response);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				holder.remove(listenerKey);
				listener.onAuthFail(error);
			}

			@Override
			public void onCancel() {
				holder.remove(listenerKey);
				listener.onCancel();
			}
		});
		
		Intent i = new Intent(context, FacebookActivity.class);
		i.putExtra("appId", info.getAppId());
		
		if(info.getPermissions() != null) {
			i.putExtra("permissions", info.getPermissions());
		}
		
		context.startActivity(i);		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, FacebookService.DEFAULT_PERMISSIONS);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#link(android.app.Activity, java.lang.String, boolean, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(final Activity context, final String token, final boolean verifyPermissions, final SocializeAuthListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		final FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
		fbInfo.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		
		if(verifyPermissions) {
			// Get the permissions for this token
			getCurrentPermissions(context, token, new OnPermissionResult() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onError(error);
					}
				}
				
				@Override
				public void onSuccess(String[] current) {
					
					// Set the permissions on the session to the REAL permissions.
					fbInfo.setPermissions(current);
					
					// Ensure the user has the required permissions
					String[] required = FacebookService.DEFAULT_PERMISSIONS;
					
					boolean authRequired = false;
					
					for (String permission : required) {
						if(Arrays.binarySearch(current, permission) < 0) {
							// Does NOT have permission, we need to auth
							authRequired = true;
							break;
						}
					}
					
					if(authRequired) {
						

						// We need to merge in the default permissions...
						// Just add to a set
						Set<String> allPermissions = new HashSet<String>();
						allPermissions.addAll(Arrays.asList(current));
						allPermissions.addAll(Arrays.asList(required));
						
						// Now set the merged permissions.  This is the final set
						fbInfo.setPermissions(allPermissions.toArray(new String[allPermissions.size()]));
						
						// Now try to auth
						authenticate(context, fbInfo, new AuthProviderListener() {
							
							@Override
							public void onError(SocializeException error) {
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
							public void onAuthSuccess(AuthProviderResponse response) {
								// Now to the actual auth!
								doSocializeAuthKnownUser(context, fbInfo, token, listener);
							}
							
							@Override
							public void onAuthFail(SocializeException error) {
								if(listener != null) {
									listener.onAuthFail(error);
								}
							}
						});
						
					}
					else {
						doSocializeAuthKnownUser(context, fbInfo, token, listener);
					}
				}
			});
		}
		else {
			// Assume default permissions
			fbInfo.setPermissions(FacebookService.DEFAULT_PERMISSIONS);
			doSocializeAuthKnownUser(context, fbInfo, token, listener);
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#link(android.app.Activity, com.socialize.listener.SocializeAuthListener, java.lang.String[])
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, permissions);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#unlink(android.content.Context, com.socialize.networks.SocializeDeAuthListener)
	 */
	@Override
	public void unlink(final Context context, final SocializeDeAuthListener listener) {
		// Clear the FB session
		try {
			// Logout does NOT clear the token.. thanks FB :/
			new FacebookSessionStore().clear(context);
			
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					
					try {
						
						SocializeSession session = getSocialize().getSession();
						session.clear(AuthProviderType.FACEBOOK);
						
						UserSettings userSettings = session.getUserSettings();
						
						if(userSettings != null) {
							userSettings.setAutoPostFacebook(false);
						}
						
						userSystem.saveSession(context, session);	
						
						getFacebook(context).logout(context);
						if(listener != null) {
							listener.onSuccess();
						}
					}
					catch (Exception e) {
						if(listener != null) {
							listener.onError(SocializeException.wrap(e));
						}
					}
					return null;
				}
			}.execute();
			
			
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Error while logging out of Facebook", e);
			}
			else {
				e.printStackTrace();
			}
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticated(AuthProviderType.FACEBOOK);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#getAccessToken(android.content.Context)
	 */
	@Override
	public String getAccessToken(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.FACEBOOK);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#extendAccessToken(android.app.Activity, com.socialize.listener.SocializeAuthListener)
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
	 * @see com.socialize.networks.facebook.FacebookService#postEntity(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.share.SocialNetworkShareListener)
	 */
	@Override
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
		ShareOptions options = ShareUtils.getUserShareOptions(context);
		options.setText(text);
		options.setShowAuthDialog(false);
		ShareUtils.shareViaSocialNetworks(context, entity, options, listener, SocialNetwork.FACEBOOK);			
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#postLike(android.app.Activity, com.socialize.entity.Entity, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(config.isOGLike()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("object", propInfo.getEntityUrl());
			post(parent, "me/og.likes",  params, listener);			
		}
		else {
			post(parent, entity, "", propInfo, listener);	
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#postComment(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
		post(parent, entity, comment, propInfo, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#postPhoto(android.app.Activity, com.socialize.entity.Share, java.lang.String, android.net.Uri, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postPhoto(Activity parent, Share share, String comment, Uri photoUri, SocialNetworkListener listener) {
		PropagationInfoResponse propagationInfoResponse = share.getPropagationInfoResponse();
		PropagationInfo propInfo = propagationInfoResponse.getPropagationInfo(ShareType.FACEBOOK);
		
		if(propInfo != null) {
			String link = propInfo.getAppUrl();
			String appId = getFacebookAppId();
			
			if(!StringUtils.isEmpty(appId)) {
				postPhoto(parent, link, comment, photoUri, listener);
			}
			else {
				String msg = "Cannot post message to Facebook.  No app id found.  Make sure you specify facebook.app.id in socialize.properties";
				onError(parent, msg, new SocializeException(msg), listener);
			}	
		}
		else {
			String msg = "Cannot post message to Facebook.  No propagation info found";
			onError(parent, msg, new SocializeException(msg), listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#postPhoto(android.app.Activity, java.lang.String, java.lang.String, android.net.Uri, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {

		try {
			Bundle params = new Bundle();
			params.putString("caption", caption + ": " + link);
			params.putByteArray("photo", imageUtils.scaleImage(parent, photoUri));
			
			Facebook fb = getFacebook(parent);
			
			final FacebookSessionStore store = newFacebookSessionStore();
			
			store.restore(fb, parent);
			
			AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
			
			RequestListener requestListener = newRequestListener(parent, listener);
			
			runner.request("me/photos", params, "POST", requestListener, null);			
		}
		catch (IOException e) {
			if(listener != null) {
				listener.onNetworkError(parent, SocialNetwork.FACEBOOK, e);
			}
			 
			if(logger != null) {
				logger.error("Unable to scale image for upload", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#postOG(android.app.Activity, com.socialize.entity.Entity, java.lang.String, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener) {
		String entityUrl = propInfo.getEntityUrl();
		String linkName = entityUrl;
		String link = entityUrl;
		
		if(entity != null) {
			linkName = entity.getDisplayName();
		}
			
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", linkName);
		params.put("message", message);
		params.put("link", link);
		params.put("type", "link");
		
		DefaultPostData postData = new DefaultPostData();
		postData.setPostValues(params);
		postData.setEntity(entity);
		postData.setPropagationInfo(propInfo);

		post(parent, listener, postData);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#post(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
		postOG(parent, entity, message, null, propInfo, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#post(android.app.Activity, com.socialize.networks.SocialNetworkListener, com.socialize.networks.PostData)
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
	 * @see com.socialize.networks.facebook.FacebookService#post(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, "POST", listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#get(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, "GET", listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#delete(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, "DELETE", listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookService#getCurrentPermissions(android.app.Activity, java.lang.String, com.socialize.networks.facebook.FacebookPermissionCallback)
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
	 * @see com.socialize.networks.facebook.FacebookService#logout(android.content.Context)
	 */
	@Override
	public void logout(Context context) {
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
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	// So we can mock
	protected String getFacebookAppId() {
		return config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
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
	protected void handlePermissionError(Activity parent, final OnPermissionResult callback, final Exception e) {
		if(callback != null) {
			parent.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					callback.onError(SocializeException.wrap(e));
				}
			});
		}
	}
	protected void handleFacebookError(final Activity parent, int code, String msg, Throwable e, SocialNetworkPostListener listener) {
		
		// Check for token error:
		// http://fbdevwiki.com/wiki/Error_codes
		if(code == 190) {
			// Clear the session cache
			getSocialize().clear3rdPartySession(parent, AuthProviderType.FACEBOOK);
		}
		
		onError(parent, msg, e, listener);
	}
	protected void onError(final Activity parent, final String msg, final Throwable e, final SocialNetworkPostListener listener) {
		
		if(logger != null) {
			if(e != null) {
				logger.error(msg, e);
			}
			else {
				logger.error(msg);
			}
		}
		else {
			if(e != null) {
				SocializeLogger.e(msg, e);
			}
			else {
				System.err.println(msg);
			}
		}
		
		if(listener != null) {
			parent.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onNetworkError(parent, SocialNetwork.FACEBOOK, SocializeException.wrap(e));
				}
			});
		}
	}	
	protected void doFacebookCall(Activity parent, Map<String, Object> postData, String graphPath, String method, SocialNetworkPostListener listener) {
		Bundle bundle = new Bundle();
		
		if(postData != null) {
			Set<Entry<String, Object>> entries = postData.entrySet();
			for (Entry<String, Object> entry : entries) {
				
				Object value = entry.getValue();
				
				if(value instanceof byte[]) {
					bundle.putByteArray(entry.getKey(), (byte[]) value);
				}
				else {
					bundle.putString(entry.getKey(), value.toString());
				}
			}	
		}

		doFacebookCall(parent, bundle, graphPath, method, listener);
	}
	protected void doFacebookCall(Activity parent, Bundle data, String graphPath, String method, SocialNetworkPostListener listener) {
		Facebook fb = getFacebook(parent);
		FacebookSessionStore store = newFacebookSessionStore();
		store.restore(fb, parent);
		AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
		RequestListener requestListener = newRequestListener(parent, listener);
		runner.request(graphPath, data, method, requestListener, null);			
	}
	protected void doSocializeAuthKnownUser(Context context, AuthProviderInfo fbInfo, String token, SocializeAuthListener listener) {
		DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
		credentials.setAuthProviderInfo(fbInfo);
		credentials.setAccessToken(token);
		getSocialize().authenticateKnownUser(
				context, 
				credentials, 
				listener);	
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
	public void setFacebookUtils(FacebookUtilsProxy facebookUtils) {
		this.facebookUtils = facebookUtils;
	}
	public void setFacebookRunnerFactory(IBeanFactory<AsyncFacebookRunner> facebookRunnerFactory) {
		this.facebookRunnerFactory = facebookRunnerFactory;
	}
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	public void setFacebookSessionStore(FacebookSessionStore facebookSessionStore) {
		this.facebookSessionStore = facebookSessionStore;
	}
	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
	public void setHolder(ListenerHolder holder) {
		this.holder = holder;
	}
}
