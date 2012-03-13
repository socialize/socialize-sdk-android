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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ShareDialogView extends BaseView {

	private SocializeButton facebookShareButton;
	private SocializeButton twitterShareButton;
	private SocializeButton emailShareButton;
	private SocializeButton smsShareButton;
	
	private IBeanFactory<ShareClickListener> otherShareClickListenerFactory;
	private IBeanFactory<ShareClickListener> emailShareClickListenerFactory;
	private IBeanFactory<ShareClickListener> facebookShareClickListenerFactory;
	private IBeanFactory<ShareClickListener> twitterShareClickListenerFactory;
	private IBeanFactory<ShareClickListener> smsShareClickListenerFactory;
	
	private OnActionBarEventListener onActionBarEventListener;
	
	private DeviceUtils deviceUtils;
	private ActionBarView actionBarView;
	private Drawables drawables;
	
	public ShareDialogView(Context context, ActionBarView actionBarView) {
		this(context, actionBarView, null);
	}
	
	public ShareDialogView(Context context, ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super(context);
		this.actionBarView = actionBarView;
		this.onActionBarEventListener = onActionBarEventListener;
	}
	
	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		EditText commentField = new EditText(getContext());
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		
		TextView otherOptions = null;
		
		TextView commentLabel = new TextView(getContext());
		commentLabel.setText("Add a comment (optional)");
		commentLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentLabel.setTextColor(Color.WHITE);
		commentLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		commentLabel.setPadding(0, padding, 0, 0);
		
		LinearLayout shareLabelLayout = new LinearLayout(getContext());
		shareLabelLayout.setPadding(0, padding, 0, padding);
		shareLabelLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		TextView shareLabel = new TextView(getContext());
		shareLabel.setText("Share to:");
		shareLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		shareLabel.setTextColor(Color.WHITE);
		shareLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//		shareLabel.setPadding(0, padding, 0, padding);
		
		shareLabelLayout.addView(shareLabel);
		
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		
//		boolean landscape = false;
		
		if(deviceUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			commentField.setLines(4);
			
			otherOptions = new TextView(getContext());
			SpannableString content = new SpannableString("More options...");
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			otherOptions.setText(content);
			otherOptions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			otherOptions.setTextColor(Color.WHITE);
			otherOptions.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
//			otherOptions.setPadding(0, deviceUtils.getDIP(24), 0, 0);
			
			LayoutParams otherOptionsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			otherOptionsLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
			
			otherOptions.setLayoutParams(otherOptionsLayout);
			otherOptions.setOnClickListener(otherShareClickListenerFactory.getBean(actionBarView, commentField, onActionBarEventListener));
			
			shareLabelLayout.addView(otherOptions);
		}
		else {
			setGravity(Gravity.TOP | Gravity.LEFT);
			commentField.setLines(1);
//			landscape = true;
		}
		
		commentField.setLayoutParams(commentFieldParams);
		buttonLayout.setLayoutParams(buttonLayoutParams);

		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		ShareClickListener facebookShareClickListener = facebookShareClickListenerFactory.getBean(actionBarView, commentField, onActionBarEventListener);
		ShareClickListener twitterShareClickListener = twitterShareClickListenerFactory.getBean(actionBarView, commentField, onActionBarEventListener);
		
		ShareClickListener emailShareClickListener = emailShareClickListenerFactory.getBean(actionBarView, commentField, onActionBarEventListener);
		ShareClickListener smsShareClickListener = smsShareClickListenerFactory.getBean(actionBarView, commentField, onActionBarEventListener);
		
		if(facebookShareButton != null && facebookShareClickListener.isAvailableOnDevice(getActivity())) {
			facebookShareButton.setCustomClickListener(facebookShareClickListener);
			buttonLayout.addView(facebookShareButton);
		}
		
		if(twitterShareButton != null && twitterShareClickListener.isAvailableOnDevice(getActivity())) {
			twitterShareButton.setCustomClickListener(twitterShareClickListener);
			buttonLayout.addView(twitterShareButton);
		}		
		
		if(emailShareButton != null && emailShareClickListener.isAvailableOnDevice(getActivity())) {
			emailShareButton.setCustomClickListener(emailShareClickListener);
			buttonLayout.addView(emailShareButton);
		}
		
		if(smsShareButton != null && smsShareClickListener.isAvailableOnDevice(getActivity())) {
			smsShareButton.setCustomClickListener(smsShareClickListener);
			buttonLayout.addView(smsShareButton);
		}
		
//		if(landscape) {
//			addView(shareLabelLayout);
//			addView(buttonLayout);			
//			addView(commentLabel);
//			addView(commentField);
//		}
//		else {
			addView(commentLabel);
			addView(commentField);
			addView(shareLabelLayout);
			addView(buttonLayout);
//		}
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
	
	public void setTwitterShareButton(SocializeButton twitterShareButton) {
		this.twitterShareButton = twitterShareButton;
	}

	public void setTwitterShareClickListenerFactory(IBeanFactory<ShareClickListener> twitterShareClickListenerFactory) {
		this.twitterShareClickListenerFactory = twitterShareClickListenerFactory;
	}

	public void setOtherShareClickListenerFactory(IBeanFactory<ShareClickListener> otherShareClickListenerFactory) {
		this.otherShareClickListenerFactory = otherShareClickListenerFactory;
	}

	public void setEmailShareClickListenerFactory(IBeanFactory<ShareClickListener> emailShareClickListenerFactory) {
		this.emailShareClickListenerFactory = emailShareClickListenerFactory;
	}

	public void setFacebookShareClickListenerFactory(IBeanFactory<ShareClickListener> facebookShareClickListenerFactory) {
		this.facebookShareClickListenerFactory = facebookShareClickListenerFactory;
	}

	public void setSmsShareClickListenerFactory(IBeanFactory<ShareClickListener> smsShareClickListenerFactory) {
		this.smsShareClickListenerFactory = smsShareClickListenerFactory;
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
}
