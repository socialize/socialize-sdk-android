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
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.profile.AutoPostFacebookOption;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class CommentEntryView extends BaseView {
	
	private CommentAddButtonListener listener;
	
	private SocializeButton postCommentButton;
	private SocializeButton cancelCommentButton;
	private DeviceUtils deviceUtils;
	private KeyboardUtils keyboardUtils;
	private EditText commentField;
	private AutoPostFacebookOption checkBox;
	
	public CommentEntryView(Context context, CommentAddButtonListener listener) {
		super(context);
		this.listener = listener;
	}

	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		checkBox = new AutoPostFacebookOption(getContext()); // TODO: make a factory
		checkBox.init();
		checkBox.setChecked(Socialize.getSocialize().getSession().getUser().isAutoPostToFacebook());
		
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		commentField = new EditText(getContext());
		commentField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		commentField.setLines(3);
		commentField.setLayoutParams(commentFieldParams);
		
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		
		TextView commentLabel = new TextView(getContext());
		commentLabel.setText("Enter a comment");
		commentLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentLabel.setTextColor(Color.WHITE);
		commentLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		commentLabel.setPadding(0, padding, 0, 0);

		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setLayoutParams(buttonLayoutParams);
		buttonLayout.setGravity( Gravity.RIGHT );

		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);

		//  && getSocializeUI().isFacebookSupported()
		
		if(cancelCommentButton != null) {
			cancelCommentButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reset();
					listener.onCancel();
				}
			});
			
			buttonLayout.addView(cancelCommentButton);
		}	
		
		if(postCommentButton != null) {
			postCommentButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					keyboardUtils.hideKeyboard(commentField);
					listener.onComment(commentField.getText().toString().trim(), checkBox.isChecked());
				}
			});
			
			buttonLayout.addView(postCommentButton);
		}
		
		addView(commentLabel);
		addView(commentField);
		addView(checkBox);
		addView(buttonLayout);
	}
	
	
	@Override
	protected void onViewRendered(int width, int height) {
		super.onViewRendered(width, height);
		commentField.requestFocus();
//		keyboardUtils.showKeyboard(commentField);
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
		this.keyboardUtils = keyboardUtils;
	}

	public void setPostCommentButton(SocializeButton facebookShareButton) {
		this.postCommentButton = facebookShareButton;
	}

	public void setCancelCommentButton(SocializeButton cancelCommentButton) {
		this.cancelCommentButton = cancelCommentButton;
	}
	
	protected void reset() {
		keyboardUtils.hideKeyboard(commentField);
		commentField.setText("");
		checkBox.setChecked(Socialize.getSocialize().getSession().getUser().isAutoPostToFacebook());
	}
	
	protected EditText getCommentField() {
		return commentField;
	}

	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}	
}
