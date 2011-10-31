package com.socialize.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public abstract class EntityView extends AuthenticatedView {

	public EntityView(Context context) {
		super(context);
	}

	public EntityView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public View getView() {
		
		Context context = getViewContext();
		
		Bundle bundle = null;
		
		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}
		
		if(bundle != null) {
			
			String[] entityKeys = getEntityKeys();
			
			if(entityKeys != null && entityKeys.length > 0) {
				Object[] values = new String[entityKeys.length];
				for (int i = 0; i < entityKeys.length; i++) {
					values[i] = bundle.getString(entityKeys[i]);
				}
				return getView(bundle, values);
			}
			else {
				return getView(bundle, (Object[]) null);
			}
		}
		
		return getErrorView(context);
	}
	
	protected View getErrorView(Context context) {
		TextView error = new TextView(context);
		error.setText("Socialize Error! No entity url specified");
		return error;
	}
	
	protected abstract View getView(Bundle bundle, Object...entityKeys);
	
	protected abstract String[] getEntityKeys();
}
