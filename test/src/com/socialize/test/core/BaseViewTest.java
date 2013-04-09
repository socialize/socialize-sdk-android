package com.socialize.test.core;

import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.test.SocializeUnitTest;
import com.socialize.view.BaseView;

public class BaseViewTest extends SocializeUnitTest {

	@UsesMocks(SocializeErrorHandler.class)
	public void testShowError() {
		SocializeErrorHandler handler = AndroidMock.createMock(SocializeErrorHandler.class);
		final Exception error = new Exception("foobar");
		
        handler.handleError(getContext(), error);
		
		AndroidMock.replay(handler);
		
		TestView view = new TestView(getContext());
		view.setErrorHandler(handler);
		
		view.showError(getContext(), error);
		
		AndroidMock.verify(handler);
	}
	
	class TestView extends BaseView {
		public TestView(Context context) {
			super(context);
		}
	}
}
