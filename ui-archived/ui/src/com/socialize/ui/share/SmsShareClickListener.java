package com.socialize.ui.share;

import android.app.Activity;
import android.content.Intent;

import com.socialize.api.action.ShareType;
import com.socialize.ui.actionbar.ActionBarView;

public class SmsShareClickListener extends ShareClickListener {
	
	public SmsShareClickListener(ActionBarView actionBarView) {
		super(actionBarView);
	}

	@Override
	protected void doShare(Activity parent, String title, String subject, String body, String comment) {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", body); 
		sendIntent.setType("vnd.android-dir/mms-sms");
		parent.startActivity(sendIntent);
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
