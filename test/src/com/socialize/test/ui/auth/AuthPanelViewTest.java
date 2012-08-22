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
package com.socialize.test.ui.auth;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.SocializeSystem;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.ioc.SocializeIOC;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookAuthClickListener;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterAuthClickListener;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.share.RememberCell;

/**
 * @author Jason Polites
 *
 */
public class AuthPanelViewTest extends SocializeActivityTest {
	
	public void testAuthPanelAuthRequiredOnComment() {
		SocializeCommentUtils commentUtils = new SocializeCommentUtils() {
			@Override
			protected boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
				return true;
			}

			@Override
			protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
				return false;
			}
		};
		
		IAuthDialogFactory authDialogFactory = new IAuthDialogFactory() {
			@Override
			public void show(Context context, AuthDialogListener listener, boolean required) {
				addResult(0, required);
			}
			
			@Override
			public void preload(Context context) {}
		};
		
		SocializeConfig config = new SocializeConfig();
		
		commentUtils.setAuthDialogFactory(authDialogFactory);
		commentUtils.setConfig(config);
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(false));
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON_COMMENT, String.valueOf(true));
		
		commentUtils.addComment(null, null,null, null, null);
		
		Boolean result = getResult(0);
		
		assertNotNull(result);
		assertTrue(result.booleanValue());
		
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(false));
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON_COMMENT, String.valueOf(false));
		
		commentUtils.addComment(null, null,null, null, null);
		
		result = getResult(0);
		
		assertNotNull(result);
		assertTrue(result.booleanValue());
		
		
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(true));
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON_COMMENT, String.valueOf(true));
		
		commentUtils.addComment(null, null,null, null, null);
		
		result = getResult(0);
		
		assertNotNull(result);
		assertFalse(result.booleanValue());
		
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(true));
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON_COMMENT, String.valueOf(false));
		
		commentUtils.addComment(null, null,null, null, null);
		
		result = getResult(0);
		
		assertNotNull(result);
		assertTrue(result.booleanValue());				
	}
	
	public void testAuthPanelAuthRequiredOnLike() {
		SocializeLikeUtils likeUtils = new SocializeLikeUtils() {
			@Override
			protected boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
				return true;
			}

			@Override
			protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
				return false;
			}
		};
		
		IAuthDialogFactory authDialogFactory = new IAuthDialogFactory() {
			@Override
			public void show(Context context, AuthDialogListener listener, boolean required) {
				addResult(0, required);
			}
			
			@Override
			public void preload(Context context) {}
		};
		
		SocializeConfig config = new SocializeConfig();
		
		likeUtils.setAuthDialogFactory(authDialogFactory);
		likeUtils.setConfig(config);
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(false));
		
		likeUtils.like(null, null,null, null);
		
		Boolean result = getResult(0);
		
		assertNotNull(result);
		assertTrue(result.booleanValue());
		
		
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(true));
		
		likeUtils.like(null, null,null, null);
		
		result = getResult(0);
		
		assertNotNull(result);
		assertFalse(result.booleanValue());
		
	}

	public void testAuthPanelUI00() throws Throwable {
		doAuthPanelUITest(true, true, View.GONE);
	}
	
	public void testAuthPanelUI01() throws Throwable {
		doAuthPanelUITest(true, false, View.VISIBLE);
	}
	
	public void testAuthPanelUI02() throws Throwable {
		doAuthPanelUITest(false, true, View.GONE);
	}
	
	public void testAuthPanelUI03() throws Throwable {
		doAuthPanelUITest(false, false, View.GONE);
	}
	
	private void doAuthPanelUITest(boolean neverAuth, boolean authRequired, int expectedVisibility) {
		SocializeConfig config = ConfigUtils.getConfig(getContext());
		config.setProperty(SocializeConfig.SOCIALIZE_ALLOW_NEVER_AUTH, String.valueOf(neverAuth));
		
		AuthPanelView view = SocializeAccess.getBean("authPanelView");
		
		view.setAuthRequired(authRequired);
		
		RememberCell remember = TestUtils.findView(view, RememberCell.class);
	
		
		if(neverAuth) {
			assertNotNull(remember);
			assertEquals(remember.getVisibility(), expectedVisibility);
		}
		else {
			assertNull(remember);
		}
	}

	public void testAuthPanelViewRenderAndClick() throws Throwable {
		
		FacebookAuthClickListener mockFBListener = new FacebookAuthClickListener() {
			@Override
			public void onClick(View view) {
				addResult(0, true);
			}
		};
		
		TwitterAuthClickListener mockTWListener = new TwitterAuthClickListener() {
			@Override
			public void onClick(View view) {
				addResult(1, true);
			}
		};	
		
		// Stub in click handlers
		SocializeIOC.registerStub("facebookAuthClickListener", mockFBListener);
		SocializeIOC.registerStub("twitterAuthClickListener", mockTWListener);
		
		SocializeSystem system = Socialize.getSocialize().getSystem();
		String[] config = system.getBeanConfig();
		
		Socialize.getSocialize().init(getContext(), config);
		
		// For some reason, this test fails when run in a suite because
		// the twitter button is not shown.  Forcing twitter config here
		ConfigUtils.getConfig(getContext()).setTwitterKeySecret("foo", "bar");
		
		final AuthPanelView view = SocializeAccess.getBean("authPanelView");
		
		final CountDownLatch latch0 = new CountDownLatch(1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		
		final Activity activity = TestUtils.getActivity(this);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.setContentView(view);
				latch0.countDown();
			}
		});
		
		latch0.await(10, TimeUnit.SECONDS);
		
		final FacebookSignInCell fbButton = TestUtils.findView(view, FacebookSignInCell.class);
		final TwitterSignInCell twButton = TestUtils.findView(view, TwitterSignInCell.class);
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				boolean clicked = fbButton.performClick();
				addResult(2, clicked);
				addResult(3, twButton.performClick());
				latch1.countDown();
			}
		});		
		
		latch1.await(10, TimeUnit.SECONDS);
		
		assertTrue((Boolean)getResult(0));
		assertTrue((Boolean)getResult(1));
		assertTrue((Boolean)getResult(2));
		assertTrue((Boolean)getResult(3));
	}
}
