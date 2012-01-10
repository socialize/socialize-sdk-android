package com.socialize.ui.profile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.socialize.Socialize;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.view.EntityView;
import com.socialize.util.Drawables;

public class ProfileView extends EntityView {
	
	private ProfileLayoutView profileLayoutView = null;
	private View view;
	
	public ProfileView(Activity context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, Object...entityKeys) {
		if (entityKeys != null) {
			if(profileLayoutView == null) {
				profileLayoutView = container.getBean("profileLayoutView", entityKeys);
				
				LayoutParams scrollViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
				LayoutParams childViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
				
				ScrollView scrollView = new ScrollView(getContext());
				scrollView.setFillViewport(true);
				scrollView.setLayoutParams(scrollViewLayout);
				scrollView.addView(profileLayoutView, childViewLayout);	
				
				LinearLayout layout = new LinearLayout(getContext());
				LayoutParams masterParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
				
				layout.setLayoutParams(masterParams);
				layout.setBackgroundDrawable(((Drawables)container.getBean("drawables")).getDrawable("slate.png", true, true, true));
				
				layout.addView(scrollView);
				
				view = layout;				
			}
			
			return view;
		}
		else {
			Log.e(SocializeLogger.LOG_TAG, "No user id specified for " + getClass().getSimpleName());
			return null;
		}			
	}

	@Override
	protected String[] getBundleKeys() {
		return new String[]{Socialize.USER_ID};
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
