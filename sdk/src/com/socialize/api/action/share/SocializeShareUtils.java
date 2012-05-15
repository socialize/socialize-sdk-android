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
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.ShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;

/**
 * @author Jason Polites
 */
public class SocializeShareUtils extends SocializeActionUtilsBase implements ShareUtilsProxy {
	
	private ShareSystem shareSystem;
	private ShareDialogFactory shareDialogFactory;
	
	@Override
	public void showShareDialog(final Activity context, final String entityKey, int options, final SocialNetworkShareListener socialNetworkListener, final ShareDialogListener dialogListener) {
		shareDialogFactory.show(context, entityKey, socialNetworkListener, new ShareDialogListener() {
			@Override
			public void onCancel(Dialog dialog) {
				if(dialogListener != null) {
					dialogListener.onCancel(dialog);
				}
			}
			
			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {
				if(dialogListener != null) {
					dialogListener.onShow(dialog, dialogView);
				}				
			}
			
			@Override
			public void onFlowInterrupted(DialogFlowController controller) {
				// Will not be called.
			}

			@Override
			public boolean onContinue(final Dialog dialog, boolean remember, final SocialNetwork... networks) {
				boolean consumed = true;
				
				if(dialogListener != null) {
					consumed = dialogListener.onContinue(dialog, remember, networks);
				}					
				
				if(!consumed) {
					doShare(dialog, context, entityKey, socialNetworkListener, "", networks);
				}
				else {
					dialogListener.onFlowInterrupted(new DialogFlowController() {
						
						@Override
						public void onContinue(String text) {
							doShare(dialog, context, entityKey, socialNetworkListener, text, networks);
						}

						@Override
						public void onCancel() {
							if(dialogListener != null) {
								dialogListener.onCancel(dialog);
							}
						}
					});
				}
				
				return false;
			}
		}, options);		
	}
	
	protected void doShare(final Dialog dialog, final Activity context, final String entityKey, final SocialNetworkShareListener socialNetworkListener, final String text, final SocialNetwork... networks) {
		final ProgressDialog progress = SafeProgressDialog.show(context);
		
		shareSystem.addShare(context, getSocialize().getSession(), entityKey, text, ShareType.OTHER, new ShareAddListener() {
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
						shareSystem.share(context, getSocialize().getSession(), share, text, null, ShareType.valueOf(network), false, socialNetworkListener);									
					}
				}
				
				progress.dismiss();
				dialog.dismiss();
			}
		}, networks);
	}

	@Override
	public void shareViaEmail(Activity context, String entityKey, ShareAddListener listener) {
		getSocialize().share(context, entityKey, "", ShareType.EMAIL, listener);
	}

	@Override
	public void shareViaSMS(Activity context, String entityKey, ShareAddListener listener) {
		getSocialize().share(context, entityKey, "", ShareType.SMS, listener);
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
	public void getSharesByEntity(Activity context, String entityKey, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByEntity(getSocialize().getSession(), entityKey, start, end, listener);
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

	public void setShareDialogFactory(ShareDialogFactory authRequestDialogFactory) {
		this.shareDialogFactory = authRequestDialogFactory;
	}
}
