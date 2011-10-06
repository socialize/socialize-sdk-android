package com.socialize.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
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
}
