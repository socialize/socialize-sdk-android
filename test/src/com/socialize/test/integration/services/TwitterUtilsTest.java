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
package com.socialize.test.integration.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.twitter.TwitterAuthProvider;
import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class TwitterUtilsTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().clearSessionCache(getContext());
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test_link () throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getActivity();
		
		// Stub in the TwitterAuthProvider
		TwitterAuthProvider mockTwitterAuthProvider = new TwitterAuthProvider() {
			@Override
			public void authenticate(TwitterAuthProviderInfo info, AuthProviderListener listener) {
				addResult(0, info);
				latch.countDown();
			}
		};
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);
		
		// Set a mock key/secret
		TwitterUtils.setCredentials(getActivity(), "foo", "bar");
		
		// Ensure we don't have a session
		TwitterUtils.unlink(getActivity());
		
		// Validate
		assertFalse(TwitterUtils.isLinked(getContext()));
		
		// Now Link
		TwitterUtils.link(context, null);
		
		latch.await(20, TimeUnit.SECONDS);
		
		TwitterAuthProviderInfo data = getResult(0);
		
		assertNotNull(data);
		assertEquals(AuthProviderType.TWITTER, data.getType());
		
		assertEquals("foo", data.getConsumerKey());
		assertEquals("bar", data.getConsumerSecret());
		
		SocializeIOC.unregisterStub("twitterProvider");
	}
	
	public void test_link_with_token () throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getActivity();
		
		// Stub in the TwitterAuthProvider to ensure we DON'T auth with TW
		TwitterAuthProvider mockTwitterAuthProvider = new TwitterAuthProvider() {
			@Override
			public void authenticate(TwitterAuthProviderInfo info, AuthProviderListener listener) {
				fail();
			}
		};
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);
		
		// Set a mock key/secret
		TwitterUtils.setCredentials(getActivity(), "foo", "bar");
		
		final String token = TestUtils.getDummyTwitterToken(getContext());
		final String secret = TestUtils.getDummyTwitterSecret(getContext());
		
		assertNotNull(token);
		assertNotNull(secret);
		
		TwitterUtils.link(context, token, secret, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				addResult(0, session);
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeSession session = getResult(0);
		
		assertNotNull(session);
		
		UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(AuthProviderType.TWITTER);
		
		assertNotNull(userProviderCredentials);
		assertEquals(token, userProviderCredentials.getAccessToken());
		assertEquals(secret, userProviderCredentials.getTokenSecret());
		assertTrue(TwitterUtils.isLinked(context));
		assertEquals(token, TwitterUtils.getAccessToken(context));
		assertEquals(secret, TwitterUtils.getTokenSecret(context));
		
		SocializeIOC.unregisterStub("twitterProvider");
	}
	
	public void test_isAvailable() {
		
		Socialize.getSocialize().getConfig().setTwitterKeySecret("foo", "bar");
		
		assertTrue(TwitterUtils.isAvailable(getContext()));
		
		Socialize.getSocialize().getConfig().setTwitterKeySecret(null, null);
		
		assertFalse(TwitterUtils.isAvailable(getContext()));
	}
	
	public void test_setCredentials() {
		String consumerKey = "foo";
		String consumerSecret = "bar";
		TwitterUtils.setCredentials(getContext(), consumerKey, consumerSecret);
		assertEquals(consumerKey, Socialize.getSocialize().getConfig().getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
		assertEquals(consumerSecret, Socialize.getSocialize().getConfig().getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
	}
	
	
	public void test_post_authed() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final String token = TestUtils.getDummyTwitterToken(getContext());
		final String secret = TestUtils.getDummyTwitterSecret(getContext());
		// Stub in the TwitterAuthProvider
		TwitterAuthProvider mockTwitterAuthProvider = new TwitterAuthProvider() {
			@Override
			public void authenticate(TwitterAuthProviderInfo info, AuthProviderListener listener) {
				AuthProviderResponse response = new AuthProviderResponse();
				response.setToken(token);
				response.setSecret(secret);
				listener.onAuthSuccess(response);
			}
		};
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);
		
		TwitterUtils.link(getContext(), new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();	
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
		
		latch.await(20, TimeUnit.SECONDS);
		do_test_post();
	}
	
	public void test_post_not_authed() throws Exception {
		Socialize.getSocialize().clearSessionCache(getContext());
		do_test_post();
	}
	
	protected void do_test_post() throws Exception {
		String entityKeyRandom = "foobar" + Math.random();
		Entity entity = Entity.newInstance(entityKeyRandom, "foobar");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final String token = TestUtils.getDummyTwitterToken(getContext());
		final String secret = TestUtils.getDummyTwitterSecret(getContext());
		
		// Stub in the TwitterAuthProvider
		TwitterAuthProvider mockTwitterAuthProvider = new TwitterAuthProvider() {
			@Override
			public void authenticate(TwitterAuthProviderInfo info, AuthProviderListener listener) {
				AuthProviderResponse response = new AuthProviderResponse();
				response.setToken(token);
				response.setSecret(secret);
				listener.onAuthSuccess(response);
			}
		};
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);

		TwitterUtils.tweetEntity(getActivity(), entity, "AndroidSDK Test", new SocialNetworkListener() {
			
			@Override
			public void onPostError(Activity context, SocialNetwork network, Exception error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				addResult(3, "onBeforePost");
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork) {
				addResult(1, "onAfterPost");
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// Make sure we have a share object
		ShareUtils.getSharesByUser(getContext(), UserUtils.getCurrentUser(getContext()), 0, 100, new ShareListListener() {
			@Override
			public void onList(ListResult<Share> entities) {
				addResult(2, entities);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		SocializeIOC.unregisterStub("twitterProvider");
		
		String onAfterPost = getResult(1);
		ListResult<Share> shares = getResult(2);
		String onBeforePost = getResult(3);
		
		assertNotNull(onAfterPost);
		assertNotNull(shares);
		assertNotNull(onBeforePost);
		
		// Find the share
		assertTrue(shares.size() > 0);
		
		List<Share> items = shares.getItems();
		Share match = null;
		for (Share share : items) {
			if(share.getEntity().getKey().equals(entityKeyRandom)) {
				match = share;
				break;
			}
		}
		
		assertNotNull(match);
	}
	
}
