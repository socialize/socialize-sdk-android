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
package com.socialize.test.ui.share;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ShareUtils;
import com.socialize.SocializeAccess;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.entity.DefaultPropagationInfo;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.Facebook;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.networks.facebook.FacebookSharer;
import com.socialize.networks.facebook.v2.FacebookFacadeV2;
import com.socialize.share.ShareHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * Tests that SocialNetworkListeners are called as expected when sharing via the UI.
 * @author Jason Polites
 */
public class SocialNetworkShareListenerTest extends SocializeActivityTest {

	@Deprecated
	public void testFacebookWallPosterCallsListener() throws InterruptedException {
		
		final String dummyResponse = "{foo:bar}";
		
		final Facebook mockFacebook = new Facebook("foobar") {
			
		};
		
		// We don't know, and shouldn't care WHICH method is called on Facebook's code so mock them all.
		final AsyncFacebookRunner mockRunner = new AsyncFacebookRunner(null) {

			@Override
			public void request(Bundle parameters, RequestListener listener, Object state) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(Bundle parameters, RequestListener listener) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(String graphPath, RequestListener listener, Object state) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(String graphPath, RequestListener listener) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(String graphPath, Bundle parameters, RequestListener listener, Object state) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(String graphPath, Bundle parameters, RequestListener listener) {
				listener.onComplete(dummyResponse, null);
			}

			@Override
			public void request(String graphPath, Bundle parameters, String httpMethod, RequestListener listener, Object state) {
				listener.onComplete(dummyResponse, null);
			}
		};
		
		final FacebookSessionStore mockStore = new FacebookSessionStore() {

			@Override
			public boolean save(Facebook session, Context context) {
				return true;
			}

			@Override
			public boolean restore(Facebook session, Context context) {
				return true;
			}

			@Override
			public void clear(Context context) {}
		};
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			protected AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
				return mockRunner;
			}

			@Override
			protected FacebookSessionStore newFacebookSessionStore() {
				return mockStore;
			}

			@Override
			protected Facebook getFacebook(Context context) {
				return mockFacebook;
			}

			@Override
			protected String getFacebookAppId() {
				return "foobar";
			}
		};
		
		final CountDownLatch latch = new CountDownLatch(2);
		
		SocialNetworkListener listener = new SocialNetworkListener() {
			
			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				// test will fail
				latch.countDown();
			}
			
			@Override
			public void onCancel() {
				// test will fail
				latch.countDown();
			}
			
			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
				addResult(1, responseObject);
				latch.countDown();
			}
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				addResult(0, postData);
				latch.countDown();
				return false;
			}
		};
		
		Entity entity = Entity.newInstance("Test", "Test");
		String mockText = "foobar";
		DefaultPropagationInfo propInfo = new DefaultPropagationInfo();
		
		poster.post(TestUtils.getActivity(this), entity, mockText, propInfo, listener);
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		PostData postData = getResult(0);
		JSONObject response = getResult(1);
		
		
		assertNotNull(response);
		assertNotNull(postData);
	}
	
	@UsesMocks ({FacebookFacadeV2.class, SocializeService.class})
	public void testFacebookSharerCallsWallPoster() {
		final FacebookFacadeV2 poster = AndroidMock.createMock(FacebookFacadeV2.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		final Activity context = TestUtils.getActivity(this);
		
		SocialNetworkShareListener listener = new SocialNetworkShareListener() {};
		Entity entity = Entity.newInstance("Test", "Test");
		String mockText = "foobar";
		DefaultPropagationInfo propInfo = new DefaultPropagationInfo();
		
		// Expect
		AndroidMock.expect(socialize.isSupported(context, AuthProviderType.FACEBOOK)).andReturn(true);
		AndroidMock.expect(socialize.isAuthenticatedForWrite(AuthProviderType.FACEBOOK)).andReturn(true);
		poster.post(TestUtils.getActivity(this), entity, mockText, propInfo, listener);
		
		AndroidMock.replay(socialize, poster);
		
		FacebookSharer sharer = new FacebookSharer() {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		sharer.setFacebookFacade(poster);
		sharer.share(context, entity, propInfo, mockText, true, ActionType.SHARE, listener);
		
		AndroidMock.verify(socialize, poster);
	}
	
	@UsesMocks ({ShareHandler.class})
	public void testShareSystemCallsSharer () {
		final ShareHandler handler = AndroidMock.createMock(ShareHandler.class);
		
		String mockText = "foobar";
		Share share = new Share();
		SocialNetworkShareListener listener = new SocialNetworkShareListener() {};
		
		// Expect
		handler.handle(AndroidMock.eq(TestUtils.getActivity(this)), AndroidMock.eq(share), (Location) AndroidMock.isNull(), AndroidMock.eq(mockText), AndroidMock.eq(listener));
		
		AndroidMock.replay(handler);
		
		SocializeShareSystem shareSystem = new SocializeShareSystem(null) {
			@Override
			protected ShareHandler getSharer(ShareType destination) {
				return handler;
			}
		};
		
		shareSystem.share(TestUtils.getActivity(this), null, share, mockText, null, ShareType.FACEBOOK, listener);
		
		AndroidMock.verify(handler);
	}
	
	public void testShareUtilsCallsShareSystem() throws InterruptedException {

		// Setup the mocks for the share system.
		TestUtils.setupSocializeOverrides(true, true);
		
		final Share mockShare = new Share();
		mockShare.setId(-1L);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final SocializeShareSystem mockShareSystem = new SocializeShareSystem(null) {

			@Override
			public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, ShareListener listener, SocialNetwork... network) {
				// Just call the listener
				listener.onCreate(mockShare);
			}

			@Override
			public void share(Activity context, SocializeSession session, SocializeAction action, String comment, Location location, ShareType destination, SocialNetworkListener listener) {
				addResult(0, action);
				addResult(1, destination);
				addResult(2, listener);
				latch.countDown();
			}
		};
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<ShareSystem> shareSystemProxy = container.getProxy("shareSystem");
				if(shareSystemProxy != null) {
					shareSystemProxy.setDelegate(mockShareSystem);
				}
				else {
					System.err.println("shareSystemProxy is null!!");
				}				
			}
		});	
		
		// Force no auth
		ShareOptions shareOptions = ShareUtils.getUserShareOptions(getContext());
		shareOptions.setShowAuthDialog(false);
		
		Entity mock = Entity.newInstance("Test", "Test");
		SocialNetworkShareListener listener = new SocialNetworkShareListener() {};
		
		ShareUtils.shareViaSocialNetworks(TestUtils.getActivity(this), mock, shareOptions, listener, SocialNetwork.FACEBOOK);
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		SocializeAction actionAfter = getResult(0);
		ShareType destinationAfter = getResult(1);
		SocialNetworkListener listenerAfter = getResult(2);
		
		assertNotNull(actionAfter);
		assertNotNull(destinationAfter);
		assertNotNull(listenerAfter);
		
		assertEquals(ShareType.FACEBOOK, destinationAfter);
		assertSame(listener, listenerAfter);
		assertTrue(actionAfter instanceof Share);
	}

}
