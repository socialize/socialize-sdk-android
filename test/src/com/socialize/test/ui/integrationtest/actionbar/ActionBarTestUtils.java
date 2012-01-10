package com.socialize.test.ui.integrationtest.actionbar;

import static junit.framework.Assert.fail;

import com.socialize.SocializeAccess;
import com.socialize.api.ApiHost;
import com.socialize.sample.mocks.MockSocializeApiHost;

@Deprecated
public class ActionBarTestUtils {
	public static void setApiHostDelegate(ApiHost delegate) {
		Object bean = SocializeAccess.getBean("socializeApiHost");
		if(bean instanceof MockSocializeApiHost) {
			MockSocializeApiHost host = (MockSocializeApiHost) bean;
			host.setDelegate(delegate);
		}
		else {
			fail("Can not set ApiHostDelegate on non MockSocializeApiHost");
		}
	}
	
	public static void clearApiHostDelegate() {
		Object bean = SocializeAccess.getBean("socializeApiHost");
		if(bean instanceof MockSocializeApiHost) {
			MockSocializeApiHost host = (MockSocializeApiHost) bean;
			host.setDelegate(null);
		}
		else {
			fail("Can not set ApiHostDelegate on non MockSocializeApiHost");
		}
	}	
		
}
