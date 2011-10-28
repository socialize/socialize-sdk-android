package com.socialize.error;

import android.content.Context;

public interface SocializeErrorHandler {
	public void handleError(Context context, Exception e);
}
