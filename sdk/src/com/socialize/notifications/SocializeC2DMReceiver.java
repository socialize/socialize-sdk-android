package com.socialize.notifications;

import android.content.Context;
import android.content.Intent;

public class SocializeC2DMReceiver extends BaseC2DMReceiver {
	
	private C2DMReceiverHandler handler;
	
	// Must be parameterless constructor
	public SocializeC2DMReceiver() {
		super("SocializeC2DMReceiver");
		handler = newC2DMReceiverHandler();
	}
	
	protected C2DMReceiverHandler newC2DMReceiverHandler() {
		return new SocializeC2DMReceiverHandler();
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		handler.onMessage(context, intent);
	}

	@Override
	public void onError(Context context, String errorId) {
		handler.onError(context, errorId);	
	}
	
	@Override
	public void onRegistrered(Context context, String registrationId)  {
		handler.onRegistered(context, registrationId);
	}

	@Override
	public void onUnregistered(Context context) {
		handler.onUnregistered(context);	
	}

	@Override
	public void onCreate() {

		handler.onCreate(getContext());
		super.onCreate();
	}
	
	// So we can mock.
	protected Context getContext() {
		return this;
	}
	

	@Override
	public void onDestroy() {
		handler.onDestroy(getContext());
		superOnDestroy();
	}
	
	// So we can mock
	protected void superOnDestroy() {
		super.onDestroy();
	}
}
