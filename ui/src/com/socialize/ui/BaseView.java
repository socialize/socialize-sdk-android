package com.socialize.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.socialize.ui.error.SocializeUIErrorHandler;

public abstract class BaseView extends LinearLayout {
	
	private SocializeUIErrorHandler errorHandler;
	
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

	@Deprecated
	public void showError(Context context, String message) {
		if(errorHandler != null) {
			errorHandler.handleError(context, message);
		}
	}

	public SocializeUIErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(SocializeUIErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
}
