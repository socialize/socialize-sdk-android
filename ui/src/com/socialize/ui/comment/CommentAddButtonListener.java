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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class CommentAddButtonListener implements OnClickListener {

	private CommentEditField field;
	private String consumerKey;
	private String consumerSecret;
	private InputMethodManager imm;
	private Context context;
	private CommentButtonCallback listener;
	
	public CommentAddButtonListener(Context context, CommentEditField field, CommentButtonCallback listener) {
		super();
		this.field = field;
		this.context = context;
		this.listener = listener;
		this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		this.consumerKey = SocializeUI.getInstance().getCustomConfigValue(context,SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		this.consumerSecret = SocializeUI.getInstance().getCustomConfigValue(context,SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
//		final String facebookAppId = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_APP_ID);
		
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		final String text = field.getText().toString();
		if(!StringUtils.isEmpty(text)) {
			imm.hideSoftInputFromWindow(field.getEditText().getWindowToken(), 0);

			// TODO: add other providers
			// TODO: enable FB auth
//			if(!Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
			if(!Socialize.getSocialize().isAuthenticated()) {
				Socialize.getSocialize().authenticate(
						consumerKey, 
						consumerSecret,
//						AuthProviderType.FACEBOOK, 
//						facebookAppId,
						new SocializeAuthListener() {

							@Override
							public void onError(SocializeException error) {
								listener.onError(context, error.getMessage());
							}

							@Override
							public void onAuthSuccess(SocializeSession session) {
								listener.onComment(text);
							}

							@Override
							public void onAuthFail(SocializeException error) {
								listener.onError(context, error.getMessage());
							}
						});
			}
			else {
				listener.onComment(text);
			}
		}
	}
}
