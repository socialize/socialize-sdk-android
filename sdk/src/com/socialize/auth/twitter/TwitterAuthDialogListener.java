package com.socialize.auth.twitter;

import android.app.Dialog;
import com.socialize.error.SocializeException;

public abstract class TwitterAuthDialogListener implements TwitterAuthListener {
	
	private Dialog dialog;

	public TwitterAuthDialogListener(Dialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void onAuthSuccess(String token, String secret, String screenName, String userId) {
		onAuthSuccess(dialog, token, secret, screenName, userId);
	}

	@Override
	public void onError(SocializeException e) {
		onError(dialog, e);
	}

	@Override
	public void onCancel() {
		onCancel(dialog);
	}
	
	public abstract void onAuthSuccess(Dialog dialog, String token, String secret, String screenName, String userId);
	public abstract void onError(Dialog dialog, Exception e);
	public abstract void onCancel(Dialog dialog);

}
