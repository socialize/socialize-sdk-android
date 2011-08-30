package com.socialize.ui.test;

import android.view.View;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.AuthenticatedView;

public class AuthenticatedViewTest extends SocializeUIActivityTest {

	@UsesMocks ({SocializeUI.class, IOCContainer.class})
	public void testOnPostSocializeInit() {
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "foobar";
		
		socializeUI.initUI(container);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(key);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(secret);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			protected boolean isRequires3rdPartyAuth() {
				return false;
			}
			
			@Override
			protected View getView() {
				return null;
			}

			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		AndroidMock.replay(socializeUI);
		
		view.onPostSocializeInit(container);
		
		AndroidMock.verify(socializeUI);
	}
	
	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
	public void testOnAttachedToWindow3rdParty() throws Throwable {
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "fb_foobar";
		
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_ID)).andReturn(null);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(null);
		
		socialize.authenticate(
				key, 
				secret, 
				AuthProviderType.FACEBOOK,
				fbId,
				listener);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			protected SocializeAuthListener getAuthListener3rdParty() {
				return null;
			}
			
			@Override
			protected SocializeAuthListener getAuthListener() {
				return listener;
			}

			@Override
			public void onPostSocializeInit(IOCContainer container) {
				// Do nothing for this test
			}
			
			@Override
			protected void initSocialize() {
				// Do nothing for this test
			}

			@Override
			protected boolean isRequires3rdPartyAuth() {
				return true;
			}
			
			@Override
			protected View getView() {
				return null;
			}

			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected void onBeforeAuthenticate() {
				addResult(true);
			}
			
			
		};
		
		view.setConsumerKey(key);
		view.setConsumerSecret(secret);
		view.setFbAppId(fbId);
		
		AndroidMock.replay(socializeUI);
		AndroidMock.replay(socialize);
		
		view.onAttachedToWindow();
		
		Boolean result = getNextResult();
		
		assertNotNull(result);
		assertTrue(result);
		
		AndroidMock.verify(socializeUI);
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
	public void testOnAttachedToWindow3rdPartyWithUser() throws Throwable {
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "fb_foobar";
		
		final String fbUserId = "fb_user_foobar";
		final String fbToken = "fb_token_foobar";
		
		
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_ID)).andReturn(fbUserId);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(fbToken);
		
		socialize.authenticateKnownUser(
				key, 
				secret, 
				AuthProviderType.FACEBOOK,
				fbId,
				fbUserId,
				fbToken,
				listener);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			protected SocializeAuthListener getAuthListener3rdParty() {
				return listener;
			}
			
			@Override
			protected SocializeAuthListener getAuthListener() {
				return null;
			}

			@Override
			public void onPostSocializeInit(IOCContainer container) {
				// Do nothing for this test
			}
			
			@Override
			protected void initSocialize() {
				// Do nothing for this test
			}

			@Override
			protected boolean isRequires3rdPartyAuth() {
				return true;
			}
			
			@Override
			protected View getView() {
				return null;
			}

			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected void onBeforeAuthenticate() {
				addResult(true);
			}
			
			
		};
		
		view.setConsumerKey(key);
		view.setConsumerSecret(secret);
		view.setFbAppId(fbId);
		
		
		AndroidMock.replay(socializeUI);
		AndroidMock.replay(socialize);
		
		view.onAttachedToWindow();
		
		Boolean result = getNextResult();
		
		assertNotNull(result);
		assertTrue(result);
		
		AndroidMock.verify(socializeUI);
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
	public void testOnAttachedToWindowNon3rdParty() throws Throwable {
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo";
		final String secret = "bar";
		
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_ID)).andReturn(null);
		AndroidMock.expect(socializeUI.getCustomConfigValue(getContext(), SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(null);
		
		socialize.authenticate(
				key, 
				secret, 
				listener);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			protected SocializeAuthListener getAuthListener3rdParty() {
				return null;
			}
			
			@Override
			protected SocializeAuthListener getAuthListener() {
				return listener;
			}

			@Override
			public void onPostSocializeInit(IOCContainer container) {
				// Do nothing for this test
			}
			
			@Override
			protected void initSocialize() {
				// Do nothing for this test
			}

			@Override
			protected boolean isRequires3rdPartyAuth() {
				return false;
			}
			
			@Override
			protected View getView() {
				return null;
			}

			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected void onBeforeAuthenticate() {
				addResult(true);
			}
		};
		
		view.setConsumerKey(key);
		view.setConsumerSecret(secret);
		
		AndroidMock.replay(socializeUI);
		AndroidMock.replay(socialize);
		
		view.onAttachedToWindow();
		
		Boolean result = getNextResult();
		
		assertNotNull(result);
		assertTrue(result);
		
		AndroidMock.verify(socializeUI);
		AndroidMock.verify(socialize);
	}
	
}
