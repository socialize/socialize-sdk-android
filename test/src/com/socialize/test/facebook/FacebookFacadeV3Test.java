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
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.v3.FacebookFacadeV3;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;

public class FacebookFacadeV3Test extends SocializeActivityTest {

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



}
