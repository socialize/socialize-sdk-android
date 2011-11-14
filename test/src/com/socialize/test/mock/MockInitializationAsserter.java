package com.socialize.test.mock;

import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.init.SocializeInitializationAsserter;
import com.socialize.listener.SocializeListener;

public class MockInitializationAsserter implements SocializeInitializationAsserter {

	@Override
	public boolean assertAuthenticated(SocializeService service, SocializeSession session, SocializeListener listener) {
		return true;
	}

	@Override
	public boolean assertInitialized(SocializeService service, SocializeListener listener) {
		return true;
	}

}
