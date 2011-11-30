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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.button.SocializeButton;
import com.socialize.ui.facebook.FacebookButton;
import com.socialize.ui.facebook.FacebookSignOutClickListener;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.user.UserService;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ProfileContentView extends BaseView {

	private ImageView profilePicture;
	private TextView displayName;
	private EditText displayNameEdit;
	private AutoPostFacebookOption autoPostFacebook;
	private SocializeButton facebookSignOutButton;
	private SocializeButton facebookSignInButton;
	private SocializeButton editButton;
	private SocializeButton saveButton;
	private SocializeButton cancelButton;
	private ProfileImageContextMenu contextMenu;
	private String userDisplayName;
	private SafeBitmapDrawable profileDrawable;
	private SafeBitmapDrawable originalProfileDrawable;
	private SafeBitmapDrawable defaultProfilePicture;
	private User currentUser;
	private boolean isLoggedOnUser = false;
	private Bitmap tmpProfileImage;
	private boolean editMode = false;
	
	private ProfileLayoutView parent;
	
	// Injected
	private IBeanFactory<SocializeButton> profileCancelButtonFactory;
	private IBeanFactory<SocializeButton> profileSaveButtonFactory;
	private IBeanFactory<SocializeButton> profileEditButtonFactory;
	private IBeanFactory<SocializeButton> facebookSignOutButtonFactory;
	private IBeanFactory<FacebookButton> facebookSignInButtonFactory;
	private IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory;
	private IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory;
	private IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory;
	private DeviceUtils deviceUtils;
	private Colors colors;
	private Drawables drawables;
	private UserService userService;
	private ImageLoader imageLoader;


	public ProfileContentView(Context context, ProfileLayoutView parent) {
		super(context);
		this.parent = parent;
	}

	public void init() {
		this.setDrawables(drawables);
		this.setUserService(userService);
		this.setImageLoader(imageLoader);
		this.setDefaultProfilePicture((SafeBitmapDrawable) drawables.getDrawable("large_user_icon.png"));
		this.setContextMenu(profileImageContextMenuFactory.getBean());
		
		final int padding = deviceUtils.getDIP(4);
		final int imagePadding = deviceUtils.getDIP(4);
		final int margin = deviceUtils.getDIP(8);
		final int imageSize = deviceUtils.getDIP(133);
		final int editTextStroke = deviceUtils.getDIP(2);
		final int minTextHeight = deviceUtils.getDIP(50);
		final int maxTextHeight = deviceUtils.getDIP(200);
		final float editTextRadius = editTextStroke;
		final int titleColor = colors.getColor(Colors.TITLE);
		
		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		editPanelLayoutParams.setMargins(margin, margin, margin, margin);
		this.setLayoutParams(editPanelLayoutParams);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(0, 0, 0, 0);
		this.setGravity(Gravity.TOP);
		
		LinearLayout masterLayout = new LinearLayout(getContext());
		LayoutParams masterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,deviceUtils.getDIP(150));
		
		masterLayout.setLayoutParams(masterLayoutParams);
		masterLayout.setOrientation(LinearLayout.HORIZONTAL);
		masterLayout.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(getContext());
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		buttonLayoutParams.setMargins(margin,margin,margin,margin);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setPadding(padding, padding, padding, padding);
		nameLayout.setGravity(Gravity.TOP);
		
		buttonLayout.setLayoutParams(buttonLayoutParams);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams textEditLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams commentMetaLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		
		final ImageView profilePicture = new ImageView(getContext());
		final TextView displayName = new TextView(getContext());
		final EditText displayNameEdit = new EditText(getContext());
		final TextView commentView = new TextView(getContext());
		final TextView commentMeta = new TextView(getContext());
		
		AutoPostFacebookOption checkBox = new AutoPostFacebookOption(getContext());
		checkBox.init();
		checkBox.setChecked(Socialize.getSocialize().getSession().getUser().isAutoPostToFacebook()); // TODO: Make a factory
		
		commentMetaLayout.gravity = Gravity.RIGHT;
		commentMeta.setGravity(Gravity.RIGHT);
		commentMeta.setLayoutParams(commentMetaLayout);
		commentMeta.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		commentMeta.setTextColor(Color.WHITE);
		
		commentView.setVisibility(View.GONE);
		commentMeta.setVisibility(View.GONE);
		checkBox.setEnabled(false);
		
		GradientDrawable commentBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.BLACK});
		commentBG.setCornerRadius(10.0f);
		commentBG.setStroke(2, Color.WHITE);
		commentBG.setAlpha(64);
		
		commentViewLayout.setMargins(0, margin, 0, margin);
		
		commentView.setBackgroundDrawable(commentBG);
		commentView.setPadding(margin, margin, margin, margin);
		commentView.setLayoutParams(commentViewLayout);
		commentView.setMinHeight(minTextHeight);
		commentView.setMinimumHeight(minTextHeight);
		commentView.setMaxHeight(maxTextHeight);
		commentView.setTextColor(Color.WHITE);
		commentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		commentView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		commentView.setScroller(new Scroller(getContext())); 
		commentView.setScrollbarFadingEnabled(true);
		commentView.setMovementMethod(new ScrollingMovementMethod());
		
		final SocializeButton editButton = profileEditButtonFactory.getBean();
		final SocializeButton saveButton = profileSaveButtonFactory.getBean();
		final SocializeButton cancelButton = profileCancelButtonFactory.getBean();
		final SocializeButton facebookSignOutButton = facebookSignOutButtonFactory.getBean();
		final FacebookButton facebookSignInButton = facebookSignInButtonFactory.getBean();
		
		final ProfileSaveButtonListener saveListener = profileSaveButtonListenerFactory.getBean(getContext(), this);
		
		FacebookSignOutClickListener facebookSignOutClickListener = facebookSignOutClickListenerFactory.getBean();
		
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
		facebookSignOutButton.setVisibility(View.GONE);
		facebookSignInButton.setVisibility(View.GONE);
		checkBox.setVisibility(View.GONE);
		
		textLayout.setMargins(margin, 0, 0, 0);
		textEditLayout.setMargins(margin,0,margin,0);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(2, Color.BLACK);
		imageBG.setAlpha(64);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
		profilePicture.setBackgroundDrawable(imageBG);
		profilePicture.setScaleType(ScaleType.CENTER_CROP);
	
		displayName.setTextColor(titleColor);
		
		displayName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		displayName.setMaxLines(1);
		displayName.setTypeface(Typeface.DEFAULT);
		displayName.setTextColor(titleColor);
		displayName.setSingleLine();
		displayName.setLayoutParams(textLayout);
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);
		
		displayNameEdit.setLayoutParams(textEditLayout);
		displayNameEdit.setMinLines(1);  
		displayNameEdit.setMaxLines(1); 
		displayNameEdit.setSingleLine(true);
		displayNameEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		displayNameEdit.setBackgroundDrawable(textBG);
		displayNameEdit.setVisibility(View.GONE);
		displayNameEdit.setPadding(padding, padding, padding, padding);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		displayNameEdit.setFilters(maxLength);
		
		this.setProfilePicture(profilePicture);
		this.setDisplayName(displayName);
		this.setDisplayNameEdit(displayNameEdit);
		this.setFacebookSignOutButton(facebookSignOutButton);
		this.setFacebookSignInButton(facebookSignInButton);
		this.setSaveButton(saveButton);
		this.setCancelButton(cancelButton);
		this.setEditButton(editButton);
		this.setAutoPostFacebook(checkBox);
		
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileContentView.this.onEdit();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileContentView.this.onCancel();
			}
		});
		
		profilePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileContentView.this.onImageEdit();
			}
		});
		
		saveButton.setOnClickListener(saveListener);
		facebookSignOutButton.setOnClickListener(facebookSignOutClickListener);
		facebookSignInButton.setAuthListener(new SocializeAuthListener() {
			
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
		
		nameLayout.addView(displayName);
		nameLayout.addView(displayNameEdit);
		nameLayout.addView(editButton);
		nameLayout.addView(facebookSignOutButton);
		nameLayout.addView(facebookSignInButton);
		
		buttonLayout.addView(cancelButton);
		buttonLayout.addView(saveButton);
		
		nameLayout.addView(buttonLayout);
		
		masterLayout.addView(profilePicture);
		masterLayout.addView(nameLayout);
		
		this.addView(masterLayout);
		this.addView(commentView);
		this.addView(commentMeta);
		this.addView(checkBox);
	}
	
	public ImageView getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(ImageView profilePicture) {
		this.profilePicture = profilePicture;
	}

	public TextView getDisplayName() {
		return displayName;
	}

	public void setDisplayName(TextView displayName) {
		this.displayName = displayName;
	}
	
	public void setDisplayNameEdit(EditText displayNameEdit) {
		this.displayNameEdit = displayNameEdit;
	}

	public Drawable getProfileDrawable() {
		return profileDrawable;
	}

	public void setProfileDrawable(SafeBitmapDrawable profileDrawable) {
		this.profileDrawable = profileDrawable;
		this.originalProfileDrawable = profileDrawable;
	}

	public SocializeButton getFacebookSignOutButton() {
		return facebookSignOutButton;
	}
	
	public EditText getDisplayNameEdit() {
		return displayNameEdit;
	}

	public void setFacebookSignOutButton(SocializeButton facebookSignOutButton) {
		this.facebookSignOutButton = facebookSignOutButton;
	}
	
	public void setFacebookSignInButton(SocializeButton facebookSignInButton) {
		this.facebookSignInButton = facebookSignInButton;
	}

	public void setUserDisplayName(String name) {
		this.userDisplayName = name;
		revertUserDisplayName();
	}
	
	public SocializeButton getEditButton() {
		return editButton;
	}

	public void setEditButton(SocializeButton editButton) {
		this.editButton = editButton;
	}

	public SocializeButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(SocializeButton saveButton) {
		this.saveButton = saveButton;
	}
	
	public SocializeButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(SocializeButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public void revertUserDisplayName() {
		if(!StringUtils.isEmpty(userDisplayName)) {
			displayNameEdit.setText(userDisplayName);
		}
		else {
			displayNameEdit.setText("");
		}
	}
	
	public Drawables getDrawables() {
		return drawables;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setDefaultProfilePicture(SafeBitmapDrawable defaultProfilePicture) {
		this.defaultProfilePicture = defaultProfilePicture;
	}

	public void onFacebookChanged() {
		if(SocializeUI.getInstance().isFacebookSupported() && isLoggedOnUser) {
			if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				facebookSignOutButton.setVisibility(View.VISIBLE);
				facebookSignInButton.setVisibility(View.GONE);
				autoPostFacebook.setVisibility(View.VISIBLE);
			}
			else {
				facebookSignOutButton.setVisibility(View.GONE);
				facebookSignInButton.setVisibility(View.VISIBLE);
				autoPostFacebook.setVisibility(View.GONE);
			}
		}
		else {
			facebookSignOutButton.setVisibility(View.GONE);
			facebookSignInButton.setVisibility(View.GONE);
			autoPostFacebook.setVisibility(View.GONE);
		}
	}
	
	public void onProfilePictureChange(Bitmap bitmap) {
		if(bitmap != null) {
			if(tmpProfileImage != null && !tmpProfileImage.isRecycled()) {
				tmpProfileImage.recycle();
			}
			tmpProfileImage = bitmap;
			profileDrawable = new SafeBitmapDrawable(bitmap);
			onEdit();
		}
	}
	
	public boolean getUpdatedAutoPostFBPreference() {
		return autoPostFacebook.isChecked();
	}
	
	public void setAutoPostFacebook(AutoPostFacebookOption autoPostFacebook) {
		this.autoPostFacebook = autoPostFacebook;
	}

	/**
	 * Returns the newly updated user profile name.
	 * @return
	 */
	public String getUpdatedUserDisplayName() {
		return displayNameEdit.getText().toString();
	}
	
	/**
	 * Returns the newly update profile picture.
	 * @return
	 */
	public Bitmap getUpdatedProfileImage() {
		return tmpProfileImage;
	}
	

	public void setContextMenu(ProfileImageContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}
	
	public AutoPostFacebookOption getAutoPostFacebook() {
		return autoPostFacebook;
	}

	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		onFacebookChanged();
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public boolean isLoggedOnUser() {
		return isLoggedOnUser;
	}

	public void setLoggedOnUser(boolean isLoggedOnUser) {
		this.isLoggedOnUser = isLoggedOnUser;
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

	public void setProfileEditButtonFactory(IBeanFactory<SocializeButton> profileEditButtonFactory) {
		this.profileEditButtonFactory = profileEditButtonFactory;
	}

	public void setFacebookSignOutButtonFactory(IBeanFactory<SocializeButton> facebookSignOutButtonFactory) {
		this.facebookSignOutButtonFactory = facebookSignOutButtonFactory;
	}

	public void setFacebookSignInButtonFactory(IBeanFactory<FacebookButton> facebookSignInButtonFactory) {
		this.facebookSignInButtonFactory = facebookSignInButtonFactory;
	}

	public void setProfileSaveButtonListenerFactory(IBeanFactory<ProfileSaveButtonListener> profileSaveButtonListenerFactory) {
		this.profileSaveButtonListenerFactory = profileSaveButtonListenerFactory;
	}

	public void setFacebookSignOutClickListenerFactory(IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory) {
		this.facebookSignOutClickListenerFactory = facebookSignOutClickListenerFactory;
	}

	public void setProfileImageContextMenuFactory(IBeanFactory<ProfileImageContextMenu> profileImageContextMenuFactory) {
		this.profileImageContextMenuFactory = profileImageContextMenuFactory;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	
	
	public void setUserDetails(User user) {
		
		String profilePicData = user.getMediumImageUri();
		final ImageView userIcon = getProfilePicture();
		
		if(!StringUtils.isEmpty(profilePicData)) {
			userIcon.getBackground().setAlpha(64);
			
			imageLoader.loadImage(user.getId(), profilePicData, new ImageLoadListener() {
				@Override
				public void onImageLoadFail(ImageLoadRequest request, Exception error) {
					error.printStackTrace();
					userIcon.post(new Runnable() {
						public void run() {
							userIcon.setImageDrawable(defaultProfilePicture);
							userIcon.getBackground().setAlpha(255);
						}
					});
				}
				
				@Override
				public void onImageLoad(ImageLoadRequest request, final SafeBitmapDrawable drawable, boolean async) {
					// Must be run on UI thread
					userIcon.post(new Runnable() {
						public void run() {
							drawable.setAlpha(255);
							setProfileDrawable(drawable);
							userIcon.setImageDrawable(drawable);
							userIcon.getBackground().setAlpha(255);
						}
					});
				}
			});
		}
		else {
			userIcon.setImageDrawable(defaultProfilePicture);
			userIcon.getBackground().setAlpha(255);
			setProfileDrawable(defaultProfilePicture);
		}
		
		getDisplayName().setText(user.getDisplayName());
		
		User currentUser = userService.getCurrentUser();
		
		setCurrentUser(currentUser);
		
		if(currentUser != null && currentUser.getId().equals(user.getId())) {
			setLoggedOnUser(true);
			setUserDisplayName(user.getDisplayName());
			getEditButton().setVisibility(View.VISIBLE);
			
			if(SocializeUI.getInstance().isFacebookSupported() &&
					Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				getFacebookSignOutButton().setVisibility(View.VISIBLE);
				getAutoPostFacebook().setVisibility(View.VISIBLE);
				getAutoPostFacebook().setChecked(user.isAutoPostToFacebook());
			}
			
			onFacebookChanged();
		}
		else {
			setLoggedOnUser(false);
			getDisplayNameEdit().setVisibility(View.GONE);
			getEditButton().setVisibility(View.GONE);
			getFacebookSignOutButton().setVisibility(View.GONE);
			getAutoPostFacebook().setVisibility(View.GONE);
		}
	}	

	/**
	 * Called when this control is instructed to enter "edit" mode.
	 */
	public void onEdit() {
		if(profileDrawable != null && isLoggedOnUser) {
			displayName.setVisibility(View.GONE);
			editButton.setVisibility(View.GONE);
			facebookSignOutButton.setVisibility(View.GONE);
			facebookSignInButton.setVisibility(View.GONE);
			
			displayNameEdit.setVisibility(View.VISIBLE);
			saveButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			
			displayNameEdit.selectAll();
			
			Drawable[] layers = new Drawable[2];
			layers[0] = profileDrawable;
			layers[1] = drawables.getDrawable("camera.png", DisplayMetrics.DENSITY_DEFAULT, true);
			
			layers[0].setAlpha(64);
			
			LayerDrawable layerDrawable = new LayerDrawable(layers);
			profilePicture.setImageDrawable(layerDrawable);
			profilePicture.getBackground().setAlpha(0);
			
			autoPostFacebook.setEnabled(true);
			
			editMode = true;
		}
	}
	
	public void onCancel() {
		displayNameEdit.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		displayName.setVisibility(View.VISIBLE);
		
		if(isLoggedOnUser) {
			editButton.setVisibility(View.VISIBLE);
		}
		
		onFacebookChanged();
	
		if(tmpProfileImage != null && !tmpProfileImage.isRecycled()) {
			tmpProfileImage.recycle();
			tmpProfileImage = null;
		}
		
		profileDrawable = originalProfileDrawable;
		profileDrawable.setAlpha(255);
		
		profilePicture.setImageDrawable(profileDrawable);
		profilePicture.getBackground().setAlpha(255);
		
		revertUserDisplayName();
		
		autoPostFacebook.setEnabled(false);
		
		editMode = false;
	}
	
	public void onSave(User user) {
		
		displayName.setText(user.getDisplayName());
		
		displayNameEdit.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		displayName.setVisibility(View.VISIBLE);
		
		if(isLoggedOnUser) {
			editButton.setVisibility(View.VISIBLE);
		}		
		
		onFacebookChanged();
		
		profileDrawable.setAlpha(255);
		
		if(originalProfileDrawable != null && originalProfileDrawable != profileDrawable) {
			// Recycle
			originalProfileDrawable.recycle();
		}
		
		// Make sure we don't recycle temp
		tmpProfileImage = null;
		
		originalProfileDrawable = profileDrawable;
		
		profilePicture.setImageDrawable(getProfileDrawable());
		profilePicture.getBackground().setAlpha(255);
		
		autoPostFacebook.setEnabled(false);
		
		editMode = false;
	}
	
	public void onImageEdit() {
		if(editMode) {
			contextMenu.show();
		}
	}
}
