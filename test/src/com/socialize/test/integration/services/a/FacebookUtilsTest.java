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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.facebook.FacebookAuthProvider;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.Facebook;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.networks.facebook.FacebookAccess;
import com.socialize.networks.facebook.FacebookPermissionCallback;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.facebook.FacebookUtilsImpl;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsTest extends SocializeActivityTest {
	
	public void test_link () throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = TestUtils.getActivity(this);
		
		// Stub in the FacebookAuthProvider
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				addResult(0, info);
				latch.countDown();
			}
		};
		
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		// Set a mock FB ID
		FacebookUtils.setAppId(TestUtils.getActivity(this), "foobar");
		
		// Ensure we don't have a session
		FacebookUtils.unlink(TestUtils.getActivity(this));
		
		// Validate
		assertFalse(FacebookUtils.isLinked(getContext()));
		
		// Now Link
		FacebookUtils.link(context, null);
		
		latch.await(20, TimeUnit.SECONDS);
		
		FacebookAuthProviderInfo data = getResult(0);
		
		assertNotNull(data);
		assertEquals(AuthProviderType.FACEBOOK, data.getType());
		assertEquals("foobar", data.getAppId());
		
		SocializeIOC.unregisterStub("facebookProvider");
	}
	
	public void test_link_with_token () throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = TestUtils.getActivity(this);
		
		// Stub in the FacebookAuthProvider to ensure we DON'T auth with FB
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				fail();
			}
		};
		
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		// Set a mock FB ID
		FacebookUtils.setAppId(TestUtils.getActivity(this), "foobar");
		
		FacebookUtils.link(context, TestUtils.getDummyFBToken(getContext()), false, new SocializeAuthListener() {
			
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
				addResult(0, session);
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeSession session = getResult(0);
		
		assertNotNull(session);
		
		UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
		
		assertNotNull(userProviderCredentials);
		assertEquals(TestUtils.getDummyFBToken(getContext()), userProviderCredentials.getAccessToken());
		assertTrue(FacebookUtils.isLinked(context));
		assertEquals(TestUtils.getDummyFBToken(getContext()), FacebookUtils.getAccessToken(context));
		
		SocializeIOC.unregisterStub("facebookProvider");
	}
	
	public void test_link_with_token_and_permission_check () throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = TestUtils.getActivity(this);
		
		// Stub in the FacebookAuthProvider to ensure we DON'T auth with FB
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				// We expect an auth to FB because out permissions will not match.
				addResult(0, info);
				
				// Make sure we call the listener to move the test along.
				listener.onAuthSuccess(null);
			}
		};
		
		DefaultFacebookWallPoster mockWallPoster = new DefaultFacebookWallPoster() {
			@Override
			public void getCurrentPermissions(Activity parent, String token, FacebookPermissionCallback callback) {
				callback.onSuccess(new String[]{"foobar_permission"});
			}
		};
		
		SocializeIOC.registerStub("facebookWallPoster", mockWallPoster);
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		// Set a mock FB ID
		FacebookUtils.setAppId(TestUtils.getActivity(this), "foobar");
		
		FacebookUtils.link(context, TestUtils.getDummyFBToken(getContext()), true, new SocializeAuthListener() {
			
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
				addResult(1, session);
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		FacebookAuthProviderInfo fbInfo = getResult(0);
		SocializeSession session = getResult(1);
		
		assertNotNull(fbInfo);
		assertNotNull(session);
		
		UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
		
		assertNotNull(userProviderCredentials);
		assertEquals(TestUtils.getDummyFBToken(getContext()), userProviderCredentials.getAccessToken());
		assertTrue(FacebookUtils.isLinked(context));
		assertEquals(TestUtils.getDummyFBToken(getContext()), FacebookUtils.getAccessToken(context));
		
		// Make sure the dbInfo contains at least the default permissions
		String[] permissions = fbInfo.getPermissions();
		
		assertNotNull(permissions);
		
		Arrays.sort(permissions);
		
		for (String required : FacebookService.DEFAULT_PERMISSIONS) {
			assertTrue(Arrays.binarySearch(permissions, required) >= 0);
		}
		
		SocializeIOC.unregisterStub("facebookProvider");
		SocializeIOC.unregisterStub("facebookWallPoster");
	}	
	
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
	
	
	public void test_post_authed() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final String token = TestUtils.getDummyFBToken(getContext());
		// Stub in the FacebookAuthProvider
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				AuthProviderResponse response = new AuthProviderResponse();
				response.setToken(token);
				listener.onAuthSuccess(response);
			}
		};
		
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		FacebookUtils.link(getContext(), new SocializeAuthListener() {
			
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
		do_test_post();
	}
	
	protected void do_test_post() throws Exception {
		String entityKeyRandom = "foobar" + Math.random();
		Entity entity = Entity.newInstance(entityKeyRandom, "foobar");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Stub in the fb runner
		AsyncFacebookRunner mockRunner = new AsyncFacebookRunner(null) {
		    public void request(
		    		final String graphPath,
                    final Bundle parameters,
                    final String httpMethod,
                    final RequestListener listener,
                    final Object state) {
		    	
		    	addResult(0, parameters);
		    	
		    	// Ensure the listener is called so we get onAfterPost
		    	listener.onComplete(null, state);
		    	
		    	latch.countDown();
		    }
		};
		
		final String token = TestUtils.getDummyFBToken(getContext());
		
		// Stub in the FacebookAuthProvider
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				AuthProviderResponse response = new AuthProviderResponse();
				response.setToken(token);
				listener.onAuthSuccess(response);
			}
		};
		
		SocializeIOC.registerStub("facebookRunner", mockRunner);
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);

		FacebookUtils.postEntity(TestUtils.getActivity(this), entity, "test", new SocialNetworkShareListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				// Add some extra data
				postData.getPostValues().put("foo", "bar");
				postData.getPostValues().put("foo2", "bar2");
				
				addResult(3, postData.getPropagationInfo());
				
				return false;
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject response) {
				addResult(1, "onAfterPost");
			}

			@Override
			public void onCancel() {}
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
		
		SocializeIOC.unregisterStub("facebookRunner");
		SocializeIOC.unregisterStub("facebookProvider");
		
		// Now verify the shares and the extra info sent to FB
		Bundle params = getResult(0);
		String onAfterPost = getResult(1);
		ListResult<Share> shares = getResult(2);
		PropagationInfo propagationInfo = getResult(3);
		
		assertNotNull(params);
		assertNotNull(onAfterPost);
		assertNotNull(shares);
		assertNotNull(propagationInfo);
		assertNotNull(propagationInfo.getEntityUrl());
		assertNotNull(propagationInfo.getAppUrl());
		
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
	
	
	@UsesMocks ({SocialNetworkShareListener.class})
	public void testFlowPostEntityNotAuthed() {
		
		final Entity entity = Entity.newInstance("foo", "bar");
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = AndroidMock.createMock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String text = "foobar";
		
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onCancel();
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
				addResult(0, text);
			}

			@Override
			public boolean isLinked(Context context) {
				return false;
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		AndroidMock.replay(mockSocialNetworkShareListener);
		
		FacebookUtils.postEntity(context, entity, text, mockSocialNetworkShareListener);
		
		AndroidMock.verify(mockSocialNetworkShareListener);
		
		assertEquals(text, getResult(0));
	}
	
	
	@UsesMocks ({SocialNetworkShareListener.class})
	public void testFlowPostNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = AndroidMock.createMock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onCancel();
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}

			@Override
			public boolean isLinked(Context context) {
				return false;
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		AndroidMock.replay(mockSocialNetworkShareListener);
		
		FacebookUtils.post(context, graphPath, params, mockSocialNetworkShareListener);
		
		AndroidMock.verify(mockSocialNetworkShareListener);
		
		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}
	
	
	@UsesMocks ({SocialNetworkShareListener.class})
	public void testFlowGetNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = AndroidMock.createMock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onCancel();
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}

			@Override
			public boolean isLinked(Context context) {
				return false;
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		AndroidMock.replay(mockSocialNetworkShareListener);
		
		FacebookUtils.get(context, graphPath, params, mockSocialNetworkShareListener);
		
		AndroidMock.verify(mockSocialNetworkShareListener);
		
		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}	
	
	
	@UsesMocks ({SocialNetworkShareListener.class})
	public void testFlowDeleteNotAuthed() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final Activity context = TestUtils.getActivity(this);
		final SocialNetworkShareListener mockSocialNetworkShareListener = AndroidMock.createMock(SocialNetworkShareListener.class);
		final SocializeException mockError = new SocializeException("TEST ERROR - IGNORE ME");
		final String graphPath = "foobarPath";
		
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onNetworkError(context, SocialNetwork.FACEBOOK, mockError);
		mockSocialNetworkShareListener.onCancel();
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {

			@Override
			public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, graphPath);
				addResult(1, postData);
			}

			@Override
			public boolean isLinked(Context context) {
				return false;
			}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				listener.onError(mockError);
				listener.onAuthFail(mockError);
				listener.onCancel();
				listener.onAuthSuccess(null);
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		AndroidMock.replay(mockSocialNetworkShareListener);
		
		FacebookUtils.delete(context, graphPath, params, mockSocialNetworkShareListener);
		
		AndroidMock.verify(mockSocialNetworkShareListener);
		
		assertEquals(graphPath, getResult(0));
		assertSame(params, getResult(1));
	}		
	
	
	public void testExtendAccessToken() throws Exception {
		
		// We have to use a real token here because we will be REALLY authenticating
		final Activity context = TestUtils.getActivity(this);
		final String newFBToken = TestUtils.getDummyFBToken(context);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// We have to be initialized to set fb
		Socialize.getSocialize().init(context);
		
		Facebook mockFacebook = new Facebook("foobar") {
			@Override
			public boolean extendAccessTokenIfNeeded(Context context, ServiceListener serviceListener) {
				Bundle values = new Bundle();
				values.putString(Facebook.TOKEN, newFBToken);
				serviceListener.onComplete(values);
				return true;
			}
		};
		
		// Override FB
		FacebookAccess.setFacebook(mockFacebook);		
		
		// We need to ensure we are linked to FB first
		FacebookUtils.link(context, newFBToken, false, new SocializeAuthListener() {
			
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
				
				// Now, swap out the REAL token for a dummy one so we invalidate the session
				UserProviderCredentials creds = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
				
				if(creds != null && creds instanceof DefaultUserProviderCredentials) {
					
					((DefaultUserProviderCredentials)creds).setAccessToken("foobar");
					
					// Now try to extend.  The session should not match locally due to the token mismatch and the new token 
					// from fake facebook should be assigned to the user.
					FacebookUtils.extendAccessToken(context, new SocializeAuthListener() {
						
						@Override
						public void onError(SocializeException error) {
							latch.countDown();
						}
						
						@Override
						public void onCancel() {
							latch.countDown();
						}
						
						@Override
						public void onAuthSuccess(SocializeSession session) {
							addResult(0, session);
							latch.countDown();
						}
						
						@Override
						public void onAuthFail(SocializeException error) {
							latch.countDown();
						}
					});
					
				}
				else {
					latch.countDown();
				}
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		SocializeSession session = getResult(0);
		assertNotNull(session);
		
		UserProviderCredentials creds = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
		
		assertNotNull(creds);
		assertEquals(newFBToken, creds.getAccessToken());
	}
	
	
	public void testFacebookPermissionCallback() {
		
		// Taken from actual response:
		// http://developers.facebook.com/tools/explorer/?method=GET&path=me%2Fpermissions
		String mockResponse = "{ \"data\": [ { \"installed\": 1, \"read_friendlists\": 1, \"user_games_activity\": 0, \"user_subscriptions\": 1 } ]}";
		
		FacebookPermissionCallback cb = new FacebookPermissionCallback() {
			
			@Override
			public void onError(SocializeException error) {}
			
			@Override
			public void onSuccess(String[] permissions) {
				addResult(0, permissions);
			}
		};
		
		cb.onComplete(mockResponse, null);
		
		
		String[] permissions = getResult(0);
		
		assertNotNull(permissions);
		assertEquals(3, permissions.length);
		
		Arrays.sort(permissions);
		
		assertTrue(Arrays.binarySearch(permissions, "installed") >= 0);
		assertTrue(Arrays.binarySearch(permissions, "read_friendlists") >= 0);
		assertTrue(Arrays.binarySearch(permissions, "user_subscriptions") >= 0);
	}
}
