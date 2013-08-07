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
package com.socialize.ui.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetworkCheckbox;
import com.socialize.networks.SocialNetworkSignOutListener;
import com.socialize.ui.user.UserService;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.SocializeButton;
import com.socialize.ui.view.SocializeEditText;
import com.socialize.util.AppUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentView extends BaseView {

	private DisplayUtils displayUtils;
	private AppUtils appUtils;
	private Drawables drawables;
	
	private LocalizationService localizationService;
	
	private ProfilePictureEditView profilePictureEditView;
	
	private SocializeEditText firstNameEdit;
	private SocializeEditText lastNameEdit;
	
	private SocializeButton saveButton;
	private SocializeButton cancelButton;
	
	private User currentUser;
	private ProfileLayoutView parent;
	private Toast toaster;
	
	// Injected
	private IBeanFactory<SocializeEditText> socializeEditTextFactory;
	private IBeanFactory<ProfilePictureEditView> profilePictureEditViewFactory;
	private IBeanFactory<SocializeButton> profileCancelButtonFactory;
	private IBeanFactory<SocializeButton> profileSaveButtonFactory;
	private IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory;
	private UserService userService;
	
	private Activity context;
	
	private TextView userId;
	
	private CheckBox autoPostFacebook;
	private CheckBox autoPostTwitter;
	
	private CustomCheckbox notificationsEnabledCheckbox;
	private CustomCheckbox locationEnabledCheckbox;
	
	private SocialNetworkCheckbox facebookEnabledCheckbox;
	private SocialNetworkCheckbox twitterEnabledCheckbox;
	
	private IBeanFactory<CustomCheckbox> notificationsEnabledCheckboxFactory;
	private IBeanFactory<CustomCheckbox> locationEnabledCheckboxFactory;
	
	private IBeanFactory<SocialNetworkCheckbox> facebookEnabledCheckboxFactory;
	private IBeanFactory<SocialNetworkCheckbox> twitterEnabledCheckboxFactory;
	
	private LayoutParams commonParams;
	
	private int buttonLayoutViewId = 0;
	
	private SocializeAuthListener socialSignInListener;
	private SocialNetworkSignOutListener socialSignOutListener;

	public ProfileContentView(Activity context, ProfileLayoutView parent) {
		super(context);
		this.parent = parent;
		this.context = context;
	}

	public void init() {
		
		setOrientation(VERTICAL);

		CompatUtils.setBackgroundDrawable(this, drawables.getDrawable("slate.png", true, true, true));

		LayoutParams viewParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		setLayoutParams(viewParams);
		
		ViewGroup master = makeMasterLayout();
		ViewGroup buttons = makeButtonLayout();
		
		int margin = displayUtils.getDIP(8);
		buttonLayoutViewId = getNextViewId(this);
		
		commonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		commonParams.setMargins(0, margin, 0, margin);
		
		profilePictureEditView = profilePictureEditViewFactory.getBean();
		
		userId = new TextView(getContext());
		userId.setTextColor(Color.WHITE);
		userId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		userId.setGravity(Gravity.RIGHT);
		
		LayoutParams userIdParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		userIdParams.gravity = Gravity.RIGHT;
		
		userId.setLayoutParams(userIdParams);

		firstNameEdit = socializeEditTextFactory.getBean();
		lastNameEdit = socializeEditTextFactory.getBean();
		
		firstNameEdit.setLabel(localizationService.getString(I18NConstants.SETTINGS_LABEL_FIRST_NAME));
		lastNameEdit.setLabel(localizationService.getString(I18NConstants.SETTINGS_LABEL_LAST_NAME));
		
		saveButton = profileSaveButtonFactory.getBean();
		cancelButton = profileCancelButtonFactory.getBean();
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		firstNameEdit.setFilters(maxLength);
		lastNameEdit.setFilters(maxLength);
		
		profilePictureEditView.setLayoutParams(commonParams);
		firstNameEdit.setLayoutParams(commonParams);
		lastNameEdit.setLayoutParams(commonParams);
		
		master.addView(userId);
		master.addView(profilePictureEditView);
		master.addView(firstNameEdit);
		master.addView(lastNameEdit);
		
		if(appUtils.isLocationAvailable(getContext())) {
			locationEnabledCheckbox = locationEnabledCheckboxFactory.getBean();
			locationEnabledCheckbox.setLayoutParams(commonParams);
			master.addView(locationEnabledCheckbox);
		}
		
		if(appUtils.isNotificationsAvailable(getContext())) {
			notificationsEnabledCheckbox = notificationsEnabledCheckboxFactory.getBean();
			notificationsEnabledCheckbox.setLayoutParams(commonParams);
			master.addView(notificationsEnabledCheckbox);
		}
		
		setupSocialButtons(master);
		setupListeners();
		
		buttons.addView(cancelButton);
		buttons.addView(saveButton);
		
		ViewGroup scrollView = makeScrollLayout();
		
		LinearLayout.LayoutParams childViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		scrollView.addView(master, childViewLayout);	
		
		addView(scrollView);
		addView(buttons);
	}
	
	protected void setupSocialButtons(ViewGroup group) {
		if(getSocialize().isSupported(context, AuthProviderType.FACEBOOK)) {
			facebookEnabledCheckbox = facebookEnabledCheckboxFactory.getBean();
			autoPostFacebook = new CheckBox(getContext());
			setupSocialButton(group, facebookEnabledCheckbox, autoPostFacebook, localizationService.getString(I18NConstants.AUTO_POST_FB));
		}
		if(getSocialize().isSupported(context, AuthProviderType.TWITTER)) {
			twitterEnabledCheckbox = twitterEnabledCheckboxFactory.getBean();
			autoPostTwitter = new CheckBox(getContext());
			setupSocialButton(group, twitterEnabledCheckbox, autoPostTwitter, localizationService.getString(I18NConstants.AUTO_POST_TW));
		}		
	}
	
	protected void setupSocialButton(ViewGroup group, SocialNetworkCheckbox checkbox, CheckBox shareOption, String shareText) {
		
		checkbox.setLayoutParams(commonParams);
		
		shareOption.setText(shareText);
		shareOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		
		LayoutParams optionsParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		shareOption.setLayoutParams(optionsParams);
		
		checkbox.setVisibility(View.INVISIBLE);
		
		shareOption.setVisibility(View.INVISIBLE);
		
		group.addView(checkbox);
		
		ViewGroup fbLayout = makeSocialNetworkOptionsLayout();
		
		fbLayout.addView(shareOption);
		
		group.addView(fbLayout);
	}
	
	protected SocializeAuthListener getSignInListener() {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				ProfileContentView.this.showErrorToast(ProfileContentView.this.getContext(), error);
			}
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				// Reload profile view
				parent.setUserId(session.getUser().getId().toString());
				parent.doGetUserProfile();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				ProfileContentView.this.showErrorToast(ProfileContentView.this.getContext(), error);
			}
		};
	}
	
	protected SocialNetworkSignOutListener getSignOutListener() {
		return new SocialNetworkSignOutListener() {
			@Override
			public void onSignOut() {
				parent.setUserId(Socialize.getSocialize().getSession().getUser().getId().toString());
				parent.doGetUserProfile();
			}

			@Override
			public void onCancel() {}
			
		};
	}
	
	protected void setupListeners() {
		
		socialSignInListener = getSignInListener();
		socialSignOutListener = getSignOutListener();
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.finish();
			}
		});
		
		saveButton.setOnClickListener(profileSaveButtonListenerFactory.getBean(getContext(), this));
		
		if(facebookEnabledCheckbox != null) {
			facebookEnabledCheckbox.setSignInListener(socialSignInListener);
			facebookEnabledCheckbox.setSignOutListener(socialSignOutListener);
		}
		
		if(twitterEnabledCheckbox != null) {
			twitterEnabledCheckbox.setSignInListener(socialSignInListener);			
			twitterEnabledCheckbox.setSignOutListener(socialSignOutListener);
		}
	}

	protected ViewGroup makeScrollLayout() {
		LinearLayout.LayoutParams scrollViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		scrollViewLayout.weight = 1.0f;
		ScrollView scrollView = new ScrollView(getContext());
		scrollView.setFillViewport(true);
		scrollView.setLayoutParams(scrollViewLayout);
		return scrollView;
	}

	protected ViewGroup makeSocialNetworkOptionsLayout() {
		LinearLayout master = new LinearLayout(getContext());
		int padding = displayUtils.getDIP(8);
		LinearLayout.LayoutParams masterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		masterParams.setMargins(padding, 0, 0, 0);
		master.setLayoutParams(masterParams);
		master.setOrientation(LinearLayout.VERTICAL);
		master.setGravity(Gravity.TOP);
		return master;
	}
	
	protected ViewGroup makeMasterLayout() {
		LinearLayout master = new LinearLayout(getContext());
		int padding = displayUtils.getDIP(8);
		LinearLayout.LayoutParams masterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		masterParams.weight = 1.0f;
		master.setLayoutParams(masterParams);
		master.setOrientation(LinearLayout.VERTICAL);
		master.setPadding(padding, padding, padding, padding);
		master.setGravity(Gravity.TOP);
		return master;
	}
	
	protected ViewGroup makeButtonLayout() {
		LinearLayout buttons = new LinearLayout(getContext());
		int padding = displayUtils.getDIP(8);
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		buttons.setId(buttonLayoutViewId);
		buttons.setLayoutParams(buttonParams);
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setPadding(padding, padding, padding, padding);
		buttons.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		
		ColorDrawable background = new ColorDrawable(Color.BLACK);
		background.setAlpha(64);

		CompatUtils.setBackgroundDrawable(buttons, background);
		
		return buttons;
	}

	public void setUserDetails(User user, UserSettings settings) {
		
		profilePictureEditView.setUserDetails(user);
		userId.setText("ID: "  + String.valueOf(user.getId()));
		firstNameEdit.setText(settings.getFirstName());
		lastNameEdit.setText(settings.getLastName());
		
		User currentUser = userService.getCurrentUser();
		
		setCurrentUser(currentUser);
			
		if(getSocialize().isSupported(context, AuthProviderType.FACEBOOK)) {
			if(Socialize.getSocialize().isAuthenticatedForRead(AuthProviderType.FACEBOOK)) {
				facebookEnabledCheckbox.setChecked(true);
				autoPostFacebook.setChecked(settings.isAutoPostFacebook());
			}
		}
		
		if(getSocialize().isSupported(context, AuthProviderType.TWITTER)) {
			if(Socialize.getSocialize().isAuthenticatedForRead(AuthProviderType.TWITTER)) {
				twitterEnabledCheckbox.setChecked(true);
				autoPostTwitter.setChecked(settings.isAutoPostTwitter());
			}
			else {
				twitterEnabledCheckbox.setChecked(false);
			}
		}		
		
		if(notificationsEnabledCheckbox != null) {
			notificationsEnabledCheckbox.setChecked(settings.isNotificationsEnabled());
		}
		
		if(locationEnabledCheckbox != null) {
			locationEnabledCheckbox.setChecked(settings.isLocationEnabled());
		}
		
		onNetworksChanged();
	}	
	
	public void onNetworksChanged() {
		if(getSocialize().isSupported(context, AuthProviderType.FACEBOOK)) {
			
			if(facebookEnabledCheckbox != null) {
				facebookEnabledCheckbox.setVisibility(View.VISIBLE);
			}
			
			if(autoPostFacebook != null) {
				if(Socialize.getSocialize().isAuthenticatedForRead(AuthProviderType.FACEBOOK)) {
					autoPostFacebook.setVisibility(View.VISIBLE);
				}
				else {
					autoPostFacebook.setVisibility(View.GONE);
				}	
			}
		}
		else {
			if(facebookEnabledCheckbox != null) facebookEnabledCheckbox.setVisibility(View.GONE);
			if(autoPostFacebook != null) autoPostFacebook.setVisibility(View.GONE);
		}
		
		if(getSocialize().isSupported(context, AuthProviderType.TWITTER)) {
			
			if(twitterEnabledCheckbox != null) {
				twitterEnabledCheckbox.setVisibility(View.VISIBLE);
			}
			
			if(autoPostTwitter != null) {
				if(Socialize.getSocialize().isAuthenticatedForRead(AuthProviderType.TWITTER)) {
					autoPostTwitter.setVisibility(View.VISIBLE);
				}
				else {
					autoPostTwitter.setVisibility(View.GONE);
				}	
			}
		}
		else {
			if(twitterEnabledCheckbox != null) twitterEnabledCheckbox.setVisibility(View.GONE);
			if(autoPostTwitter != null) autoPostTwitter.setVisibility(View.GONE);
		}		
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		onNetworksChanged();
	}

	public void onProfilePictureChange(Bitmap image) {
		profilePictureEditView.setProfileImage(new SafeBitmapDrawable(image));
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setProfileCancelButtonFactory(IBeanFactory<SocializeButton> profileCancelButtonFactory) {
		this.profileCancelButtonFactory = profileCancelButtonFactory;
	}

	public void setProfileSaveButtonFactory(IBeanFactory<SocializeButton> profileSaveButtonFactory) {
		this.profileSaveButtonFactory = profileSaveButtonFactory;
	}

	public void setProfileSaveButtonListenerFactory(IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory) {
		this.profileSaveButtonListenerFactory = profileSaveButtonListenerFactory;
	}

	public void setProfilePictureEditViewFactory(IBeanFactory<ProfilePictureEditView> profilePictureEditViewFactory) {
		this.profilePictureEditViewFactory = profilePictureEditViewFactory;
	}

	public void setSocializeEditTextFactory(IBeanFactory<SocializeEditText> socializeEditTextFactory) {
		this.socializeEditTextFactory = socializeEditTextFactory;
	}
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setNotificationsEnabledCheckboxFactory(IBeanFactory<CustomCheckbox> notificationsEnabledCheckboxFactory) {
		this.notificationsEnabledCheckboxFactory = notificationsEnabledCheckboxFactory;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	protected ProfilePictureEditView getProfilePictureEditView() {
		return profilePictureEditView;
	}

	protected SocializeEditText getFirstNameEdit() {
		return firstNameEdit;
	}

	protected SocializeEditText getLastNameEdit() {
		return lastNameEdit;
	}

	protected CheckBox getAutoPostFacebook() {
		return autoPostFacebook;
	}
	
	protected CheckBox getAutoPostTwitter() {
		return autoPostTwitter;
	}

	protected CustomCheckbox getNotificationsEnabledCheckbox() {
		return notificationsEnabledCheckbox;
	}
	
	protected CustomCheckbox getLocationEnabledCheckbox() {
		return locationEnabledCheckbox;
	}

	protected SocialNetworkCheckbox getFacebookEnabledCheckbox() {
		return facebookEnabledCheckbox;
	}

	public void setFacebookEnabledCheckboxFactory(IBeanFactory<SocialNetworkCheckbox> facebookEnabledCheckboxFactory) {
		this.facebookEnabledCheckboxFactory = facebookEnabledCheckboxFactory;
	}
	
	public void setTwitterEnabledCheckboxFactory(IBeanFactory<SocialNetworkCheckbox> twitterEnabledCheckboxFactory) {
		this.twitterEnabledCheckboxFactory = twitterEnabledCheckboxFactory;
	}
	
	public void setLocationEnabledCheckboxFactory(IBeanFactory<CustomCheckbox> locationEnabledCheckboxFactory) {
		this.locationEnabledCheckboxFactory = locationEnabledCheckboxFactory;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	protected void toast(String text) {
		if(toaster != null) {
			toaster.cancel();
		}
		
		toaster = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
		toaster.show();
	}	
}
