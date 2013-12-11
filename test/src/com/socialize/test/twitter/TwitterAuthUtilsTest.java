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
package com.socialize.test.twitter;

import android.app.Dialog;
import android.content.Context;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.twitter.TwitterAuthDialogListener;
import com.socialize.auth.twitter.TwitterAuthListener;
import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.auth.twitter.TwitterAuthUtils;
import com.socialize.auth.twitter.TwitterAuthView;
import com.socialize.error.SocializeException;
import com.socialize.test.SocializeActivityTest;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public class TwitterAuthUtilsTest extends SocializeActivityTest {

	@SuppressWarnings("unchecked")
	public void testShowAuthDialogCancel() throws Throwable {
		
		final Context context = getContext();
		final String key = "foo";
		final String secret = "bar";
		
		TwitterAuthView view = new TwitterAuthView(context) {
			@Override
			public void authenticate() {
				addResult(0, "authenticate");
			}
		};
		
		final IBeanFactory<TwitterAuthView> twitterAuthViewFactory = Mockito.mock(IBeanFactory.class);
		final TwitterAuthProviderInfo info = Mockito.mock(TwitterAuthProviderInfo.class);
		final TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		
		Mockito.when(info.getConsumerKey()).thenReturn(key);
		Mockito.when(info.getConsumerSecret()).thenReturn(secret);
		Mockito.when(twitterAuthViewFactory.getBean(key, secret)).thenReturn(view);

		final TwitterAuthUtils utils = new TwitterAuthUtils();
		
		utils.setTwitterAuthViewFactory(twitterAuthViewFactory);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final List<Dialog> holder = new ArrayList<Dialog>(1);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Dialog dialog = utils.showAuthDialog(context, info, listener);
				holder.add(dialog);
				latch.countDown();
			}
		});
		
		latch.await(10, TimeUnit.SECONDS);
		
		final Dialog dialog = holder.get(0);
		
		// Wait for show;
		sleep(500);
		
		final CountDownLatch latch1 = new CountDownLatch(1);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialog.cancel();
				latch1.countDown();
			}
		});		
		
		latch.await(10, TimeUnit.SECONDS);
		
		// Wait for hide;
		sleep(500);

		Mockito.verify(listener).onCancel();
	}
	
	public void test_newTwitterAuthDialogListener() {
		
		PublicTwitterAuthUtils utils = new PublicTwitterAuthUtils();
		
		final String token = "foobar_token";
		final String secret = "foobar_secret";
		final String screenName = "foobar_screenName";
		final String userId = "foobar_userId";
		final SocializeException e = new SocializeException();
		
		Dialog dialog = Mockito.mock(Dialog.class);
		TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		
		TwitterAuthDialogListener newTwitterAuthDialogListener = utils.newTwitterAuthDialogListener(dialog, listener);
	
		newTwitterAuthDialogListener.onAuthSuccess(token, secret, screenName, userId);
		newTwitterAuthDialogListener.onAuthSuccess(dialog, token, secret, screenName, userId);
		newTwitterAuthDialogListener.onCancel();
		newTwitterAuthDialogListener.onCancel(dialog);
		newTwitterAuthDialogListener.onError(e);
		newTwitterAuthDialogListener.onError(dialog, e);

        Mockito.verify(listener, Mockito.times(2)).onAuthSuccess(token, secret, screenName, userId);
        Mockito.verify(listener, Mockito.times(2)).onCancel();
        Mockito.verify(listener, Mockito.times(2)).onError(e);
		Mockito.verify(dialog, Mockito.times(6)).dismiss();
	}
	
}
