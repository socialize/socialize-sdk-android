package com.socialize.ui.view;

import com.socialize.log.SocializeLogger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
		
		try {
			Context context = getViewContext();
			
			Bundle bundle = null;
			
			if(context instanceof Activity) {
				Activity a = (Activity) context;
				bundle = a.getIntent().getExtras();
			}
			
			if(bundle != null) {
				String[] entityKeys = getEntityKeys();
				if(entityKeys != null && entityKeys.length > 0) {
					Object[] values = new Object[entityKeys.length];
					for (int i = 0; i < entityKeys.length; i++) {
						values[i] = bundle.get(entityKeys[i]);
					}
					return getView(bundle, values);
				}
			}
			
			return getView(bundle, (Object[]) null);
		} 
		catch (Throwable e) {
			Log.e(SocializeLogger.LOG_TAG, "", e);
			return getErrorView();
		}
	}
	
	protected View getErrorView() {
		
		FrameLayout view = new FrameLayout(getContext());
		
		FrameLayout.LayoutParams loadingScreenLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
		FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		loadingScreenLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		setLayoutParams(loadingScreenLayoutParams);
		
		TextView errorView = new TextView(getContext());
		
		errorView.setText("Unable to load view");
		
		view.addView(errorView);			
		
		return view;
	}
	
	protected abstract View getView(Bundle bundle, Object...entityKeys);
	
	protected abstract String[] getEntityKeys();
}
