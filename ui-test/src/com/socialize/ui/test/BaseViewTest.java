package com.socialize.ui.test;

import android.content.Context;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.BaseView;
import com.socialize.ui.error.SocializeUIErrorHandler;

public class BaseViewTest extends SocializeUITestCase {

	@UsesMocks(SocializeUIErrorHandler.class)
	public void testShowError() {
		SocializeUIErrorHandler handler = AndroidMock.createMock(SocializeUIErrorHandler.class);
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
