/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.api.action.share;

import android.app.Activity;
import android.app.Dialog;
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkShareListener;
import com.socialize.share.ShareHandlerListener;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.dialog.AuthRequestDialogFactory;

/**
 * @author Jason Polites
 */
public class SocializeShareUtils extends SocializeActionUtilsBase implements ShareUtilsProxy {
	
	private ShareSystem shareSystem;
	private AuthRequestDialogFactory authRequestDialogFactory;
	
	@Override
	public void showShareDialog(final Activity context, final Entity e, int options, final SocialNetworkShareListener listener) {
		authRequestDialogFactory.show(context, new ShareDialogListener() {
			@Override
			public void onCancel(Dialog dialog) {
				listener.onCancel();
			}
			
			@Override
			public void onContinue(Dialog dialog, final SocialNetwork... networks) {
				shareSystem.addShare(context, getSocialize().getSession(), e, "", ShareType.OTHER, null, new ShareAddListener() {
					@Override
					public void onError(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
					
					@Override
					public void onCreate(Share share) {
						if(listener != null) {
							listener.onCreate(share);
						}
						
						if(share != null && shareSystem != null) {
							for (SocialNetwork network : networks) {
								handleActionShare(context, getSocialize().getSession(), network, share, listener);	
							}
						}
					}
				});
			}
			
			@Override
			public void onAuthFail(Dialog dialog, SocialNetwork network, SocializeException error) {
				listener.onSocialNetworkError(network, error);
			}
		}, options);		
	}

	@Override
	public void shareViaSocialNetworks(final Activity context, final Entity e, final SocialNetworkShareListener listener) {
		showShareDialog(context, e, ShareUtils.FACEBOOK | ShareUtils.TWITTER, listener);
	}
	
	protected void handleActionShare(Activity activity, final SocializeSession session, final SocialNetwork socialNetwork, SocializeAction action, final SocialNetworkShareListener listener) {
		shareSystem.share(activity, session, action, "", null, ShareType.valueOf(socialNetwork), false, new ShareHandlerListener() {
			@Override
			public void onError(Activity parent, SocializeAction action, Exception error) {
				if(listener != null) {
					listener.onSocialNetworkError(socialNetwork, error);
				}
			}
			
			@Override
			public void onBeforePost(Activity parent) {}
			
			@Override
			public void onAfterPost(Activity parent, SocializeAction action) {}
		});	
	}

	@Override
	public void shareViaEmail(Activity context, Entity e, ShareAddListener listener) {
		getSocialize().share(context, e, "", ShareType.EMAIL, listener);
	}

	@Override
	public void shareViaSMS(Activity context, Entity e, ShareAddListener listener) {
		getSocialize().share(context, e, "", ShareType.SMS, listener);
	}

	@Override
	public void getShare(Activity context, ShareGetListener listener, long id) {
		shareSystem.getShare(getSocialize().getSession(), id, listener);
	}

	@Override
	public void getShares(Activity context, ShareListListener listener, long... ids) {
		shareSystem.getSharesById(getSocialize().getSession(), listener, ids);
	}

	@Override
	public void getSharesByUser(Activity context, User user, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByUser(getSocialize().getSession(), user.getId(), start, end, listener);
	}

	@Override
	public void getSharesByEntity(Activity context, Entity e, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByEntity(getSocialize().getSession(), e.getKey(), start, end, listener);
	}

	@Override
	public boolean canShareViaEmail(Activity context) {
		return getSocialize().canShare(context, ShareType.EMAIL);
	}

	@Override
	public boolean canShareViaSMS(Activity context) {
		return getSocialize().canShare(context, ShareType.SMS);
	}

	public void setShareSystem(ShareSystem shareSystem) {
		this.shareSystem = shareSystem;
	}

	public void setAuthRequestDialogFactory(AuthRequestDialogFactory authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
}
