package com.socialize.ui.test.integrationtest.actionbar;

import com.socialize.SocializeBeanAccess;
import com.socialize.api.ApiHost;
import com.socialize.ui.sample.mock.MockSocializeApiHost;
import static junit.framework.Assert.*;

public class ActionBarTestUtils {
	public static void setApiHostDelegate(ApiHost delegate) {
		SocializeBeanAccess accessor = new SocializeBeanAccess();
		Object bean = accessor.getBean("socializeApiHost");
		if(bean instanceof MockSocializeApiHost) {
			MockSocializeApiHost host = (MockSocializeApiHost) bean;
			host.setDelegate(delegate);
		}
		else {
			fail("Can not set ApiHostDelegate on non MockSocializeApiHost");
		}
	}
	
	public static void clearApiHostDelegate() {
		SocializeBeanAccess accessor = new SocializeBeanAccess();
		Object bean = accessor.getBean("socializeApiHost");
		if(bean instanceof MockSocializeApiHost) {
			MockSocializeApiHost host = (MockSocializeApiHost) bean;
			host.setDelegate(null);
		}
		else {
			fail("Can not set ApiHostDelegate on non MockSocializeApiHost");
		}
	}	
		
}
