package com.socialize.test.core;

import android.content.Context;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.test.SocializeUnitTest;
import com.socialize.view.BaseView;
import org.mockito.Mockito;

public class BaseViewTest extends SocializeUnitTest {

	public void testShowError() {
		SocializeErrorHandler handler = Mockito.mock(SocializeErrorHandler.class);
		final Exception error = new Exception("foobar");
		
        TestView view = new TestView(getContext());
		view.setErrorHandler(handler);
		
		view.showError(getContext(), error);

        Mockito.verify(handler).handleError(getContext(), error);
	}
	
	class TestView extends BaseView {
		public TestView(Context context) {
			super(context);
		}
	}
}
