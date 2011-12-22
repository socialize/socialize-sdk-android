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

import android.content.Context;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.config.SocializeConfig;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class CommentAddButtonListener {

	private String consumerKey;
	private String consumerSecret;
	private Context context;
	private CommentButtonCallback callback;
	
	public CommentAddButtonListener(Context context) {
		super();
		this.context = context;
	}

	public CommentAddButtonListener(
			Context context, 
			CommentButtonCallback callback) {
		
		this(context);
		
		this.callback = callback;
		this.consumerKey = getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		this.consumerSecret = getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void onCancel() {
		callback.onCancel();
	}

	public void onComment(String comment, boolean autoPostToFacebook, boolean shareLocation) {
		if(!StringUtils.isEmpty(comment)) {
			if(!getSocialize().isAuthenticated()) {
				getSocialize().authenticate(
						context,
						consumerKey, 
						consumerSecret,
						new CommentReAuthListener(context, callback, comment, autoPostToFacebook, shareLocation));
			}
			else {
				callback.onComment(comment, autoPostToFacebook, shareLocation);
			}
		}
	}

	public void setCallback(CommentButtonCallback callback) {
		this.callback = callback;
	}

	public CommentButtonCallback getCallback() {
		return callback;
	}
}
