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
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.networks.facebook.FacebookButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class AuthRequestDialogView extends LinearLayout {

	private FacebookButton facebookSignInButton;
	private DisplayUtils displayUtils;
	private TextView textView;
	private String text;
	
	public AuthRequestDialogView(Context context) {
		super(context);
	}
	
	public void init() {
		
		int padding = displayUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(padding, padding, padding, padding);
		
		setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		
		if(!StringUtils.isEmpty(text)) {
			textView = new TextView(getContext());
			
			LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			textParams.setMargins(padding, padding, padding, padding);
			
			textView.setLayoutParams(textParams);
			textView.setTextColor(Color.WHITE);
			textView.setText(text);
			textView.setGravity(Gravity.TOP | Gravity.LEFT);
			addView(textView);
		}
		
		if(facebookSignInButton != null) {
			addView(facebookSignInButton);
		}
		
	}

	public void setFacebookSignInButton(FacebookButton facebookSignInButton) {
		this.facebookSignInButton = facebookSignInButton;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public FacebookButton getFacebookSignInButton() {
		return facebookSignInButton;
	}

	public void setText(String text) {
		this.text = text;
		
		if(textView != null) {
			textView.setText(text);
		}
	}
}
