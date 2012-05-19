package com.socialize;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.listener.SocializeInitListener;

public class SocializeAccess {
	public static <T extends Object> T getBean(String beanName, Object...args) {
		return getBean(Socialize.getSocialize(), beanName, args);
	}
	
	public static <T extends Object> T getBean(SocializeService socialize, String beanName, Object...args) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer().getBean(beanName, args);
		}
		return null;
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(String beanName) {
		return getProxy(Socialize.getSocialize(), beanName);
	}
	
	public static <T extends Object> ProxyObject<T> getProxy(SocializeService socialize, String beanName) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer().getProxy(beanName);
		}
		return null;
	}
	
	public static IOCContainer getContainer() {
		return getContainer(Socialize.getSocialize());
	}
	
	public static IOCContainer getContainer(SocializeService socialize) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer();
		}
		return null;
	}
	
	public static void clearBeanOverrides() {
		Socialize.getSocialize().getSystem().setBeanOverrides();
	}
	
	public static void setBeanOverrides(String...override) {
		Socialize.getSocialize().getSystem().setBeanOverrides(override);
	}
	
	public static void setInitListener(SocializeInitListener listener) {
		Socialize.getSocialize().getSystem().setSystemInitListener(listener);
	}
	
	public static void setSocialize(SocializeServiceImpl service) {
		Socialize.instance = service;
	}
}
