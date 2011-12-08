package com.socialize.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import com.socialize.api.action.ShareType;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class SmsShareClickListener extends BaseShareClickListener {
	
	public SmsShareClickListener(ActionBarView actionBarView) {
		super(actionBarView);
	}


	public SmsShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
		super(actionBarView, commentView, onActionBarEventListener);
	}


	@Override
	protected void doShare(Activity parent, String title, String subject, String body, String comment) {
		Intent sendIntent = getIntent();
		sendIntent.putExtra("sms_body", body); 
		parent.startActivity(sendIntent);
	}
	
	protected Intent getIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setType("vnd.android-dir/mms-sms");
		return sendIntent;
	}

	@Override
	public boolean isAvailableOnDevice(Activity parent) {
		return isAvailable(parent, getIntent());
	}

	@Override
	protected boolean isHtml() {
		return false;
	}
	
	@Override
	protected boolean isIncludeSocialize() {
		return true;
	}
	
	@Override
	protected ShareType getShareType() {
		return ShareType.SMS;
	}
}
