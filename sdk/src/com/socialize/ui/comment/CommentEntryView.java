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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.util.Colors;
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
	
	private IBeanFactory<CustomCheckbox> autoPostFacebookOptionFactory;
	private IBeanFactory<CustomCheckbox> autoPostTwitterOptionFactory;
	private IBeanFactory<CustomCheckbox> locationEnabledOptionFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private CustomCheckbox facebookCheckbox;
	private CustomCheckbox twitterCheckbox;
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
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
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
					keyboardUtils.hideKeyboard(commentField);
					boolean shareLocation = false;
					SocialNetwork[] shareTo = null;
					
					if(facebookCheckbox != null && facebookCheckbox.isChecked()) {
						if(twitterCheckbox != null && twitterCheckbox.isChecked()) {
							shareTo = new SocialNetwork[] {SocialNetwork.FACEBOOK, SocialNetwork.TWITTER};
						}
						else {
							shareTo = new SocialNetwork[] {SocialNetwork.FACEBOOK};
						}
					}
					else if(twitterCheckbox != null && twitterCheckbox.isChecked()) {
						shareTo = new SocialNetwork[] {SocialNetwork.TWITTER};
					}
					
					if(locationCheckBox != null) {
						shareLocation = locationCheckBox.isChecked(); 
					}
					
					listener.onComment(commentField.getText().toString().trim(), shareLocation, notificationsEnabled, shareTo);
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
		
		initShareToolbar();
		
		if(notificationsAvailable && displayUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {

			// Notification layout
			LinearLayout notificationMasterLayout = new LinearLayout(getContext());
			LayoutParams notificationMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
			notificationMasterLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			notificationMasterLayoutParams.weight = 1.0f;
			notificationMasterLayoutParams.setMargins(0, -displayUtils.getDIP(20), 0, 0);
			notificationMasterLayout.setLayoutParams(notificationMasterLayoutParams);
			
			LinearLayout notificationContentLayout = new LinearLayout(getContext());
			LayoutParams notificationContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
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
	
	protected void initShareToolbar() {
		
		final boolean fbSupported = Socialize.getSocialize().isSupported(AuthProviderType.FACEBOOK);
		final boolean twSupported = Socialize.getSocialize().isSupported(AuthProviderType.TWITTER);
		final boolean locationSupported = appUtils.isLocationAvaiable(getContext());
		
		if(fbSupported || twSupported || locationSupported) {
			
			User user = Socialize.getSocialize().getSession().getUser();
			final boolean fbOK = Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK);
			final boolean twOK = Socialize.getSocialize().isAuthenticated(AuthProviderType.TWITTER);
			
			int padding = displayUtils.getDIP(4);
			
			LinearLayout toolbarLayout = new LinearLayout(getContext());
			LinearLayout toolbarLayoutLeft = new LinearLayout(getContext());
			LinearLayout toolbarLayoutRight = new LinearLayout(getContext());		
			
			LayoutParams toolbarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LayoutParams toolbarLayoutLeftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LayoutParams toolbarLayoutRightParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			
			
			GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { colors.getColor(Colors.COMMENT_ENTRY_BOTTOM), colors.getColor(Colors.COMMENT_ENTRY_TOP) });
			toolbarLayout.setBackgroundDrawable(background);
			toolbarLayout.setPadding(padding, padding, padding, padding);		
			
			toolbarLayoutLeftParams.weight = 1.0f;
			
			toolbarLayout.setLayoutParams(toolbarLayoutParams);
			toolbarLayoutLeft.setLayoutParams(toolbarLayoutLeftParams);
			toolbarLayoutRight.setLayoutParams(toolbarLayoutRightParams);
			
			toolbarLayoutRight.setOrientation(HORIZONTAL);
			toolbarLayoutLeft.setOrientation(HORIZONTAL);
			
			toolbarLayout.addView(toolbarLayoutLeft);
			toolbarLayout.addView(toolbarLayoutRight);		
			
			if(fbSupported) {
				facebookCheckbox = autoPostFacebookOptionFactory.getBean();
			}
			
			if(twSupported) {
				twitterCheckbox = autoPostTwitterOptionFactory.getBean();
			}
			
			if(locationSupported) {
				locationCheckBox = locationEnabledOptionFactory.getBean();
			}		
			
			if(facebookCheckbox != null) {
				
				if(fbOK) {
					facebookCheckbox.setChecked(user.isAutoPostToFacebook());
				}
				else {
					facebookCheckbox.setChecked(false);
				}
				
				facebookCheckbox.setOnClickListener(getSocialNetworkClickListener(facebookCheckbox, AuthProviderType.FACEBOOK, "Facebook sharing enabled", "Facebook sharing disabled"));
			}
			
			if(twitterCheckbox != null) {
				if(twOK) {
					twitterCheckbox.setChecked(user.isAutoPostToTwitter());
				}
				else {
					twitterCheckbox.setChecked(false);
				}
				twitterCheckbox.setOnClickListener(getSocialNetworkClickListener(twitterCheckbox, AuthProviderType.TWITTER, "Twitter sharing enabled", "Twitter sharing disabled"));
			}		

			if(locationCheckBox != null) {
				locationCheckBox.setChecked(user.isShareLocation());
			}		
			
			if(facebookCheckbox != null || twitterCheckbox != null || notifyCheckBox != null) {
				
				if(facebookCheckbox != null) {
					toolbarLayoutRight.addView(facebookCheckbox);
				}
				
				if(twitterCheckbox != null) {
					toolbarLayoutRight.addView(twitterCheckbox);
				}			
				
				if(notifyCheckBox != null && displayUtils.getOrientation() != Configuration.ORIENTATION_PORTRAIT) {
					toolbarLayoutRight.addView(notifyCheckBox);
				}
			}	
			
			if(locationCheckBox != null) {
				toolbarLayoutLeft.addView(locationCheckBox);
			}		
			
			addView(toolbarLayout);
		}
	}
	
	protected OnClickListener getSocialNetworkClickListener(final CustomCheckbox chkbox, final AuthProviderType authProviderType, final String checkedMsg, final String uncheckedMsg) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(chkbox.isChecked()) {
					if(Socialize.getSocialize().isAuthenticated(authProviderType)) {
						toast(checkedMsg);
					}
					else {
						// Show auth
						getSDK().authenticate(getContext(), authProviderType, new SocializeAuthListener() {

							@Override
							public void onError(SocializeException error) {
								chkbox.setChecked(false);
								showErrorToast(getContext(), error);
							}

							@Override
							public void onAuthSuccess(SocializeSession session) {
								chkbox.setChecked(true);
								toast(checkedMsg);
							}

							@Override
							public void onAuthFail(SocializeException error) {
								chkbox.setChecked(false);
								showErrorToast(getContext(), error);
							}

							@Override
							public void onCancel() {
								chkbox.setChecked(false);
							}
						});
					}	
				}
				else {
					toast(uncheckedMsg);
				}
			}
		};	
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
	
	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setNotificationEnabledOptionFactory(IBeanFactory<CustomCheckbox> notificationOptionFactory) {
		this.notificationEnabledOptionFactory = notificationOptionFactory;
	}
	
	public void setAutoPostTwitterOptionFactory(IBeanFactory<CustomCheckbox> autoPostTwitterOptionFactory) {
		this.autoPostTwitterOptionFactory = autoPostTwitterOptionFactory;
	}

	public void setNotificationsEnabled(boolean enabled) {
		notificationsEnabled = enabled;
		updateUI();
	}

	protected void updateUI() {
		
		if(notificationsAvailable) {
			if(notificationsText != null) notificationsText.setVisibility(View.VISIBLE);
			if(notificationsTitle != null) notificationsTitle.setVisibility(View.VISIBLE);
			if(subscribeNotificationButton != null) subscribeNotificationButton.setVisibility(View.VISIBLE);
			if(notificationBannerImage != null) notificationBannerImage.setVisibility(View.VISIBLE);
	
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
		
		User user = Socialize.getSocialize().getSession().getUser();
		
		if(facebookCheckbox != null) {
			if(!facebookCheckbox.isChanged() && Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				facebookCheckbox.setChecked(user.isAutoPostToFacebook());
			}
			else {
				facebookCheckbox.setChecked(false);
			}
		}
		
		if(twitterCheckbox != null) {
			if(!twitterCheckbox.isChanged() && Socialize.getSocialize().isAuthenticated(AuthProviderType.TWITTER)) {
				twitterCheckbox.setChecked(user.isAutoPostToTwitter());
			}
			else {
				twitterCheckbox.setChecked(false);
			}
		}		
		
		if(locationCheckBox != null && !locationCheckBox.isChanged()) {
			locationCheckBox.setChecked(user.isShareLocation());
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
		update();
	}
	
	public void update() {
		if(facebookCheckbox != null) {
			facebookCheckbox.setChanged(false);
		}
		
		if(twitterCheckbox != null) {
			twitterCheckbox.setChanged(false);
		}
		
		if(locationCheckBox != null) {
			locationCheckBox.setChanged(false);
		}
		
		User user = Socialize.getSocialize().getSession().getUser();
		
		if(user != null) {
			notificationsAvailable = user.isNotificationsEnabled() && appUtils.isNotificationsAvailable(getContext());
		}
		
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
