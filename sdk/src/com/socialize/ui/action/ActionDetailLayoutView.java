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
package com.socialize.ui.action;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ActionDetailLayoutView extends BaseView {

	private String userId; // may be null
	private String actionId;
	private ActionDetailContentView content;
	private ProgressDialog dialog = null;
	private Drawable defaultProfilePicture;
	
	private SocializeAction currentAction;
	private int count = 0;
	
	// Injected
	private Drawables drawables;
	private IBeanFactory<ActionDetailContentView> actionDetailContentViewFactory;
	private ProgressDialogFactory progressDialogFactory;
	private ImageLoader imageLoader;
	// End injected
	
	public ActionDetailLayoutView(Activity context, String userId) {
		this(context);
		this.userId = userId;
		
		if(this.userId != null) {
			this.userId = this.userId.trim();
		}
	}
	
	public ActionDetailLayoutView(Activity context, String userId, String actionId) {
		this(context, userId);
		this.actionId = actionId;
	}
	
	public ActionDetailLayoutView(Context context) {
		super(context);
	}

	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setPadding(0, 0, 0, 0);
		setVerticalFadingEdgeEnabled(false);

		content = actionDetailContentViewFactory.getBean();
		defaultProfilePicture = drawables.getDrawable(Socialize.DEFAULT_USER_ICON);
		
		addView(content);
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		reload();
	}
	
	public void reload() {
		if(getSocialize().isAuthenticated()) {
			
			dialog = progressDialogFactory.show(getContext(), "Loading", "Please wait...");
			
			count = 1;
			
			doGetAction();
		}
		else {
			showError(getContext(), new SocializeException("Socialize not authenticated"));
		}
	}
	
	protected void countdown() {
		count--;
		
		if(count <= 0) {
			if(dialog != null) {
				dialog.dismiss();
			}
			count = 0;
		}
	}
	
	public void doGetAction() {
		if(!StringUtils.isEmpty(actionId)) {
			int id = Integer.parseInt(actionId);
			
			// TODO: this should be able to process generic actions.
			CommentUtils.getComment(getActivity(), new CommentGetListener() {
				@Override
				public void onError(SocializeException error) {
					countdown();
					showError(getContext(), error);
				}
				
				@Override
				public void onGet(Comment entity) {
					ActionDetailLayoutView.this.currentAction = entity;
					content.setAction(entity);
					if(entity.getUser() != null) {
						doGetUserProfile(entity.getUser().getId(), entity);
					}
					else if(!StringUtils.isEmpty(userId)) {
						doGetUserProfile(Long.parseLong(userId), entity);
					}
				}
			}, id);
		}
		else if(!StringUtils.isEmpty(userId)) {
			doGetUserProfile(Long.parseLong(userId), null);
		}
	}
	
	protected void doGetUserProfile(SocializeAction action) {
		if(action.getUser() != null) {
			doGetUserProfile(action.getUser().getId(), action);
		}
		else if(!StringUtils.isEmpty(userId)) {
			doGetUserProfile(Long.parseLong(userId), action);
		}
	}
	
	protected void doGetUserProfile(final long userId, final SocializeAction action) {
		
		if(userId >= 0) {
			
			UserUtils.getUser(getActivity(), userId, new UserGetListener() {
				
				@Override
				public void onGet(User user) {
					// Set the user details into the view elements
					setUserDetails(user, action);
					countdown();
				}
				
				@Override
				public void onError(SocializeException error) {
					countdown();
					showError(getContext(), error);
				}
			});
		}
		else {
			countdown();
		}
	}
	
	public void setUserDetails(User user, SocializeAction action) {
		
		String profilePicData = user.getSmallImageUri();
		final ImageView userIcon = content.getProfilePicture();
		
		if(!StringUtils.isEmpty(profilePicData)) {
			userIcon.getBackground().setAlpha(64);
			
			imageLoader.loadImageByUrl(profilePicData, new ImageLoadListener() {
				@Override
				public void onImageLoadFail(ImageLoadRequest request, Exception error) {
					Log.e(SocializeLogger.LOG_TAG, error.getMessage(), error);
					userIcon.post(new Runnable() {
						public void run() {
							userIcon.setImageDrawable(defaultProfilePicture);
							userIcon.getBackground().setAlpha(255);
						}
					});
				}
				
				@Override
				public void onImageLoad(ImageLoadRequest request, final SafeBitmapDrawable drawable) {
					// Must be run on UI thread
					userIcon.post(new Runnable() {
						public void run() {
							drawable.setAlpha(255);
							userIcon.setImageDrawable(drawable);
							userIcon.getBackground().setAlpha(255);
						}
					});
				}
			});
		}
		else {
			userIcon.setImageDrawable(defaultProfilePicture);
			userIcon.getBackground().setAlpha(255);
		}
		
		content.getDisplayName().setText(user.getDisplayName());
		content.loadUserActivity(user, action);
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setUserId(String entityKey) {
		this.userId = entityKey;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setActionDetailContentViewFactory(IBeanFactory<ActionDetailContentView> actionDetailContentViewFactory) {
		this.actionDetailContentViewFactory = actionDetailContentViewFactory;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
	
	public SocializeAction getCurrentAction() {
		return currentAction;
	}

	public void onProfileUpdate() {
		dialog = progressDialogFactory.show(getContext(), "Loading", "Please wait...");
		count = 1;
		doGetUserProfile(currentAction);
	}
}
