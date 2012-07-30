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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.EmptyAuthProvider;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookAuthProvider;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.ServiceListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.ImageUtils;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsImpl implements FacebookUtilsProxy {
	
	private UserSystem userSystem;
	private FacebookWallPoster facebookWallPoster;
	private FacebookAuthProvider facebookAuthProvider;
	private ImageUtils imageUtils;
	private Facebook facebook;
	private SocializeLogger logger;
	private SocializeConfig config;

	@Override
	public synchronized Facebook getFacebook(Context context) {
		if(facebook == null) {
			facebook = new Facebook(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		}
		return facebook;
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, FacebookService.DEFAULT_PERMISSIONS);
	}
	
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, permissions);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(final Activity context, final String token, final boolean verifyPermissions, final SocializeAuthListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		final FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
		fbInfo.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		
		if(verifyPermissions) {
			// Get the permissions for this token
			getCurrentPermissions(context, token, new FacebookPermissionCallback() {
				
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
						facebookAuthProvider.authenticate(fbInfo, new AuthProviderListener() {
							
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
	
	protected void doSocializeAuthKnownUser(Context context, AuthProviderInfo fbInfo, String token, SocializeAuthListener listener) {
		DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
		credentials.setAuthProviderInfo(fbInfo);
		credentials.setAccessToken(token);
		getSocialize().authenticateKnownUser(
				context, 
				credentials, 
				listener);	
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#unlink(android.app.Activity)
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
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticated(AuthProviderType.FACEBOOK);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#isAvailable(android.content.Context)
	 */
	@Override
	public boolean isAvailable(Context context) {
		return getSocialize().isSupported(AuthProviderType.FACEBOOK);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#setAppId(android.content.Context, java.lang.String)
	 */
	@Override
	public void setAppId(Context context, String appId) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		config.setFacebookAppId(appId);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#getAccessToken(android.content.Context)
	 */
	@Override
	public String getAccessToken(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.FACEBOOK);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;
	}

	@Override
	public void postEntity(final Activity context, final Entity entity, final String text, final SocialNetworkShareListener listener) {
		ShareOptions options = ShareUtils.getUserShareOptions(context);
		options.setText(text);
		options.setShowAuthDialog(false);
		ShareUtils.shareViaSocialNetworks(context, entity, options, listener, SocialNetwork.FACEBOOK);		
	}
	
	@Override
	public void extendAccessToken(final Activity context, final SocializeAuthListener listener) {
		Facebook facebook = getFacebook(context);
		if(isLinked(context)) {
			
			if(!facebook.extendAccessTokenIfNeeded(context, new ServiceListener() {
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
		else {
			// Ensure the local fb session is cleared
			String accessToken = facebook.getAccessToken();
			if(!StringUtils.isEmpty(accessToken)) {
				new FacebookSessionStore().clear(context);
			}
		}
	}
	
	
	@Override
	public void getCurrentPermissions(Activity context, String token, FacebookPermissionCallback callback) {
		facebookWallPoster.getCurrentPermissions(context, token, callback);
	}

	@Override
	public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		facebookWallPoster.post(context, graphPath, postData, listener);
	}

	@Override
	public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		facebookWallPoster.get(context, graphPath, postData, listener);
	}

	@Override
	public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		facebookWallPoster.delete(context, graphPath, postData, listener);
	}

	@Override
	public byte[] getImageForPost(Activity context, Uri imagePath) throws IOException {
		return imageUtils.scaleImage(context, imagePath);
	}
	
	@Override
	public byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException {
		return imageUtils.scaleImage(context, image, format);
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}
	
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setFacebookAuthProvider(FacebookAuthProvider facebookAuthProvider) {
		this.facebookAuthProvider = facebookAuthProvider;
	}
	
	public void setFacebookAuthProvider(EmptyAuthProvider p) {
		// This is just here so we don't get console warnings when we want to load the notifications context.
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}
}
