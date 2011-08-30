package com.socialize.ui.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class CommentView extends EntityView {
	
	private Dialog progress;
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CommentView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, String entityKey) {
		return container.getBean("commentList", entityKey);
	}

	@Override
	public boolean isRequires3rdPartyAuth() {
		return false;
	}

	@Override
	protected void onBeforeSocializeInit() {
		progress = ProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
	}

	@Override
	public void onAfterAuthenticate() {
		if(progress != null) {
			progress.dismiss();
		}
	}
}
