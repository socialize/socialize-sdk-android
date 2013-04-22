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
package com.socialize.api.action.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.android.ioc.Container;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentDeleteListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentView;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 */
public class SocializeCommentUtils extends SocializeActionUtilsBase implements CommentUtilsProxy {
	
	private CommentSystem commentSystem;
	private IAuthDialogFactory authDialogFactory;
	private IShareDialogFactory shareDialogFactory;
	private ListenerHolder listenerHolder;
	private Container container;
	private SocializeLogger logger;
	
	@Override
	public void onCreate(Container container) {
		this.container = container;
	}

	@Override
	public void onDestroy(Container container) {
		this.container = null;
	}

	@Override
	public void showCommentView(Activity context, Entity entity) {
		showCommentView(context, entity, null);
	}
	
	@Override
	public void preloadCommentView(Activity context) {
		// Just do a get bean as it will be cached
		if(container != null) {
			container.getBean("commentList");
		}
	}

	@Override
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {
		if(listener != null && listenerHolder != null) {
			listenerHolder.push(CommentView.COMMENT_LISTENER, listener);
		}

		try {
			Intent i = newIntent(context, CommentActivity.class);
			i.putExtra(Socialize.ENTITY_OBJECT, entity);
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			logger.error("Could not find CommentActivity.  Make sure you have added this to your AndroidManifest.xml");
		} 
	}

	@Override
	public CommentOptions getUserCommentOptions(Context context) {
		CommentOptions options = new CommentOptions();
		populateActionOptions(context, options);
		options.setSubscribeToUpdates(true);
		return options;		
	}
	
	@Override
	public void deleteComment(Activity context, long id, CommentDeleteListener listener) {
		commentSystem.deleteComment(getSocialize().getSession(), id, listener);
	}

	@Override
	public void addComment(Activity context, Entity entity, String text, CommentAddListener listener) {
		addComment(context, entity, text, getUserCommentOptions(context), listener);
	}

