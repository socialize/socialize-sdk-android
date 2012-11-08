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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.FacebookAuthClickListener;
import com.socialize.networks.facebook.FacebookPermissionCallback;
import com.socialize.networks.facebook.FacebookUtilsProxy;
import com.socialize.test.SocializeActivityTest;
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
public class FacebookAuthClickListenerTest extends SocializeActivityTest {

	static enum AUTH_REPSONSE {SUCCESS, FAIL, ERROR, CANCEL};

	public void testOnClickWithAuthSuccess() throws Exception {
		doOnClickTest(AUTH_REPSONSE.SUCCESS);
	}
	
	public void testOnClickWithAuthFail() throws Exception {
		doOnClickTest(AUTH_REPSONSE.FAIL);
	}
	
	public void testOnClickWithAuthError() throws Exception {
		doOnClickTest(AUTH_REPSONSE.ERROR);
	}
	
	public void testOnClickWithCancel() throws Exception {
		doOnClickTest(AUTH_REPSONSE.CANCEL);
	}
	
	@SuppressWarnings("unchecked")
	protected void doOnClickTest(final AUTH_REPSONSE response) throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		SocializeAuthListener listener = null;
		SimpleDialogFactory<ProgressDialog> dialogFactory = AndroidMock.createMock(SimpleDialogFactory.class);
		ProgressDialog dialog = AndroidMock.createMock(ProgressDialog.class, getContext());
		View view = AndroidMock.createMock(View.class, getContext());
		FacebookUtilsProxy facebookUtils = new FacebookUtilsProxy() {
			
			@Override
			public void unlink(Context context, SocializeDeAuthListener listener) {}
			
			@Override
			public void setAppId(Context context, String appId) {}
			
			@Override
			public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {}
			
			@Override
			public void link(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener) {}

			@Override
			public void link(Activity context, SocializeAuthListener listener, String... permissions) {}

			@Override
			public void link(Activity context, SocializeAuthListener listener) {
				addResult(0, listener);
				latch.countDown();
			}
			
			@Override
			public boolean isLinked(Context context) {
				return false;
			}
			
			@Override
			public boolean isAvailable(Context context) {
				return true;
			}
			
			@Override
			public String getAccessToken(Context context) {
				return null;
			}
			
			@Override
			public byte[] getImageForPost(Activity context, Uri imagePath) throws IOException {
				return null;
			}
			
			@Override
			public byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException {
				return null;
			}

			@Override
			public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {}

			@Override
			public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {}

			@Override
			public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {}

			@Override
			public void extendAccessToken(Activity context, SocializeAuthListener listener) {}

			@Override
			public void getCurrentPermissions(Activity parent, String token, FacebookPermissionCallback callback) {}

			@Override
			public Facebook getFacebook(Context context) {
				return null;
			}
		};
		
		// Stub in facebook utils
		SocializeIOC.registerStub("facebookUtils", facebookUtils);
		
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final SocializeException error = new SocializeException("DUMMY - IGNORE ME"); 
		
		view.setEnabled(false);
		
		AndroidMock.expect(dialogFactory.show(getContext(), "socialize_auth_dialog", "socialize_auth_dialog_message")).andReturn(dialog);
		
		dialog.dismiss();
		
		view.setEnabled(true);
		
		AndroidMock.replay(dialogFactory,dialog,view);
			
		FacebookAuthClickListener facebookAuthClickListener = new FacebookAuthClickListener();
		facebookAuthClickListener.setDialogFactory(dialogFactory);
		facebookAuthClickListener.setListener(listener);
		facebookAuthClickListener.onClick(view);

		assertTrue(latch.await(20, TimeUnit.SECONDS));
		
		listener = getResult(0);
		
		assertNotNull(listener);
		
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
			
		AndroidMock.verify(dialogFactory,dialog,view);
	}
	
}
