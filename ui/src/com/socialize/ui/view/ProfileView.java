package com.socialize.ui.view;

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
		return container.getBean("profileView", entityKey);
	}

	@Override
	public boolean isRequires3rdPartyAuth() {
		return true;
	}
}
