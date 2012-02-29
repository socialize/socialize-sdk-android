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

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 *
 */
public class CommentReAuthListener implements SocializeAuthListener {

	private CommentButtonCallback callback;
	private String comment;
	
	@Deprecated
	private boolean autoPostToFacebook;
	
	private boolean shareLocation;
	private boolean subscribe;
	private Context context;
	
	private SocialNetwork[] networks;
	
	@Deprecated
	public CommentReAuthListener(Context context, CommentButtonCallback callback, String comment, boolean autoPostToFacebook, boolean shareLocation, boolean subscribe) {
		this(context, callback, comment, shareLocation, subscribe, (autoPostToFacebook) ? SocialNetwork.FACEBOOK : SocialNetwork.NONE);
	}
	
	public CommentReAuthListener(Context context, CommentButtonCallback callback, String comment, boolean shareLocation, boolean subscribe, SocialNetwork...networks) {
		super();
		this.callback = callback;
		this.comment = comment;
		this.context = context;
		this.networks = networks;
		this.shareLocation = shareLocation;
		this.subscribe = subscribe;
	}	
	
	@Override
	public void onError(SocializeException error) {
		callback.onError(context, error);
	}

	@Override
	public void onAuthSuccess(SocializeSession session) {
		callback.onComment(comment, shareLocation, subscribe, networks);
	}

	@Override
	public void onAuthFail(SocializeException error) {
		callback.onError(context, error);
	}

	@Override
	public void onCancel() {
		// Nothing
	}
}
