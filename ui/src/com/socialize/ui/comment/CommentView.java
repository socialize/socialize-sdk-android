package com.socialize.ui.comment;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

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
	protected void onBeforeSocializeInit() {
		progress = ProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
	}

	@Override
	public void onAfterAuthenticate() {
		if(progress != null) {
			progress.dismiss();
		}
	}
	
	@Override
	protected String getEntityKey() {
		return SocializeUI.ENTITY_KEY;
	}
}
