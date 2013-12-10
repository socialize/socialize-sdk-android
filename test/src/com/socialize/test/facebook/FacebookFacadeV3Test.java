package com.socialize.test.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookServiceException;
import com.facebook.MockResponse;
import com.facebook.MockSession;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
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
import org.mockito.Mockito;

public class FacebookFacadeV3Test extends SocializeActivityTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Session.setActiveSession(null);
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

	public void testOnResume() {
		// On resume we expect a valid session to be created
		Activity context = TestUtils.getActivity(this);

		final String tokenString = "foobar_token";

		final SocializeConfig mockConfig = Mockito.mock(SocializeConfig.class);

		Mockito.when(mockConfig.getProperty(SocializeConfig.FACEBOOK_APP_ID)).thenReturn("foobar");

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

	public void testLoginCallbackOpenForRead() {
		MockSession session = Mockito.mock(MockSession.class);

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

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.OPENED;

		loginCallback.call(session, state, null);

		assertSame(session, getResult(0));
	}

	public void testLoginCallbackClosed() {
		Activity context = TestUtils.getActivity(this);
		final Exception mockException = new Exception("foobar");
		MockSession session = Mockito.mock(MockSession.class);

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

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.CLOSED;

		loginCallback.call(session, state, mockException);

		assertSame(mockException, getResult(0));
	}

	public void testLoginCallbackFailed() {
		final Exception mockException = new Exception("foobar");
		MockSession session = Mockito.mock(MockSession.class);

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

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.CLOSED_LOGIN_FAILED;

		loginCallback.call(session, state, mockException);

		assertSame(mockException, getResult(0));
	}


	public void testLoginCallbackCancel() {
		final Exception mockException = new FacebookOperationCanceledException("foobar");
		MockSession session = Mockito.mock(MockSession.class);

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

		Session.StatusCallback loginCallback = facade.createLoginCallback(listener);

		SessionState state = SessionState.OPENED;

		loginCallback.call(session, state, mockException);

		assertSame(listener, getResult(0));
	}

	public void testHandleMethodsCallAuthListener() {

		final MockSession session = Mockito.mock(MockSession.class);
		GraphUser user = Mockito.mock(GraphUser.class);
		AuthProviderListener listener = Mockito.mock(AuthProviderListener.class);
		SocializeException error = new SocializeException("foobar");

        // FB session method is final :/
        TestUtils.setProperty(session, "lock", new Object());

		Mockito.when(user.getId()).thenReturn("foo");

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
            @Override
            protected Session createNewSession(Activity context, String appId) {
                return session;
            }
        };

		facade.handleAuthFail(error, listener);
		facade.handleCancel(listener);
		facade.handleError(error, listener);
		facade.handleResult(session, user, listener);

        Mockito.verify(listener).onError(error);
        Mockito.verify(listener).onAuthFail(error);
        Mockito.verify(listener).onCancel();
        Mockito.verify(listener).onAuthSuccess((AuthProviderResponse) Mockito.anyObject());
	}

	public void testHandleFBResponseWithError() {

		Activity context = TestUtils.getActivity(this);
		FacebookRequestError mockError = new FacebookRequestError(102, "foo", "bar"); // 102 is EC_INVALID_SESSION

		MockResponse response = new MockResponse(null, null, mockError);// Can't mock it.. thanks FB :/

		SocializeLogger logger = Mockito.mock(SocializeLogger.class);
		SocialNetworkPostListener listener = Mockito.mock(SocialNetworkPostListener.class);

		PublicFacebookFacadeV3 facade = new PublicFacebookFacadeV3() {
			@Override
			public void unlink(Context context, SocializeDeAuthListener listener) {
				addResult(0, context);
			}
		};

		facade.setLogger(logger);

		facade.handleFBResponse(context, response, listener);

        Mockito.verify(listener).onNetworkError(Mockito.eq(context), Mockito.eq(SocialNetwork.FACEBOOK), (FacebookServiceException) Mockito.anyObject());
        Mockito.verify(logger).error(Mockito.eq("bar"), (FacebookServiceException) Mockito.anyObject());

		assertSame(context, getResult(0));
	}
}
