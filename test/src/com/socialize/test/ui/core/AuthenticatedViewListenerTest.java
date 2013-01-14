package com.socialize.test.ui.core;

import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.ui.view.AuthenticatedViewListener;

public class AuthenticatedViewListenerTest extends SocializeActivityTest {

	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class, IOCContainer.class})
	public void testOnError() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
		View subView = AndroidMock.createMock(View.class, TestUtils.getActivity(this));
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeListener errorListener = AndroidMock.createMock(SocializeListener.class);
		
		view.onAfterAuthenticate(container);
		AndroidMock.expect(view.getView()).andReturn(subView);
		AndroidMock.expect(view.getOnErrorListener()).andReturn(errorListener).anyTimes();
		
		errorListener.onError(error);
		view.removeAllViews();
		view.addView(subView);
		
		view.showError(TestUtils.getActivity(this), error);
		
		AndroidMock.replay(view, errorListener);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(TestUtils.getActivity(this), view, container);
		listener.onError(error);
		
		AndroidMock.verify(view, errorListener);		
	}
	
	@UsesMocks ({AuthenticatedView.class, View.class, IOCContainer.class})
	public void testOnAuthSuccess() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
		View subView = AndroidMock.createMock(View.class, TestUtils.getActivity(this));
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		view.onAfterAuthenticate(container);
		AndroidMock.expect(view.getView()).andReturn(subView);
		view.removeAllViews();
		view.addView(subView);
		
		AndroidMock.replay(view);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(TestUtils.getActivity(this), view, container);
		listener.onAuthSuccess(null);
		
		AndroidMock.verify(view);
	}
	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class, IOCContainer.class, SocializeListener.class})
	public void testOnAuthFail() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
		View subView = AndroidMock.createMock(View.class, TestUtils.getActivity(this));
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeListener errorListener = AndroidMock.createMock(SocializeListener.class);
		
		view.onAfterAuthenticate(container);
		AndroidMock.expect(view.getView()).andReturn(subView);
		AndroidMock.expect(view.getOnErrorListener()).andReturn(errorListener).anyTimes();
		
		errorListener.onError(error);
		view.removeAllViews();
		view.addView(subView);
		
		view.showError(TestUtils.getActivity(this), error);
		
		AndroidMock.replay(view, errorListener);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(TestUtils.getActivity(this), view, container);
		listener.onAuthFail(error);
		
		AndroidMock.verify(view, errorListener);
	}
	
}
