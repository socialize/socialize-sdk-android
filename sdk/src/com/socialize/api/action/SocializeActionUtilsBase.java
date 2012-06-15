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
package com.socialize.api.action;

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.UserUtils;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.SocializeAction;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.share.ShareHandler;
import com.socialize.share.ShareHandlers;
import com.socialize.ui.profile.UserSettings;


/**
 * @author Jason Polites
 *
 */
public abstract class SocializeActionUtilsBase {
	
	private ShareHandlers shareHandlers;
	
	protected void populateActionOptions(Context context, ActionOptions options) {
		options.setShowAuthDialog(ConfigUtils.getConfig(context).isAuthRequired());
	}	
	
	protected boolean isDisplayAuthDialog(Context context, ActionOptions options, SocialNetwork...networks) {
		boolean authRequired = true;
		boolean authSupported = false;
		
		if(options != null) {
			authRequired = options.isShowAuthDialog();
		}
		
		if(networks == null || networks.length == 0) {
			networks = new SocialNetwork[] {SocialNetwork.TWITTER, SocialNetwork.FACEBOOK};
		}
		
		if(authRequired) {
			
			for (SocialNetwork network : networks) {
				AuthProviderType type = AuthProviderType.valueOf(network);
				if(getSocialize().isSupported(type)) {
					authSupported = true;
					if(getSocialize().isAuthenticated(type)) {
						authRequired = false;
						break;
					}
				}
				
				if(!authRequired) {
					break;
				}
			}
		}
		
		return (authRequired && authSupported);
	}
	
	
	protected boolean isDisplayAuthDialog(Context context) {
		
		boolean authRequired = ConfigUtils.getConfig(context).isAuthRequired();
		boolean authSupported = false;
		
		if(authRequired) {
			SocialNetwork[] all = SocialNetwork.values();
			for (SocialNetwork network : all) {
				AuthProviderType type = AuthProviderType.valueOf(network);
				if(getSocialize().isSupported(type)) {
					authSupported = true;
					if(getSocialize().isAuthenticated(type)) {
						authRequired = false;
						break;
					}
				}
				
				if(!authRequired) {
					break;
				}
			}
		}
		return (authRequired && authSupported);
	}	
	
	protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
		
		if(options == null || options.isShowShareDialog()) {
			
			boolean fbSupported = FacebookUtils.isAvailable(context);
			boolean twSupported = TwitterUtils.isAvailable(context);
			
			boolean shareRequired = fbSupported || twSupported;
			
			if(shareRequired) {
				UserSettings settings = UserUtils.getUserSettings(context);
				
				if(shareRequired && fbSupported) {
					shareRequired &= !settings.isAutoPostFacebook();
				}
				
				if(shareRequired && twSupported) {
					shareRequired &= !settings.isAutoPostTwitter();
				}	
			}
			
			return shareRequired;
		}
		
		return false;

	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected void doActionShare(final Activity context, final SocializeAction action, final String text, final SocialNetworkListener listener, final SocialNetwork...networks) {

		if(networks != null && networks.length > 0) {
			for (SocialNetwork socialNetwork : networks) {
				ShareHandler handler = shareHandlers.getShareHandler(ShareType.valueOf(socialNetwork));
				if(handler != null) {
					handler.handle(context, action, null, text, new SocialNetworkListener() {
						@Override
						public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
							if(listener != null) {
								listener.onNetworkError(context, network, error);
							}
						}
						
						@Override
						public void onCancel() {
							if(listener != null) {
								listener.onCancel();
							}
						}

						@Override
						public void onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
							if(listener != null) {
								listener.onBeforePost(parent, socialNetwork, postData);
							}
						}
						
						@Override
						public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
							if(listener != null) {
								listener.onAfterPost(parent, socialNetwork, responseObject);
							}
						}
					});
				}
				else {
					// TODO: Log error!
				}
			}
		}
	}
	
	public void setShareHandlers(ShareHandlers shareHandlers) {
		this.shareHandlers = shareHandlers;
	}

	
}
