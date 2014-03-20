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
package com.socialize.api.action.share;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;
import org.json.JSONObject;

/**
 * @author Jason Polites
 */
public class SocializeShareUtils extends SocializeActionUtilsBase implements ShareUtilsProxy {
	
	private ShareSystem shareSystem;
	private IShareDialogFactory shareDialogFactory;
	private IAuthDialogFactory authDialogFactory;
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getUserShareOptions(android.content.Context)
	 */
	@Override
	public ShareOptions getUserShareOptions(Context context) {
		ShareOptions options = new ShareOptions();
		populateActionOptions(context, options);
		return options;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#showLinkDialog(android.app.Activity, com.socialize.ui.auth.AuthDialogListener)
	 */
	@Override
	public void showLinkDialog(Activity context, AuthDialogListener listener) {
		authDialogFactory.show(context, listener, !config.isAllowSkipAuthOnAllActions());
	}
	
	@Override
	public void preloadShareDialog(Activity context) {
		shareDialogFactory.preload(context);
	}

	@Override
	public void preloadLinkDialog(Activity context) {
		authDialogFactory.preload(context);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#showShareDialog(android.app.Activity, com.socialize.entity.Entity, int, com.socialize.api.action.share.SocialNetworkShareListener, com.socialize.ui.share.ShareDialogListener)
	 */
	@Override
	public void showShareDialog(final Activity context, final Entity entity, int options, final SocialNetworkShareListener socialNetworkListener, final ShareDialogListener dialogListener) {
		shareDialogFactory.show(context, entity, socialNetworkListener, new ShareDialogListener() {
			
			SharePanelView dialogView;
			
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
				this.dialogView = dialogView;
			}
			
			@Override
			public void onSimpleShare(ShareType type) {
				if(dialogListener != null) {
					dialogListener.onSimpleShare(type);
				}	
			}

			@Override
			public void onFlowInterrupted(DialogFlowController controller) {
				// Will not be called.
			}

			@Override
			public boolean onContinue(final Dialog dialog, boolean remember, final SocialNetwork... networks) {
				boolean consumed = false;
				
				if(dialogListener != null) {
					consumed = dialogListener.onContinue(dialog, remember, networks);
				}					
				
				if(!consumed) {
					doShare(dialog, context, entity, socialNetworkListener, networks);
				}
				else {
                    dialogListener.onFlowInterrupted(new DialogFlowController() {

                        @Override
                        public void onContinue(String text) {

                            ShareOptions options = new ShareOptions();
                            options.setText(text);

                            doShare(dialog, context, entity, socialNetworkListener, options, networks);
                        }

                        @Override
                        public void onCancel() {

                            if(dialogView != null) {
                                dialogView.getContinueButton().setEnabled(true);
                            }

                            dialogListener.onCancel(dialog);
                        }
                    });
				}
				
				return false;
			}
		}, options);		
	}
	
	protected void doShare(final Activity context, final Entity entity, final SocialNetworkShareListener socialNetworkListener, final ShareOptions shareOptions, final SocialNetwork... networks) {
		doShare(null, context, entity, socialNetworkListener, shareOptions, networks);
	}
	
	protected void doShare(final Dialog dialog, final Activity context, final Entity entity, final SocialNetworkShareListener socialNetworkListener, final SocialNetwork... networks) {
		doShare(dialog, context, entity, socialNetworkListener, null, networks);
	}
	
	protected void doShare(final Dialog dialog, final Activity context, final Entity entity, final SocialNetworkShareListener socialNetworkListener, final ShareOptions shareOptions, final SocialNetwork... networks) {
		final ProgressDialog progress = SafeProgressDialog.show(context);
		
		ShareType shareType = ShareType.OTHER;
		
		if(networks != null && networks.length == 1) {
			shareType = ShareType.valueOf(networks[0]);
		}
		
		String text = null;
		
		if(shareOptions != null) {
			text = shareOptions.getText();
		}
		
		if(text == null) {
			text = "";
		}
		
		final String fText = text;
		
		shareSystem.addShare(context, getSocialize().getSession(), entity, text, shareType, new ShareAddListener() {
			@Override
			public void onError(SocializeException error) {
				if(socialNetworkListener != null) {
					socialNetworkListener.onError(error);
				}
				
				progress.dismiss();
				
				if(dialog != null) {
					dialog.dismiss();
				}
				
			}
			
			@Override
			public void onCreate(Share share) {
				if(socialNetworkListener != null) {
					socialNetworkListener.onCreate(share);
				}
				
				if(share != null && shareSystem != null && networks != null && networks.length > 0) {
					for (final SocialNetwork network : networks) {
						shareSystem.share(context, getSocialize().getSession(), share, fText, null, ShareType.valueOf(network), socialNetworkListener);
					}
				}
				
				progress.dismiss();
				
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		}, networks);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#registerShare(android.app.Activity, com.socialize.entity.Entity, com.socialize.api.action.share.ShareOptions, com.socialize.listener.share.ShareAddListener, com.socialize.networks.SocialNetwork[])
	 */
	@Override
	public void registerShare(Activity context, Entity entity, ShareOptions shareOptions, ShareAddListener listener, SocialNetwork...networks) {
		
		String text = null;
		if(shareOptions != null)  text = shareOptions.getText();
		if(text == null) text = "";
		
		ShareType shareType = ShareType.OTHER;
		
		if(networks != null && networks.length > 0) {
			if(networks.length == 1) {
				shareType = ShareType.valueOf(networks[0]);
			}
		}
		
		shareSystem.addShare(context, getSocialize().getSession(), entity, text, shareType, listener, networks);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#shareViaEmail(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.share.ShareAddListener)
	 */
	@Override
	public void shareViaEmail(Activity context, Entity entity, ShareAddListener listener) {
		doShare(context, entity, ShareType.EMAIL, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#shareViaGooglePlus(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.share.ShareAddListener)
	 */
	@Override
	public void shareViaGooglePlus(Activity context, Entity entity, ShareAddListener listener) {
		doShare(context, entity, ShareType.GOOGLE_PLUS, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#shareViaOther(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.share.ShareAddListener)
	 */
	@Override
	public void shareViaOther(Activity context, Entity entity, ShareAddListener listener) {
		doShare(context, entity, ShareType.OTHER, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#shareViaSMS(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.share.ShareAddListener)
	 */
	@Override
	public void shareViaSMS(Activity context, Entity entity, ShareAddListener listener) {
		doShare(context, entity, ShareType.SMS, listener);
	}
	
	// Creates the socialize share object.
	protected void doShare(final Activity context, final Entity entity, final ShareType shareType, final ShareAddListener shareAddListener) {
		final SocializeSession session = getSocialize().getSession();
		shareSystem.addShare(context, session, entity, "", shareType, null, new ShareAddListener() {
			@Override
			public void onError(SocializeException error) {
				if(shareAddListener != null) {
					shareAddListener.onError(error);
				}
			}
			
			@Override
			public void onCreate(Share share) {
				if(share != null && shareSystem != null) {
					handleNonNetworkShare(context, session, shareType, share, "", null, shareAddListener);
				}
			}
		});	
	}
	
	// Handles a non-network share like email or SMS.
	protected void handleNonNetworkShare(Activity activity, final SocializeSession session, final ShareType shareType, final Share share, String shareText, Location location, final ShareAddListener shareAddListener) {

		SocialNetworkListener snListener = new SocialNetworkListener() {

			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				if(shareAddListener != null) {
					shareAddListener.onError(SocializeException.wrap(error));
				}
			}

			@Override
			public void onCancel() {
				if(shareAddListener != null) {
					shareAddListener.onCancel();
				}
			}

			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				return shareAddListener instanceof SocialNetworkListener && ((SimpleShareListener) shareAddListener).onBeforePost(parent, socialNetwork, postData);
			}

			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
				if(shareAddListener != null) {
					shareAddListener.onCreate(share);
				}
			}
		};

		shareSystem.share(activity, session, share, shareText, location, shareType, snListener);
	}	

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#shareViaSocialNetworks(android.app.Activity, com.socialize.entity.Entity, com.socialize.api.action.share.ShareOptions, com.socialize.api.action.share.SocialNetworkShareListener, com.socialize.networks.SocialNetwork[])
	 */
	@Override
	public void shareViaSocialNetworks(Activity context, final Entity entity, final ShareOptions shareOptions, final SocialNetworkShareListener listener, final SocialNetwork...networks) {
		
		final SocializeSession session = getSocialize().getSession();
		
		if(isDisplayAuthDialog(context, session, shareOptions, networks)) {
			
			authDialogFactory.show(context, new AuthDialogListener() {
				
				@Override
				public void onShow(Dialog dialog, AuthPanelView dialogView) {}
				
				@Override
				public void onCancel(Dialog dialog) {
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onSkipAuth(Activity context, Dialog dialog) {
					dialog.dismiss();
					doShare(context, entity, listener, shareOptions, networks);
				}

				@Override
				public void onError(Activity context, Dialog dialog, Exception error) {
					dialog.dismiss();
					if(listener != null) {
						listener.onError(SocializeException.wrap(error));
					}
				}

				@Override
				public void onAuthenticate(Activity context, Dialog dialog, SocialNetwork network) {
					dialog.dismiss();
					doShare(context, entity, listener, shareOptions, network);
				}
			}, !config.isAllowSkipAuthOnAllActions());
		}
		else {
			doShare(context, entity, listener, shareOptions, networks);
		}			
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getShare(android.app.Activity, com.socialize.listener.share.ShareGetListener, long)
	 */
	@Override
	public void getShare(Activity context, ShareGetListener listener, long id) {
		shareSystem.getShare(getSocialize().getSession(), id, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getShares(android.app.Activity, com.socialize.listener.share.ShareListListener, long[])
	 */
	@Override
	public void getShares(Activity context, ShareListListener listener, long... ids) {
		shareSystem.getSharesById(getSocialize().getSession(), listener, ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getSharesByUser(android.app.Activity, long, int, int, com.socialize.listener.share.ShareListListener)
	 */
	@Override
	public void getSharesByUser(Activity context, User user, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByUser(getSocialize().getSession(), user.getId(), start, end, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getSharesByEntity(android.app.Activity, java.lang.String, int, int, com.socialize.listener.share.ShareListListener)
	 */
	@Override
	public void getSharesByEntity(Activity context, String entityKey, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByEntity(getSocialize().getSession(), entityKey, start, end, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#getSharesByApplication(android.app.Activity, int, int, com.socialize.listener.share.ShareListListener)
	 */
	@Override
	public void getSharesByApplication(Activity context, int start, int end, ShareListListener listener) {
		shareSystem.getSharesByApplication(getSocialize().getSession(), start, end, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#canShareViaEmail(android.app.Activity)
	 */
	@Override
	public boolean canShareViaEmail(Activity context) {
		return getSocialize().canShare(context, ShareType.EMAIL);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.share.ShareUtilsProxy#canShareViaSMS(android.app.Activity)
	 */
	@Override
	public boolean canShareViaSMS(Activity context) {
		return getSocialize().canShare(context, ShareType.SMS);
	}

	public void setShareSystem(ShareSystem shareSystem) {
		this.shareSystem = shareSystem;
	}

	public void setShareDialogFactory(IShareDialogFactory shareDialogFactory) {
		this.shareDialogFactory = shareDialogFactory;
	}
	
	public void setAuthDialogFactory(IAuthDialogFactory authDialogFactory) {
		this.authDialogFactory = authDialogFactory;
	}
}
