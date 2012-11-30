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
package com.socialize.test.integration.services.b;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.apache.http.entity.mime.HttpMultipartMode;
import com.socialize.apache.http.entity.mime.MultipartEntity;
import com.socialize.apache.http.entity.mime.content.ByteArrayBody;
import com.socialize.apache.http.entity.mime.content.ContentBody;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
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
import com.socialize.net.HttpRequestListener;
import com.socialize.net.HttpRequestProvider;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.twitter.PhotoTweet;
import com.socialize.networks.twitter.Tweet;
import com.socialize.networks.twitter.TwitterAccess;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.networks.twitter.TwitterUtilsImpl;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class TwitterUtilsTest extends SocializeActivityTest {
	
	public void test_link () throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = TestUtils.getActivity(this);
		
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
		TwitterUtils.setCredentials(TestUtils.getActivity(this), "foo", "bar");
		
		// Ensure we don't have a session
		TwitterUtils.unlink(TestUtils.getActivity(this));
		
		// Validate
		assertFalse(TwitterUtils.isLinked(getContext()));
		
		// Now Link
		TwitterUtils.link(context, null);
		
		latch.await(10, TimeUnit.SECONDS);
		
		TwitterAuthProviderInfo data = getResult(0);
		
		assertNotNull(data);
		assertEquals(AuthProviderType.TWITTER, data.getType());
		
		assertEquals("foo", data.getConsumerKey());
		assertEquals("bar", data.getConsumerSecret());
		
		SocializeIOC.unregisterStub("twitterProvider");
	}
	
	public void test_link_with_token () throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = TestUtils.getActivity(this);
		
		// Stub in the TwitterAuthProvider to ensure we DON'T auth with TW
		TwitterAuthProvider mockTwitterAuthProvider = new TwitterAuthProvider() {
			@Override
			public void authenticate(TwitterAuthProviderInfo info, AuthProviderListener listener) {
				addResult(1, "fail");
			}
		};
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);
		
		final String token = TestUtils.getDummyTwitterToken(context);
		final String secret = TestUtils.getDummyTwitterSecret(context);
		
		assertNotNull(token);
		assertNotNull(secret);
		
		assertFalse(StringUtils.isEmpty(token));
		assertFalse(StringUtils.isEmpty(secret));
		
		TwitterUtils.link(context, token, secret, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				Log.e("Socialize", error.getMessage(), error);
				addResult(1, "fail");
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				addResult(1, "fail");
				latch.countDown();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				addResult(0, session);
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				Log.e("Socialize", error.getMessage(), error);
				addResult(1, "fail");
			}
		});
		
		latch.await(10, TimeUnit.SECONDS);
		
		SocializeSession session = getResult(0);
		String fail = getResult(1);
		
		assertNull(fail);
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
		
		ConfigUtils.getConfig(getContext()).setTwitterKeySecret("foo", "bar");
		
		assertTrue(TwitterUtils.isAvailable(getContext()));
		
		ConfigUtils.getConfig(getContext()).setTwitterKeySecret(null, null);
		
		assertFalse(TwitterUtils.isAvailable(getContext()));
	}
	
	public void test_setCredentials() {
		String consumerKey = "foo";
		String consumerSecret = "bar";
		TwitterUtils.setCredentials(getContext(), consumerKey, consumerSecret);
		assertEquals(consumerKey, ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
		assertEquals(consumerSecret, ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
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
				addResult(0, "fail");
			}
			
			@Override
			public void onCancel() {
				addResult(0, "fail");
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
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		assertNull(getResult(0));
		
		do_test_post();
	}
	
	public void test_post_not_authed() throws Exception {
		Socialize.getSocialize().clearSessionCache(getContext());
		do_test_post();
	}
	
	public void testTweetPhoto() {
		
		final CustomMultipartEntity entity = new CustomMultipartEntity();
		
		TwitterUtilsImpl utils = new TwitterUtilsImpl() {
			@Override
			protected void post(Activity context, String resource, HttpEntity entity, SocialNetworkPostListener listener) {
				addResult(0, resource);
				addResult(1, entity);
				addResult(2, listener);
			}

			@Override
			protected MultipartEntity newMultipartEntity(HttpMultipartMode mode) {
				return entity;
			}
		};
		
		PhotoTweet tweet = new PhotoTweet();
		tweet.setImageData(new byte[] {1,2,3});
		tweet.setText("foobar");
		
		SocialNetworkPostListener listener = new SocialNetworkPostListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {}
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
		};
		
		utils.tweetPhoto(TestUtils.getActivity(this), tweet, listener);
		
		String resource = getResult(0);
		HttpEntity entityAfter = getResult(1);
		SocialNetworkPostListener listenerAfter = getResult(2);
		
		assertNotNull(resource);
		assertNotNull(entityAfter);
		assertNotNull(listenerAfter);
		
		assertSame(listener, listenerAfter);
		assertSame(entity, entityAfter);
		
		assertEquals("https://upload.twitter.com/1/statuses/update_with_media.json", resource);
		assertTrue(entityAfter instanceof CustomMultipartEntity);
		
		CustomMultipartEntity ce = (CustomMultipartEntity) entityAfter;
		Map<String, ContentBody> parts = ce.parts;
		
		assertNotNull(parts);
		
		ContentBody mediaBody = parts.get("media");
		ContentBody statusBody = parts.get("status");
		ContentBody possibly_sensitiveBody = parts.get("possibly_sensitive");
		
		assertNotNull(mediaBody);
		assertNotNull(statusBody);
		assertNotNull(possibly_sensitiveBody);
		
		assertTrue(mediaBody instanceof ByteArrayBody);
	}
	
	public void testTweetPhotoFlowAuthed() {
		
		TwitterUtilsImpl proxy = new TwitterUtilsImpl() {
			@Override
			public boolean isLinked(Context context) {
				return true;
			}

			@Override
			public void tweetPhoto(Activity context, PhotoTweet tweet, SocialNetworkPostListener listener) {
				addResult(0, "tweetPhoto");
			}
		};
		
		TwitterAccess.setTwitterUtilsProxy(proxy);
		
		PhotoTweet tweet = new PhotoTweet();
		tweet.setImageData(new byte[] {1,2,3});
		tweet.setText("foobar");
		
		TwitterUtils.tweetPhoto(TestUtils.getActivity(this), tweet, null);
		
		String tweetPhoto = getResult(0);
		assertNotNull(tweetPhoto);
		assertEquals("tweetPhoto", tweetPhoto);
	}
	
	enum LISTENER_ACTION  {AUTH_SUCCESS, ERROR, CANCEL};
	
	public void testTweetPhotoFlowNotAuthed() {
		doTestTweetPhotoFlowNotAuthed(LISTENER_ACTION.AUTH_SUCCESS);
		
		TestUtils.clear();
		
		doTestTweetPhotoFlowNotAuthed(LISTENER_ACTION.ERROR);
		
		TestUtils.clear();
		
		doTestTweetPhotoFlowNotAuthed(LISTENER_ACTION.CANCEL);
	}
	
	protected void doTestTweetPhotoFlowNotAuthed(final LISTENER_ACTION action) {
		
		TwitterUtilsImpl proxy = new TwitterUtilsImpl() {
			@Override
			public boolean isLinked(Context context) {
				return false;
			}

			@Override
			public void tweetPhoto(Activity context, PhotoTweet tweet, SocialNetworkPostListener listener) {
				addResult(0, "tweetPhoto");
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				switch (action) {
				case AUTH_SUCCESS:
					listener.onAuthSuccess(null);
					break;
					
				case CANCEL:
					listener.onCancel();
					break;
				case ERROR:
					listener.onError(null);
					break;					
				}
			}
		};
		
		TwitterAccess.setTwitterUtilsProxy(proxy);
		
		PhotoTweet tweet = new PhotoTweet();
		tweet.setImageData(new byte[] {1,2,3});
		tweet.setText("foobar");
		
		TwitterUtils.tweetPhoto(TestUtils.getActivity(this), tweet, new SocialNetworkPostListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				addResult(1, "onNetworkError");
			}
			
			@Override
			public void onCancel() {
				addResult(2, "onCancel");
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
		});
		
		
		String tweetPhoto = getResult(0);
		String onNetworkError = getResult(1);
		String onCancel = getResult(2);
		
		switch (action) {
		case AUTH_SUCCESS:
			assertNull(onNetworkError);
			assertNull(onCancel);
			assertNotNull(tweetPhoto);
			assertEquals("tweetPhoto", tweetPhoto);
			break;
			
		case CANCEL:
			assertNull(onNetworkError);
			assertNull(tweetPhoto);
			assertNotNull(onCancel);
			assertEquals("onCancel", onCancel);
			break;
		case ERROR:
			assertNull(tweetPhoto);
			assertNull(onCancel);
			assertNotNull(onNetworkError);
			assertEquals("onNetworkError", onNetworkError);
			break;					
		}
	}	
	
	public void testTweetFlowNotAuthed() {
		doTestTweetFlowNotAuthed(LISTENER_ACTION.AUTH_SUCCESS);
		
		TestUtils.clear();
		
		doTestTweetFlowNotAuthed(LISTENER_ACTION.ERROR);
		
		TestUtils.clear();
		
		doTestTweetFlowNotAuthed(LISTENER_ACTION.CANCEL);
	}

	protected void doTestTweetFlowNotAuthed(final LISTENER_ACTION action) {
		
		TwitterUtilsImpl proxy = new TwitterUtilsImpl() {
			@Override
			public boolean isLinked(Context context) {
				return false;
			}
			
			@Override
			public void post(Activity context, String resource, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, "tweeted");
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				switch (action) {
				case AUTH_SUCCESS:
					listener.onAuthSuccess(null);
					break;
					
				case CANCEL:
					listener.onCancel();
					break;
				case ERROR:
					listener.onError(null);
					break;					
				}
			}
		};
		
		TwitterAccess.setTwitterUtilsProxy(proxy);
		
		Tweet tweet = new Tweet();
		tweet.setText("foobar");
		
		TwitterUtils.tweet(TestUtils.getActivity(this), tweet, new SocialNetworkListener() {
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				addResult(1, "onBeforePost");
				return false;
			}

			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				addResult(2, "onNetworkError");
			}
			
			@Override
			public void onCancel() {
				addResult(3, "onCancel");
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
		});
		
		
		String tweeted = getResult(0);
		String onBeforePost = getResult(1);
		String onNetworkError = getResult(2);
		String onCancel = getResult(3);
		
		switch (action) {
		case AUTH_SUCCESS:
			assertNull(onNetworkError);
			assertNull(onCancel);
			assertNotNull(onBeforePost);
			assertNotNull(tweeted);
			assertEquals("tweeted", tweeted);
			break;
			
		case CANCEL:
			assertNull(onNetworkError);
			assertNull(tweeted);
			assertNull(onBeforePost);
			assertNotNull(onCancel);
			assertEquals("onCancel", onCancel);
			break;
		case ERROR:
			assertNull(tweeted);
			assertNull(onCancel);
			assertNull(onBeforePost);
			assertNotNull(onNetworkError);
			assertEquals("onNetworkError", onNetworkError);
			break;					
		}
	}	
	
	
	public void testPostFlowNotAuthed() {
		doTestPostFlowNotAuthed(LISTENER_ACTION.AUTH_SUCCESS);
		
		TestUtils.clear();
		
		doTestPostFlowNotAuthed(LISTENER_ACTION.ERROR);
		
		TestUtils.clear();
		
		doTestPostFlowNotAuthed(LISTENER_ACTION.CANCEL);
	}
	
	public void testTweetUsesCustomPath() {
		
		final String path = "foobar_path";
		
		TwitterUtilsImpl twitterUtils = new TwitterUtilsImpl() {
			@Override
			protected void post(final Activity context, String resource, HttpEntity entity, final SocialNetworkPostListener listener) {
				addResult(0, resource);
			}
		};
		
		SocialNetworkListener listener = new SocialNetworkListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {}
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				postData.setPath(path);
				return false;
			}
		};
		
		Tweet tweet = new Tweet();
		tweet.setShareLocation(false);
		tweet.setText("foobar");
		
		twitterUtils.setApiEndpoint("mock");
		twitterUtils.tweet(TestUtils.getActivity(this), tweet, listener);
		
		String result = getResult(0);
		
		assertEquals("mock"+path, result);
		
	}

	protected void doTestPostFlowNotAuthed(final LISTENER_ACTION action) {
		
		TwitterUtilsImpl proxy = new TwitterUtilsImpl() {
			@Override
			public boolean isLinked(Context context) {
				return false;
			}
			
			@Override
			public void post(Activity context, String resource, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, resource);
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				switch (action) {
				case AUTH_SUCCESS:
					listener.onAuthSuccess(null);
					break;
					
				case CANCEL:
					listener.onCancel();
					break;
				case ERROR:
					listener.onError(null);
					break;					
				}
			}
		};
		
		TwitterAccess.setTwitterUtilsProxy(proxy);
		
		String resource = "foobar";
		Map<String, Object> postData = new HashMap<String, Object>();
		
		TwitterUtils.post(TestUtils.getActivity(this), resource, postData, new SocialNetworkPostListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				addResult(2, "onNetworkError");
			}
			
			@Override
			public void onCancel() {
				addResult(3, "onCancel");
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
		});
		
		
		String post = getResult(0);
		String onNetworkError = getResult(2);
		String onCancel = getResult(3);
		
		switch (action) {
		case AUTH_SUCCESS:
			assertNull(onNetworkError);
			assertNull(onCancel);
			assertNotNull(post);
			assertEquals(resource, post);
			break;
			
		case CANCEL:
			assertNull(onNetworkError);
			assertNull(post);
			assertNotNull(onCancel);
			assertEquals("onCancel", onCancel);
			break;
		case ERROR:
			assertNull(post);
			assertNull(onCancel);
			assertNotNull(onNetworkError);
			assertEquals("onNetworkError", onNetworkError);
			break;					
		}
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
		
		final HttpResponse mockResponse = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("https", 1, 0), 200, ""));
		final String responseData = "{foo:bar}";
		
		// Stub in the http provider factory for the tweet
		HttpRequestProvider mockProvider = new HttpRequestProvider() {
			
			@Override
			public void post(HttpPost request, HttpRequestListener listener) {
				listener.onSuccess(mockResponse, responseData);
			}
			
			@Override
			public void get(HttpGet request, HttpRequestListener listener) {
				listener.onSuccess(mockResponse, responseData);
			}
		};
		
		
		SocializeIOC.registerStub("twitterProvider", mockTwitterAuthProvider);
		SocializeIOC.registerStub("httpRequestProvider", mockProvider);

		TwitterUtils.tweetEntity(TestUtils.getActivity(this), entity, "AndroidSDK Test", new SocialNetworkShareListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				addResult(3, "onBeforePost");
				
				return false;
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject response) {
				addResult(1, "onAfterPost");
				latch.countDown();
			}
			
			@Override
			public void onCancel() {}
		});
		
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		
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
		
		assertTrue(latch2.await(20, TimeUnit.SECONDS));
		
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

	class CustomMultipartEntity extends MultipartEntity {
		Map<String, ContentBody> parts = new HashMap<String, ContentBody>();
		@Override
		public void addPart(String name, ContentBody contentBody) {
			parts.put(name, contentBody);
			super.addPart(name, contentBody);
		}
	}
	
}
