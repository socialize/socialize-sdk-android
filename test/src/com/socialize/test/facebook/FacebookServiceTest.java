package com.socialize.test.facebook;

import android.app.Activity;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;

public class FacebookServiceTest extends SocializeActivityTest {

	@UsesMocks({AuthProviderListener.class, AuthProviderResponse.class})
	public void testAuthenticate() {

		final Activity context = TestUtils.getActivity(this);
		final SocializeException error = new SocializeException("foobar");
		final AuthProviderListener listener = AndroidMock.createMock(AuthProviderListener.class);
		final AuthProviderResponse response = AndroidMock.createMock(AuthProviderResponse.class);
		final String[] mock_permissions = {"foo", "bar"};

		// Create a fake facebook facade, but not a mock
		FacebookFacade facade = new PublicFacebookFacadeV3() {
			@Override
			public void authenticate(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
				// Just call the listener
				listener.onCancel();
				listener.onError(error);
				listener.onAuthFail(error);
				listener.onAuthSuccess(response);
			}
		};

		FacebookService service = new FacebookService("foobar", facade, listener, null) {
			@Override
			public void finish(Activity context) {
				TestUtils.incrementCount();
			}
		};

		listener.onCancel();
		AndroidMock.expectLastCall().times(5);

		listener.onError(error);
		AndroidMock.expectLastCall().times(5);

		listener.onAuthFail(error);
		AndroidMock.expectLastCall().times(5);

		listener.onAuthSuccess(response);
		AndroidMock.expectLastCall().times(5);

		AndroidMock.replay(listener, response);

		service.authenticateForRead(context, true, mock_permissions);
		service.authenticateForWrite(context, true, mock_permissions);
		service.authenticate(context);
		service.authenticate(context, true);
		service.authenticate(context, true, mock_permissions);

		AndroidMock.verify(listener, response);
	}
}
