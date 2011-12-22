package com.socialize.test.ui;

import android.view.View;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.AuthenticatedView;

public class AuthenticatedViewTest extends SocializeUIActivityTest {

	@UsesMocks ({SocializeUI.class, IOCContainer.class, SocializeService.class, SocializeAuthListener.class})
	public void testOnViewLoad() {
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "foobar";
		
		socializeUI.initUI(container);
		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(key);
		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(secret);
		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		
		socialize.authenticate(key, secret, listener);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			public View getView() {
				return null;
			}
			
			@Override
			public View getLoadingView() {
				return null;
			}

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public SocializeAuthListener getAuthListener(IOCContainer container) {
				return listener;
			}
		};
		
		AndroidMock.replay(socializeUI);
		
		view.onViewLoad(container);
		
		AndroidMock.verify(socializeUI);
	}
	
//	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
//	public void testOnAttachedToWindow3rdParty() throws Throwable {
//		
//		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
//		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
//		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
//		
//		final String key = "foo";
//		final String secret = "bar";
//		final String fbId = "fb_foobar";
//		
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_ID)).andReturn(null);
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(null);
//		
//		socialize.authenticate(
//				key, 
//				secret, 
//				AuthProviderType.FACEBOOK,
//				fbId,
//				listener);
//		
//		AuthenticatedView view = new AuthenticatedView(getContext()) {
//			
//			@Override
//			public SocializeAuthListener getAuthListener3rdParty() {
//				return null;
//			}
//			
//			@Override
//			public SocializeAuthListener getAuthListener() {
//				return listener;
//			}
//
//			@Override
//			public void onPostSocializeInit(IOCContainer container) {
//				// Do nothing for this test
//			}
//			
//			@Override
//			protected void initSocialize() {
//				// Do nothing for this test
//			}
//			
//			@Override
//			public View getView() {
//				return null;
//			}
//
//			@Override
//			public SocializeUI getSocializeUI() {
//				return socializeUI;
//			}
//
//			@Override
//			public SocializeService getSocialize() {
//				return socialize;
//			}
//
//			@Override
//			public void onBeforeAuthenticate() {
//				addResult(true);
//			}
//			
//			
//		};
//		
//		view.setConsumerKey(key);
//		view.setConsumerSecret(secret);
//		view.setFbAppId(fbId);
//		
//		AndroidMock.replay(socializeUI);
//		AndroidMock.replay(socialize);
//		
//		view.onAttachedToWindow();
//		
//		Boolean result = getNextResult();
//		
//		assertNotNull(result);
//		assertTrue(result);
//		
//		AndroidMock.verify(socializeUI);
//		AndroidMock.verify(socialize);
//	}
	
//	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
//	public void testOnAttachedToWindow3rdPartyWithUser() throws Throwable {
//		
//		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
//		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
//		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
//		
//		final String key = "foo";
//		final String secret = "bar";
//		final String fbId = "fb_foobar";
//		
//		final String fbUserId = "fb_user_foobar";
//		final String fbToken = "fb_token_foobar";
//		
//		
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_ID)).andReturn(fbUserId);
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(fbToken);
//		
//		socialize.authenticateKnownUser(
//				key, 
//				secret, 
//				AuthProviderType.FACEBOOK,
//				fbId,
//				fbUserId,
//				fbToken,
//				listener);
//		
//		AuthenticatedView view = new AuthenticatedView(getContext()) {
//			
//			@Override
//			public SocializeAuthListener getAuthListener3rdParty() {
//				return listener;
//			}
//			
//			@Override
//			public SocializeAuthListener getAuthListener() {
//				return null;
//			}
//
//			@Override
//			public void onPostSocializeInit(IOCContainer container) {
//				// Do nothing for this test
//			}
//			
//			@Override
//			protected void initSocialize() {
//				// Do nothing for this test
//			}
//			
//			@Override
//			public View getView() {
//				return null;
//			}
//
//			@Override
//			public SocializeUI getSocializeUI() {
//				return socializeUI;
//			}
//
//			@Override
//			public SocializeService getSocialize() {
//				return socialize;
//			}
//
//			@Override
//			public void onBeforeAuthenticate() {
//				addResult(true);
//			}
//			
//			
//		};
//		
//		view.setConsumerKey(key);
//		view.setConsumerSecret(secret);
//		view.setFbAppId(fbId);
//		
//		
//		AndroidMock.replay(socializeUI);
//		AndroidMock.replay(socialize);
//		
//		view.onAttachedToWindow();
//		
//		Boolean result = getNextResult();
//		
//		assertNotNull(result);
//		assertTrue(result);
//		
//		AndroidMock.verify(socializeUI);
//		AndroidMock.verify(socialize);
//	}
	
