package com.socialize.ui;

import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;

@Deprecated
public class SocializeUIBeanOverrider {
	public void setBeanOverrides(String...override) {
		setBeanOverrides(SocializeUI.getInstance(),override);
	}
	public void setBeanOverrides(SocializeUI instance, String...override) {
		SocializeAccess.setBeanOverrides(override);
	}
	
	public IOCContainer getContainer(SocializeUI instance) {
		return SocializeAccess.getContainer();
	}
	
	public IOCContainer getContainer() {
		return getContainer(SocializeUI.getInstance());
	}
}
