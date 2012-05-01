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
package com.socialize.api.action.comment;

import android.app.Activity;
import android.app.Dialog;
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.dialog.AuthRequestDialogFactory;


/**
 * @author Jason Polites
 *
 */
public class SocializeCommentUtils extends SocializeActionUtilsBase implements CommentUtilsProxy {
	
	private CommentSystem commentSystem;
	private AuthRequestDialogFactory authRequestDialogFactory;

	@Override
	public void addComment(Activity context, final Entity e, final String text, final CommentAddListener listener) {
		
		final SocializeSession session = getSocialize().getSession();
		
		if(isDisplayAuthDialog()) {
			authRequestDialogFactory.show(context, new ShareDialogListener() {
				
				@Override
				public void onShow(Dialog dialog, AuthPanelView dialogView) {}
				
				@Override
				public void onContinue(Dialog dialog, SocialNetwork... networks) {
					commentSystem.addComment(session, e, text, getDefaultShareOptions(), listener);
				}

				@Override
				public void onCancel(Dialog dialog) {}
			}, ShareUtils.FACEBOOK | ShareUtils.TWITTER);
		}
		else {
			commentSystem.addComment(session, e, text, getDefaultShareOptions(), listener);
		}		
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
	public void getCommentsByEntity(Activity context, Entity e, int start, int end, CommentListListener listener) {
		commentSystem.getCommentsByEntity(getSocialize().getSession(), e.getKey(), start, end, listener);
	}
	
	public void setCommentSystem(CommentSystem commentSystem) {
		this.commentSystem = commentSystem;
	}
	
	public void setAuthRequestDialogFactory(AuthRequestDialogFactory authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
	
	
}
