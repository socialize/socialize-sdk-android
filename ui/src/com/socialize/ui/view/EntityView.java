package com.socialize.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.socialize.ui.SocializeAuthenticatedView;
import com.socialize.ui.SocializeUI;
import com.socialize.util.StringUtils;

public abstract class EntityView extends SocializeAuthenticatedView {

	
	public EntityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View getView() {
		Bundle bundle = null;
		
		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}
		
		if(bundle != null && !StringUtils.isEmpty(SocializeUI.ENTITY_KEY)) {
			String entityKey = bundle.getString(SocializeUI.ENTITY_KEY);
			
			if(!StringUtils.isEmpty(entityKey)) {
				return getView(bundle, entityKey);
			}
		}
		
		TextView error = new TextView(context);
		error.setText("Socialize Error! No entity url specified");
		return error;
	}
	
	protected abstract View getView(Bundle bundle, String entityKey);
}
