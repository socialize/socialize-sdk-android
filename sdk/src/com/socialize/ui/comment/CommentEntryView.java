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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.AppUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class CommentEntryView extends BaseView {
	
	private CommentAddButtonListener listener;
	private SocializeButton postCommentButton;
	private SocializeButton cancelCommentButton;
	private SocializeButton subscribeNotificationButton;
	private DeviceUtils deviceUtils;
	private AppUtils appUtils;
	private Drawables drawables;
	private KeyboardUtils keyboardUtils;
	private EditText commentField;
	private CustomCheckbox facebookCheckbox;
	private CustomCheckbox locationCheckBox;
	private CustomCheckbox notifyCheckBox;
	private IBeanFactory<CustomCheckbox> autoPostFacebookOptionFactory;
	private IBeanFactory<CustomCheckbox> locationEnabledOptionFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private boolean notificationsEnabled = true;
	private boolean notificationsAvailable = true;
	
	private TextView notificationsTitle;
	private TextView notificationsText;
	
	private Toast toaster;
	
	public CommentEntryView(Context context, CommentAddButtonListener listener) {
		super(context);
		this.listener = listener;
	}

	public void init() {
		
		int padding = deviceUtils.getDIP(8);
		
		User user = Socialize.getSocialize().getSession().getUser();
		
		toaster = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
		
		notificationsEnabled = true;
		notificationsAvailable = user.isNotificationsEnabled() && appUtils.isNotificationsAvaiable(getContext());
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		fill.setMargins(0,0,0,0);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			facebookCheckbox = autoPostFacebookOptionFactory.getBean();
		}
		
		if(appUtils.isLocationAvaiable(getContext())) {
			locationCheckBox = locationEnabledOptionFactory.getBean();
		}
		
		if(notificationsAvailable) {
			notifyCheckBox = notificationEnabledOptionFactory.getBean();
		}
		
		if(facebookCheckbox != null) {
			
			facebookCheckbox.setChecked(user.isAutoPostCommentsFacebook());
			facebookCheckbox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String msg = null;
					if(facebookCheckbox.isChecked()) {
						msg = "Facebook sharing enabled";
					}
					else {
						msg = "Facebook sharing disabled";
					}
					
					toast(msg);
				}
			});
		}

		if(locationCheckBox != null) {
			
			locationCheckBox.setChecked(user.isShareLocation());
			locationCheckBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String msg = null;
					if(locationCheckBox.isChecked()) {
						msg = "Location sharing enabled";
					}
					else {
						msg = "Location sharing disabled";
					}
					
					toast(msg);
				}
			});
		}
		
		if(notifyCheckBox != null) {
			notifyCheckBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					toggleNotifications();
					String msg = null;
					if(notifyCheckBox.isChecked()) {
						msg = "Notifications enabled";
					}
					else {
						msg = "Notifications disabled";
					}
					
					toast(msg);
				}
			});
		}
		
				
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		buttonLayoutParams.setMargins(0, 0, 0, 0);
		
		commentField = new EditText(getContext());
		commentField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		commentField.setLines(6);
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
		
		if(facebookCheckbox != null || locationCheckBox != null || notifyCheckBox != null) {
			LinearLayout optionsLayout = new LinearLayout(getContext());
			LayoutParams optionsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			optionsLayout.setLayoutParams(optionsLayoutParams);
			optionsLayout.setOrientation(HORIZONTAL);
			
			if(facebookCheckbox != null) {
				optionsLayout.addView(facebookCheckbox);
			}
			
			if(locationCheckBox != null) {
				optionsLayout.addView(locationCheckBox);
			}
			
			if(notifyCheckBox != null && deviceUtils.getOrientation() != Configuration.ORIENTATION_PORTRAIT) {
				optionsLayout.addView(notifyCheckBox);
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
					
					if(facebookCheckbox != null) {
						autoPost = facebookCheckbox.isChecked(); 
					}
					
					if(locationCheckBox != null) {
						shareLocation = locationCheckBox.isChecked(); 
					}
					
					listener.onComment(commentField.getText().toString().trim(), autoPost, shareLocation, notificationsEnabled);
				}
			});
			
			buttonLayout.addView(postCommentButton);
		}
		
		if(subscribeNotificationButton != null) {
			subscribeNotificationButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleNotifications();
				}
			});
		}		
		
		toolbarLayout.addView(buttonLayout);

		addView(commentField);
		addView(toolbarLayout);

		if(notificationsAvailable && deviceUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {

			// Notification layout
			LinearLayout notificationMasterLayout = new LinearLayout(getContext());
			LayoutParams notificationMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
			notificationMasterLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			notificationMasterLayoutParams.weight = 1.0f;
			notificationMasterLayoutParams.setMargins(0, -deviceUtils.getDIP(20), 0, 0);
			notificationMasterLayout.setLayoutParams(notificationMasterLayoutParams);
			
			LinearLayout notificationContentLayout = new LinearLayout(getContext());
			LayoutParams notificationContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
			notificationContentLayout.setOrientation(LinearLayout.VERTICAL);
			notificationContentLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			notificationContentLayout.setLayoutParams(notificationContentLayoutParams);
			
			ImageView notificationBannerImage = new ImageView(getContext());
			
			LayoutParams notificationBannerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			notificationBannerParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			
			notificationBannerImage.setLayoutParams(notificationBannerParams);
			notificationBannerImage.setImageDrawable(drawables.getDrawable("notification_banner.png", DisplayMetrics.DENSITY_DEFAULT));

			notificationsTitle = new TextView(getContext());
			notificationsText = new TextView(getContext());
			
			notificationsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			notificationsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			
			notificationsTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			notificationsText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

			notificationsTitle.setTextColor(Color.WHITE);
			notificationsTitle.setTypeface(Typeface.DEFAULT_BOLD);
			notificationsText.setTextColor(Color.GRAY);
			
			notificationContentLayout.addView(notificationBannerImage);
			notificationContentLayout.addView(notificationsTitle);
			notificationContentLayout.addView(notificationsText);
			
			if(subscribeNotificationButton != null) {
				LayoutParams subscribeNotificationButtonLayoutParams = new LinearLayout.LayoutParams(subscribeNotificationButton.getButtonWidth(), subscribeNotificationButton.getButtonHeight());
				
				subscribeNotificationButtonLayoutParams.setMargins(0, deviceUtils.getDIP(20), 0, 0);
				subscribeNotificationButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				subscribeNotificationButton.setLayoutParams(subscribeNotificationButtonLayoutParams);			
				
				notificationContentLayout.addView(subscribeNotificationButton);
			}

			notificationMasterLayout.addView(notificationContentLayout);
			
			addView(notificationMasterLayout);		
		}	
		
		
		updateUI();

	}
	
	protected void toggleNotifications() {
		setNotificationsEnabled(!notificationsEnabled);
	}
	
	@Override
	public void onViewRendered(int width, int height) {
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
	
	public void setSubscribeNotificationButton(SocializeButton subscribeNotificationButton) {
		this.subscribeNotificationButton = subscribeNotificationButton;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setNotificationEnabledOptionFactory(IBeanFactory<CustomCheckbox> notificationOptionFactory) {
		this.notificationEnabledOptionFactory = notificationOptionFactory;
	}
	
	public void setNotificationsEnabled(boolean enabled) {
		notificationsEnabled = enabled;
		updateUI();
	}
	
	protected void updateUI() {
		
		if(notificationsEnabled) {
			if(notificationsText != null) {
				notificationsText.setText("We will notify you when someone replies.");
			}
			if(notificationsTitle != null) {
				notificationsTitle.setText("You will be subscribed.");
			}
			if(subscribeNotificationButton != null) {
				subscribeNotificationButton.setText("Unsubscribe");
			}
		}
		else {
			if(notificationsText != null) {
				notificationsText.setText("Click subscribe to receive updates.");
			}
			if(notificationsTitle != null) {
				notificationsTitle.setText("You will not be subscribed.");
			}
			if(subscribeNotificationButton != null) {
				subscribeNotificationButton.setText("Subscribe");
			}
		}
		
		if(notifyCheckBox != null) {
			notifyCheckBox.setChecked(notificationsEnabled);
		}
		
		if(facebookCheckbox != null) {
			User user = Socialize.getSocialize().getSession().getUser();
			facebookCheckbox.setChecked(user.isAutoPostCommentsFacebook());
		}
	}
	
	protected void toast(String text) {
		if(toaster != null) {
			toaster.cancel();
			toaster.setText(text);
			toaster.show();
		}
	}

	protected void reset() {
		keyboardUtils.hideKeyboard(commentField);
		commentField.setText("");
		updateUI();
	}
	
	protected EditText getCommentField() {
		return commentField;
	}

	protected CustomCheckbox getFacebookCheckbox() {
		return facebookCheckbox;
	}

	protected CustomCheckbox getLocationCheckBox() {
		return locationCheckBox;
	}

	protected CustomCheckbox getNotifyCheckBox() {
		return notifyCheckBox;
	}
	
	protected void setNotifySubscribeState(boolean subscribed) {
		notificationsEnabled = subscribed;
		if(notifyCheckBox != null) {
			notifyCheckBox.setChecked(notificationsEnabled);
			updateUI();
		}
	}
}
