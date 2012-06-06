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
package com.socialize.api.action.like;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import com.socialize.ShareUtils;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;

/**
 * @author Jason Polites
 */
public class SocializeLikeUtils extends SocializeActionUtilsBase implements LikeUtilsProxy {

	private IAuthDialogFactory authDialogFactory;
	private IShareDialogFactory shareDialogFactory;
	private LikeSystem likeSystem;

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#like(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.like.LikeAddListener)
	 */
	@Override
	public void like(Activity context, final Entity entity, final LikeAddListener listener) {

		final SocializeSession session = getSocialize().getSession();

		if(isDisplayAuthDialog()) {
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
					doLikeWithoutShare(context, session, entity, listener);
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
					doLikeWithShare(context, session, entity, listener);
				}
			});
		}
		else {
			doLikeWithShare(context, session, entity, listener);
		}
	}

	@Override
	public void like(final Activity context, final Entity entity, final LikeOptions likeOptions, final LikeAddListener listener, final SocialNetwork...networks) {
		final SocializeSession session = getSocialize().getSession();

		if(isDisplayAuthDialog(likeOptions, networks)) {
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
					doLikeWithoutShare(context, session, entity, likeOptions, listener);
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
					doLikeWithoutShare(context, session, entity, likeOptions, listener, network);
				}
			});
		}
		else {
			doLikeWithoutShare(context, session, entity, likeOptions, listener, networks);
		}		
	}
	
	@Override
	public LikeOptions getUserLikeOptions(Context context) {
		LikeOptions options = new LikeOptions();
		populateActionOptions(options);
		return options;
	}

	protected void doLikeWithoutShare(final Activity context, final SocializeSession session, final Entity entity, final LikeAddListener listener) {
		doLikeWithoutShare(context, session, entity, getUserLikeOptions(context), listener, UserUtils.getAutoPostSocialNetworks(context));
	}
	
	protected void doLikeWithoutShare(final Activity context, final SocializeSession session, final Entity entity, final LikeOptions likeOptions, final LikeAddListener listener, final SocialNetwork...networks) {
		final SafeProgressDialog progress = SafeProgressDialog.show(context, "Posting like", "Please wait...");
		likeSystem.addLike(session, entity, likeOptions, new LikeAddListener() {
			@Override
			public void onError(SocializeException error) {
				progress.dismiss();
				if(listener != null) {
					listener.onError(error);
				}
			}
			
			@Override
			public void onCreate(Like like) {
				if(listener != null) {
					listener.onCreate(like);
				}
				if(networks != null) {
					doActionShare(context, like, null, progress, listener, networks);
				}
				else {
					if(progress != null) {
						progress.dismiss();
					}
				}
			}
		}, networks);		
	}	
	
	protected void doLikeWithShare(final Activity context, final SocializeSession session, final Entity entity, final LikeAddListener listener) {
		
		if(isDisplayShareDialog(context)) {

			shareDialogFactory.show(context, entity, null, new ShareDialogListener() {

				@Override
				public void onShow(Dialog dialog, SharePanelView dialogView) {}

				@Override
				public void onFlowInterrupted(DialogFlowController controller) {}

				@Override
				public boolean onContinue(final Dialog dialog, boolean remember, final SocialNetwork... networks) {

					int count = 0;
					
					UserSettings settings = session.getUserSettings();
					
					if(networks != null) {
						count = networks.length;
					}
					
					final SafeProgressDialog progress = SafeProgressDialog.show(context, count);
					
					if(remember && settings.setAutoPostPreferences(networks)) {
						UserUtils.saveUserSettings(context, settings, null);
					}

					LikeOptions options = new LikeOptions();
					options.setShareLocation(settings.isLocationEnabled());

					LikeAddListener overrideListener = new LikeAddListener() {

						@Override
						public void onError(SocializeException error) {
							progress.dismissAll();
							dialog.dismiss();
							if(listener != null) {
								listener.onError(error);
							}
						}

						@Override
						public void onCreate(final Like like) {
							
							if(listener != null) {
								listener.onCreate(like);
							}
							
							doActionShare(context, like, null, progress, listener, networks);
							
							dialog.dismiss();
						}
					};

					likeSystem.addLike(session, entity, options, overrideListener, networks);

					return false;
				}
				@Override
				public void onCancel(Dialog dialog) {
					if(listener != null) {
						listener.onCancel();
					}
				}
			}, ShareUtils.COMMENT_AND_LIKE|ShareUtils.SHOW_REMEMBER);
		}
		else {
			doLikeWithoutShare(context, session, entity, listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#unlike(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.like.LikeDeleteListener)
	 */
	@Override
	public void unlike(Activity context, String entityKey, final LikeDeleteListener listener) {
		final SocializeSession session = getSocialize().getSession();
		// Get the like based on the key
		likeSystem.getLike(session, entityKey , new LikeGetListener() {
			@Override
			public void onGet(Like entity) {
				if(entity != null) {
					likeSystem.deleteLike(session, entity.getId(), listener);
				}
				else {
					if(listener != null) {
						listener.onDelete();
					}
				}
			}

			@Override
			public void onError(SocializeException error) {

				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						if(listener != null) {
							listener.onDelete();
						}
						return;
					}
				}

				if(listener != null) {
					listener.onError(error);
				}
			}
		});
	}


	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#getLike(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.like.LikeGetListener)
	 */
	@Override
	public void getLike(Activity context, String entityKey, LikeGetListener listener) {
		final SocializeSession session = getSocialize().getSession();
		likeSystem.getLike(session, entityKey, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#getLikesByUser(android.app.Activity, com.socialize.entity.User, int, int, com.socialize.listener.like.LikeListListener)
	 */
	@Override
	public void getLikesByUser(Activity context, User user, int start, int end, LikeListListener listener) {
		final SocializeSession session = getSocialize().getSession();
		likeSystem.getLikesByUser(session, user.getId(), start, end, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#getLikesByEntity(android.app.Activity, com.socialize.entity.Entity, int, int, com.socialize.listener.like.LikeListListener)
	 */
	@Override
	public void getLikesByEntity(Activity context, String entityKey, int start, int end, LikeListListener listener) {
		final SocializeSession session = getSocialize().getSession();
		likeSystem.getLikesByEntity(session, entityKey, start, end, listener);
	}

	public void setAuthDialogFactory(IAuthDialogFactory authDialogFactory) {
		this.authDialogFactory = authDialogFactory;
	}

	public void setShareDialogFactory(IShareDialogFactory shareDialogFactory) {
		this.shareDialogFactory = shareDialogFactory;
	}

	public void setLikeSystem(LikeSystem likeSystem) {
		this.likeSystem = likeSystem;
	}
}


