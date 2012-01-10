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
package com.socialize.test.ui.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.facebook.RequestListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.AppUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	SocialNetworkListener.class,
	AppUtils.class,
	ShareMessageBuilder.class
})
public class FacebookWallPosterTest extends SocializeActivityTest {

	public void testPostLike() {
		
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		ShareMessageBuilder builder = AndroidMock.createMock(ShareMessageBuilder.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String entityLink = "foobar_link";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		AndroidMock.expect(appUtils.getAppName()).andReturn(appName);
		AndroidMock.expect(builder.getEntityLink(entity, false)).andReturn(entityLink);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
			}
		};
		
		String expectedString = "Likes foobar_link\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(appUtils);
		AndroidMock.replay(builder);
		
		
		poster.setAppUtils(appUtils);
		poster.setShareMessageBuilder(builder);
		poster.postLike(parent, entity, null, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		
		AndroidMock.verify(appUtils);
		AndroidMock.verify(builder);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
	
	public void testPostComment() {
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		ShareMessageBuilder builder = AndroidMock.createMock(ShareMessageBuilder.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String comment = "foobar_comment";
		final String entityLink = "foobar_link";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		AndroidMock.expect(appUtils.getAppName()).andReturn(appName);
		AndroidMock.expect(builder.getEntityLink(entity, false)).andReturn(entityLink);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
			}
		};
		
		String expectedString = "foobar_link\n\nfoobar_comment\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(appUtils);
		AndroidMock.replay(builder);
		
		poster.setAppUtils(appUtils);
		poster.setShareMessageBuilder(builder);
		poster.postComment(parent, entity, comment, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		
		AndroidMock.verify(appUtils);
		AndroidMock.verify(builder);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
	
	@UsesMocks ({
		SocializeConfig.class,
		AppUtils.class,
		SocialNetworkListener.class,
		SocializeService.class
	})
	public void testPost() {
		
		final String fbId = "foobar";
		final String linkName = "foobar_linkname";
		final String link = "foobar_url";
		final String message = "foobar_message";
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		AndroidMock.expect(socialize.getConfig()).andReturn(config);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		AndroidMock.expect(appUtils.getAppName()).andReturn(linkName);
		AndroidMock.expect(appUtils.getMarketUrl()).andReturn(link);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {
			@Override
			public void post(Activity parent, String appId, String linkName, String message, String link, String caption, SocialNetworkListener listener) {
				addResult(0, appId);
				addResult(1, linkName);
				addResult(2, message);
				addResult(3, link);
				addResult(4, caption);
				addResult(5, listener);
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};		
		
		poster.setAppUtils(appUtils);
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		AndroidMock.replay(appUtils);
		
		poster.post(getActivity(), message, listener);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
		AndroidMock.verify(appUtils);
		
		String fbIdAfter = getResult(0);
		String linkNameAfter = getResult(1);
		String messageAfter = getResult(2);
		String linkAfter = getResult(3);
		String captionAfter = getResult(4);
		SocialNetworkListener listenerAfter = getResult(5);
		
		assertEquals(fbId, fbIdAfter);
		assertEquals(linkName, linkNameAfter);
		assertEquals(message, messageAfter);
		assertEquals(link, linkAfter);
		assertEquals("Download the app now to join the conversation.", captionAfter);
		assertSame(listener, listenerAfter);
	}
	
	@UsesMocks ({
		AsyncFacebookRunner.class,
		Facebook.class,
		FacebookSessionStore.class,
		RequestListener.class,
		SocialNetworkListener.class
	})
	public void testPost2() {
		
		final String fbId = "foobar";
		final String linkName = "foobar_linkname";
		final String link = "foobar_url";
		final String message = "foobar_message";
		final String caption = "foobar_caption";
		
		final Facebook fb  = AndroidMock.createMock(Facebook.class);
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
			protected Facebook newFacebook(String appId, Drawables drawables) {
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
			protected RequestListener newRequestListener(Activity parent, SocialNetworkListener listener) {
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
		
		assertEquals("me/feed", graphPath);
		assertEquals("POST", httpMethod);
		assertSame(requestListener, listener);
		assertSame(socialNetworkListener, socListener);
		
		assertNotNull(params);
		
		assertEquals(linkName, params.getString("name"));
		assertEquals(message, params.getString("message"));
		assertEquals(link, params.getString("link"));
		assertEquals(caption, params.getString("caption"));
	}
	
	@UsesMocks ({JSONObject.class, SocializeService.class, SocializeSession.class, AuthProvider.class, SocialNetworkListener.class})
	public void testRequestListenerWithError() throws JSONException {
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final JSONObject json = AndroidMock.createMock(JSONObject.class);
		final JSONObject error = AndroidMock.createMock(JSONObject.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final AuthProvider authProvider = AndroidMock.createMock(AuthProvider.class);
		final AuthProviderType authProviderType = AuthProviderType.FACEBOOK;
		final SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		final String get3rdPartyAppId = "foobar";
		final String errorMessage = "foobar_error";
		final String responseData = "foobar_repsonse";
		
		AndroidMock.expect(json.has("error")).andReturn(true);
		
		AndroidMock.expect(socialize.getSession()).andReturn(session);
		AndroidMock.expect(session.getAuthProvider()).andReturn(authProvider);
		AndroidMock.expect(session.getAuthProviderType()).andReturn(authProviderType);
		AndroidMock.expect(session.get3rdPartyAppId()).andReturn(get3rdPartyAppId);
		
		authProvider.clearCache(getActivity(), get3rdPartyAppId);
		
		AndroidMock.expect(json.getJSONObject("error")).andReturn(error);
		
		AndroidMock.expect(error.has("message")).andReturn(true);
		AndroidMock.expect(error.isNull("message")).andReturn(false);
		AndroidMock.expect(error.getString("message")).andReturn(errorMessage);
		
//		listener.onAfterPost(getActivity(), SocialNetwork.FACEBOOK);
		
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
			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkListener listener) {
				addResult(1, msg);
				addResult(2, e);
				addResult(3, listener);
			}
		};
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(session);
		AndroidMock.replay(authProvider);
		AndroidMock.replay(json);
		AndroidMock.replay(error);
		AndroidMock.replay(listener);
		
		RequestListener newRequestListener = poster.newRequestListener(getActivity(), listener);
		
		newRequestListener.onComplete(responseData, null);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(session);
		AndroidMock.verify(authProvider);
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
		listener.onAfterPost(getActivity(), SocialNetwork.FACEBOOK);
		
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

			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkListener listener) {
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
			protected void onError(Activity parent, String msg, Throwable e, SocialNetworkListener listener) {
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
		public RequestListener newRequestListener(Activity parent, SocialNetworkListener listener) {
			return super.newRequestListener(parent, listener);
		}
	}
}
