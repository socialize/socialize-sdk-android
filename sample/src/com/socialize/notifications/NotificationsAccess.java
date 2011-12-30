package com.socialize.notifications;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;

public class NotificationsAccess {
	
	public static <T extends Object> T getBean(SocializeC2DMReceiver receiver, String beanName) {
		return receiver.getNotificationContainer().getContainer().getBean(beanName);
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(SocializeC2DMReceiver receiver,String beanName) {
		return receiver.getNotificationContainer().getContainer().getProxy(beanName);
	}
	
	public static IOCContainer getContainer(SocializeC2DMReceiver receiver) {
		return receiver.getNotificationContainer().getContainer();
	}

	public static void setBeanOverrides(SocializeC2DMReceiver receiver, String...override) {
		receiver.getNotificationContainer().setConfigPaths(override);
	}
}
