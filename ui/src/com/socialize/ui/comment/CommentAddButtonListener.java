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

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class CommentAddButtonListener implements OnClickListener {

	private CommentEditField field;
	private String consumerKey;
	private String consumerSecret;
	private KeyboardUtils keyboardUtils;
	private Context context;
	private CommentButtonCallback callback;
	
	public CommentAddButtonListener(Context context) {
		super();
		this.context = context;
	}

	public CommentAddButtonListener(
			Context context, 
			CommentEditField field, 
			CommentButtonCallback callback,
			KeyboardUtils keyboardUtils) {
		
		this(context);
		
		this.field = field;
		this.callback = callback;
		this.keyboardUtils = keyboardUtils;
		this.consumerKey = getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		this.consumerSecret = getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		
		final String text = field.getText();
		
		if(!StringUtils.isEmpty(text)) {
			
			keyboardUtils.hideKeyboard(field.getEditText());

			// TODO: add auth popup
			// TODO: enable FB auth
//			if(!Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
			if(!getSocialize().isAuthenticated()) {
				getSocialize().authenticate(
						consumerKey, 
						consumerSecret,
//						AuthProviderType.FACEBOOK, 
//						facebookAppId,
						new CommentReAuthListener(context, callback, text));
			}
			else {
				callback.onComment(text);
			}
		}
	}

	public void setField(CommentEditField field) {
		this.field = field;
	}

	public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
		this.keyboardUtils = keyboardUtils;
	}

	public void setCallback(CommentButtonCallback callback) {
		this.callback = callback;
	}

	public CommentButtonCallback getCallback() {
		return callback;
	}
}
