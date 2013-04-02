package com.socialize.test.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.v3.FacebookFacadeV3;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;

public class FacebookFacadeV3Test extends SocializeActivityTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Session.setActiveSession(null);
	}

	@UsesMocks({Session.class, SharedPreferencesTokenCachingStrategy.class})
	public void testOnActivityResult() {

		Activity context = TestUtils.getActivity(this);

		final Session mockSession = AndroidMock.createMock(Session.class, context);
		final Intent mockData = new Intent(); // Can't mock

		mockSession.onActivityResult(context, 1, 2, mockData);

		AndroidMock.replay(mockSession);

		FacebookFacadeV3 facade = new FacebookFacadeV3() {
			@Override
			protected Session getActiveSession(Context context) {
				return mockSession;
			}
		};

		facade.onActivityResult(context, 1, 2, mockData);

		AndroidMock.verify(mockSession);

	}

	public void testAuthenticate0() {

		final String appId = "foobar";
		final String[] permissions = {"foo", "bar"};
		final boolean sso = false;
		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {}
			@Override
			public void onAuthFail(SocializeException error) {}
			@Override
			public void onCancel() {}
			@Override
			public void onError(SocializeException error) {}
		};


		FacebookFacadeV3 facade = new FacebookFacadeV3() {
			@Override
			public void logout(Context context) {
				addResult(0, true);
			}

			@Override
			protected void login(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
				addResult(1, appId);
				addResult(2, permissions);
				addResult(3, sso);
				addResult(4, read);
				addResult(5, listener);
			}

		};

		facade.authenticate(getContext(), appId, permissions, sso, listener);

		assertTrue((Boolean) getResult(0));
		assertEquals(appId, getResult(1));
		assertSame(permissions, getResult(2));
		assertFalse((Boolean) getResult(3));
		assertTrue((Boolean) getResult(4));
		assertSame(listener, getResult(5));
	}


	public void testAuthenticate1() {

		final String appId = "foobar";
		final String[] permissions = {"foo", "bar"};
		final boolean sso = true;
		final boolean read = false;
		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {}
			@Override
			public void onAuthFail(SocializeException error) {}
			@Override
			public void onCancel() {}
			@Override
			public void onError(SocializeException error) {}
		};


		FacebookFacadeV3 facade = new FacebookFacadeV3() {
			@Override
			protected void login(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
				addResult(0, appId);
				addResult(1, permissions);
				addResult(2, sso);
				addResult(3, read);
				addResult(4, listener);
			}
		};

		facade.authenticate(getContext(), appId, permissions, sso, read, listener);

		assertEquals(appId, getResult(0));
		assertSame(permissions, getResult(1));
		assertTrue((Boolean) getResult(2));
		assertFalse((Boolean) getResult(3));
		assertSame(listener, getResult(4));
	}

	@UsesMocks({SocializeConfig.class})
	public void testOnResume() {
		// On resume we expect a valid session to be created
		Activity context = TestUtils.getActivity(this);

		final String tokenString = "foobar_token";

		final SocializeConfig mockConfig = AndroidMock.createMock(SocializeConfig.class);

		AndroidMock.expect(mockConfig.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn("foobar");

		AndroidMock.replay(mockConfig);

		FacebookFacadeV3 facade = new FacebookFacadeV3() {
			@Override
			protected void openSessionWithToken(Session session, AccessToken token) {
				addResult(0, token);
			}

			@Override
			public String getAccessToken(Context context) {
				return tokenString;
			}
		};

		facade.setConfig(mockConfig);
		facade.onResume(context, null);

		AndroidMock.verify(mockConfig);

		AccessToken token = getResult(0);
		assertNotNull(token);
		assertEquals(tokenString, token.getToken());
	}

	public void testLoginPublish() {
		final Activity activity = TestUtils.getActivity(this);
		final String appId = "foobar";
		final String[] permissions = {"foo", "bar"};
		final boolean sso = true;
		final boolean read = false;
		final AuthProviderListener listener = null;

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void openSessionForRead(Session session, Session.OpenRequest auth) {

			}

			@Override
			public void openSessionForPublish(Session session, Session.OpenRequest auth) {
				addResult(0, session);
				addResult(1, auth);
			}
		};

		facade.login(activity,appId,permissions,sso,read,listener);

		Session session = getResult(0);
		Session.OpenRequest auth = getResult(1);

		assertNotNull(session);
		assertNotNull(auth);
	}

	public void testLoginRead() {
		final Activity activity = TestUtils.getActivity(this);
		final String appId = "foobar";
		final String[] permissions = {"foo", "bar"};
		final boolean sso = true;
		final boolean read = true;
		final AuthProviderListener listener = null;

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void openSessionForRead(Session session, Session.OpenRequest auth) {
				addResult(0, session);
				addResult(1, auth);
			}

			@Override
			public void openSessionForPublish(Session session, Session.OpenRequest auth) {

			}
		};

		facade.login(activity,appId,permissions,sso,read,listener);

		Session session = getResult(0);
		Session.OpenRequest auth = getResult(1);

		assertNotNull(session);
		assertNotNull(auth);
	}

	@UsesMocks({MockSession.class})
	public void testLoginCallbackOpenForRead() {
		Activity context = TestUtils.getActivity(this);
		MockSession session = AndroidMock.createMock(MockSession.class, context, "foobar");

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			protected void getUser(Session session, AuthProviderListener listener) {
				addResult(0, session);
			}

			@Override
			protected boolean isSessionOpen(Session session) {
				return true;
			}
		};

		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {

			}

			@Override
			public void onAuthFail(SocializeException error) {

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(SocializeException error) {

			}
		};


		AndroidMock.replay(session);

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.OPENED;

		loginCallback.call(session, state, null);

		AndroidMock.verify(session);

		assertSame(session, getResult(0));
	}

	@UsesMocks({MockSession.class})
	public void testLoginCallbackClosed() {
		Activity context = TestUtils.getActivity(this);
		final Exception mockException = new Exception("foobar");
		MockSession session = AndroidMock.createMock(MockSession.class, context, "foobar");

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void handleError(Exception exception, SocializeListener listener) {
				addResult(0, exception);
			}
		};

		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {

			}

			@Override
			public void onAuthFail(SocializeException error) {

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(SocializeException error) {

			}
		};


		AndroidMock.replay(session);

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.CLOSED;

		loginCallback.call(session, state, mockException);

		AndroidMock.verify(session);

		assertSame(mockException, getResult(0));
	}

	@UsesMocks({MockSession.class})
	public void testLoginCallbackFailed() {
		Activity context = TestUtils.getActivity(this);
		final Exception mockException = new Exception("foobar");
		MockSession session = AndroidMock.createMock(MockSession.class, context, "foobar");

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void handleAuthFail(Exception exception, AuthProviderListener listener) {
				addResult(0, exception);
			}
		};

		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {}
			@Override
			public void onAuthFail(SocializeException error) {}
			@Override
			public void onCancel() {}
			@Override
			public void onError(SocializeException error) {}
		};

		AndroidMock.replay(session);

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.CLOSED_LOGIN_FAILED;

		loginCallback.call(session, state, mockException);

		AndroidMock.verify(session);

		assertSame(mockException, getResult(0));
	}


	@UsesMocks({MockSession.class})
	public void testLoginCallbackCancel() {
		Activity context = TestUtils.getActivity(this);
		final Exception mockException = new FacebookOperationCanceledException("foobar");
		MockSession session = AndroidMock.createMock(MockSession.class, context, "foobar");

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void handleCancel(AuthProviderListener listener) {
				addResult(0, listener);
			}
		};

		final AuthProviderListener listener = new AuthProviderListener() {
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {}
			@Override
			public void onAuthFail(SocializeException error) {}
			@Override
			public void onCancel() {}
			@Override
			public void onError(SocializeException error) {}
		};

		AndroidMock.replay(session);

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.OPENED;

		loginCallback.call(session, state, mockException);

		AndroidMock.verify(session);

		assertSame(listener, getResult(0));
	}

	@UsesMocks({AuthProviderListener.class, GraphUser.class, MockSession.class})
	public void testHandleMethodsCallAuthListener() {

		Activity context = TestUtils.getActivity(this);
		MockSession session = AndroidMock.createMock(MockSession.class, context, "foobar");
		GraphUser user = AndroidMock.createMock(GraphUser.class);
		AuthProviderListener listener = AndroidMock.createMock(AuthProviderListener.class);
		SocializeException error = new SocializeException("foobar");

		AndroidMock.expect(user.getId()).andReturn("foo");

		listener.onError(error);
		listener.onAuthFail(error);
		listener.onCancel();
		listener.onAuthSuccess((AuthProviderResponse) AndroidMock.anyObject());

		AndroidMock.replay(user, session, listener);

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3();

		facade.handleAuthFail(error, listener);
		facade.handleCancel(listener);
		facade.handleError(error, listener);
		facade.handleResult(session, user, listener);

		AndroidMock.verify(user, session, listener);

	}

	@UsesMocks({SocialNetworkPostListener.class, SocializeLogger.class})
	public void testHandleFBResponseWithError() {

		Activity context = TestUtils.getActivity(this);
		FacebookException mockException = new FacebookException("foobar");
		FacebookRequestError mockError = new FacebookRequestError(102, "foo", "bar"); // 102 is EC_INVALID_SESSION

		MockResponse response = new MockResponse(null, null, mockError);// Can't mock it.. thanks FB :/

		SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
		SocialNetworkPostListener listener = AndroidMock.createMock(SocialNetworkPostListener.class);

		// Expectations
		listener.onNetworkError(AndroidMock.eq(context), AndroidMock.eq(SocialNetwork.FACEBOOK), (FacebookServiceException)AndroidMock.anyObject());
		logger.error(AndroidMock.eq("bar"), (FacebookServiceException)AndroidMock.anyObject());

		AndroidMock.replay(listener, logger);

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void unlink(Context context, SocializeDeAuthListener listener) {
				addResult(0, context);
			}
		};

		facade.setLogger(logger);

		facade.handleFBResponse(context, response, listener);

		AndroidMock.verify(listener, logger);

		assertSame(context, getResult(0));
	}
}
