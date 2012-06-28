/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.test.ui.actionbar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.auth.AuthDialogFactory;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.share.ShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;

/**
 * @author Jason Polites
 *
 */
public class ActionBarTestUtils2 {
	
	public void testLikePromptsForAuth(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final AuthDialogFactory mockFactory = new AuthDialogFactory() {
			@Override
			public void show(Context parent, AuthDialogListener listener) {
				TestUtils.addResult("success");
				latch.countDown();
			}
		};
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			
			@Override
			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
				TestUtils.addResult("fail");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
			}
		};		
		
		// Add stubs
		SocializeIOC.registerStub("authDialogFactory", mockFactory);
		SocializeIOC.registerStub("likeSystem", mockLikeSystem);
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		TestUtils.waitForIdle(testCase, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				try {
					assertTrue(latch.await(10, TimeUnit.SECONDS));
					String result = TestUtils.getNextResult();
					assertNotNull(result);
					assertEquals("success", result);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					fail();
				}
			}
		});				
	}
	
	public void testLikeCallsApiHost(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(testCase.getActivity()).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(testCase.getActivity()).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(testCase.getActivity()).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		
//		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
//			@Override
//			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
//				TestUtils.addResult("success");
//			}
//
//			@Override
//			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
//				listener.onGet(null); // not liked
//			}
//		};
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void like(Activity context, Entity entity, LikeAddListener listener) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Disable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				
				if(bean != null) {
					bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
				}
				else {
					System.err.println("No bean with name config!!!");
				}
				
//				ProxyObject<LikeSystem> proxy = container.getProxy("likeSystem");
//				if(proxy != null) {
//					proxy.setDelegate(mockLikeSystem);
//				}
//				else {
//					System.err.println("Proxy is null!!");
//				}
//				
			}
		});
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		TestUtils.waitForIdle(testCase, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(500);
				String result = TestUtils.getNextResult();
				assertNotNull(result);
				assertEquals("success", result);
			}
		});		
	}
	
	public void testLikeDoesNotPromptForAuthWhenNetworksNotSupported(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final AuthDialogFactory mockFactory = new AuthDialogFactory() {
			@Override
			public void show(Context parent, AuthDialogListener listener) {
				TestUtils.addResult("fail");
			}
		};
		
//		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
//			
//			@Override
//			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
//				TestUtils.addResult("success");
//			}
//
//			@Override
//			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
//				listener.onGet(null); // not liked
//			}
//		};		
		
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void like(Activity context, Entity entity, LikeAddListener listener) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);		
		
		final AuthProviderInfoBuilder mockAuthProviderInfoBuilder = new AuthProviderInfoBuilder() {
			@Override
			public <I extends AuthProviderInfo> AuthProviderInfoFactory<I> getFactory(AuthProviderType type) {
				return null;
			}

			@Override
			public boolean validateAll() {
				return false;
			}

			@Override
			public boolean isSupported(AuthProviderType type) {
				return false;
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Enable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				
				if(bean != null) {
					bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "true");
				}
				else {
					System.err.println("No bean with name config!!");
				}
				
				ProxyObject<AuthDialogFactory> proxy = container.getProxy("authDialogFactory");
				if(proxy != null) {
					proxy.setDelegate(mockFactory);
					proxy.setStaticProxy(true);
				}
				else {
					System.err.println("Proxy is null!!");
				}
				
//				ProxyObject<LikeSystem> proxyLike = container.getProxy("likeSystem");
//				if(proxyLike != null) {
//					proxyLike.setDelegate(mockLikeSystem);
//				}
//				else {
//					System.err.println("proxyLike is null!!");
//				}
				
				
				ProxyObject<AuthProviderInfoBuilder> proxyAuthProviderInfoBuilder = container.getProxy("authProviderInfoBuilder");
				
				if(proxyAuthProviderInfoBuilder != null) {
					proxyAuthProviderInfoBuilder.setDelegate(mockAuthProviderInfoBuilder);
				}
				else {
					System.err.println("proxyAuthProviderInfoBuilder is null!!");
				}
				
			}
		});		
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		TestUtils.waitForIdle(testCase, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				assertNotNull(result);
				assertEquals("success", result);
			}
		});				
	}	
	
	@UsesMocks ({SocializeSession.class, UserProviderCredentials.class})
	public void testLikeDoesNotPromptForAuthWhenUserIsAuthenticated(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		final UserProviderCredentials creds = AndroidMock.createMock(UserProviderCredentials.class);
		
		Entity entity = Entity.newInstance("http://entity1.com" + Math.random(), "no name");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			
			@Override
			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
			}
		};		
		
		final AuthDialogFactory mockFactory = new AuthDialogFactory() {
			@Override
			public void show(Context context, AuthDialogListener listener) {
				TestUtils.addResult("fail");
				latch.countDown();
			}
		};
		
		final ShareDialogFactory mockShareDialogFactory = new ShareDialogFactory() {

			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				TestUtils.addResult("success");
				latch.countDown();
			}
		};
		
		final AuthProviderInfoBuilder mockAuthProviderInfoBuilder = new AuthProviderInfoBuilder() {
			@Override
			public <I extends AuthProviderInfo> AuthProviderInfoFactory<I> getFactory(AuthProviderType type) {
				return null;
			}

			@Override
			public boolean validateAll() {
				return false;
			}

			@Override
			public boolean isSupported(AuthProviderType type) {
				return true;
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				fail();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Enable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "true");
				
				ProxyObject<AuthDialogFactory> proxy = container.getProxy("authDialogFactory");
				if(proxy != null) {
					proxy.setDelegate(mockFactory);
					proxy.setStaticProxy(true);
				}
				else {
					System.err.println("Proxy is null!!");
				}
				
				ProxyObject<ShareDialogFactory> proxyShareDialogFactory = container.getProxy("shareDialogFactory");
				if(proxyShareDialogFactory != null) {
					proxyShareDialogFactory.setDelegate(mockShareDialogFactory);
					proxyShareDialogFactory.setStaticProxy(true);
				}
				else {
					System.err.println("Proxy is null!!");
				}
				
				ProxyObject<AuthProviderInfoBuilder> proxyAuthProviderInfoBuilder = container.getProxy("authProviderInfoBuilder");
				
				if(proxyAuthProviderInfoBuilder != null) {
					proxyAuthProviderInfoBuilder.setDelegate(mockAuthProviderInfoBuilder);
				}
				else {
					System.err.println("proxyAuthProviderInfoBuilder is null!!");
				}
				
				ProxyObject<LikeSystem> proxyLike = container.getProxy("likeSystem");
				if(proxyLike != null) {
					proxyLike.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("proxyLike is null!!");
				}
				
			}
		});		
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		TestUtils.waitForIdle(testCase, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Dummy session
				Socialize.getSocialize().getSession().getUserProviderCredentials().put(AuthProviderType.TWITTER, creds);
				assertTrue(actionBar.getLikeButton().performClick());
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		String result = TestUtils.getNextResult();
		assertNotNull(result);
		assertEquals("success", result);
		
		Socialize.getSocialize().getSession().getUserProviderCredentials().remove(AuthProviderType.TWITTER);
	}		
	
}