//	@UsesMocks ({SocializeUI.class, SocializeService.class, SocializeAuthListener.class})
//	public void testOnAttachedToWindowNon3rdParty() throws Throwable {
//		
//		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
//		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
//		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
//		
//		final String key = "foo";
//		final String secret = "bar";
//		
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_ID)).andReturn(null);
//		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.FACEBOOK_USER_TOKEN)).andReturn(null);
//		
//		socialize.authenticate(
//				key, 
//				secret, 
//				listener);
//		
//		AuthenticatedView view = new AuthenticatedView(getContext()) {
//			
//			@Override
//			public SocializeAuthListener getAuthListener3rdParty() {
//				return null;
//			}
//			
//			@Override
//			public SocializeAuthListener getAuthListener() {
//				return listener;
//			}
//
//			@Override
//			public void onPostSocializeInit(IOCContainer container) {
//				// Do nothing for this test
//			}
//			
//			@Override
//			protected void initSocialize() {
//				// Do nothing for this test
//			}
//			
//			@Override
//			public View getView() {
//				return null;
//			}
//
//			@Override
//			public SocializeUI getSocializeUI() {
//				return socializeUI;
//			}
//
//			@Override
//			public SocializeService getSocialize() {
//				return socialize;
//			}
//
//			@Override
//			public void onBeforeAuthenticate() {
//				addResult(true);
//			}
//		};
//		
//		view.setConsumerKey(key);
//		view.setConsumerSecret(secret);
//		
//		AndroidMock.replay(socializeUI);
//		AndroidMock.replay(socialize);
//		
//		view.onAttachedToWindow();
//		
//		Boolean result = getNextResult();
//		
//		assertNotNull(result);
//		assertTrue(result);
//		
//		AndroidMock.verify(socializeUI);
//		AndroidMock.verify(socialize);
//	}
	
//	@UsesMocks ({Activity.class, Intent.class})
//	public void testGetBundleValue() {
//		
//		final String key = "foobar";
//		Bundle mockBundle = new Bundle();
//		mockBundle.putString(SocializeUI.ENTITY_KEY, key);
//		
//		final Activity activity = AndroidMock.createMock(Activity.class);
//		Intent intent = AndroidMock.createMock(Intent.class);
//		
//		AndroidMock.expect(activity.getIntent()).andReturn(intent);
//		AndroidMock.expect(intent.getExtras()).andReturn(mockBundle);
//		
//		AndroidMock.replay(activity);
//		AndroidMock.replay(intent);
//		
//		PublicAuthenticatedView view = new PublicAuthenticatedView(getContext()) {
//			
//			@Override
//			public View getView() {
//				return null;
//			}
//
//			@Override
//			protected Context getViewContext() {
//				return activity;
//			}
//		};
//		
//		String value = view.getBundleValue(SocializeUI.ENTITY_KEY);
//		
//		AndroidMock.verify(activity);
//		AndroidMock.verify(intent);
//		
//		assertNotNull(value);
//		assertEquals(key, value);
//	}
//	
//	abstract class PublicAuthenticatedView extends AuthenticatedView {
//		public PublicAuthenticatedView(Context context) {
//			super(context);
//		}
//
//		@Override
//		public String getBundleValue(String key) {
//			return super.getBundleValue(key);
//		}
//	}
	
}
