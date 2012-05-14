/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test.ui.integrationtest.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.ShareOptions;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.auth.AuthDialogFactory;
import com.socialize.ui.auth.AuthDialogListener;

/**
 * @author Jason Polites
 *
 */
public class ActionBarTestUtils2 {
	
	public void testLikePromptsForAuth(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final AuthDialogFactory mockFactory = new AuthDialogFactory() {
			@Override
			public void show(Context parent, AuthDialogListener listener) {
				TestUtils.addResult("success");
			}
		};
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			@Override
			public void addLike(SocializeSession session, Entity entity, ShareOptions options, LikeListener listener) {
				TestUtils.addResult("fail");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
			}
		};		
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				ActivityInstrumentationTestCase2.fail();
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
		
		testCase.getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		ActivityInstrumentationTestCase2.assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivityInstrumentationTestCase2.assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				ActivityInstrumentationTestCase2.assertNotNull(result);
				ActivityInstrumentationTestCase2.assertEquals("success", result);
			}
		});				
	}
	
	public void testLikeCallsApiHost(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			@Override
			public void addLike(SocializeSession session, Entity entity, ShareOptions options, LikeListener listener) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				ActivityInstrumentationTestCase2.fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Disable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
				
				ProxyObject<LikeSystem> proxy = container.getProxy("likeSystem");
				if(proxy != null) {
					proxy.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("Proxy is null!!");
				}
			}
		});
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		testCase.getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		ActivityInstrumentationTestCase2.assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivityInstrumentationTestCase2.assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				ActivityInstrumentationTestCase2.assertNotNull(result);
				ActivityInstrumentationTestCase2.assertEquals("success", result);
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
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			@Override
			public void addLike(SocializeSession session, Entity entity, ShareOptions options, LikeListener listener) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
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
				return false;
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				ActivityInstrumentationTestCase2.fail();
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
				
				ProxyObject<LikeSystem> proxyLike = container.getProxy("likeSystem");
				if(proxyLike != null) {
					proxyLike.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("proxyLike is null!!");
				}
				
				
				ProxyObject<AuthProviderInfoBuilder> proxyAuthProviderInfoBuilder = container.getProxy("authProviderInfoBuilder");
				
				if(proxyLike != null) {
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
		
		testCase.getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		ActivityInstrumentationTestCase2.assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivityInstrumentationTestCase2.assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				ActivityInstrumentationTestCase2.assertNotNull(result);
				ActivityInstrumentationTestCase2.assertEquals("success", result);
			}
		});				
	}	
	
	@UsesMocks ({SocializeSession.class, UserProviderCredentials.class})
	public void testLikeDoesNotPromptForAuthWhenUserIsAuthenticated(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
//		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final UserProviderCredentials creds = AndroidMock.createMock(UserProviderCredentials.class);
		
//		AndroidMock.expect(session.getUserProviderCredentials((AuthProviderType)AndroidMock.anyObject())).andReturn(creds).anyTimes();
//		AndroidMock.replay(session);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final AuthDialogFactory mockFactory = new AuthDialogFactory() {
			@Override
			public void show(Context context, AuthDialogListener listener) {
				TestUtils.addResult("fail");
			}
		};
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			@Override
			public void addLike(SocializeSession session, Entity entity, ShareOptions options, LikeListener listener) {
				TestUtils.addResult("success");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
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
				ActivityInstrumentationTestCase2.fail();
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
				
				ProxyObject<LikeSystem> proxyLike = container.getProxy("likeSystem");
				if(proxyLike != null) {
					proxyLike.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("proxyLike is null!!");
				}
				
				ProxyObject<AuthProviderInfoBuilder> proxyAuthProviderInfoBuilder = container.getProxy("authProviderInfoBuilder");
				
				if(proxyLike != null) {
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
		
		testCase.getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		ActivityInstrumentationTestCase2.assertNotNull(actionBar);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Dummy session
				Socialize.getSocialize().getSession().getUserProviderCredentials().put(AuthProviderType.TWITTER, creds);
				
				ActivityInstrumentationTestCase2.assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				latch.countDown();
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		String result = TestUtils.getNextResult();
		ActivityInstrumentationTestCase2.assertNotNull(result);
		ActivityInstrumentationTestCase2.assertEquals("success", result);
		
		Socialize.getSocialize().getSession().getUserProviderCredentials().remove(AuthProviderType.TWITTER);
		
//		AndroidMock.verify(session);
	}		
	
}
