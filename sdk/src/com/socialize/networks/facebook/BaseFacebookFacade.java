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
package com.socialize.networks.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.facebook.HttpMethod;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.*;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.facebook.FacebookAuthProviderInfo.PermissionType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.*;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.ArrayUtils;
import com.socialize.util.ImageUtils;

import java.util.*;
import java.util.Map.Entry;


/**
 * @author Jason Polites
 */
public abstract class BaseFacebookFacade implements FacebookFacade {
	
	protected ListenerHolder holder; // This is a singleton
	protected UserSystem userSystem;
	protected SocializeLogger logger;
	protected SocializeConfig config;	
	protected ImageUtils imageUtils;

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#authenticate(android.app.Activity, com.socialize.auth.facebook.FacebookAuthProviderInfo, com.socialize.listener.AuthProviderListener)
	 */
	@Override
	public void authenticateWithActivity(Activity context, FacebookAuthProviderInfo info, boolean sso, final AuthProviderListener listener) {

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
		i.putExtra("sso", sso);
		i.putExtra("type", info.getPermissionType().toString());
		
		switch(info.getPermissionType()) {
			case READ:
				if(info.getReadPermissions() != null) {
					i.putExtra("permissions", info.getReadPermissions());
				}
				break;
				
			case WRITE:
				if(info.getWritePermissions() != null) {
					i.putExtra("permissions", info.getWritePermissions());
				}
				break;
		}
		
		context.startActivity(i);	
	}		
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Deprecated
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		linkForWrite(context, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener, java.lang.String[])
	 */
	@Deprecated
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, permissions);
	}	
	
	@Deprecated
	@Override
	public void link(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener) {
		doLink(context, token, verifyPermissions, false, listener, DEFAULT_PERMISSIONS);
	}

	@Override
	public void linkForRead(Activity context, SocializeAuthListener listener, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.READ_PERMISSIONS;
		}			
		getSocialize().authenticateForRead(context, AuthProviderType.FACEBOOK, listener, permissions);
	}

	@Override
	public void linkForWrite(Activity context, SocializeAuthListener listener, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.WRITE_PERMISSIONS;
		}			
		getSocialize().authenticateForWrite(context, AuthProviderType.FACEBOOK, listener, permissions);
	}

	@Override
	public void linkForRead(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.READ_PERMISSIONS;
		}				
		doLink(context, token, verifyPermissions, true, listener, permissions);
	}

	@Override
	public void linkForWrite(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.WRITE_PERMISSIONS;
		}				
		doLink(context, token, verifyPermissions, false, listener, permissions);
	}

	private void doLink(final Activity context, final String token, final boolean verifyPermissions, final boolean read, final SocializeAuthListener listener, final String...permissions) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		final FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
		fbInfo.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		
		if(read) {
			fbInfo.setPermissionType(PermissionType.READ);
		}
		else {
			fbInfo.setPermissionType(PermissionType.WRITE);
		}
		
		final boolean sso = config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true);
		
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
					if(read) {
						fbInfo.setReadPermissions(current);
					}
					else {
						fbInfo.setWritePermissions(current);
					}
					
					// Ensure the user has the required permissions
					String[] required = (read) ? READ_PERMISSIONS : WRITE_PERMISSIONS;
					
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
						allPermissions.addAll(Arrays.asList(permissions));
						allPermissions.addAll(Arrays.asList(current));
						allPermissions.addAll(Arrays.asList(required));
						
						// Now set the merged permissions.  This is the final set
						if(read) {
							fbInfo.setReadPermissions(allPermissions.toArray(new String[allPermissions.size()]));
						}
						else {
							fbInfo.setWritePermissions(allPermissions.toArray(new String[allPermissions.size()]));
						}						
						
						// Now try to auth
						authenticateWithActivity(context, fbInfo, sso, new AuthProviderListener() {
							
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
			if(read) {
				fbInfo.setReadPermissions(permissions);
			}
			else {
				fbInfo.setWritePermissions(permissions);
			}
			
			doSocializeAuthKnownUser(context, fbInfo, token, listener);
		}		
	}	

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#isLinked(android.content.Context)
	 */
	@Deprecated
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticated(AuthProviderType.FACEBOOK);
	}
	
	@Override
	public boolean isLinkedForRead(Context context, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.READ_PERMISSIONS;
		}			
		return getSocialize().isAuthenticatedForRead(AuthProviderType.FACEBOOK, permissions);
	}

	@Override
	public boolean isLinkedForWrite(Context context, String...permissions) {
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.WRITE_PERMISSIONS;
		}			
		return getSocialize().isAuthenticatedForWrite(AuthProviderType.FACEBOOK, permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#getAccessToken(android.content.Context)
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
	 * @see com.socialize.networks.facebook.FacebookFacade#unlink(android.content.Context, com.socialize.networks.SocializeDeAuthListener)
	 */
	@Override
	public void unlink(final Context context, final SocializeDeAuthListener listener) {
		// Clear the FB session
		try {
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
						
						logout(context);
						
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
	 * @see com.socialize.networks.facebook.FacebookFacade#postLike(android.app.Activity, com.socialize.entity.Entity, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
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
	 * @see com.socialize.networks.facebook.FacebookFacade#postEntity(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.share.SocialNetworkShareListener)
	 */
	@Override
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
		ShareOptions options = ShareUtils.getUserShareOptions(context);
		options.setText(text);
		options.setShowAuthDialog(false);
		ShareUtils.shareViaSocialNetworks(context, entity, options, listener, SocialNetwork.FACEBOOK);			
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postComment(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
		post(parent, entity, comment, propInfo, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postOG(android.app.Activity, com.socialize.entity.Entity, java.lang.String, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
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
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
		postOG(parent, entity, message, null, propInfo, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, com.socialize.networks.SocialNetworkListener, com.socialize.networks.PostData)
	 */
	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
		doFacebookCall(parent, postData.getPostValues(), "me/links", HttpMethod.POST, listener);
	}	
		
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, HttpMethod.POST, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#get(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, HttpMethod.GET, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#delete(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		doFacebookCall(parent, postData, graphPath, HttpMethod.DELETE, listener);
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
	
	protected void doFacebookCall(Activity parent, Map<String, Object> postData, String graphPath, HttpMethod method, SocialNetworkPostListener listener) {
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
	
	protected abstract void doFacebookCall(Activity parent, Bundle data, String graphPath, HttpMethod method, SocialNetworkPostListener listener);
	
	// So we can mock
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	// So we can mock
	protected String getFacebookAppId() {
		return config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
	}	
	
	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
	public void setHolder(ListenerHolder holder) {
		this.holder = holder;
	}
	
}
