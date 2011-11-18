package com.socialize.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.user.UserService;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.BitmapUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class ProfileLayoutView extends BaseView {

	private String userId;
	
	private Drawables drawables;
	private UserService userService;
	
	private ViewFactory<ProfileHeader> profileHeaderFactory;
	private ProfileContentViewFactory profileContentViewFactory;
	
	private ProfileHeader header;
	private ProfileContentView content;
	private ProgressDialog dialog = null;
	private ProgressDialogFactory progressDialogFactory;
	
	private ImageLoader imageLoader;
	private BitmapUtils bitmapUtils;
	
	private SafeBitmapDrawable defaultProfilePicture;
	
	public ProfileLayoutView(Activity context, String userId) {
		this(context);
		this.userId = userId;
		
		if(this.userId != null) {
			this.userId = this.userId.trim();
		}
	}
	
	public ProfileLayoutView(Context context) {
		super(context);
	}

	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		setPadding(0, 0, 0, 0);
		setVerticalFadingEdgeEnabled(false);

		header = profileHeaderFactory.make(getContext());
		content = profileContentViewFactory.make(getContext());
		defaultProfilePicture = (SafeBitmapDrawable) drawables.getDrawable("large_user_icon.png");

		addView(header);
		addView(content);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		if(getSocialize().isAuthenticated()) {
			doGetUserProfile();
		}
		else {
			showError(getContext(), new SocializeException("Socialize not authenticated"));
		}
	}

	public void doGetUserProfile() {
		int id = Integer.parseInt(userId);
		dialog = progressDialogFactory.show(getContext(), "Loading", "Please wait...");
		
		getSocialize().getUser(id, new UserGetListener() {
			
			@Override
			public void onGet(User user) {
				
				// Merge the current session user
				User sessionUser = Socialize.getSocialize().getSession().getUser();
				
				if(sessionUser.getId().equals(user.getId())) {
					sessionUser.merge(user);
					user = sessionUser;
				}
				
				// Set the user details into the view elements
				setUserDetails(user);
				
				if(dialog != null) {
					dialog.dismiss();
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error);
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	
	/**
	 * Called when the profile picture has been changed by the user.
	 * @param bitmap
	 */
	public void onImageChange(Bitmap bitmap) {
		if(bitmap != null) {
			Bitmap scaled = bitmapUtils.getScaledBitmap(bitmap, 200, 200);
			content.onProfilePictureChange(scaled);
		}
	}
	
	public void setUserDetails(User user) {
		
		String profilePicData = user.getMediumImageUri();
		final ImageView userIcon = content.getProfilePicture();
		
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
							content.setProfileDrawable(drawable);
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
			content.setProfileDrawable(defaultProfilePicture);
		}
		
		content.getDisplayName().setText(user.getDisplayName());
		
		User currentUser = userService.getCurrentUser();
		
		if(currentUser != null && currentUser.getId().equals(user.getId())) {
			content.setUserDisplayName(user.getDisplayName());
			content.getEditButton().setVisibility(View.VISIBLE);
			
			if(SocializeUI.getInstance().isFacebookSupported() &&
					Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				content.getFacebookSignOutButton().setVisibility(View.VISIBLE);
				content.getAutoPostFacebook().setVisibility(View.VISIBLE);
				content.getAutoPostFacebook().setChecked(user.isAutoPostToFacebook());
			}
		}
		else {
			content.getDisplayNameEdit().setVisibility(View.GONE);
			content.getEditButton().setVisibility(View.GONE);
			content.getFacebookSignOutButton().setVisibility(View.GONE);
			content.getAutoPostFacebook().setVisibility(View.GONE);
		}
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setUserId(String entityKey) {
		this.userId = entityKey;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setProfileHeaderFactory(ViewFactory<ProfileHeader> profileHeaderFactory) {
		this.profileHeaderFactory = profileHeaderFactory;
	}

	public void setProfileContentViewFactory(ProfileContentViewFactory profileContentViewFactory) {
		this.profileContentViewFactory = profileContentViewFactory;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
	
	
}
