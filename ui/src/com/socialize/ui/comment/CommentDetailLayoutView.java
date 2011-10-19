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
package com.socialize.ui.comment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.ui.BaseView;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class CommentDetailLayoutView extends BaseView {

	private String userId;
	private String commentId;
	private CommentHeader header;
	private CommentDetailContentView content;
	private ProgressDialog dialog = null;
	private Drawable defaultProfilePicture;
	
	private int count = 0;
	
	// Injected
	private Drawables drawables;
	private ViewFactory<CommentHeader> commentHeaderFactory;
	private CommentDetailContentViewFactory commentDetailContentViewFactory;
	private ProgressDialogFactory progressDialogFactory;
	private ImageLoader imageLoader;
	// End injected
	
	public CommentDetailLayoutView(Activity context, String userId) {
		this(context);
		this.userId = userId;
		
		if(this.userId != null) {
			this.userId = this.userId.trim();
		}
	}
	
	public CommentDetailLayoutView(Activity context, String userId, String commentId) {
		this(context, userId);
		this.commentId = commentId;
	}
	
	public CommentDetailLayoutView(Context context) {
		super(context);
	}

	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		setPadding(0, 0, 0, 0);
		setVerticalFadingEdgeEnabled(false);

		header = commentHeaderFactory.make(getContext());
		content = commentDetailContentViewFactory.make(getContext());
		defaultProfilePicture = drawables.getDrawable("default_user_icon.png");
		
		OnClickListener profileClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SocializeUI.getInstance().showUserProfileViewForResult(getActivity(), userId, CommentActivity.PROFILE_UPDATE);
			}
		};
		
		content.getHeaderView().setOnClickListener(profileClickListener);

		addView(header);
		addView(content);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		if(getSocialize().isAuthenticated()) {
			
			dialog = progressDialogFactory.show(getContext(), "Loading", "Please wait...");
			
			count = 2;
			
			doGetUserProfile();
			doGetComment();
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
	
	public void doGetComment() {
		if(!StringUtils.isEmpty(commentId)) {
			int id = Integer.parseInt(commentId);
			getSocialize().getCommentById(id, new CommentGetListener() {
				
				@Override
				public void onError(SocializeException error) {
					// Ignore.. 
					// TODO: log error
					error.printStackTrace();
					countdown();
				}
				
				@Override
				public void onGet(Comment entity) {
					content.setComment(entity);
					countdown();
				}
			});
		}
	}
	
	public void doGetUserProfile() {
		int id = Integer.parseInt(userId);
		
		getSocialize().getUser(id, new UserGetListener() {
			
			@Override
			public void onGet(User user) {
				// Set the user details into the view elements
				setUserDetails(user);
				countdown();
			}
			
			@Override
			public void onError(SocializeException error) {
				countdown();
				showError(getContext(), error);
			}
		});
	}
	
	public void setUserDetails(User user) {
		
		String profilePicData = user.getSmallImageUri();
		final ImageView userIcon = content.getProfilePicture();
		
		if(!StringUtils.isEmpty(profilePicData)) {
			userIcon.getBackground().setAlpha(64);
			
			imageLoader.loadImage(profilePicData, new ImageLoadListener() {
				@Override
				public void onImageLoadFail(Exception error) {
					error.printStackTrace();
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
//		content.getLocation().setText(user.getLocation());
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setUserId(String entityKey) {
		this.userId = entityKey;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setCommentHeaderFactory(ViewFactory<CommentHeader> profileHeaderFactory) {
		this.commentHeaderFactory = profileHeaderFactory;
	}

	public void setCommentDetailContentViewFactory(CommentDetailContentViewFactory profileContentViewFactory) {
		this.commentDetailContentViewFactory = profileContentViewFactory;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void onProfileUpdate() {
		dialog = progressDialogFactory.show(getContext(), "Loading", "Please wait...");
		count = 1;
		doGetUserProfile();
	}
}
