/*
 * Copyright (c) 2012 Socialize Inc.
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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.AppUtils;
import com.socialize.util.DisplayUtils;
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
	private DisplayUtils displayUtils;
	private AppUtils appUtils;
	private Drawables drawables;
	private Colors colors;
	private KeyboardUtils keyboardUtils;
	private EditText commentField;
	private LocalizationService localizationService;
	
	private IBeanFactory<CustomCheckbox> locationEnabledOptionFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private CustomCheckbox locationCheckBox;
	private CustomCheckbox notifyCheckBox;
	
	private boolean notificationsEnabled = true;
	private boolean notificationsAvailable = true;
	
	private TextView notificationsTitle;
	private TextView notificationsText;
	
	private ImageView notificationBannerImage;
	
	private Toast toaster;
	
	public CommentEntryView(Context context, CommentAddButtonListener listener) {
		super(context);
		this.listener = listener;
	}

	public void init() {
		
		int padding = displayUtils.getDIP(4);
		int textPadding = displayUtils.getDIP(2);
		
		notificationsEnabled = true;
		notificationsAvailable = appUtils.isNotificationsAvailable(getContext());
		
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		fill.setMargins(0,0,0,0);
		
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

		CompatUtils.setBackgroundDrawable(this,drawables.getDrawable("slate.png", true, true, true));

		setLayoutParams(fill);
		
		LinearLayout buttonLayoutLeft = new LinearLayout(getContext());
		LinearLayout buttonLayoutRight = new LinearLayout(getContext());
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		LinearLayout commentLayout = new LinearLayout(getContext());
		
		LayoutParams buttonLayoutLeftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams buttonLayoutRightParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		buttonLayoutLeftParams.weight = 1.0f;
		
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity( Gravity.RIGHT );
		buttonLayout.setPadding(padding, padding, padding, padding);
		
		buttonLayout.setLayoutParams(buttonLayoutParams);
		buttonLayoutLeft.setLayoutParams(buttonLayoutLeftParams);
		buttonLayoutRight.setLayoutParams(buttonLayoutRightParams);
		
		commentLayout.setPadding(textPadding, textPadding, textPadding, 0);
		
		buttonLayout.addView(buttonLayoutLeft);
		buttonLayout.addView(buttonLayoutRight);
		
		if(notificationsAvailable) {
			notifyCheckBox = notificationEnabledOptionFactory.getBean();
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
		
		commentField = new EditText(getContext());
		commentField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentField.setGravity(Gravity.TOP | Gravity.LEFT);
		commentField.setLines(5);
		commentField.setLayoutParams(commentFieldParams);
		
		commentLayout.addView(commentField);

		if(cancelCommentButton != null) {
			cancelCommentButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reset();
					listener.onCancel();
				}
			});
			
			buttonLayoutLeft.addView(cancelCommentButton);
		}	
		
		if(postCommentButton != null) {
			postCommentButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					postCommentButton.setEnabled(false);
					keyboardUtils.hideKeyboard(commentField);
					boolean shareLocation = false;
					if(locationCheckBox != null) {
						shareLocation = locationCheckBox.isChecked(); 
					}
					
					listener.onComment(commentField.getText().toString().trim(), shareLocation, notificationsEnabled);
				}
			});
			
			buttonLayoutRight.addView(postCommentButton);
		}
		
		if(subscribeNotificationButton != null) {
			subscribeNotificationButton.setCustomClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleNotifications();
				}
			});
		}		
		
		addView(commentLayout);
		addView(buttonLayout);
		
		initLocationToolbar();
		
		if(notificationsAvailable && displayUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {

			// Notification layout
			LinearLayout notificationMasterLayout = new LinearLayout(getContext());
			LayoutParams notificationMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
					
			notificationMasterLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			notificationMasterLayoutParams.weight = 1.0f;
			notificationMasterLayoutParams.setMargins(0, displayUtils.getDIP(20), 0, 0);
			notificationMasterLayout.setLayoutParams(notificationMasterLayoutParams);
			
			LinearLayout notificationContentLayout = new LinearLayout(getContext());
			LayoutParams notificationContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
					
			notificationContentLayout.setOrientation(LinearLayout.VERTICAL);
			notificationContentLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			notificationContentLayout.setLayoutParams(notificationContentLayoutParams);
			
			notificationBannerImage = new ImageView(getContext());
			
			LayoutParams notificationBannerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			notificationBannerParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			
			notificationBannerImage.setLayoutParams(notificationBannerParams);
			notificationBannerImage.setImageDrawable(drawables.getDrawable("notification_banner.png"));

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
				
				subscribeNotificationButtonLayoutParams.setMargins(0, displayUtils.getDIP(20), 0, 0);
				subscribeNotificationButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				subscribeNotificationButton.setLayoutParams(subscribeNotificationButtonLayoutParams);			
				
				notificationContentLayout.addView(subscribeNotificationButton);
			}

			notificationMasterLayout.addView(notificationContentLayout);
			
			addView(notificationMasterLayout);		
		}	
	}
	
	protected void initLocationToolbar() {
		
		if(appUtils.isLocationAvailable(getContext())) {
			
			UserSettings settings = Socialize.getSocialize().getSession().getUserSettings();
			
			int padding = displayUtils.getDIP(4);
			
			LinearLayout toolbarLayout = new LinearLayout(getContext());
			LinearLayout toolbarLayoutLeft = new LinearLayout(getContext());
			LinearLayout toolbarLayoutRight = new LinearLayout(getContext());		
			
			LayoutParams toolbarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LayoutParams toolbarLayoutLeftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LayoutParams toolbarLayoutRightParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { colors.getColor(Colors.COMMENT_ENTRY_BOTTOM), colors.getColor(Colors.COMMENT_ENTRY_TOP) });

			CompatUtils.setBackgroundDrawable(toolbarLayout,background);

			toolbarLayout.setPadding(padding, padding, padding, padding);
			
			toolbarLayoutLeftParams.weight = 1.0f;
			
			toolbarLayout.setLayoutParams(toolbarLayoutParams);
			toolbarLayoutLeft.setLayoutParams(toolbarLayoutLeftParams);
			toolbarLayoutRight.setLayoutParams(toolbarLayoutRightParams);
			
			toolbarLayoutRight.setOrientation(HORIZONTAL);
			toolbarLayoutLeft.setOrientation(HORIZONTAL);
			
			toolbarLayout.addView(toolbarLayoutLeft);
			toolbarLayout.addView(toolbarLayoutRight);		
			
			locationCheckBox = locationEnabledOptionFactory.getBean();
			locationCheckBox.setChecked(settings.isLocationEnabled());
			toolbarLayoutLeft.addView(locationCheckBox);

			if(notifyCheckBox != null && displayUtils.getOrientation() != Configuration.ORIENTATION_PORTRAIT) {
				toolbarLayoutRight.addView(notifyCheckBox);
			}
			
			addView(toolbarLayout);
		}
	}
	
	protected void toggleNotifications() {
		setNotificationsEnabled(!notificationsEnabled);
	}
	
	@Override
	public void onViewRendered(int width, int height) {
		super.onViewRendered(width, height);
		commentField.requestFocus();
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
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
	
	public void setLocationEnabledOptionFactory(IBeanFactory<CustomCheckbox> locationEnabledOptionFactory) {
		this.locationEnabledOptionFactory = locationEnabledOptionFactory;
	}
	
	public void setSubscribeNotificationButton(SocializeButton subscribeNotificationButton) {
		this.subscribeNotificationButton = subscribeNotificationButton;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setColors(Colors colors) {
		this.colors = colors;
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

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	protected void updateUI() {
		
		if(notificationsAvailable) {
			if(notificationsText != null) notificationsText.setVisibility(View.VISIBLE);
			if(notificationsTitle != null) notificationsTitle.setVisibility(View.VISIBLE);
			if(subscribeNotificationButton != null) subscribeNotificationButton.setVisibility(View.VISIBLE);
			if(notificationBannerImage != null) notificationBannerImage.setVisibility(View.VISIBLE);
	
			if(notificationsEnabled) {
				if(notificationsText != null) {
					notificationsText.setText(localizationService.getString(I18NConstants.COMMENT_SMARTALERTS_SUBSCRIBED_WILL_NOTIFY));
				}
				if(notificationsTitle != null) {
					notificationsTitle.setText(localizationService.getString(I18NConstants.COMMENT_SMARTALERTS_SUBSCRIBED_YES));
				}
				if(subscribeNotificationButton != null) {
					subscribeNotificationButton.setTextKey(I18NConstants.COMMENT_SMARTALERTS_UNSUBSCRIBE);
				}
			}
			else {
				if(notificationsText != null) {
					notificationsText.setText(localizationService.getString(I18NConstants.COMMENT_SMARTALERTS_SUBSCRIBE_ASK));
				}
				if(notificationsTitle != null) {
					notificationsTitle.setText(localizationService.getString(I18NConstants.COMMENT_SMARTALERTS_SUBSCRIBED_NO));
				}
				if(subscribeNotificationButton != null) {
					subscribeNotificationButton.setTextKey(I18NConstants.COMMENT_SMARTALERTS_SUBSCRIBE);
				}
			}
		}
		else {
			if(notificationsText != null) notificationsText.setVisibility(View.GONE);
			if(notificationsTitle != null) notificationsTitle.setVisibility(View.GONE);
			if(subscribeNotificationButton != null) subscribeNotificationButton.setVisibility(View.GONE);
			if(notificationBannerImage != null) notificationBannerImage.setVisibility(View.GONE);
		}

		
		if(notifyCheckBox != null) {
			notifyCheckBox.setChecked(notificationsEnabled);
		}
		
		if(postCommentButton != null) {
			postCommentButton.setEnabled(true);
		}		
		
		SocializeSession session = Socialize.getSocialize().getSession();
		
		if(session != null) {
			if(locationCheckBox != null && !locationCheckBox.isChanged()) {
				locationCheckBox.setChecked(session.getUserSettings().isLocationEnabled());
			}
		}
	}
	
	protected void toast(String text) {
		if(toaster != null) {
			toaster.cancel();
			toaster.setText(text);
		}
		else {
			toaster = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
		}
		
		toaster.show();
	}

	protected void reset() {
		keyboardUtils.hideKeyboard(commentField);
		commentField.setText("");
	}
	
	public void update() {
		if(locationCheckBox != null) {
			locationCheckBox.setChanged(false);
		}
		
		SocializeSession session = Socialize.getSocialize().getSession();
		
		if(session != null) {
			UserSettings user = session.getUserSettings();
			if(user != null) {
				notificationsAvailable = user.isNotificationsEnabled() && appUtils.isNotificationsAvailable(getContext());
			}
		}
		
		updateUI();
	}
	
	public EditText getCommentField() {
		return commentField;
	}

	protected CustomCheckbox getLocationCheckBox() {
		return locationCheckBox;
	}

	protected CustomCheckbox getNotifyCheckBox() {
		return notifyCheckBox;
	}
	
	protected SocializeButton getPostCommentButton() {
		return postCommentButton;
	}

	protected void setNotifySubscribeState(boolean subscribed) {
		if(subscribed) {
			notificationsEnabled = subscribed;
		}
			
		if(notifyCheckBox != null) {
			notifyCheckBox.setChecked(notificationsEnabled);
			updateUI();
		}
	}
	
	
}
