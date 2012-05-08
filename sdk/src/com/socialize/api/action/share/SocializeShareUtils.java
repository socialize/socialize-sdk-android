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
import android.app.ProgressDialog;
import com.socialize.ShareUtils;
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
import com.socialize.share.ShareHandlerListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.dialog.AuthDialogFactory;
import com.socialize.ui.dialog.SafeProgressDialog;

/**
 * @author Jason Polites
 */
public class SocializeShareUtils extends SocializeActionUtilsBase implements ShareUtilsProxy {
	
	private ShareSystem shareSystem;
	private AuthDialogFactory authRequestDialogFactory;
	
	@Override
	public void showShareDialog(final Activity context, final Entity e, int options, final SocialNetworkShareListener socialNetworkListener, final ShareDialogListener dialogListener) {
		authRequestDialogFactory.show(context, e, socialNetworkListener, new ShareDialogListener() {
			@Override
			public void onCancel(Dialog dialog) {
				if(dialogListener != null) {
					dialogListener.onCancel(dialog);
				}
			}
			
			@Override
			public void onShow(Dialog dialog, AuthPanelView dialogView) {
				if(dialogListener != null) {
					dialogListener.onShow(dialog, dialogView);
				}				
			}

			@Override
			public void onContinue(final Dialog dialog, final SocialNetwork... networks) {
				
				if(dialogListener != null) {
					dialogListener.onContinue(dialog, networks);
				}					
				
				final ProgressDialog progress = SafeProgressDialog.show(context);
				
				shareSystem.addShare(context, getSocialize().getSession(), e, ShareType.OTHER, new ShareAddListener() {
					@Override
					public void onError(SocializeException error) {
						if(socialNetworkListener != null) {
							socialNetworkListener.onError(error);
						}
						
						progress.dismiss();
						dialog.dismiss();
					}
					
					@Override
					public void onCreate(Share share) {
						if(socialNetworkListener != null) {
							socialNetworkListener.onCreate(share);
						}
						
						if(share != null && shareSystem != null && networks != null && networks.length > 0) {
							for (int i = 0; i < networks.length; i++) {
							
								final SocialNetwork network = networks[i];
								
								shareSystem.share(context, getSocialize().getSession(), share, "", null, ShareType.valueOf(network), false, new ShareHandlerListener() {
									@Override
									public void onError(Activity parent, SocializeAction action, Exception error) {
										if(socialNetworkListener != null) {
											socialNetworkListener.onSocialNetworkError(network, error);
										}
									}
									
									@Override
									public void onBeforePost(Activity parent) {}
									
									@Override
									public void onAfterPost(Activity parent, SocializeAction action) {
									
									}
								});									
							}
						}
						
						progress.dismiss();
						dialog.dismiss();
					}
				}, networks);
			}
		}, options);		
	}

	@Override
	public void shareViaSocialNetworks(final Activity context, final Entity e, final SocialNetworkShareListener listener) {
		showShareDialog(context, e, ShareUtils.FACEBOOK | ShareUtils.TWITTER, listener, null);
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

	public void setAuthRequestDialogFactory(AuthDialogFactory authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
}
