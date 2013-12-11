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
package com.socialize.test.facebook;

import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeService;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.share.SocialNetworkShareListener;
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
import com.socialize.networks.facebook.v2.FacebookFacadeV2;
import com.socialize.test.PublicSocialize;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class FacebookSharerTest extends SocializeActivityTest {

	@SuppressWarnings("unchecked")
	public void testShareNotAuthenticated() {
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		final AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory = Mockito.mock(AuthProviderInfoFactory.class);
		final AuthProviderInfo authProviderInfo = Mockito.mock(AuthProviderInfo.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final boolean autoAuth = true;

		Mockito.when(socialize.isSupported(getContext(), AuthProviderType.FACEBOOK)).thenReturn(true);
		Mockito.when(socialize.isAuthenticatedForWrite(AuthProviderType.FACEBOOK)).thenReturn(false);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).thenReturn(consumerKey);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).thenReturn(consumerSecret);
		Mockito.when(authProviderInfoFactory.getInstanceForWrite()).thenReturn(authProviderInfo);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		sharer.setConfig(config);
		sharer.setAuthProviderInfoFactory(authProviderInfoFactory);
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, autoAuth, ActionType.COMMENT, null);
		
		Mockito.verify(socialize).authenticate(
                Mockito.eq(getContext()),
                Mockito.eq(consumerKey),
                Mockito.eq(consumerSecret),
                Mockito.eq(authProviderInfo),
                (SocializeAuthListener) Mockito.anyObject());
	}
	
	@SuppressWarnings("unchecked")
	public void testShareAuthListener() {
		
		final PublicSocialize socialize = new PublicSocialize() {
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener) {
				addResult(authListener);
			}

			@Override
			public boolean isSupported(Context context, AuthProviderType type) {
				return true;
			}

			@Override
			public boolean isAuthenticated(AuthProviderType providerType) {
				return false;
			}
		};
		
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		final AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory = Mockito.mock(AuthProviderInfoFactory.class);
		final AuthProviderInfo authProviderInfo = Mockito.mock(AuthProviderInfo.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final boolean autoAuth = true;
		
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).thenReturn(consumerKey);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).thenReturn(consumerSecret);
		Mockito.when(authProviderInfoFactory.getInstanceForWrite()).thenReturn(authProviderInfo);
		
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
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, autoAuth, ActionType.COMMENT, null);
		
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
	
	public void testDoError() {
		
		final String msg = "Error sharing to FACEBOOK";
		
		SocializeLogger logger = Mockito.mock(SocializeLogger.class);
		SocialNetworkShareListener listener = Mockito.mock(SocialNetworkShareListener.class);
		SocializeException error = new SocializeException("foobar");
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setLogger(logger);
		
        sharer.doError(error, TestUtils.getActivity(this), listener);

		Mockito.verify(logger).error(msg, error);
		Mockito.verify(listener).onNetworkError(getContext(), SocialNetwork.FACEBOOK, error);
	}
	
	public void testDoShareComment() {
		FacebookFacadeV2 facebookWallPoster = Mockito.mock(FacebookFacadeV2.class);
		SocialNetworkShareListener listener = Mockito.mock(SocialNetworkShareListener.class);
		PropagationInfo info = Mockito.mock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookFacade(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);

		sharer.doShare(TestUtils.getActivity(this), entity, info, comment, listener, ActionType.COMMENT);

        Mockito.verify(facebookWallPoster).postComment(TestUtils.getActivity(this), entity, comment, info, listener);
	}
	
	public void testDoShareShare() {
		FacebookFacadeV2 facebookWallPoster = Mockito.mock(FacebookFacadeV2.class);
		SocialNetworkShareListener listener = Mockito.mock(SocialNetworkShareListener.class);
		PropagationInfo info = Mockito.mock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookFacade(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);
		
		sharer.doShare(TestUtils.getActivity(this), entity, info, comment, listener, ActionType.SHARE);

        Mockito.verify(facebookWallPoster).post(TestUtils.getActivity(this), entity, comment, info, listener);
	}
	
	public void testDoShareLike() {
		FacebookFacadeV2 facebookWallPoster = Mockito.mock(FacebookFacadeV2.class);
		SocialNetworkShareListener listener = Mockito.mock(SocialNetworkShareListener.class);
		PropagationInfo info = Mockito.mock(PropagationInfo.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookFacade(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);

		sharer.doShare(TestUtils.getActivity(this), entity, info, comment, listener, ActionType.LIKE);

        Mockito.verify(facebookWallPoster).postLike(TestUtils.getActivity(this), entity, info, listener);
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
