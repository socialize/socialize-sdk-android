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
package com.socialize.test;

import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public class PublicCommentUtils extends SocializeCommentUtils {

	@Override
	public void doCommentWithShareDialog(Activity context, SocializeSession session, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener) {
		super.doCommentWithShareDialog(context, session, entity, text, commentOptions, listener);
	}

	@Override
	public void doCommentWithoutShareDialog(Activity context, SocializeSession session, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener) {
		super.doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener);
	}

	@Override
	public void doCommentWithoutShareDialog(Activity context, SocializeSession session, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork... networks) {
		super.doCommentWithoutShareDialog(context, session, entity, text, commentOptions, listener, networks);
	}

	@Override
	public boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
		return super.isDisplayShareDialog(context, options);
	}

	@Override
	public SocializeService getSocialize() {
		return super.getSocialize();
	}

	@Override
	public void doActionShare(Activity context, SocializeAction action, String text, SocialNetworkListener listener, SocialNetwork... networks) {
		super.doActionShare(context, action, text, listener, networks);
	}
}
