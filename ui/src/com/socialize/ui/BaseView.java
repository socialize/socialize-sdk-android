package com.socialize.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.ui.actionbar.ActionBarEditView;
import com.socialize.ui.error.SocializeUIErrorHandler;

public abstract class BaseView extends LinearLayout {
	
	private SocializeUIErrorHandler errorHandler;
	
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

	public SocializeUIErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(SocializeUIErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
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
			else if(visibility == View.INVISIBLE)
			{
				if(loadCount > 0) loadCount--;
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
	
	private boolean checkLoaded() {
		boolean loaded = (loadCount > 0);
		loadCount++;
		return loaded;
	}
	
	// Subclasses override
	protected View getEditModeView() {
		return new ActionBarEditView(getContext());
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	protected void onViewUpdate() {}

	protected void onViewLoad() {
		loaded = true;
	}
}
