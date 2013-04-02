package com.socialize.test.facebook;

import android.app.Activity;
import com.facebook.AccessToken;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeListener;
import com.socialize.networks.SocialNetworkPostListener;
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

	@Override
	public void handleAuthFail(Exception exception, AuthProviderListener listener) {
		super.handleAuthFail(exception, listener);
	}

	@Override
	public void handleCancel(AuthProviderListener listener) {
		super.handleCancel(listener);
	}

	@Override
	public void handleError(Exception exception, SocializeListener listener) {
		super.handleError(exception, listener);
	}

	@Override
	public void handleFBResponse(Activity context, Response response, SocialNetworkPostListener listener) {
		super.handleFBResponse(context, response, listener);
	}

	@Override
	public void handleNonListenerError(String msg, Exception error) {
		super.handleNonListenerError(msg, error);
	}

	@Override
	public void handleNotSignedIn(Activity context, SocializeListener listener) {
		super.handleNotSignedIn(context, listener);
	}

	@Override
	public void handleNotSignedIn(Activity context, SocialNetworkPostListener listener) {
		super.handleNotSignedIn(context, listener);
	}

	@Override
	public void handleResult(Session session, GraphUser user, AuthProviderListener listener) {
		super.handleResult(session, user, listener);
	}
}
