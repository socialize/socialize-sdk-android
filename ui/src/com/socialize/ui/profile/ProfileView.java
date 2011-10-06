package com.socialize.ui.profile;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class ProfileView extends EntityView {
	
	ProfileLayoutView profileLayoutView = null;
	
	public ProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ProfileView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, String entityKey) {
		if(profileLayoutView == null) {
			profileLayoutView = container.getBean("profileLayoutView", entityKey);
		}
		return profileLayoutView;
	}

	@Override
	protected String getEntityKey() {
		return SocializeUI.USER_ID;
	}
	
	/**
	 * Called when the user's profile image is changed.
	 * @param bitmap
	 */
	public void onImageChange(Bitmap bitmap) {
		profileLayoutView.onImageChange(bitmap);
	}
}
