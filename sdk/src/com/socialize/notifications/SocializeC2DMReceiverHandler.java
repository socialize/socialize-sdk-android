package com.socialize.notifications;

import android.content.Context;
import android.content.Intent;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.log.SocializeLogger;

public class SocializeC2DMReceiverHandler implements C2DMReceiverHandler {
	
	private SocializeLogger logger;
	private NotificationContainer container;
	private C2DMCallback notificationCallback;
	
	// Must be parameterless constructor
	public SocializeC2DMReceiverHandler() {
		this.logger = newSocializeLogger();
		this.container = newNotificationContainer();
	}
	
	protected void initBeans() {
		IOCContainer ioc = container.getContainer();
		if(ioc != null) {
			logger = ioc.getBean("logger");
			notificationCallback = ioc.getBean("notificationCallback");
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onMessage(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onMessage(Context context, Intent intent) {
		try {
			if(notificationCallback != null) {
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("SocializeC2DMReceiver received message");
				}					
				notificationCallback.onMessage(context, intent.getExtras());
			}
			else {
				logWarn("No notificationCallback foind in C2DM receiver.  Initialization may have failed.");
			}
		} 
		catch (Exception e) {
			logError("Error processing C2DM message", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onError(android.content.Context, java.lang.String)
	 */
	@Override
	public void onError(Context context, String errorId) {
		if(logger != null) {
			logger.error("C2DM registration failed with error: " + errorId);
		}
		
		if(notificationCallback != null) {
			notificationCallback.onError(context, errorId);
		}
		else {
			logWarn("No notificationCallback found in C2DM receiver.  Initialization may have failed.");
		}			
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onRegistrered(android.content.Context, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onUnregistered(android.content.Context)
	 */
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

	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onCreate()
	 */
	@Override
	public void onCreate(Context context) {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("SocializeC2DMReceiver creating..");
		}
		
		try {
			container.onCreate(context);
			initBeans();
		}
		catch (Exception e) {
			logError("Error initializing C2DM receiver!", e);
		}
	}
	
	protected void logError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(msg, e);
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

	/* (non-Javadoc)
	 * @see com.socialize.notifications.C2DMReceiverHandler#onDestroy()
	 */
	@Override
	public void onDestroy(Context context) {
		if(container != null) {
			container.onDestroy(context);
		}		
	}
	
	/**
	 * Expert only (not documented)
	 * @return
	 */
	NotificationContainer getNotificationContainer() {
		return container;
	}
}
