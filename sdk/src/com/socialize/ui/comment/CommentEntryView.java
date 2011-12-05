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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
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
	private Drawables drawables;
	private KeyboardUtils keyboardUtils;
	private EditText commentField;
	private CustomCheckbox checkBox;
	private CustomCheckbox locationBox;
	private IBeanFactory<CustomCheckbox> autoPostFacebookOptionFactory;
	private IBeanFactory<CustomCheckbox> locationEnabledOptionFactory;
	
	public CommentEntryView(Context context, CommentAddButtonListener listener) {
		super(context);
		this.listener = listener;
	}

	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		if(getSocializeUI().isFacebookSupported()) {
			checkBox = autoPostFacebookOptionFactory.getBean();
		}
		
		if(deviceUtils.isLocationAvaiable(getContext())) {
			locationBox = locationEnabledOptionFactory.getBean();
		}
		
		checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = null;
				if(checkBox.isChecked()) {
					msg = "Facebook sharing enabled";
				}
				else {
					msg = "Facebook sharing disabled";
				}
				
				Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
		
		locationBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = null;
				if(locationBox.isChecked()) {
					msg = "Location sharing enabled";
				}
				else {
					msg = "Location sharing disabled";
				}
				
				Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
				
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		buttonLayoutParams.setMargins(0, 0, 0, 0);
		buttonLayoutParams.weight = 1.0f;
		
		commentField = new EditText(getContext());
		commentField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		commentField.setLines(8);
		commentField.setLayoutParams(commentFieldParams);
		
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setLayoutParams(buttonLayoutParams);
		buttonLayout.setGravity( Gravity.RIGHT );

		setLayoutParams(fill);
		setPadding(padding, padding, padding, padding);
		
		LinearLayout toolbarLayout = new LinearLayout(getContext());
		LayoutParams toolbarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		toolbarLayout.setLayoutParams(toolbarLayoutParams);
		
		if(checkBox != null || locationBox != null) {
			LinearLayout optionsLayout = new LinearLayout(getContext());
			LayoutParams optionsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			optionsLayout.setLayoutParams(optionsLayoutParams);
			optionsLayout.setOrientation(HORIZONTAL);
			
			if(checkBox != null) {
				optionsLayout.addView(checkBox);
			}
			
			if(locationBox != null) {
				optionsLayout.addView(locationBox);
			}
			
			toolbarLayout.addView(optionsLayout);
		}		

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
					boolean autoPost = false;
					boolean shareLocation = false;
					
					if(checkBox != null) {
						autoPost = checkBox.isChecked(); 
					}
					
					if(locationBox != null) {
						shareLocation = locationBox.isChecked(); 
					}
					
					listener.onComment(commentField.getText().toString().trim(), autoPost, shareLocation);
				}
			});
			
			buttonLayout.addView(postCommentButton);
		}
		
		toolbarLayout.addView(buttonLayout);

		addView(commentField);
		addView(toolbarLayout);

	}
	
	
	@Override
	protected void onViewRendered(int width, int height) {
		super.onViewRendered(width, height);
		commentField.requestFocus();
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
	
	public void setAutoPostFacebookOptionFactory(IBeanFactory<CustomCheckbox> autoPostFacebookOptionFactory) {
		this.autoPostFacebookOptionFactory = autoPostFacebookOptionFactory;
	}
	
	public void setLocationEnabledOptionFactory(IBeanFactory<CustomCheckbox> locationEnabledOptionFactory) {
		this.locationEnabledOptionFactory = locationEnabledOptionFactory;
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	protected void reset() {
		keyboardUtils.hideKeyboard(commentField);
		commentField.setText("");
		
		if(checkBox != null) {
			checkBox.setChecked(Socialize.getSocialize().getSession().getUser().isAutoPostToFacebook());
		}
		
		if(deviceUtils.isLocationAvaiable(getContext())) {
			locationBox.setChecked(true);
		}
	}
	
	protected EditText getCommentField() {
		return commentField;
	}

	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}	
}
