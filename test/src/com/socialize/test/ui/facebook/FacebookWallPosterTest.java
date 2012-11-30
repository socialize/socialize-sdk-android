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
package com.socialize.test.ui.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeAccess;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.config.SocializeConfigUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.Share;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.AsyncFacebookRunner.RequestListener;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.networks.DefaultPostData;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockableRequestListener;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.ImageUtils;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	SocialNetworkListener.class,
	ShareMessageBuilder.class,
	SocializeConfig.class,
	PropagationInfo.class
})
public class FacebookWallPosterTest extends SocializeActivityTest {

	
	public void testPostComment() {
		testPostComment("foobar_comment");
	}
	
	public void testPostLikeNoOG() {
		
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		Activity parent = TestUtils.getActivity(this);
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		AndroidMock.expect(config.isOGLike()).andReturn(false);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			
			@Override
			public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
				addResult(3, propInfo);
			}
		};
		
		AndroidMock.replay(config);
		
		poster.setConfig(config);
		poster.postLike(parent, entity, info, listener);
		
		AndroidMock.verify(config);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		PropagationInfo infoAfter = getResult(3);
		
		assertSame(listener, listenerAfter);
		assertSame(info, infoAfter);
		assertEquals("", messageAfter);
		assertSame(parent, parentAfter);
	}	
	
	public void testPostLikeWithOG() {
		
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		Activity parent = TestUtils.getActivity(this);
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String entityUrl = "foobar_url";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		AndroidMock.expect(config.isOGLike()).andReturn(true);
		AndroidMock.expect(info.getEntityUrl()).andReturn(entityUrl);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			
			@Override
			public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
				addResult(0, parent);
				addResult(1, graphPath);
				addResult(2, listener);
				addResult(3, postData);
			}
		};
		
		AndroidMock.replay(config, info);
		
		poster.setConfig(config);
		poster.postLike(parent, entity, info, listener);
		
		AndroidMock.verify(config, info);
		
		Activity parentAfter = getResult(0);
		String graphPathAfter = getResult(1);
		SocialNetworkListener listenerAfter = getResult(2);
		Map<String, Object> postDataAfter = getResult(3);
		
		assertNotNull(parentAfter);
		assertNotNull(graphPathAfter);
		assertNotNull(listenerAfter);
		assertNotNull(postDataAfter);
		
		assertSame(listener, listenerAfter);
		assertSame(parent, parentAfter);
		assertEquals("me/og.likes", graphPathAfter);
		
		Object object = postDataAfter.get("object");
		assertNotNull(object);
		assertEquals(entityUrl, object);
		
	}		
	
	public void testPostComment(String expectedString) {
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		Activity parent = TestUtils.getActivity(this);
		
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String comment = "foobar_comment";
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
				addResult(3, propInfo);
			}
		};
		
		poster.postComment(parent, entity, comment, info, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		PropagationInfo infoAfter = getResult(3);
		
		assertSame(info, infoAfter);
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}	
	
	@UsesMocks ({
		SocializeConfig.class,
		SocialNetworkListener.class,
		SocializeService.class,
		PropagationInfo.class
	})
	public void testPost() {
		
		final String linkName = "foobar_linkname";
		final String link = "foobar_url";
		final String message = "foobar_message";
		
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		AndroidMock.expect(info.getEntityUrl()).andReturn(link);
		
		SocializeConfigUtils mockConfigUtils = new SocializeConfigUtils() {

			@Override
			public SocializeConfig getConfig(Context context) {
				return config;
			}
		};
		
		SocializeAccess.setConfigUtilsProxy(mockConfigUtils);
		
		Entity entity = Entity.newInstance(link, linkName);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			
			@Override
			public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
				addResult(0, postData);
				addResult(1, listener);
			}
			
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};		
		
		poster.setConfig(config);
		
		AndroidMock.replay(socialize, config, info);
		
		poster.post(TestUtils.getActivity(this), entity, message, info, listener);
		
		AndroidMock.verify(socialize, config, info);
		
		PostData data = getResult(0);
		SocialNetworkListener listenerAfter = getResult(1);
		
		assertNotNull(data);
		Map<String, Object> postValues = data.getPostValues();
		
		assertNotNull(postValues);
		
		String linkNameAfter = postValues.get("name").toString();
		String messageAfter = postValues.get("message").toString();
		String linkAfter = postValues.get("link").toString();
		
		assertEquals(linkName, linkNameAfter);
		assertEquals(message, messageAfter);
		assertEquals(link, linkAfter);
		assertSame(listener, listenerAfter);
	}
	
	@UsesMocks ({
		Share.class, 
		PropagationInfoResponse.class, 
		PropagationInfo.class, 
		SocializeService.class,
		SocialNetworkListener.class,
		SocializeConfig.class})
	public void testPostPhoto() throws IOException {
		
		final Share share = AndroidMock.createMock(Share.class);
		final PropagationInfoResponse propagationInfoResponse = AndroidMock.createMock(PropagationInfoResponse.class);
		final PropagationInfo propInfo = AndroidMock.createMock(PropagationInfo.class);
		final SocializeService socializeService = AndroidMock.createMock(SocializeService.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final SocialNetworkListener socialNetworkListener = AndroidMock.createMock(SocialNetworkListener.class);
		
		final String link = "foobar_url";
		final String appId = "foobar_appId";
		final String caption = "foobar_caption";
		final Uri photoUri = Uri.parse("http://www.getsocialize.com");
		
		AndroidMock.expect(share.getPropagationInfoResponse()).andReturn(propagationInfoResponse);
		AndroidMock.expect(propagationInfoResponse.getPropagationInfo(ShareType.FACEBOOK)).andReturn(propInfo);
		AndroidMock.expect(propInfo.getAppUrl()).andReturn(link);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(appId);
		
		AndroidMock.replay(share,propagationInfoResponse,propInfo,socializeService,config);
		
		SocializeConfigUtils mockConfigUtils = new SocializeConfigUtils() {

			@Override
			public SocializeConfig getConfig(Context context) {
				return config;
			}
		};
		
		SocializeAccess.setConfigUtilsProxy(mockConfigUtils);		
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			@Override
			protected SocializeService getSocialize() {
				return socializeService;
			}

			@Override
			public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
				addResult(0, appId);
				addResult(1, link);
				addResult(2, caption);
				addResult(3, photoUri);
				addResult(4, listener);
			}
		};
		
		poster.setConfig(config);
		poster.postPhoto(TestUtils.getActivity(this), share, caption, photoUri, socialNetworkListener);
		
		AndroidMock.verify(share,propagationInfoResponse,propInfo,socializeService,config);
		
		assertEquals(appId, getResult(0));
		assertEquals(link, getResult(1));
		assertEquals(caption, getResult(2));
		assertEquals(photoUri, getResult(3));
		assertSame(socialNetworkListener, getResult(4));
	}
	
	@UsesMocks ({
		AsyncFacebookRunner.class,
		Facebook.class,
		FacebookSessionStore.class,
		MockableRequestListener.class,
		SocialNetworkListener.class,
		ImageUtils.class
	})
	public void testPostPhoto2() throws IOException {
		
		final String fbId = "foobar";
		final String link = "foobar_url";
		final String caption = "foobar_caption";
		final Uri photoUri = Uri.parse("http://www.getsocialize.com");
		final byte[] image = new byte[]{69};
		
		final Facebook fb  = AndroidMock.createMock(Facebook.class, fbId);
		final SocialNetworkListener socialNetworkListener = AndroidMock.createMock(SocialNetworkListener.class);
		final FacebookSessionStore store = AndroidMock.createMock(FacebookSessionStore.class);
		final MockableRequestListener requestListener = AndroidMock.createMock(MockableRequestListener.class);
		final ImageUtils imageUtils = AndroidMock.createMock(ImageUtils.class);
		
		final AsyncFacebookRunner runner = new AsyncFacebookRunner(fb) {
			@Override
			public void request(String graphPath, Bundle parameters, String httpMethod, RequestListener listener, Object state) {
				addResult(0, graphPath);
				addResult(1, parameters);
				addResult(2, httpMethod);
				addResult(3, listener);
			}
		};
		
		AndroidMock.expect( store.restore(fb, TestUtils.getActivity(this)) ).andReturn(true);
		AndroidMock.expect(imageUtils.scaleImage(TestUtils.getActivity(this), photoUri)).andReturn(image);
		
		AndroidMock.replay(store, imageUtils);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			protected Facebook getFacebook(Context context) {
				return fb;
			}

			@Override
			protected AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
				return runner;
			}

			@Override
			protected FacebookSessionStore newFacebookSessionStore() {
				return store;
			}

			@Override
			protected RequestListener newRequestListener(Activity parent, SocialNetworkPostListener listener) {
				addResult(4, listener);
				return requestListener;
			}
		};	
		
		poster.setImageUtils(imageUtils);
		
		poster.postPhoto(TestUtils.getActivity(this), link, caption, photoUri, socialNetworkListener);
		
		AndroidMock.verify(store, imageUtils);
		
		String graphPath = getResult(0);
		Bundle params = getResult(1);
		String httpMethod = getResult(2);
		RequestListener listener = getResult(3);
		SocialNetworkListener socListener = getResult(4);
		
		assertEquals("me/photos", graphPath);
		assertEquals("POST", httpMethod);
		assertSame(requestListener, listener);
		assertSame(socialNetworkListener, socListener);
		
		assertNotNull(params);
		
		assertEquals(caption + ": " + link, params.getString("caption"));
		TestUtils.assertByteArrayEquals(image, params.getByteArray("photo"));
	}	
	
	
	@UsesMocks ({JSONObject.class, SocializeService.class, SocializeSession.class, AuthProvider.class, SocialNetworkListener.class})
	public void testRequestListenerWithError() throws JSONException {
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final JSONObject json = AndroidMock.createMock(JSONObject.class);
		final JSONObject error = AndroidMock.createMock(JSONObject.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final String errorMessage = "foobar_error";
		final String responseData = "foobar_repsonse";
		
		AndroidMock.expect(json.has("error")).andReturn(true);
		
		socialize.clear3rdPartySession(TestUtils.getActivity(this), AuthProviderType.FACEBOOK);
		
		AndroidMock.expect(json.getJSONObject("error")).andReturn(error);
		
		AndroidMock.expect(error.has("message")).andReturn(true);
		AndroidMock.expect(error.isNull("message")).andReturn(false);
		AndroidMock.expect(error.getString("message")).andReturn(errorMessage);
		
		AndroidMock.expect(error.has("code")).andReturn(true);
		AndroidMock.expect(error.isNull("code")).andReturn(false);
		AndroidMock.expect(error.getInt("code")).andReturn(190); // Clears session
		
		
		PublicFacebookWallPoster poster = new PublicFacebookWallPoster() {
			@Override
			protected JSONObject newJSONObject(String response) throws JSONException {
				addResult(0, response);
				return json;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkPostListener listener) {
				addResult(1, msg);
				addResult(2, e);
				addResult(3, listener);
			}
		};
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(session);
		AndroidMock.replay(json);
		AndroidMock.replay(error);
		AndroidMock.replay(listener);
		
		RequestListener newRequestListener = poster.newRequestListener(TestUtils.getActivity(this), listener);
		
		newRequestListener.onComplete(responseData, null);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(session);
		AndroidMock.verify(json);
		AndroidMock.verify(error);
		AndroidMock.verify(listener);
		
		String response = getResult(0);
		String msg = getResult(1);
		Exception e = getResult(2);
		SocialNetworkListener listenerAfter = getResult(3);
		
		assertEquals(responseData, response);
		assertEquals(errorMessage, msg);
		assertEquals(errorMessage, e.getMessage());
		assertSame(listener, listenerAfter);
	}
	
	@UsesMocks ({JSONObject.class, SocializeService.class, SocializeSession.class, AuthProvider.class, SocialNetworkListener.class})
	public void testRequestListenerCalledOnSuccess() throws Throwable {
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final JSONObject json = AndroidMock.createMock(JSONObject.class);
		final SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final String responseData = "foobar_repsonse";
		
		AndroidMock.expect(json.has("error")).andReturn(false);
		listener.onAfterPost(TestUtils.getActivity(this), SocialNetwork.FACEBOOK, json);
		
		final PublicFacebookWallPoster poster = new PublicFacebookWallPoster() {
			@Override
			protected JSONObject newJSONObject(String response) throws JSONException {
				addResult(0, response);
				return json;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkPostListener listener) {
				fail();
			}
		};
		
		final Activity activity = TestUtils.getActivity(this);
		
		// Must run on UI thread because listener is called on UI thread.
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AndroidMock.replay(json);
				AndroidMock.replay(listener);
				
				RequestListener newRequestListener = poster.newRequestListener(activity, listener);
				
				newRequestListener.onComplete(responseData, null);
				
				AndroidMock.verify(json);
				AndroidMock.verify(listener);
				
				String response = getResult(0);
				
				assertEquals(responseData, response);
			}
		});
	}	
	
	public void testRequestListenerErrorCallFlow() throws Throwable {
		final PublicFacebookWallPoster poster = new PublicFacebookWallPoster() {
			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkPostListener listener) {
				count++;
			}
		};
		
		RequestListener newRequestListener = poster.newRequestListener(TestUtils.getActivity(this), null);
		
		newRequestListener.onFileNotFoundException(new FileNotFoundException(), null);
		newRequestListener.onIOException(new IOException(), null);
		newRequestListener.onMalformedURLException(new MalformedURLException(), null);
		newRequestListener.onFacebookError(new FacebookError("foobar"), null);
		
		assertEquals(4, poster.count);
	}		
	
	public void testPostUsesCustomPath() {
		
		final String path = "foobar_path";
		
		final DefaultPostData postData = new DefaultPostData();
		
		final Facebook mockFacebook = new Facebook("foobar");
		final FacebookSessionStore mockFacebookSessionStore = new FacebookSessionStore() {
			@Override
			public boolean restore(Facebook session, Context context) {
				return true;
			}
		};
		final AsyncFacebookRunner mockRunner = new AsyncFacebookRunner(null) {
			@Override
		    public void request(final String graphPath,
                    final Bundle parameters,
                    final String httpMethod,
                    final RequestListener listener,
                    final Object state) {
		    	addResult(0, graphPath);
		    }
		};
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			@Override
			protected AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
				return mockRunner;
			}

			@Override
			protected Facebook getFacebook(Context context) {
				return mockFacebook;
			}

			@Override
			protected FacebookSessionStore newFacebookSessionStore() {
				return mockFacebookSessionStore;
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
		
		poster.post(TestUtils.getActivity(this), listener, postData);
		
		String result = getResult(0);
		
		assertEquals(path, result);
	}
	
	
	@UsesMocks ({Facebook.class, AsyncFacebookRunner.class, FacebookSessionStore.class, MockableRequestListener.class})
	public void testFacebookCall() {
		
		final String appId = "foobar";
		final Activity context = TestUtils.getActivity(this);
		final Facebook mockFacebook = AndroidMock.createMock(Facebook.class, appId);
		final AsyncFacebookRunner mockAsyncFacebookRunner = AndroidMock.createMock(AsyncFacebookRunner.class, mockFacebook);
		final FacebookSessionStore mockFacebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		final MockableRequestListener mockRequestListener = AndroidMock.createMock(MockableRequestListener.class);
		final String method = "GET";
		final String graphPath = "foobarPath";
		
		Map<String, Object> postData = new HashMap<String, Object>();
		
		postData.put("foo", "bar");
		
		AndroidMock.expect(mockFacebookSessionStore.restore(mockFacebook, context)).andReturn(true);
		
		mockAsyncFacebookRunner.request(AndroidMock.eq(graphPath), (Bundle)AndroidMock.anyObject(), AndroidMock.eq(method), AndroidMock.eq(mockRequestListener), AndroidMock.isNull());	
		
		AndroidMock.replay(mockFacebookSessionStore, mockAsyncFacebookRunner);
		
		final PublicFacebookWallPoster poster = new PublicFacebookWallPoster() {

			@Override
			public RequestListener newRequestListener(Activity parent, SocialNetworkPostListener listener) {
				return mockRequestListener;
			}

			@Override
			public AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
				return mockAsyncFacebookRunner;
			}

			@Override
			public FacebookSessionStore newFacebookSessionStore() {
				return mockFacebookSessionStore;
			}

			@Override
			public Facebook getFacebook(Context context) {
				return mockFacebook;
			}

			@Override
			public void doFacebookCall(Activity parent, Bundle data, String graphPath, String method, SocialNetworkPostListener listener) {
				// Inspect the bundle
				addResult(0, data.getString("foo"));
				
				// Call super
				super.doFacebookCall(parent, data, graphPath, method, listener);
			}
		};
		
		poster.doFacebookCall(context, postData, graphPath, method, null);
		
		AndroidMock.verify(mockFacebookSessionStore, mockAsyncFacebookRunner);
	}
	
	public class PublicFacebookWallPoster extends DefaultFacebookWallPoster {
		public int count = 0;
		@Override
		public RequestListener newRequestListener(Activity parent, SocialNetworkPostListener listener) {
			return super.newRequestListener(parent, listener);
		}
		@Override
		public void doFacebookCall(Activity parent, Map<String, Object> postData, String graphPath, String method, SocialNetworkPostListener listener) {
			super.doFacebookCall(parent, postData, graphPath, method, listener);
		}
		@Override
		public void doFacebookCall(Activity parent, Bundle data, String graphPath, String method, SocialNetworkPostListener listener) {
			super.doFacebookCall(parent, data, graphPath, method, listener);
		}
		
		@Override
		public Facebook getFacebook(Context context) {
			return super.getFacebook(context);
		}
		@Override
		public AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
			return super.newAsyncFacebookRunner(fb);
		}
		@Override
		public FacebookSessionStore newFacebookSessionStore() {
			return super.newFacebookSessionStore();
		}
	}
}
