package com.socialize.notifications;

import android.content.Context;
import android.content.Intent;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.log.SocializeLogger;

public class SocializeC2DMReceiver extends BaseC2DMReceiver {
	
	private SocializeLogger logger;
	private NotificationContainer container;
	private C2DMCallback notificationCallback;
	
	// Must be parameterless constructor
	public SocializeC2DMReceiver() {
		super("SocializeC2DMReceiver");
		logger = newSocializeLogger();
		container = newNotificationContainer();
	}
	
	protected void initBeans() {
		IOCContainer ioc = container.getContainer();
		if(ioc != null) {
			logger = ioc.getBean("logger");
			notificationCallback = ioc.getBean("notificationCallback");
		}
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		try {
			if(notificationCallback != null) {
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("SocializeC2DMReceiver received message");
				}					
				notificationCallback.onMessage(getContext(), intent.getExtras());
			}
			else {
				logWarn("No notificationCallback foind in C2DM receiver.  Initialization may have failed.");
			}
		} 
		catch (Exception e) {
			logError("Error processing C2DM message", e);
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		if(logger != null) {
			logger.error("C2DM registration failed with error: " + errorId);
		}
//		if(assertInitialized()) {
			
			if(notificationCallback != null) {
				notificationCallback.onError(context, errorId);
			}
			else {
				logWarn("No notificationCallback found in C2DM receiver.  Initialization may have failed.");
			}			
//		}
	}
	
	@Override
	public void onRegistrered(Context context, String registrationId)  {
		try {
			if(notificationCallback != null) {
				notificationCallback.onRegister(context, registrationId);
			}
			else {
				logWarn("No notificationCallback found in C2DM receiver.  Initialization may have failed.");
			}	
		} 
		catch (Exception e) {
			logError("C2DM registration failed", e);
		}
	}

	@Override
	public void onUnregistered(Context context) {
		if(notificationCallback != null) {
			notificationCallback.onUnregister(context);
			
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("SocializeC2DMReceiver successfully unregistered");
			}
		}
		else {
			logWarn("No notificationCallback found in C2DM receiver.  Initialization may have failed.");
		}	
	}

	@Override
	public void onCreate() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("SocializeC2DMReceiver creating..");
		}
		
		try {
			container.onCreate(getContext());
			initBeans();
		}
		catch (Exception e) {
			logError("Error initializing C2DM receiver!", e);
		}
		
		super.onCreate();
	}
	
	protected void logError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			e.printStackTrace();
		}
	}
	
	protected void logWarn(String msg) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
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
	
	// So we can mock.	
	protected SocializeLogger newSocializeLogger() {
		return new SocializeLogger();
	}
	
	protected void setNotificationCallback(C2DMCallback notificationCallback) {
		this.notificationCallback = notificationCallback;
	}

	@Override
	public void onDestroy() {
		if(container != null) {
			container.onDestroy(getContext());
		}		
		superOnDestroy();
	}
	
	// So we can mock
	protected void superOnDestroy() {
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
