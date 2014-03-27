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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import com.socialize.LikeUtils;
import com.socialize.ShareUtils;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.*;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbutton.LikeButtonListener;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.IAuthDialogFactory;
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
		like(context, entity, getUserLikeOptions(context), listener);
	}

	@Override
	public void like(final Activity context, final Entity entity, final LikeOptions likeOptions, final LikeAddListener listener, final SocialNetwork...networks) {

		try {

			final boolean doShare = isDisplayShareDialog(context, likeOptions);
			final SocializeSession session = getSocialize().getSession();

			if(isDisplayAuthDialog(context, session, likeOptions, networks)) {
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
						doLikeWithoutShareDialog(context, session, entity, likeOptions, listener);
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

						try {
							if(doShare) {
								doLikeWithShareDialog(context, session, entity, likeOptions, listener);
							}
							else {
								if(networks == null || networks.length == 0) {
									doLikeWithoutShareDialog(context, session, entity, likeOptions, listener, UserUtils.getAutoPostSocialNetworks(context));
								}
								else {
									doLikeWithoutShareDialog(context, session, entity, likeOptions, listener, networks);
								}
							}
						}
						catch (SocializeException e) {
							if(listener != null) {
								listener.onError(e);
							}

							Log.e(SocializeLogger.LOG_TAG, "Error adding like", e);
						}
					}
				}, !config.isAllowSkipAuthOnAllActions());
			}
			else {
				if(doShare) {
					doLikeWithShareDialog(context, session, entity, likeOptions, listener);
				}
				else {
					if(networks == null || networks.length == 0) {
						doLikeWithoutShareDialog(context, session, entity, likeOptions, listener, UserUtils.getAutoPostSocialNetworks(context));
					}
					else {
						doLikeWithoutShareDialog(context, session, entity, likeOptions, listener, networks);
					}
				}
			}
		}
		catch (Throwable e) {
			if(listener != null) {
				listener.onError(SocializeException.wrap(e));
			}

			Log.e(SocializeLogger.LOG_TAG, "Error adding like", e);
		}

	}
	
	@Override
	public LikeOptions getUserLikeOptions(Context context) {
		LikeOptions options = new LikeOptions();
		populateActionOptions(context, options);
		return options;
	}

	protected void doLikeWithoutShareDialog(final Activity context, final SocializeSession session, final Entity entity, final LikeAddListener listener) throws SocializeException {
		doLikeWithoutShareDialog(context, session, entity, getUserLikeOptions(context), listener, UserUtils.getAutoPostSocialNetworks(context));
	}
	
	protected void doLikeWithoutShareDialog(final Activity context, final SocializeSession session, final Entity entity, final LikeOptions likeOptions, final LikeAddListener listener, final SocialNetwork...networks) {
		likeSystem.addLike(session, entity, likeOptions, new LikeAddListener() {
			@Override
			public void onError(SocializeException error) {
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
					doActionShare(context, like, null, listener, networks);
				}
			}
		}, networks);		
	}	
	
	protected void doLikeWithShareDialog(final Activity context, final SocializeSession session, final Entity entity, final LikeOptions likeOptions, final LikeAddListener listener) throws SocializeException {
		
		if(isDisplayShareDialog(context, likeOptions)) {

			shareDialogFactory.show(context, entity, null, new ShareDialogListener() {

				@Override
				public void onShow(Dialog dialog, SharePanelView dialogView) {}

				@Override
				public void onFlowInterrupted(DialogFlowController controller) {}
				
				@Override
				public void onSimpleShare(ShareType type) {}

				@Override
				public boolean onContinue(final Dialog dialog, boolean remember, final SocialNetwork... networks) {

					
					UserSettings settings = session.getUserSettings();
					
					if(remember && settings.setAutoPostPreferences(networks)) {
						UserUtils.saveUserSettings(context, settings, null);
					}

					LikeAddListener overrideListener = new LikeAddListener() {

						@Override
						public void onError(SocializeException error) {
							if(dialog != null) {
								dialog.dismiss();
							}
							
							if(listener != null) {
								listener.onError(error);
							}
						}

						@Override
						public void onCreate(final Like like) {
							
							if(listener != null) {
								listener.onCreate(like);
							}
							
							doActionShare(context, like, null, listener, networks);
							
							if(dialog != null) {
								dialog.dismiss();
							}
						}
					};

					likeSystem.addLike(session, entity, likeOptions, overrideListener, networks);

					return false;
				}
				@Override
				public void onCancel(Dialog dialog) {
					if(listener != null) {
						listener.onCancel();
					}
				}
			}, ShareUtils.SOCIAL|ShareUtils.SHOW_REMEMBER|ShareUtils.ALWAYS_CONTINUE);
		}
		else {
			doLikeWithoutShareDialog(context, session, entity, listener);
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
	 * @see com.socialize.api.action.like.LikeUtilsProxy#getLike(android.app.Activity, long, com.socialize.listener.like.LikeGetListener)
	 */
	@Override
	public void getLike(Activity context, long id, LikeGetListener listener) {
		final SocializeSession session = getSocialize().getSession();
		likeSystem.getLike(session, id, listener);
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
		likeSystem.getLikesByEntity(getSocialize().getSession(), entityKey, start, end, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeUtilsProxy#getLikesByApplication(android.app.Activity, int, int, com.socialize.listener.like.LikeListListener)
	 */
	@Override
	public void getLikesByApplication(Activity context, int start, int end, LikeListListener listener) {
		likeSystem.getLikesByApplication(getSocialize().getSession(), start, end, listener);
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
	
	@Override
	public void makeLikeButton(final Activity context, final CompoundButton button, final Entity entity, final LikeButtonListener listener) {
		
		if(listener != null) {
			// Clear any current listener
			button.setOnCheckedChangeListener(null);
		}
		
		// Use onclick because we don't want to trigger the checked listener when we change state
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(listener != null) {
					listener.onClick(button);
				}
				
				if(button.isChecked()) {
					LikeUtils.like(context, entity, new LikeAddListener() {
						@Override
						public void onError(SocializeException error) {
							button.setChecked(false);
							if(listener != null) {
								listener.onError(button, error);
							}
						}
						
						@Override
						public void onCreate(Like result) {
							if(listener != null) {
								listener.onCheckedChanged(button, true);
							}
						}

						@Override
						public void onCancel() {
							button.setChecked(false);
							if(listener != null) {
								listener.onCheckedChanged(button, false);
							}
						}
					});
				}
				else {
					LikeUtils.unlike(context, entity.getKey(), new LikeDeleteListener() {
						@Override
						public void onError(SocializeException error) {
							button.setChecked(true);
							if(listener != null) {
								listener.onError(button, error);
							}
						}
						
						@Override
						public void onDelete() {
							button.setChecked(false);
							if(listener != null) {
								listener.onCheckedChanged(button, false);
							}
						}
					});
				}
			}
		});
		
		// Get the initial state
		LikeUtils.isLiked(context, entity.getKey(), new IsLikedListener() {
			@Override
			public void onNotLiked() {
				button.setChecked(false);
				if(listener != null) {
					listener.onCheckedChanged(button, false);
				}
			}
			
			@Override
			public void onLiked(Like like) {
				button.setChecked(true);
				if(listener != null) {
					listener.onCheckedChanged(button, true);
				}
			}
		});
	}
}


