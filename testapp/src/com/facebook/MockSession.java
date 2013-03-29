package com.facebook;

import android.content.Context;

public class MockSession extends Session{
	public MockSession(Context currentContext) {
		super(currentContext, "foobar", null);
	}

	public MockSession(Context currentContext, String appId) {
		super(currentContext, appId, null);
	}
}
