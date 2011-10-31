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
package com.socialize.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.button.SocializeButton;
import com.socialize.util.DeviceUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ShareDialogView extends BaseView {

	private SocializeButton facebookShareButton;
	private SocializeButton emailShareButton;
	private SocializeButton smsShareButton;
	
	private IBeanFactory<OtherShareClickListener> otherShareClickListenerFactory;
	private IBeanFactory<EmailShareClickListener> emailShareClickListenerFactory;
	private IBeanFactory<FacebookShareClickListener> facebookShareClickListenerFactory;
	private IBeanFactory<SmsShareClickListener> smsShareClickListenerFactory;
	
	private DeviceUtils deviceUtils;
	private Dialog parent;
	private ActionBarView actionBarView;
	
	public ShareDialogView(Context context, ActionBarView actionBarView) {
		super(context);
		this.actionBarView = actionBarView;
	}
	
	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		TextView otherOptions = new TextView(getContext());
		SpannableString content = new SpannableString("More options...");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		otherOptions.setText(content);
		otherOptions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		otherOptions.setTextColor(Color.WHITE);
		otherOptions.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		otherOptions.setPadding(0, padding, 0, 0);
		
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		
		otherOptions.setLayoutParams(textLayout);
		otherOptions.setOnClickListener(otherShareClickListenerFactory.getBean(actionBarView));
		
		OnClickListener closeDialogOnClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(parent != null) {
					parent.dismiss();
				}
			}
		};
		
		if(facebookShareButton != null) {
			facebookShareButton.setCustomClickListener(facebookShareClickListenerFactory.getBean(actionBarView));
			facebookShareButton.addOnClickListenerBefore(closeDialogOnClick);
			addView(facebookShareButton);
		}
		
		if(emailShareButton != null) {
			emailShareButton.setCustomClickListener(emailShareClickListenerFactory.getBean(actionBarView));
			emailShareButton.addOnClickListenerBefore(closeDialogOnClick);
			addView(emailShareButton);
		}
		
		if(smsShareButton != null) {
			smsShareButton.setCustomClickListener(smsShareClickListenerFactory.getBean(actionBarView));
			smsShareButton.addOnClickListenerBefore(closeDialogOnClick);
			addView(smsShareButton);
		}
		
		addView(otherOptions);
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setFacebookShareButton(SocializeButton facebookShareButton) {
		this.facebookShareButton = facebookShareButton;
	}

	public void setEmailShareButton(SocializeButton emailShareButton) {
		this.emailShareButton = emailShareButton;
	}

	public void setSmsShareButton(SocializeButton smsShareButton) {
		this.smsShareButton = smsShareButton;
	}

	public void setParent(Dialog parent) {
		this.parent = parent;
	}

	public void setOtherShareClickListenerFactory(IBeanFactory<OtherShareClickListener> otherShareClickListenerFactory) {
		this.otherShareClickListenerFactory = otherShareClickListenerFactory;
	}

	public void setEmailShareClickListenerFactory(IBeanFactory<EmailShareClickListener> emailShareClickListenerFactory) {
		this.emailShareClickListenerFactory = emailShareClickListenerFactory;
	}

	public void setFacebookShareClickListenerFactory(IBeanFactory<FacebookShareClickListener> facebookShareClickListenerFactory) {
		this.facebookShareClickListenerFactory = facebookShareClickListenerFactory;
	}

	public void setSmsShareClickListenerFactory(IBeanFactory<SmsShareClickListener> smsShareClickListenerFactory) {
		this.smsShareClickListenerFactory = smsShareClickListenerFactory;
	}
	
}
