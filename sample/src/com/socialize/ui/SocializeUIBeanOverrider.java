package com.socialize.ui;

import com.socialize.android.ioc.IOCContainer;


public class SocializeUIBeanOverrider {
	public void setBeanOverrides(String...override) {
		setBeanOverrides(SocializeUI.getInstance(),override);
	}
	public void setBeanOverrides(SocializeUI instance, String...override) {
		instance.setBeanOverrides(override);
	}
	
	public IOCContainer getContainer(SocializeUI instance) {
		return instance.getContainer();
	}
	
	public IOCContainer getContainer() {
		return getContainer(SocializeUI.getInstance());
	}
}
