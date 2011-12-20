package com.socialize.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.util.BitmapUtils;
import com.socialize.view.BaseView;

public class ProfileLayoutView extends BaseView {

	private String userId;
	
	private IBeanFactory<SocializeHeader> profileHeaderFactory;
	private IBeanFactory<ProfileContentView> profileContentViewFactory;
	
	private SocializeHeader header;
	private ProfileContentView content;
	private ProgressDialog dialog = null;
	private ProgressDialogFactory progressDialogFactory;
	private BitmapUtils bitmapUtils;
	
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
//		setBackgroundDrawable(drawables.getDrawable("slate.png", true, true, true));
		setPadding(0, 0, 0, 0);
//		setVerticalFadingEdgeEnabled(false);

		header = profileHeaderFactory.getBean();
		content = profileContentViewFactory.getBean(this);

		addView(header);
		addView(content);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		if(getSocialize().isAuthenticated()) {
			doGetUserProfile();
		}
		else {
			showError(getContext(), new SocializeException("Socialize not authenticated"));
		}
	}

	public void doGetUserProfile() {
		long id = Long.parseLong(userId);
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
		content.setUserDetails(user);
	}
	
	public void setUserId(String entityKey) {
		this.userId = entityKey;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setProfileHeaderFactory(IBeanFactory<SocializeHeader> profileHeaderFactory) {
		this.profileHeaderFactory = profileHeaderFactory;
	}

	public void setProfileContentViewFactory(IBeanFactory<ProfileContentView> profileContentViewFactory) {
		this.profileContentViewFactory = profileContentViewFactory;
	}

	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
}
