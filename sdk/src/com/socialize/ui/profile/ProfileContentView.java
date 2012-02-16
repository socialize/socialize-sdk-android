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
package com.socialize.ui.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookCheckbox;
import com.socialize.networks.facebook.FacebookSignOutListener;
import com.socialize.ui.user.UserService;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.SocializeButton;
import com.socialize.ui.view.SocializeEditText;
import com.socialize.util.AppUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentView extends BaseView {

	private DeviceUtils deviceUtils;
	private AppUtils appUtils;
	private Drawables drawables;
	
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
	
	private CheckBox autoPostLikesFacebook;
	private CheckBox autoPostCommentsFacebook;
	
	private CustomCheckbox notificationsEnabledCheckbox;
	private FacebookCheckbox facebookEnabledCheckbox;
	
	private IBeanFactory<CustomCheckbox> notificationsEnabledCheckboxFactory;
	private IBeanFactory<FacebookCheckbox> facebookEnabledCheckboxFactory;
	
	private int buttonLayoutViewId = 0;

	public ProfileContentView(Activity context, ProfileLayoutView parent) {
		super(context);
		this.parent = parent;
		this.context = context;
	}

	public void init() {
		
		setOrientation(VERTICAL);
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		
		LayoutParams viewParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		setLayoutParams(viewParams);
		
		ViewGroup master = makeMasterLayout();
		ViewGroup buttons = makeButtonLayout();
		
		int margin = deviceUtils.getDIP(8);
		buttonLayoutViewId = getNextViewId(this);
		
		LayoutParams commonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		commonParams.setMargins(0, margin, 0, margin);
		
		profilePictureEditView = profilePictureEditViewFactory.getBean();

		firstNameEdit = socializeEditTextFactory.getBean();
		lastNameEdit = socializeEditTextFactory.getBean();
		
		firstNameEdit.setLabel("First Name");
		lastNameEdit.setLabel("Last Name");
		
		saveButton = profileSaveButtonFactory.getBean();
		cancelButton = profileCancelButtonFactory.getBean();
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		firstNameEdit.setFilters(maxLength);
		lastNameEdit.setFilters(maxLength);
		
		profilePictureEditView.setLayoutParams(commonParams);
		firstNameEdit.setLayoutParams(commonParams);
		lastNameEdit.setLayoutParams(commonParams);
		
		master.addView(profilePictureEditView);
		master.addView(firstNameEdit);
		master.addView(lastNameEdit);
		
		if(appUtils.isNotificationsAvaiable(getContext())) {
			notificationsEnabledCheckbox = notificationsEnabledCheckboxFactory.getBean();
			notificationsEnabledCheckbox.setLayoutParams(commonParams);
			
			master.addView(notificationsEnabledCheckbox);
		}
		
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			
			facebookEnabledCheckbox = facebookEnabledCheckboxFactory.getBean();
			
			facebookEnabledCheckbox.setLayoutParams(commonParams);
			
			autoPostLikesFacebook = new CheckBox(getContext());
			autoPostCommentsFacebook = new CheckBox(getContext());		
			
			autoPostLikesFacebook.setText("Post Likes to my Facebook Wall");
			autoPostLikesFacebook.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			
			autoPostCommentsFacebook.setText("Post Comments to my Facebook Wall");
			autoPostCommentsFacebook.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			
			LayoutParams optionsParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			autoPostLikesFacebook.setLayoutParams(optionsParams);
			autoPostCommentsFacebook.setLayoutParams(optionsParams);
			
			facebookEnabledCheckbox.setVisibility(View.INVISIBLE);
			autoPostLikesFacebook.setVisibility(View.INVISIBLE);
			autoPostCommentsFacebook.setVisibility(View.INVISIBLE);
			
			master.addView(facebookEnabledCheckbox);
			
			ViewGroup fbLayout = makeFacebookOptionsLayout();
			
			fbLayout.addView(autoPostLikesFacebook);
			fbLayout.addView(autoPostCommentsFacebook);
			
			master.addView(fbLayout);
		}
		
		buttons.addView(cancelButton);
		buttons.addView(saveButton);
		
		setupListeners();
		
		ViewGroup scrollView = makeScrollLayout();
		
		LinearLayout.LayoutParams childViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		scrollView.addView(master, childViewLayout);	
		
		addView(scrollView);
		addView(buttons);
	}
	
	protected void setupListeners() {
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.finish();
			}
		});
		
		saveButton.setOnClickListener(profileSaveButtonListenerFactory.getBean(getContext(), this));
		
		if(facebookEnabledCheckbox != null) {
			facebookEnabledCheckbox.setSignInListener(new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					ProfileContentView.this.showError(ProfileContentView.this.getContext(), error);
				}
				
				@Override
				public void onCancel() { }
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					// Reload profile view
					parent.setUserId(session.getUser().getId().toString());
					parent.doGetUserProfile();
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					ProfileContentView.this.showError(ProfileContentView.this.getContext(), error);
				}
			});
			
			facebookEnabledCheckbox.setSignOutListener(new FacebookSignOutListener() {
				@Override
				public void onSignOut() {
					parent.setUserId(Socialize.getSocialize().getSession().getUser().getId().toString());
					parent.doGetUserProfile();
				}

				@Override
				public void onCancel() {}
				
			});
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

	protected ViewGroup makeFacebookOptionsLayout() {
		LinearLayout master = new LinearLayout(getContext());
		int padding = deviceUtils.getDIP(8);
		LinearLayout.LayoutParams masterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		masterParams.setMargins(padding, 0, 0, 0);
		master.setLayoutParams(masterParams);
		master.setOrientation(LinearLayout.VERTICAL);
		master.setGravity(Gravity.TOP);
		return master;
	}
	
	protected ViewGroup makeMasterLayout() {
		LinearLayout master = new LinearLayout(getContext());
		int padding = deviceUtils.getDIP(8);
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
		int padding = deviceUtils.getDIP(8);
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		buttons.setId(buttonLayoutViewId);
		buttons.setLayoutParams(buttonParams);
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setPadding(padding, padding, padding, padding);
		buttons.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		
		ColorDrawable background = new ColorDrawable(Color.BLACK);
		background.setAlpha(64);
		
		buttons.setBackgroundDrawable(background);		
		
		return buttons;
	}

	public void setUserDetails(User user) {
		
		profilePictureEditView.setUserDetails(user);
		
		firstNameEdit.setText(user.getFirstName());
		lastNameEdit.setText(user.getLastName());
		
		User currentUser = userService.getCurrentUser();
		
		setCurrentUser(currentUser);
			
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			
			if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				facebookEnabledCheckbox.setChecked(true);
				autoPostLikesFacebook.setChecked(user.isAutoPostLikesFacebook());
				autoPostCommentsFacebook.setChecked(user.isAutoPostCommentsFacebook());
			}
			else {
				facebookEnabledCheckbox.setChecked(false);
			}
		}
		
		if(notificationsEnabledCheckbox != null) {
			notificationsEnabledCheckbox.setChecked(user.isNotificationsEnabled());
		}
		
		onFacebookChanged();
	}	
	
	public void onFacebookChanged() {
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			
			if(facebookEnabledCheckbox != null) {
				facebookEnabledCheckbox.setVisibility(View.VISIBLE);
			}
			
			if(autoPostCommentsFacebook != null && autoPostLikesFacebook != null) {
				if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					autoPostLikesFacebook.setVisibility(View.VISIBLE);
					autoPostCommentsFacebook.setVisibility(View.VISIBLE);
				}
				else {
					autoPostLikesFacebook.setVisibility(View.GONE);
					autoPostCommentsFacebook.setVisibility(View.GONE);
				}	
			}
		}
		else {
			if(facebookEnabledCheckbox != null) facebookEnabledCheckbox.setVisibility(View.GONE);
			if(autoPostLikesFacebook != null) autoPostLikesFacebook.setVisibility(View.GONE);
			if(autoPostCommentsFacebook != null) autoPostCommentsFacebook.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		onFacebookChanged();
	}

	public void onProfilePictureChange(Bitmap image) {
		profilePictureEditView.setImage(image);
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
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
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

	protected CheckBox getAutoPostLikesFacebook() {
		return autoPostLikesFacebook;
	}

	protected CheckBox getAutoPostCommentsFacebook() {
		return autoPostCommentsFacebook;
	}

	protected CustomCheckbox getNotificationsEnabledCheckbox() {
		return notificationsEnabledCheckbox;
	}
	
	protected FacebookCheckbox getFacebookEnabledCheckbox() {
		return facebookEnabledCheckbox;
	}

	public void setFacebookEnabledCheckboxFactory(IBeanFactory<FacebookCheckbox> facebookEnabledCheckboxFactory) {
		this.facebookEnabledCheckboxFactory = facebookEnabledCheckboxFactory;
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
