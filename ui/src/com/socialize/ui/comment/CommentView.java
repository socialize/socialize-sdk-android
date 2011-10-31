package com.socialize.ui.comment;

import com.socialize.android.ioc.IOCContainer;
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
	private CommentListView commentListView;
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CommentView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, Object...entityKey) {
		if(commentListView == null) {
			commentListView =  container.getBean("commentList", entityKey[0]);
		}
		return commentListView;
	}

	@Override
	protected void onBeforeSocializeInit() {
		progress = ProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
	}

	@Override
	public void onAfterAuthenticate(IOCContainer container) {
		if(progress != null) {
			progress.dismiss();
		}
	}
	
	@Override
	protected String[] getEntityKeys() {
		return new String[]{SocializeUI.ENTITY_KEY};
	}

	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		commentListView.onProfileUpdate();
	}
}
