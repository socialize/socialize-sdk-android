package com.socialize.notifications;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.log.SocializeLogger;

public class SocializeC2DMReceiver extends C2DMBaseReceiver {
	
	private SocializeLogger logger;
	private NotificationContainer container;
	private NotificationSystem notificationSystem;
	
	private String registrationId;

	public SocializeC2DMReceiver(String senderId) {
		super(senderId);
		logger = new SocializeLogger();
		container = newNotificationContainer();
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		if(notificationSystem != null) {
			notificationSystem.onMessage(getContext(), intent.getExtras());
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		if(logger != null) {
			logger.error("C2DM registration failed with error code: " + errorId);
		}
	}

	@Override
	public void onRegistrered(Context context, String registrationId) throws IOException {
		super.onRegistrered(context, registrationId);
		
		if(notificationSystem != null) {
			notificationSystem.onRegister(context, registrationId);
		}
		else {
			// Just keep this for later
			this.registrationId = registrationId; 
		}
	}

	@Override
	public void onUnregistered(Context context) {
		super.onUnregistered(context);
		if(notificationSystem != null) {
			notificationSystem.onUnregister(context);
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			container.onCreate(getContext());
			initBeans();
		} 
		catch (Exception e) {
			if(logger != null) {
				logger.error("Failed to initialize container",e);
			}
		}
	}
	
	// So we can mock.
	protected Context getContext() {
		return this;
	}
	
	// So we can mock.	
	protected NotificationContainer newNotificationContainer() {
		return new NotificationContainer();
	}
	
	protected void initBeans() {
		IOCContainer ioc = container.getContainer();
		if(ioc != null) {
			logger = ioc.getBean("logger");
			notificationSystem = ioc.getBean("notificationSystem");
			
			if(registrationId != null) {
				notificationSystem.onRegister(getContext(), registrationId);
			}
		}
	}

	@Override
	public void onDestroy() {
		if(container != null) {
			container.onDestroy(getContext());
		}		
		
		registrationId = null;
		super.onDestroy();
	}
	
	/**
	 * Expert only (not documented)
	 * @return
	 */
	NotificationContainer getNotificationContainer() {
		return container;
	}
}
