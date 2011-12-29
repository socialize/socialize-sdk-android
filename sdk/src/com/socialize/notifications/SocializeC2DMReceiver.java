package com.socialize.notifications;

import android.content.Context;
import android.content.Intent;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.socialize.log.SocializeLogger;

public class SocializeC2DMReceiver extends C2DMBaseReceiver {
	
	private SocializeLogger logger;

	public SocializeC2DMReceiver(String senderId) {
		super(senderId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
	}

	@Override
	public void onError(Context context, String errorId) {
		
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	

}
