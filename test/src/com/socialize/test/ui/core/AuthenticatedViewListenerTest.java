package com.socialize.test.ui.core;

import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.ui.view.AuthenticatedViewListener;

public class AuthenticatedViewListenerTest extends SocializeActivityTest {

	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class, IOCContainer.class})
	public void testOnError() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		view.onAfterAuthenticate(container);
		view.removeAllViews();
		view.showError(TestUtils.getActivity(this), error);
		
		AndroidMock.replay(view);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(TestUtils.getActivity(this), view, container);
		listener.onError(error);
		
		AndroidMock.verify(view);
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
	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class, IOCContainer.class})
	public void testOnAuthFail() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		view.onAfterAuthenticate(container);
		view.showError(TestUtils.getActivity(this), error);
		
		AndroidMock.replay(view);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(TestUtils.getActivity(this), view, container);
		listener.onAuthFail(error);
		
		AndroidMock.verify(view);
	}
//	
//	@Deprecated
//	@UsesMocks ({AuthenticatedView.class, SocializeException.class, SocializeService.class, SocializeAuthListener.class})
//	public void test3rdPartyOnAuthFail() {
//		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, TestUtils.getActivity(this));
//		SocializeException error = AndroidMock.createMock(SocializeException.class);
//		SocializeService socialize = AndroidMock.createMock(SocializeService.class);
//		SocializeAuthListener authListener = AndroidMock.createMock(SocializeAuthListener.class);
//		
//		AndroidMock.expect(view.getSocialize()).andReturn(socialize);
//		
//		String consumerKey = "foo";
//		String consumerSecret = "bar";
//		String fbAppId = "foobar_id";
//		
//		AndroidMock.expect(view.getConsumerKey()).andReturn(consumerKey);
//		AndroidMock.expect(view.getConsumerSecret()).andReturn(consumerSecret);
//		AndroidMock.expect(view.getFbAppId()).andReturn(fbAppId);
//		AndroidMock.expect(view.getAuthListener()).andReturn(authListener);
//		
//		socialize.authenticate(
//				consumerKey, 
//				consumerSecret, 
//				AuthProviderType.FACEBOOK,
//				fbAppId,
//				authListener);
//		
//		AndroidMock.replay(view);
//		AndroidMock.replay(socialize);
//		
//		AuthenticatedViewListener3rdParty listener = new AuthenticatedViewListener3rdParty(TestUtils.getActivity(this), view);
//		listener.onAuthFail(error);
//		
//		AndroidMock.verify(view);
//		AndroidMock.verify(socialize);
//	}
	
}
