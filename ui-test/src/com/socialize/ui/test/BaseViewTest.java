package com.socialize.ui.test;

import android.content.Context;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.BaseView;
import com.socialize.ui.error.SocializeUIErrorHandler;

public class BaseViewTest extends SocializeUITest {

	@UsesMocks(SocializeUIErrorHandler.class)
	public void testShowError() {
		SocializeUIErrorHandler handler = AndroidMock.createMock(SocializeUIErrorHandler.class);
		final String message = "foobar";
		
		handler.handleError(getContext(), message);
		
		AndroidMock.replay(handler);
		
		TestView view = new TestView(getContext());
		view.setErrorHandler(handler);
		
		view.showError(getContext(), message);
		
		AndroidMock.verify(handler);
	}
	
	
	class TestView extends BaseView {
		public TestView(Context context) {
			super(context);
		}

		@Override
		public void showError(Context context, String message) {
			super.showError(context, message);
		}
	}
}
