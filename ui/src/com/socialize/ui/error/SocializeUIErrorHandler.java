package com.socialize.ui.error;

import android.content.Context;

public interface SocializeUIErrorHandler {

	@Deprecated
	public void handleError(Context context, String message);
	
	public void handleError(Context context, Exception e);
}
