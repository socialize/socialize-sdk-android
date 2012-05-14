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
package com.socialize.test.unit.facebook;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookAuthClickListener;
import com.socialize.test.PublicSocialize;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.dialog.SimpleDialogFactory;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	SocializeConfig.class,
	SocializeAuthListener.class,
	SimpleDialogFactory.class,
	ProgressDialog.class,
	View.class,
	SocializeService.class,
	SocializeSession.class
})
public class FacebookAuthClickListenerTest extends SocializeUnitTest {

	static enum AUTH_REPSONSE {SUCCESS, FAIL, ERROR, CANCEL};

	public void testOnClickWithAuthSuccess() {
		doOnClickTest(AUTH_REPSONSE.SUCCESS);
	}
	
	public void testOnClickWithAuthFail() {
		doOnClickTest(AUTH_REPSONSE.FAIL);
	}
	
	public void testOnClickWithAuthError() {
		doOnClickTest(AUTH_REPSONSE.ERROR);
	}
	
	public void testOnClickWithCancel() {
		doOnClickTest(AUTH_REPSONSE.CANCEL);
	}
	
	@SuppressWarnings("unchecked")
	protected void doOnClickTest(final AUTH_REPSONSE response) {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String fbId = "foobar_FB";
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SimpleDialogFactory<ProgressDialog> dialogFactory = AndroidMock.createMock(SimpleDialogFactory.class);
		ProgressDialog dialog = AndroidMock.createMock(ProgressDialog.class, getContext());
		View view = AndroidMock.createMock(View.class, getContext());
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final SocializeException error = new SocializeException("DUMMY - IGNORE ME"); 
		
		final PublicSocialize socialize = new PublicSocialize() {
			
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener listener) {
				addResult(0, consumerKey);
				addResult(1, consumerSecret);
				addResult(2, authProviderInfo);
				
				// Call each method of the listener for the test
				switch (response) {
					case SUCCESS:
						listener.onAuthSuccess(session);
						break;
					case CANCEL:
						listener.onCancel();
						break;
					case ERROR:
						listener.onError(error);
						break;
					case FAIL:
						listener.onAuthFail(error);
						break;					
				}
			}
			
		};
		
		view.setEnabled(false);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		AndroidMock.expect(dialogFactory.show(getContext(), "Authentication", "Authenticating...")).andReturn(dialog);
		
		dialog.dismiss();
		view.setEnabled(true);
		
		switch (response) {
			case SUCCESS:
				listener.onAuthSuccess(session);
				break;
			case CANCEL:
				listener.onCancel();
				break;
			case ERROR:
				listener.onError(error);
				break;
			case FAIL:
				listener.onAuthFail(error);
				break;					
		}
		
		AndroidMock.replay(config);
		AndroidMock.replay(dialogFactory);
		AndroidMock.replay(dialog);
		AndroidMock.replay(view);
		AndroidMock.replay(listener);
			
		FacebookAuthClickListener facebookAuthClickListener = new FacebookAuthClickListener() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		facebookAuthClickListener.setConfig(config);
		facebookAuthClickListener.setDialogFactory(dialogFactory);
		facebookAuthClickListener.setListener(listener);
		
		facebookAuthClickListener.onClick(view);
		
		AndroidMock.verify(config);
		AndroidMock.verify(dialogFactory);
		AndroidMock.verify(dialog);
		AndroidMock.verify(view);
		AndroidMock.verify(listener);
		
		
		final String consumerKeyAfter = getResult(0);
		final String consumerSecretAfter = getResult(1);
		final AuthProviderInfo authProviderInfo = getResult(2);
		
		assertEquals(consumerKey, consumerKeyAfter);
		assertEquals(consumerSecret, consumerSecretAfter);
		
		assertTrue((authProviderInfo instanceof FacebookAuthProviderInfo));
		
		FacebookAuthProviderInfo fba = (FacebookAuthProviderInfo) authProviderInfo;
		
		assertEquals(AuthProviderType.FACEBOOK, fba.getType());
		assertEquals(fbId, fba.getAppId());
	}
	
}
