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

import android.app.Activity;
import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookSharer;
import com.socialize.networks.facebook.FacebookWallPoster;
import com.socialize.test.PublicSocialize;
import com.socialize.test.ui.SocializeActivityTestCase;

/**
 * @author Jason Polites
 *
 */
public class FacebookSharerTest extends SocializeActivityTestCase {

	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeService.class, SocializeConfig.class, AuthProviderInfoFactory.class, AuthProviderInfo.class})
	public void testShareNotAuthenticated() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory = AndroidMock.createMock(AuthProviderInfoFactory.class);
		final AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final boolean autoAuth = true;

		AndroidMock.expect(socialize.isSupported(AuthProviderType.FACEBOOK)).andReturn(true);
		AndroidMock.expect(socialize.isAuthenticated(AuthProviderType.FACEBOOK)).andReturn(false);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(authProviderInfoFactory.getInstance()).andReturn(authProviderInfo);
		
		socialize.authenticate(
				AndroidMock.eq ( getContext() ), 
				AndroidMock.eq (consumerKey), 
				AndroidMock.eq (consumerSecret),
				AndroidMock.eq (authProviderInfo),
				(SocializeAuthListener) AndroidMock.anyObject());
		
		PublicFacebookSharer sharer = new PublicFacebookSharer() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		sharer.setConfig(config);
		sharer.setAuthProviderInfoFactory(authProviderInfoFactory);
		
		AndroidMock.replay(authProviderInfoFactory);
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, autoAuth, ActionType.COMMENT, null);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
		AndroidMock.verify(authProviderInfoFactory);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeConfig.class, AuthProviderInfoFactory.class, AuthProviderInfo.class})
	public void testShareAuthListener() {
		
		final PublicSocialize socialize = new PublicSocialize() {
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
				fail();
			}
			
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener) {
				addResult(authListener);
			}

			@Override
			public boolean isSupported(AuthProviderType type) {
				return true;
			}

			@Override
			public boolean isAuthenticated(AuthProviderType providerType) {
				return false;
			}
		};
		
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory = AndroidMock.createMock(AuthProviderInfoFactory.class);
		final AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final boolean autoAuth = true;
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(authProviderInfoFactory.getInstance()).andReturn(authProviderInfo);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer() {

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
				addResult("doError");
			}

			@Override
			public void doShare(Activity context, Entity entity, PropagationInfo urlSet, String comment, SocialNetworkListener listener, ActionType type) {
				addResult("doShare");
			}
		};
		
		sharer.setConfig(config);
		sharer.setAuthProviderInfoFactory(authProviderInfoFactory);
		
		AndroidMock.replay(config);
		AndroidMock.replay(authProviderInfoFactory);
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, autoAuth, ActionType.COMMENT, null);
		
		AndroidMock.verify(config);		
		AndroidMock.verify(authProviderInfoFactory);

		// Get the listener
		SocializeAuthListener listener = getResult(0);
		
		clearResults();
		
		assertNotNull(listener);
		
		listener.onAuthSuccess(null);
		listener.onAuthFail(null);
		listener.onError(null);
		
		String doShare = getNextResult();
		String doError0 = getNextResult();
		String doError1 = getNextResult();
		
		assertNotNull(doShare);
		assertNotNull(doError0);
		assertNotNull(doError1);
		
		assertEquals("doShare", doShare);
		assertEquals("doError", doError0);
		assertEquals("doError", doError1);
	}
	
	@UsesMocks ({SocialNetworkListener.class, SocializeLogger.class})
	public void testDoError() {
		
		final String msg = "Error sharing to FACEBOOK";
		
		SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		SocializeException error = new SocializeException("foobar");
		
		logger.error(msg, error);
		listener.onError(getActivity(), SocialNetwork.FACEBOOK, msg, error);
		
		AndroidMock.replay(logger);
		AndroidMock.replay(listener);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setLogger(logger);
		
		sharer.doError(error, getActivity(), listener);
		
		AndroidMock.verify(logger);
		AndroidMock.verify(listener);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class, PropagationInfo.class}) 
	public void testDoShareComment() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);
		
		facebookWallPoster.postComment(getActivity(), entity, comment, info, listener);
		
		AndroidMock.replay(facebookWallPoster, listener, info);
		
		sharer.doShare(getActivity(), entity, info, comment, listener, ActionType.COMMENT);
		
		AndroidMock.verify(facebookWallPoster, listener, info);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class, ShareMessageBuilder.class, PropagationInfo.class}) 
	public void testDoShareShare() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		ShareMessageBuilder shareMessageBuilder = AndroidMock.createMock(ShareMessageBuilder.class);
		PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		sharer.setShareMessageBuilder(shareMessageBuilder);
		
		final String comment = "foobar";
		final String body = "foobar_body";
		final Entity entity = Entity.newInstance("blah", null);
		
		AndroidMock.expect(shareMessageBuilder.buildShareMessage( entity, info, comment, false, true)).andReturn(body);
		facebookWallPoster.post(getActivity(), body, info, listener);
		
		AndroidMock.replay(facebookWallPoster, shareMessageBuilder, listener, info);

		sharer.doShare(getActivity(), entity, info, comment, listener, ActionType.SHARE);
		
		AndroidMock.verify(facebookWallPoster, shareMessageBuilder, listener, info);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class, PropagationInfo.class}) 
	public void testDoShareLike() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);
		
		facebookWallPoster.postLike(getActivity(), entity, info, listener);
		
		AndroidMock.replay(facebookWallPoster, listener, info);
		
		sharer.doShare(getActivity(), entity, info, comment, listener, ActionType.LIKE);
		
		AndroidMock.verify(facebookWallPoster, listener, info);
	}	
	
	public class PublicFacebookSharer extends FacebookSharer {

		@Override
		public void doShare(Activity context, Entity entity, PropagationInfo urlSet, String comment, SocialNetworkListener listener, ActionType type) {
			super.doShare(context, entity, urlSet, comment, listener, type);
		}

		@Override
		public void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
			super.doError(e, parent, listener);
		}
		
		@Override
		public SocializeService getSocialize() {
			return super.getSocialize();
		}
	}
}
