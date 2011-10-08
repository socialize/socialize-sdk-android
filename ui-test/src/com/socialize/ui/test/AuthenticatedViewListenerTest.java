package com.socialize.ui.test;

import android.view.View;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.ui.view.AuthenticatedViewListener;
import com.socialize.ui.view.AuthenticatedViewListener3rdParty;

public class AuthenticatedViewListenerTest extends SocializeUIActivityTest {

	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class})
	public void testOnError() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, getActivity());
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		error.printStackTrace();
		
		view.onAfterAuthenticate();
		view.showError(getActivity(), error);
		
		AndroidMock.replay(view);
		AndroidMock.replay(error);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(getActivity(), view);
		listener.onError(error);
		
		AndroidMock.verify(view);
		AndroidMock.verify(error);
	}
	
	@UsesMocks ({AuthenticatedView.class, View.class})
	public void testOnAuthSuccess() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, getActivity());
		View subView = AndroidMock.createMock(View.class, getActivity());
		
		view.onAfterAuthenticate();
		AndroidMock.expect(view.getView()).andReturn(subView);
		view.addView(subView);
		
		AndroidMock.replay(view);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(getActivity(), view);
		listener.onAuthSuccess(null);
		
		AndroidMock.verify(view);
	}
	
	@UsesMocks ({AuthenticatedView.class, SocializeException.class})
	public void testOnAuthFail() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, getActivity());
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		error.printStackTrace();
		
		view.onAfterAuthenticate();
		view.showError(getActivity(), error);
		
		AndroidMock.replay(view);
		AndroidMock.replay(error);
		
		AuthenticatedViewListener listener = new AuthenticatedViewListener(getActivity(), view);
		listener.onAuthFail(error);
		
		AndroidMock.verify(view);
		AndroidMock.verify(error);
	}
	
	@Deprecated
	@UsesMocks ({AuthenticatedView.class, SocializeException.class, SocializeService.class, SocializeAuthListener.class})
	public void test3rdPartyOnAuthFail() {
		AuthenticatedView view = AndroidMock.createMock(AuthenticatedView.class, getActivity());
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		SocializeAuthListener authListener = AndroidMock.createMock(SocializeAuthListener.class);
		
		AndroidMock.expect(view.getSocialize()).andReturn(socialize);
		
		String consumerKey = "foo";
		String consumerSecret = "bar";
		String fbAppId = "foobar_id";
		
		AndroidMock.expect(view.getConsumerKey()).andReturn(consumerKey);
		AndroidMock.expect(view.getConsumerSecret()).andReturn(consumerSecret);
		AndroidMock.expect(view.getFbAppId()).andReturn(fbAppId);
		AndroidMock.expect(view.getAuthListener()).andReturn(authListener);
		
		socialize.authenticate(
				consumerKey, 
				consumerSecret, 
				AuthProviderType.FACEBOOK,
				fbAppId,
				authListener);
		
		AndroidMock.replay(view);
		AndroidMock.replay(socialize);
		
		AuthenticatedViewListener3rdParty listener = new AuthenticatedViewListener3rdParty(getActivity(), view);
		listener.onAuthFail(error);
		
		AndroidMock.verify(view);
		AndroidMock.verify(socialize);
	}
	
}
