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
package com.socialize.ui.auth;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.facebook.FacebookButton;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogView extends LinearLayout {

	private FacebookButton facebookSignInButton;
	private SocializeButton socializeSkipAuthButton;
	private DeviceUtils deviceUtils;
	private TextView textView;
	private String text;
	
	public AuthRequestDialogView(Context context) {
		super(context);
	}
	
	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		if(!StringUtils.isEmpty(text)) {
			textView = new TextView(getContext());
			textView.setTextColor(Color.WHITE);
			textView.setText(text);
			addView(textView);
		}
		addView(facebookSignInButton);
		addView(socializeSkipAuthButton);
	}

	public void setFacebookSignInButton(FacebookButton facebookSignInButton) {
		this.facebookSignInButton = facebookSignInButton;
	}

	public void setSocializeSkipAuthButton(SocializeButton socializeSkipAuthButton) {
		this.socializeSkipAuthButton = socializeSkipAuthButton;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public FacebookButton getFacebookSignInButton() {
		return facebookSignInButton;
	}

	public SocializeButton getSocializeSkipAuthButton() {
		return socializeSkipAuthButton;
	}

	public void setText(String text) {
		this.text = text;
		
		if(textView != null) {
			textView.setText(text);
		}
	}
	
	
}
