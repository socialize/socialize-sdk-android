package com.socialize.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.socialize.util.StringUtils;

public abstract class EntityView extends AuthenticatedView {

	public EntityView(Context context) {
		super(context);
	}

	public EntityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public View getView() {
		Bundle bundle = null;
		
		Context context = getViewContext();
		
		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}
		
		if(bundle != null) {
			String entityKey = bundle.getString(getEntityKey());
			if(!StringUtils.isEmpty(entityKey)) {
				return getView(bundle, entityKey);
			}
		}
		
		return getErrorView(context);
	}
	
	protected View getErrorView(Context context) {
		TextView error = new TextView(context);
		error.setText("Socialize Error! No entity url specified");
		return error;
	}
	
	protected abstract View getView(Bundle bundle, String entityKey);
	
	protected abstract String getEntityKey();
}
