package com.socialize.ui.profile;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ProfileView extends EntityView {
	
	private ProfileLayoutView profileLayoutView = null;
	
	public ProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ProfileView(Context context) {
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
			Log.e("Socialize", "No user id specified for " + getClass().getSimpleName());
			return null;
		}			
	}

	@Override
	protected String[] getEntityKeys() {
		return new String[]{SocializeUI.USER_ID};
	}
	
	/**
	 * Called when the user's profile image is changed.
	 * @param bitmap
	 */
	public void onImageChange(Bitmap bitmap) {
		profileLayoutView.onImageChange(bitmap);
	}
	
	@Override
	public View getLoadingView() {
		return null;
	}
}
