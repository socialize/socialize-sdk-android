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
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.facebook.RequestListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.test.SocializeActivityTest;
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

	public void testPostLike() {
		doTestPostLike("");
	}
	
	public void testPostComment() {
		testPostComment("foobar_comment");
	}
	
	public void doTestPostLike(String expectedString) {
		
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
//		ShareMessageBuilder builder = AndroidMock.createMock(ShareMessageBuilder.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		Activity parent = getActivity();
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
//		final String entityLink = "foobar_link";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
//		AndroidMock.expect(builder.getEntityLink(entity, info, false)).andReturn(entityLink);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			
			@Override
			public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
				addResult(3, propInfo);
			}
		};
		
//		AndroidMock.replay(builder);
		
//		poster.setShareMessageBuilder(builder);
		poster.postLike(parent, entity, info, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		PropagationInfo infoAfter = getResult(3);
		
//		AndroidMock.verify(builder);
		
		assertSame(listener, listenerAfter);
		assertSame(info, infoAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}	
	
	public void testPostComment(String expectedString) {
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		Activity parent = getActivity();
		
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
		
		final String fbId = "foobar";
		final String linkName = "foobar_linkname";
		final String link = "foobar_url";
		final String message = "foobar_message";
		
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
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
			public void post(Activity parent, String appId, SocialNetworkListener listener, PostData postData) {
				addResult(0, appId);
				addResult(1, postData);
				addResult(2, listener);
			}
			
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};		
		
		AndroidMock.replay(socialize, config, info);
		
		poster.post(getActivity(), entity, message, info, listener);
		
		AndroidMock.verify(socialize, config, info);
		
		String fbIdAfter = getResult(0);
		PostData data = getResult(1);
		SocialNetworkListener listenerAfter = getResult(2);
		
		assertNotNull(data);
		Map<String, Object> postValues = data.getPostValues();
		
		assertNotNull(postValues);
		
		String linkNameAfter = postValues.get("name").toString();
		String messageAfter = postValues.get("message").toString();
		String linkAfter = postValues.get("link").toString();
		
		assertEquals(fbId, fbIdAfter);
		assertEquals(linkName, linkNameAfter);
		assertEquals(message, messageAfter);
		assertEquals(link, linkAfter);
		assertSame(listener, listenerAfter);
	}
	
	@UsesMocks ({
		AsyncFacebookRunner.class,
		Facebook.class,
		FacebookSessionStore.class,
		RequestListener.class,
		SocialNetworkListener.class
	})
	@Deprecated
	public void testPost2() {
		
		final String fbId = "foobar";
		final String linkName = "foobar_linkname";
		final String link = "foobar_url";
		final String message = "foobar_message";
		final String caption = "foobar_caption";
		
		final Facebook fb  = AndroidMock.createMock(Facebook.class, fbId);
		final SocialNetworkListener socialNetworkListener = AndroidMock.createMock(SocialNetworkListener.class);
		final FacebookSessionStore store = AndroidMock.createMock(FacebookSessionStore.class);
		final RequestListener requestListener = AndroidMock.createMock(RequestListener.class);
		
		final AsyncFacebookRunner runner = new AsyncFacebookRunner(fb) {
			@Override
			public void request(String graphPath, Bundle parameters, String httpMethod, RequestListener listener, Object state) {
				addResult(0, graphPath);
				addResult(1, parameters);
				addResult(2, httpMethod);
				addResult(3, listener);
			}
		};
		
		AndroidMock.expect( store.restore(fb, getActivity()) ).andReturn(true);
		
		AndroidMock.replay(store);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			protected Facebook newFacebook(String appId) {
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
		
		poster.post(getActivity(), fbId, linkName, message, link, caption, socialNetworkListener);
		
		AndroidMock.verify(store);
		
		String graphPath = getResult(0);
		Bundle params = getResult(1);
		String httpMethod = getResult(2);
		RequestListener listener = getResult(3);
		SocialNetworkListener socListener = getResult(4);
		
		assertEquals("me/links", graphPath);
		assertEquals("POST", httpMethod);
		assertSame(requestListener, listener);
		assertSame(socialNetworkListener, socListener);
		
		assertNotNull(params);
		
		assertEquals(linkName, params.getString("name"));
		assertEquals(message, params.getString("message"));
		assertEquals(link, params.getString("link"));
		assertEquals(caption, params.getString("caption"));
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
			public void postPhoto(Activity parent, String appId, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
				addResult(0, appId);
				addResult(1, link);
				addResult(2, caption);
				addResult(3, photoUri);
				addResult(4, listener);
			}
		};
		
		poster.postPhoto(getActivity(), share, caption, photoUri, socialNetworkListener);
		
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
		RequestListener.class,
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
		final RequestListener requestListener = AndroidMock.createMock(RequestListener.class);
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
		
		AndroidMock.expect( store.restore(fb, getActivity()) ).andReturn(true);
		AndroidMock.expect(imageUtils.scaleImage(getActivity(), photoUri)).andReturn(image);
		
		AndroidMock.replay(store, imageUtils);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			protected Facebook newFacebook(String appId) {
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
		
		poster.postPhoto(getActivity(), fbId, link, caption, photoUri, socialNetworkListener);
		
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
		
		socialize.clear3rdPartySession(getActivity(), AuthProviderType.FACEBOOK);
		
		AndroidMock.expect(json.getJSONObject("error")).andReturn(error);
		
		AndroidMock.expect(error.has("message")).andReturn(true);
		AndroidMock.expect(error.isNull("message")).andReturn(false);
		AndroidMock.expect(error.getString("message")).andReturn(errorMessage);
		
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
		
		RequestListener newRequestListener = poster.newRequestListener(getActivity(), listener);
		
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
		listener.onAfterPost(getActivity(), SocialNetwork.FACEBOOK, json);
		
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
		
		// Must run on UI thread because listener is called on UI thread.
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AndroidMock.replay(json);
				AndroidMock.replay(listener);
				
				RequestListener newRequestListener = poster.newRequestListener(getActivity(), listener);
				
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
		
		RequestListener newRequestListener = poster.newRequestListener(getActivity(), null);
		
		newRequestListener.onFileNotFoundException(new FileNotFoundException(), null);
		newRequestListener.onIOException(new IOException(), null);
		newRequestListener.onMalformedURLException(new MalformedURLException(), null);
		newRequestListener.onFacebookError(new FacebookError("foobar"), null);
		
		assertEquals(4, poster.count);
	}		
	
	public class PublicFacebookWallPoster extends DefaultFacebookWallPoster {
		public int count = 0;
		@Override
		public RequestListener newRequestListener(Activity parent, SocialNetworkPostListener listener) {
			return super.newRequestListener(parent, listener);
		}
	}
}
