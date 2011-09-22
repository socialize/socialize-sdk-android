package com.socialize.ui.profile;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class ProfileView extends EntityView {
	public ProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ProfileView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, String entityKey) {
		return container.getBean("profileLayoutView", entityKey);
	}

	@Override
	protected String getEntityKey() {
		return SocializeUI.USER_ID;
	}
}
