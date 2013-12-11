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
package com.socialize.test.integration.services.a;

import android.app.Activity;
import android.content.Context;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookAccess;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.facebook.FacebookUtilsImpl;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsTest extends SocializeActivityTest {

	public void test_isAvailable() {
		
		ConfigUtils.getConfig(getContext()).setFacebookAppId("foobar");
		
		assertTrue(FacebookUtils.isAvailable(getContext()));
		
		ConfigUtils.getConfig(getContext()).setFacebookAppId(null);
		
		assertFalse(FacebookUtils.isAvailable(getContext()));
	}
	
	public void test_setAppId() {
		String appId = "foobar";
		FacebookUtils.setAppId(getContext(), appId);
		assertEquals(appId, ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.FACEBOOK_APP_ID));
	}

	public void testFlowPostEntityNotAuthed() {
		
		final Entity entity = Entity.newInstance("foo", "bar");
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = Mockito.mock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String text = "foobar";

		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
				addResult(0, text);
			}

			@Override
			public boolean isLinkedForWrite(Context context, String... permissions) {
				return false;
			}

			@Override
			public void linkForWrite(Activity context, SocializeAuthListener listener, String... permissions) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		FacebookUtils.postEntity(context, entity, text, mockSocialNetworkShareListener);

        Mockito.verify(mockSocialNetworkShareListener, Mockito.times(2)).onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
        Mockito.verify(mockSocialNetworkShareListener).onCancel();

		assertEquals(text, getResult(0));
	}
	
	
	public void testFlowPostNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = Mockito.mock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		

		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}

			@Override
			public boolean isLinkedForWrite(Context context, String... permissions) {
				return false;
			}

			@Override
			public void linkForWrite(Activity context, SocializeAuthListener listener, String... permissions) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}			
			
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		FacebookUtils.post(context, graphPath, params, mockSocialNetworkShareListener);

        Mockito.verify(mockSocialNetworkShareListener, Mockito.times(2)).onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
        Mockito.verify(mockSocialNetworkShareListener).onCancel();

		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}
	
	
	public void testFlowGetNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = Mockito.mock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}
			
			@Override
			public boolean isLinkedForRead(Context context, String... permissions) {
				return false;
			}

			@Override
			public void linkForRead(Activity context, SocializeAuthListener listener, String... permissions) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		FacebookUtils.get(context, graphPath, params, mockSocialNetworkShareListener);

        Mockito.verify(mockSocialNetworkShareListener, Mockito.times(2)).onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
        Mockito.verify(mockSocialNetworkShareListener).onCancel();

		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}	
	
	
	public void testFlowDeleteNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = Mockito.mock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public boolean isLinkedForWrite(Context context, String... permissions) {
				return false;
			}

			@Override
			public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}
			
			@Override
			public void linkForWrite(Activity context, SocializeAuthListener listener, String... permissions) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
        FacebookUtils.delete(context, graphPath, params, mockSocialNetworkShareListener);

        Mockito.verify(mockSocialNetworkShareListener, Mockito.times(2)).onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
        Mockito.verify(mockSocialNetworkShareListener).onCancel();
		
		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}		
	

	public void testLinkIsLinkedForRead() throws Exception {
		
		// We have to use a real token here because we will be REALLY authenticating
		final Activity context = TestUtils.getActivity(this);
		final String newFBToken = TestUtils.getDummyFBToken(context);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// We have to be initialized to set fb
		Socialize.getSocialize().init(context);	
		
		FacebookUtils.linkForRead(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				latch.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});		
		
		boolean result = latch.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		assertTrue(FacebookUtils.isLinkedForRead(context));
		assertFalse(FacebookUtils.isLinkedForWrite(context));
	}	
	

	public void testLinkIsLinkedForWrite() throws Exception {
		
		// We have to use a real token here because we will be REALLY authenticating
		final Activity context = TestUtils.getActivity(this);
		final String newFBToken = TestUtils.getDummyFBToken(context);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// We have to be initialized to set fb
		Socialize.getSocialize().init(context);	
		
		FacebookUtils.linkForWrite(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				latch.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});		
		
		boolean result = latch.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		assertTrue(FacebookUtils.isLinkedForWrite(context));
		assertTrue(FacebookUtils.isLinkedForRead(context));
	}	
	
	public void testLinkIsLinkedForReadAndWrite() throws Exception {
		
		// We have to use a real token here because we will be REALLY authenticating
		final Activity context = TestUtils.getActivity(this);
		final String newFBToken = TestUtils.getDummyFBToken(context);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// We have to be initialized to set fb
		Socialize.getSocialize().init(context);	
		
		FacebookUtils.linkForWrite(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error)   {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				latch.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});		
		
		boolean result = latch.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		FacebookUtils.linkForRead(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
			
			@Override
			public void onCancel() {
				latch2.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch2.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});				
		
		result = latch2.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		assertTrue(FacebookUtils.isLinkedForRead(context));
		assertTrue(FacebookUtils.isLinkedForWrite(context));
	}	
	
	public void testLinkIsLinkedForWriteAndRead() throws Exception {
		
		// We have to use a real token here because we will be REALLY authenticating
		final Activity context = TestUtils.getActivity(this);
		final String newFBToken = TestUtils.getDummyFBToken(context);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// We have to be initialized to set fb
		Socialize.getSocialize().init(context);	
		
		FacebookUtils.linkForRead(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				latch.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});		
		
		boolean result = latch.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		FacebookUtils.linkForWrite(context, newFBToken, false, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
			
			@Override
			public void onCancel() {
				latch2.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				latch2.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});				
		
		result = latch2.await(10, TimeUnit.SECONDS);
		
		assertTrue(result);		
		
		assertTrue(FacebookUtils.isLinkedForRead(context));
		assertTrue(FacebookUtils.isLinkedForWrite(context));
	}	
		

}
