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
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ShareDialogView extends BaseView implements ShareInfoProvider {

	private SocializeButton facebookShareButton;
	private SocializeButton twitterShareButton;
	private SocializeButton emailShareButton;
	private SocializeButton smsShareButton;
	private IBeanFactory<ShareClickListener> shareClickListenerFactory;
	
	private OnActionBarEventListener onActionBarEventListener;
	
	private DisplayUtils displayUtils;
	private ActionBarView actionBarView;
	private Drawables drawables;
	private EditText commentField;
	
	public ShareDialogView(Context context, ActionBarView actionBarView) {
		this(context, actionBarView, null);
	}
	
	public ShareDialogView(Context context, ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super(context);
		this.actionBarView = actionBarView;
		this.onActionBarEventListener = onActionBarEventListener;
	}
	
	@Override
	public String getShareText() {
		return (commentField == null ? "" : commentField.getText().toString());
	}

	public void init() {
		
		int padding = displayUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		commentField = new EditText(getContext());
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		setOrientation(LinearLayout.VERTICAL);
		
		if(drawables != null) {
			setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		}
		
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
		
		shareLabelLayout.addView(shareLabel);
		
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		
		final Entity entity = actionBarView.getEntity();
		
		if(displayUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT && getSocialize().canShare(getActivity(), ShareType.OTHER)) {
			setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			commentField.setLines(4);
			
			otherOptions = new TextView(getContext());
			SpannableString content = new SpannableString("More options...");
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			otherOptions.setText(content);
			otherOptions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			otherOptions.setTextColor(Color.WHITE);
			otherOptions.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			
			LayoutParams otherOptionsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			otherOptionsLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
			
			otherOptions.setLayoutParams(otherOptionsLayout);
			otherOptions.setOnClickListener(shareClickListenerFactory.getBean(entity, ShareType.OTHER, this, onActionBarEventListener, actionBarView));
			
			shareLabelLayout.addView(otherOptions);
		}
		else {
			setGravity(Gravity.TOP | Gravity.LEFT);
			commentField.setLines(1);
		}
		
		commentField.setLayoutParams(commentFieldParams);
		buttonLayout.setLayoutParams(buttonLayoutParams);

		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		if(facebookShareButton != null && getSocialize().canShare(getActivity(), ShareType.FACEBOOK)) {
			ShareClickListener facebookShareClickListener = shareClickListenerFactory.getBean(entity, ShareType.FACEBOOK, this, onActionBarEventListener, actionBarView);
			facebookShareButton.setCustomClickListener(facebookShareClickListener);
			buttonLayout.addView(facebookShareButton);
		}
		
		if(twitterShareButton != null && getSocialize().canShare(getActivity(), ShareType.TWITTER)) {
			ShareClickListener twitterShareClickListener = shareClickListenerFactory.getBean(entity, ShareType.TWITTER, this, onActionBarEventListener, actionBarView);
			twitterShareButton.setCustomClickListener(twitterShareClickListener);
			buttonLayout.addView(twitterShareButton);
		}		
		
		if(emailShareButton != null && getSocialize().canShare(getActivity(), ShareType.EMAIL)) {
			ShareClickListener emailShareClickListener = shareClickListenerFactory.getBean(entity, ShareType.EMAIL, this, onActionBarEventListener, actionBarView);
			emailShareButton.setCustomClickListener(emailShareClickListener);
			buttonLayout.addView(emailShareButton);
		}
		
		if(smsShareButton != null && getSocialize().canShare(getActivity(), ShareType.SMS)) {
			ShareClickListener smsShareClickListener = shareClickListenerFactory.getBean(entity, ShareType.SMS, this, onActionBarEventListener, actionBarView);
			smsShareButton.setCustomClickListener(smsShareClickListener);
			buttonLayout.addView(smsShareButton);
		}
		
		addView(commentLabel);
		addView(commentField);
		addView(shareLabelLayout);
		addView(buttonLayout);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
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
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setShareClickListenerFactory(IBeanFactory<ShareClickListener> shareClickListenerFactory) {
		this.shareClickListenerFactory = shareClickListenerFactory;
	}
}
