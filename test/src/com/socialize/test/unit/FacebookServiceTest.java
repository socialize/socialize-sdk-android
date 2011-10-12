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
package com.socialize.test.unit;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.DialogListener;
import com.socialize.listener.AuthProviderListener;
import com.socialize.sample.mock.MockAlertDialog;
import com.socialize.sample.mock.MockBuilder;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.DialogFactory;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	Activity.class, 
	Facebook.class, 
	FacebookSessionStore.class, 
	AuthProviderListener.class,
	Drawables.class,
	DialogFactory.class})
	public class FacebookServiceTest extends SocializeActivityTest {

	public void testAuthenticate() {

		final String appId = "foobar";
		final String[] permissions = new String[]{};

		Activity context = AndroidMock.createMock(Activity.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);

		// Expect
		AndroidMock.expect(facebookSessionStore.restore(facebook, context)).andReturn(true);
		facebook.authorize(AndroidMock.eq(context), AndroidMock.eq(permissions), (DialogListener) AndroidMock.anyObject());

		AndroidMock.replay(facebookSessionStore);
		AndroidMock.replay(facebook);

		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory);
		service.authenticate(permissions);

		AndroidMock.verify(facebookSessionStore);
		AndroidMock.verify(facebook);
	}

	public void testFinishCallsFinishOnActivity() {

		final String appId = "foobar";

		Activity context = AndroidMock.createMock(Activity.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);


		// Expect
		context.finish();

		AndroidMock.replay(context);

		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory);
		service.finish();

		AndroidMock.verify(context);
	}

	public void testLogoutCallsLogoutOnFacebook() throws Exception {

		final String appId = "foobar";

		Activity context = AndroidMock.createMock(Activity.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);


		// Expect
		AndroidMock.expect(facebook.logout(context)).andReturn(appId);

		AndroidMock.replay(facebook);

		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory);
		service.logout();

		AndroidMock.verify(facebook);
	}


	public void testLogoutFail() throws Exception {

		final String appId = "foobar";
		final String errorMessage = "foobar_error";

		Activity context = AndroidMock.createMock(Activity.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);

		AuthProviderListener authProviderListener = new AuthProviderListener() {

			@Override
			public void onError(SocializeException error) {
				addResult(error);
			}

			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				fail();

			}

			@Override
			public void onAuthFail(SocializeException error) {
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
		};

		// Expect
		AndroidMock.expect(facebook.logout(context)).andThrow(new IOException(errorMessage));
		AndroidMock.replay(facebook);

		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory);
		service.logout();

		AndroidMock.verify(facebook);

		SocializeException error = getNextResult();

		assertNotNull(error);
		assertNotNull(error.getCause());
		assertEquals(errorMessage, error.getCause().getMessage());

	}

	public void testAuthenticateCallsAuthenticateWithCorrectPermissions() {
		FacebookService service = new FacebookService(null, null, null, null, null) {
			@Override
			public void authenticate(String[] permissions) {
				assertNotNull(permissions);
				assertTrue(permissions.length == 2);
				assertEquals(FacebookService.DEFAULT_PERMISSIONS[0], permissions[0]);
				assertEquals(FacebookService.DEFAULT_PERMISSIONS[1], permissions[1]);
				addResult(true);
			}
		};

		service.authenticate();

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

	@UsesMocks ({MockBuilder.class, MockAlertDialog.class})
	public void testErrorUI() {

		final String errorMessage = "foobar_error";
		final String appId = "foobar";

		Activity context = AndroidMock.createMock(Activity.class);
		MockAlertDialog dialog = AndroidMock.createMock(MockAlertDialog.class,getActivity());
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);
		MockBuilder builder = AndroidMock.createMock(MockBuilder.class, context);

		// Expect
		AndroidMock.expect(dialogFactory.getAlertDialogBuilder(context)).andReturn(builder);

		AndroidMock.expect(builder.setTitle("Oops!")).andReturn(builder);
		AndroidMock.expect(builder.setMessage("Oops!\nSomething went wrong...\n[" + errorMessage + "]")).andReturn(builder);
		AndroidMock.expect(builder.setCancelable(false)).andReturn(builder);
		AndroidMock.expect(builder.setPositiveButton(AndroidMock.eq("Try again"), (OnClickListener) AndroidMock.anyObject())).andReturn(builder);	
		AndroidMock.expect(builder.setNegativeButton(AndroidMock.eq("Cancel"), (OnClickListener) AndroidMock.anyObject())).andReturn(builder);	
		AndroidMock.expect(builder.create()).andReturn(dialog);
		
		dialog.show();

		AndroidMock.replay(dialogFactory);
		AndroidMock.replay(builder);
		AndroidMock.replay(dialog);

		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory);
		service.doErrorUI(errorMessage, null, false);

		AndroidMock.verify(dialogFactory);
		AndroidMock.verify(builder);
		AndroidMock.verify(dialog);
	}

	public void testDoError() throws Throwable {

		final String errorMessage = "foobar_error - IGNORE ME";

		final CountDownLatch lock = new CountDownLatch(1);

		final FacebookService service = new FacebookService(getActivity(), null, null, null, null) {
			@Override
			public void doErrorUI(String error, String[] permissions, boolean sso) {
				addResult(error);
				lock.countDown();
			}
		};

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.doError(new Throwable(errorMessage), null, false);
			} 
		});

		lock.await();

		String msg = getNextResult();

		assertNotNull(msg);
		assertEquals(errorMessage, msg);
	}

	@UsesMocks (AlertDialog.class)
	public void testDoErrorUI() {

		final String error = "foobar";
		final AlertDialog mockDialog = AndroidMock.createMock(AlertDialog.class, getActivity());

		mockDialog.show();

		final FacebookService service = new FacebookService(getActivity(), null, null, null, null) {
			@Override
			public AlertDialog makeErrorDialog(String error, String[] permissions, boolean sso) {
				addResult(error);
				return mockDialog;
			}

		};

		AndroidMock.replay(mockDialog);

		service.doErrorUI(error, null, false);

		String result = getNextResult();

		assertNotNull(result);
		assertEquals(error, result);

		AndroidMock.verify(mockDialog);

	}

	@UsesMocks (DialogInterface.class)
	public void testMakeErrorDialogListener() {

		final String appId = "foobar";
		final String error = "foobar_error";
		
		DialogInterface dialog = AndroidMock.createMock(DialogInterface.class);
		Activity context = AndroidMock.createMock(Activity.class);
		Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);

		AlertDialog.Builder builder = new AlertDialog.Builder(context) {

			@Override
			public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
				addResult(listener);
				return this;
			}

			@Override
			public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
				addResult(listener);
				return this;
			}

			@Override
			public AlertDialog create() {
				addResult(true);
				return null;
			}
		};
		
		
		AndroidMock.expect(dialogFactory.getAlertDialogBuilder(context)).andReturn(builder);

		dialog.dismiss();
		dialog.dismiss();
		
		AndroidMock.replay(dialogFactory);
		AndroidMock.replay(dialog);
		
		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener, dialogFactory) {
			@Override
			public void authenticate() {
				addResult("auth_called");
			}

			@Override
			public void finish() {
				addResult("finish_called");
			}
		};
		
		service.makeErrorDialog(error, null, false);
		
		// Reverse order
		Boolean created = getNextResult();
		OnClickListener negative = getNextResult();
		OnClickListener positive = getNextResult();
		
		assertNotNull(positive);
		assertNotNull(negative);
		assertNotNull(created);
		
		assertTrue(created);
		
		positive.onClick(dialog, -1);
		String authenticated = getNextResult();
		assertNotNull(authenticated);
		assertEquals("auth_called", authenticated);
		
		negative.onClick(dialog, -1);
		String finish = getNextResult();
		assertNotNull(finish);
		assertEquals("finish_called", finish);
		
		AndroidMock.verify(dialogFactory);
		AndroidMock.verify(dialog);
	}
}
