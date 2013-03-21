package com.socialize.notifications;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;

public class NotificationsAccess {
	
	public static <T extends Object> T getBean(SocializeC2DMReceiverHandler receiver, String beanName) {
		return receiver.getNotificationContainer().getContainer().getBean(beanName);
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(SocializeC2DMReceiverHandler receiver,String beanName) {
		return receiver.getNotificationContainer().getContainer().getProxy(beanName);
	}
	
	public static IOCContainer getContainer(SocializeC2DMReceiverHandler receiver) {
		return receiver.getNotificationContainer().getContainer();
	}

	public static void setBeanOverrides(SocializeC2DMReceiverHandler receiver, String...override) {
		NotificationContainer.NotificationBeans = override;
	}
	
	public static void destroy(SocializeC2DMReceiverHandler receiver) {
		if(receiver != null) {
			receiver.getNotificationContainer().getContainer().destroy();
		}
	}
}
