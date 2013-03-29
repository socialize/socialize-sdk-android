package com.socialize.test.facebook;

import android.app.Activity;
import com.facebook.AccessToken;
import com.facebook.Session;
import com.socialize.listener.AuthProviderListener;
import com.socialize.networks.facebook.v3.FacebookFacadeV3;

public class PublicFacebookFacadeV3 extends FacebookFacadeV3 {

	@Override
	public void login(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
		super.login(context, appId, permissions, sso, read, listener);
	}

	@Override
	public void login(Activity context, boolean read, AuthProviderListener listener) {
		super.login(context, read, listener);
	}

	@Override
	public void openSessionForPublish(Session session, Session.OpenRequest auth) {
		super.openSessionForPublish(session, auth);
	}

	@Override
	public void openSessionForRead(Session session, Session.OpenRequest auth) {
		super.openSessionForRead(session, auth);
	}

	@Override
	public void openSessionWithToken(Session session, AccessToken token) {
		super.openSessionWithToken(session, token);
	}

	@Override
	public Session.StatusCallback createLoginCallback(AuthProviderListener listener) {
		return super.createLoginCallback(listener);
	}
}
