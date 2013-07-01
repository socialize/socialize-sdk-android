package com.socialize.ui.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.view.EntityView;

public class ProfileView extends EntityView {
	
	private ProfileLayoutView profileLayoutView = null;
	
	public ProfileView(Activity context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, Object...entityKeys) {
		if (entityKeys != null) {
			if(profileLayoutView == null) {
				profileLayoutView = container.getBean("profileLayoutView", entityKeys);
			}
			return profileLayoutView;
		}
		else {
			SocializeLogger.e("No user id specified for " + getClass().getSimpleName());
			return null;
		}			
	}

	@Override
	protected String[] getBundleKeys() {
		return new String[]{Socialize.USER_ID};
	}
	
	/**
	 * Called when the user's profile image is changed.
	 * @param bitmap The image.
	 */
	public void onImageChange(Bitmap bitmap, String localPath) {
		if(profileLayoutView != null) {
			profileLayoutView.onImageChange(bitmap);
		}
		
		// Set the image in the user settings
		try {
			UserUtils.getUserSettings(getContext()).setLocalImagePath(localPath);
		}
		catch (SocializeException e) {
			Log.e(SocializeLogger.LOG_TAG, "Error getting user settings", e);
		}
	}
	
	@Override
	public View getLoadingView() {
		return null;
	}
}
