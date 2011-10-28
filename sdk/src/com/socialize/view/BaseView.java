package com.socialize.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.error.SocializeErrorHandler;

public abstract class BaseView extends LinearLayout {
	
	private SocializeErrorHandler errorHandler;
	
	private int loadCount = 0;
	
	private boolean loaded = false;
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseView(Context context) {
		super(context);
	}
	
	public void showError(Context context, Exception e) {
		if(errorHandler != null) {
			errorHandler.handleError(context, e);
		}
	}

	public SocializeErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(SocializeErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected Activity getActivity() {
		Context context = getContext();
		if(context instanceof Activity) {
			return (Activity) context;
		}
		return null;
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		
		if(!isInEditMode()) {
			if(visibility == VISIBLE) {
				if(!checkLoaded()) {
					onViewLoad();
				}
				else {
					onViewUpdate();
				}
			}
			else if(visibility == View.INVISIBLE || visibility == View.GONE)
			{
				decrementLoaded();
			}
		}
		else {
			// Add the default Socialize View for display
			View editView = getEditModeView();
			if(editView != null) {
				addView(editView);
			}
		}
	}
	
	protected void incrementLoaded() {
		loadCount++;
	}
	
	protected void decrementLoaded() {
		if(loadCount > 0) loadCount--;
	}
	
	protected boolean checkLoaded() {
		boolean loaded = (loadCount > 0);
		incrementLoaded();
		return loaded;
	}
	
	// Subclasses override
	protected View getEditModeView() {
		return null;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public void assignId(View parent) {
		setId(getNextViewId(parent));
	}

	protected void onViewUpdate() {}

	protected void onViewLoad() {
		loaded = true;
	}
	
	protected int getNextViewId(View parent) {
		int id = Integer.MAX_VALUE;
		
		if(parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) parent;
			id = 0;
			int childCount = group.getChildCount();
			
			for (int i = 0; i < childCount; i++) {
				View child = group.getChildAt(i);
				int childId = child.getId();
				if(childId > id) {
					id = childId;
				}
			}
			
			if(id < Integer.MAX_VALUE) {
				id++;
			}
		}
		
		return id;
	}	
}