	@Override
	public void addComment(final Activity context, final Entity entity, final String text, final CommentOptions commentOptions, final CommentAddListener listener, final SocialNetwork...networks) {

		try {

			final boolean doShare = isDisplayShareDialog(context, commentOptions);
			final SocializeSession session = getSocialize().getSession();

			if(isDisplayAuthDialog(context, session, commentOptions, networks)) {

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
						try {
							doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener);
						}
						catch (SocializeException e) {
							if(listener != null) {
								listener.onError(e);
							}
							if(logger != null) {
								logger.error("Error adding comment", e);
							}
							else {
								Log.e(SocializeLogger.LOG_TAG, "Error adding comment", e);
							}
						}
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
								doCommentWithShareDialog(context, session, entity, text, commentOptions, listener);
							}
							else {
								if(networks == null || networks.length == 0) {

										doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, UserUtils.getAutoPostSocialNetworks(context));

								}
								else {
									doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, networks);
								}
							}
						}
						catch (SocializeException e) {
							if(listener != null) {
								listener.onError(e);
							}
							if(logger != null) {
								logger.error("Error adding comment", e);
							}
							else {
								Log.e(SocializeLogger.LOG_TAG, "Error adding comment", e);
							}
						}
					}
				}, !(config.isAllowSkipAuthOnComments() && config.isAllowSkipAuthOnAllActions()));
			}
			else {
				if(doShare) {
					doCommentWithShareDialog(context, session, entity, text, commentOptions, listener);
				}
				else {
					if(networks == null || networks.length == 0) {
						doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, UserUtils.getAutoPostSocialNetworks(context));
					}
					else {
						doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, networks);
					}
				}
			}
		}
		catch (Throwable e) {
			if(listener != null) {
				listener.onError(SocializeException.wrap(e));
			}

			if(logger != null) {
				logger.error("Error adding comment", e);
			}
			else {
				Log.e(SocializeLogger.LOG_TAG, "Error adding comment", e);
			}
		}
	}

	protected void doCommentWithShareDialog(final Activity context, final SocializeSession session, final Entity entity, final String text, final CommentOptions commentOptions, final CommentAddListener listener) throws SocializeException {
		
		if(isDisplayShareDialog(context, commentOptions)) {
			shareDialogFactory.show(context, entity, null, new ShareDialogListener() {
				@Override
				public void onShow(Dialog dialog, SharePanelView dialogView) {}
				
				@Override
				public void onFlowInterrupted(DialogFlowController controller) {}
				
				@Override
				public void onSimpleShare(ShareType type) {}

				@Override
				public boolean onContinue(final Dialog dialog, boolean remember, final SocialNetwork...networks) {
					
					UserSettings settings = session.getUserSettings();
					
					if(remember && settings.setAutoPostPreferences(networks)) {
						UserUtils.saveUserSettings(context, settings, null);
					}

					CommentAddListener overrideListener = new CommentAddListener() {

						@Override
						public void onError(SocializeException error) {
							dialog.dismiss();
							if(listener != null) {
								listener.onError(error);
							}
						}

						@Override
						public void onCreate(Comment comment) {
							
							if(listener != null) {
								listener.onCreate(comment);
							}
							
							doActionShare(context, comment, text, listener, networks);
							
							dialog.dismiss();
						}
					};

					commentSystem.addComment(session, entity, text, commentOptions, overrideListener, networks);

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
			doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener);
		}
	}	
	
	protected void doCommentWithoutShareDialog(final Activity context, final SocializeSession session, final Entity entity, final String text, CommentOptions commentOptions, final CommentAddListener listener) throws SocializeException {
		doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, UserUtils.getAutoPostSocialNetworks(context));
	}
	
	protected void doCommentWithoutShareDialog(final Activity context, final SocializeSession session, final Entity entity, final String text, final CommentOptions commentOptions, final CommentAddListener listener, final SocialNetwork...networks) {
		commentSystem.addComment(session, entity, text, commentOptions, new CommentAddListener() {
			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(error);
				}
			}
			
			@Override
			public void onCreate(Comment comment) {
				if(listener != null) {
					listener.onCreate(comment);
				}
				if(networks != null && networks.length > 0) {
					doActionShare(context, comment, text, listener, networks);
				}
			}
		}, networks);		
	}
	
	protected Intent newIntent(Activity context, Class<?> cls) {
		return new Intent(context, cls);
	}

	@Override
	public void getComment(Activity context, long id, CommentGetListener listener) {
		commentSystem.getComment(getSocialize().getSession(), id, listener);
	}

	@Override
	public void getComments(Activity context, CommentListListener listener, long... ids) {
		commentSystem.getCommentsById(getSocialize().getSession(), listener, ids);
	}

	@Override
	public void getCommentsByUser(Activity context, User user, int start, int end, CommentListListener listener) {
		commentSystem.getCommentsByUser(getSocialize().getSession(), user.getId(), start, end, listener);
	}

	@Override
	public void getCommentsByEntity(Activity context, String entityKey, int start, int end, CommentListListener listener) {
		commentSystem.getCommentsByEntity(getSocialize().getSession(), entityKey, start, end, listener);
	}
	
	@Override
	public void getCommentsByApplication(Activity context, int start, int end, CommentListListener listener) {
		commentSystem.getCommentsByApplication(getSocialize().getSession(), start, end, listener);
	}

	public void setCommentSystem(CommentSystem commentSystem) {
		this.commentSystem = commentSystem;
	}
	
	public void setAuthDialogFactory(IAuthDialogFactory authDialogFactory) {
		this.authDialogFactory = authDialogFactory;
	}

	public void setShareDialogFactory(IShareDialogFactory shareDialogFactory) {
		this.shareDialogFactory = shareDialogFactory;
	}

	public void setListenerHolder(ListenerHolder listenerHolder) {
		this.listenerHolder = listenerHolder;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
