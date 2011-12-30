package com.socialize.notifications;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;

public class SocializeC2DMReceiver extends C2DMBaseReceiver {
	
	private SocializeLogger logger;
	private SocializeConfig config;
	private NotificationContainer container;
	private NotificationSystem notificationSystem;
	
	
	// Must be parameterless constructor
	public SocializeC2DMReceiver() {
		super("SocializeC2DMReceiver");
		logger = new SocializeLogger();
		container = newNotificationContainer();
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("SocializeC2DMReceiver received message");
		}		
		
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
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("SocializeC2DMReceiver successfully registered: " + registrationId);
		}
	}

	@Override
	public void onUnregistered(Context context) {
		super.onUnregistered(context);
		if(notificationSystem != null) {
			notificationSystem.onUnregister(context);
		}
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("SocializeC2DMReceiver successfully unregistered");
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("SocializeC2DMReceiver creating..");
		}
		
		try {
			container.onCreate(getContext());
			initBeans();
			
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("SocializeC2DMReceiver created");
			}			
			
			super.setSenderId(config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID));
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
			config = ioc.getBean("config");
			notificationSystem = ioc.getBean("notificationSystem");
		}
	}

	@Override
	public void onDestroy() {
		if(container != null) {
			container.onDestroy(getContext());
		}		
		
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
