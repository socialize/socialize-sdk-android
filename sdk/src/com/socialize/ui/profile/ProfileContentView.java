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
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.facebook.FacebookSignOutCell;
import com.socialize.ui.user.UserService;
import com.socialize.ui.view.SocializeButton;
import com.socialize.ui.view.SocializeEditText;
import com.socialize.util.DeviceUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentView extends BaseView {

	private DeviceUtils deviceUtils;
	
	private ProfilePictureEditView profilePictureEditView;
	
	private SocializeEditText firstNameEdit;
	private SocializeEditText lastNameEdit;
	
	private FacebookSignInCell facebookSignInCell;
	private FacebookSignOutCell facebookSignOutCell;
	
	private SocializeButton saveButton;
	private SocializeButton cancelButton;
	
	private User currentUser;
	private ProfileLayoutView parent;
	
	// Injected
	private IBeanFactory<SocializeEditText> socializeEditTextFactory;
	private IBeanFactory<ProfilePictureEditView> profilePictureEditViewFactory;
	private IBeanFactory<SocializeButton> profileCancelButtonFactory;
	private IBeanFactory<SocializeButton> profileSaveButtonFactory;
	private IBeanFactory<FacebookSignInCell> facebookSignInCellFactory;
	private IBeanFactory<FacebookSignOutCell> facebookSignOutCellFactory;
	private IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory;
	private UserService userService;
	
	private Activity context;

	public ProfileContentView(Activity context, ProfileLayoutView parent) {
		super(context);
		this.parent = parent;
		this.context = context;
	}

	public void init() {
		
		setOrientation(VERTICAL);
		
		ViewGroup master = makeMasterLayout();
		ViewGroup buttons = makeButtonLayout();
		
		int margin = deviceUtils.getDIP(8);
		
		LayoutParams commonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		commonParams.setMargins(0, margin, 0, margin);
		
		profilePictureEditView = profilePictureEditViewFactory.getBean();

		firstNameEdit = socializeEditTextFactory.getBean();
		lastNameEdit = socializeEditTextFactory.getBean();
		
		firstNameEdit.setLabel("First name");
		lastNameEdit.setLabel("Last name");
		
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
		
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			facebookSignInCell = facebookSignInCellFactory.getBean();
			facebookSignOutCell = facebookSignOutCellFactory.getBean();
			
			facebookSignInCell.setLayoutParams(commonParams);
			facebookSignOutCell.setLayoutParams(commonParams);
			
			facebookSignInCell.setVisibility(View.INVISIBLE);
			facebookSignOutCell.setVisibility(View.INVISIBLE);
			
			master.addView(facebookSignInCell);
			master.addView(facebookSignOutCell);
		}
		
		buttons.addView(cancelButton);
		buttons.addView(saveButton);
		
		setupListeners();
		
		addView(master);
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
		
		if(facebookSignInCell != null) {
			facebookSignInCell.setAuthListener(new SocializeAuthListener() {
				
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
		}
	}

	protected ViewGroup makeMasterLayout() {
		LinearLayout master = new LinearLayout(getContext());
		int padding = deviceUtils.getDIP(8);
		LayoutParams masterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		master.setLayoutParams(masterParams);
		master.setOrientation(LinearLayout.VERTICAL);
		master.setPadding(padding, padding, padding, padding);
		master.setGravity(Gravity.TOP);
		masterParams.weight = 1.0f;
		return master;
	}
	
	protected ViewGroup makeButtonLayout() {
		LinearLayout buttons = new LinearLayout(getContext());
		int padding = deviceUtils.getDIP(8);
		LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		buttons.setLayoutParams(buttonParams);
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setPadding(padding, padding, padding, padding);
		buttons.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		return buttons;
	}
	

	public void setUserDetails(User user) {
		
		profilePictureEditView.setUserDetails(user);
		
		firstNameEdit.setText(user.getFirstName());
		lastNameEdit.setText(user.getLastName());
		
		User currentUser = userService.getCurrentUser();
		
		setCurrentUser(currentUser);
			
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK) &&
				Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
			facebookSignOutCell.setVisibility(View.VISIBLE);
//			autoPostFacebook.setChecked(user.isAutoPostToFacebook());
		}
		
		onFacebookChanged();
	}	
	
	public void onFacebookChanged() {
		if(facebookSignOutCell != null && facebookSignInCell != null) {
			if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
				if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					facebookSignOutCell.setVisibility(View.VISIBLE);
					facebookSignInCell.setVisibility(View.GONE);
//					autoPostFacebook.setVisibility(View.VISIBLE);
				}
				else {
					facebookSignOutCell.setVisibility(View.GONE);
					facebookSignInCell.setVisibility(View.VISIBLE);
//					autoPostFacebook.setVisibility(View.GONE);
				}
			}
			else {
				facebookSignOutCell.setVisibility(View.GONE);
				facebookSignInCell.setVisibility(View.GONE);
//				autoPostFacebook.setVisibility(View.GONE);
			}
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

	public void setFacebookSignOutButtonFactory(IBeanFactory<FacebookSignOutCell> facebookSignOutCellFactory) {
		this.facebookSignOutCellFactory = facebookSignOutCellFactory;
	}

	public void setFacebookSignInButtonFactory(IBeanFactory<FacebookSignInCell> facebookSignInCellFactory) {
		this.facebookSignInCellFactory = facebookSignInCellFactory;
	}

	public void setProfileSaveButtonListenerFactory(IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory) {
		this.profileSaveButtonListenerFactory = profileSaveButtonListenerFactory;
	}

//	public void setAutoPostFacebookOptionFactory(IBeanFactory<CustomCheckbox> autoPostFacebookOptionFactory) {
//		this.autoPostFacebookOptionFactory = autoPostFacebookOptionFactory;
//	}

	public void setProfilePictureEditViewFactory(IBeanFactory<ProfilePictureEditView> profilePictureEditViewFactory) {
		this.profilePictureEditViewFactory = profilePictureEditViewFactory;
	}

	public void setSocializeEditTextFactory(IBeanFactory<SocializeEditText> socializeEditTextFactory) {
		this.socializeEditTextFactory = socializeEditTextFactory;
	}
	
	public void setFacebookSignInCellFactory(IBeanFactory<FacebookSignInCell> facebookSignInCellFactory) {
		this.facebookSignInCellFactory = facebookSignInCellFactory;
	}

	public void setFacebookSignOutCellFactory(IBeanFactory<FacebookSignOutCell> facebookSignOutCellFactory) {
		this.facebookSignOutCellFactory = facebookSignOutCellFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
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

//	protected CustomCheckbox getAutoPostFacebook() {
//		return autoPostFacebook;
//	}
}
