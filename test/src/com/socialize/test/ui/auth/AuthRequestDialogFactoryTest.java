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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookButton;
import com.socialize.test.mock.MockAlertDialog;
import com.socialize.test.mock.MockBuilder;
import com.socialize.test.ui.SocializeUIActivityTest;
import com.socialize.ui.auth.AuthConfirmDialogFactory;
import com.socialize.ui.auth.AuthRequestDialogFactory;
import com.socialize.ui.auth.AuthRequestDialogView;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactoryTest extends SocializeUIActivityTest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({
		IBeanFactory.class, 
		AuthRequestDialogView.class, 
		Drawables.class, 
		AuthRequestListener.class, 
		FacebookButton.class,
		MockBuilder.class,
		MockAlertDialog.class,
		Drawable.class})
	public void test_show_and_clickFB() throws Throwable {

		final IBeanFactory<AuthRequestDialogView> authRequestDialogViewFactory = AndroidMock.createMock(IBeanFactory.class);
		final Drawables drawables = AndroidMock.createMock(Drawables.class);
		final AuthRequestListener listener = AndroidMock.createMock(AuthRequestListener.class);
		final AuthRequestDialogView view = AndroidMock.createNiceMock(AuthRequestDialogView.class, getContext());
		final Builder builder = AndroidMock.createMock(MockBuilder.class, getContext());

		final AlertDialog alertDialog = AndroidMock.createMock(MockAlertDialog.class, getContext());
		final Drawable drawable = AndroidMock.createMock(Drawable.class);
		
		final FacebookButton button = new FacebookButton(getContext()) {
			@Override
			public void setAuthListener(SocializeAuthListener listener) {
				addResult(0, listener);
			}
		};
		
		AndroidMock.expect(authRequestDialogViewFactory.getBean()).andReturn(view);
		AndroidMock.expect(builder.setView(view)).andReturn(builder);
		AndroidMock.expect(builder.create()).andReturn(alertDialog);
		
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png")).andReturn(drawable);
		
		alertDialog.setIcon(drawable);
		alertDialog.setTitle("Sign in to post comments");
		alertDialog.setOnCancelListener((OnCancelListener)AndroidMock.anyObject());
		
		// 3 times because we are going to test each case for the listener.
		
		alertDialog.dismiss();
		alertDialog.dismiss();
		alertDialog.dismiss();
		
		listener.onResult(alertDialog);
		listener.onResult(alertDialog);
		listener.onResult(alertDialog);
		
		AndroidMock.expect(view.getFacebookSignInButton()).andReturn(button);
		
		AndroidMock.replay(drawables, authRequestDialogViewFactory, view, listener, alertDialog, builder);
		
		AuthRequestDialogFactory dialogFactory = new AuthRequestDialogFactory() {
			@Override
			protected Builder newBuilder(Context context) {
				return builder;
			}

			@Override
			protected void handleError(String msg, SocializeException error) {
				// ignore for test
			}
		};
		
		dialogFactory.setDrawables(drawables);
		dialogFactory.setAuthRequestDialogViewFactory(authRequestDialogViewFactory);
		
		dialogFactory.create(getContext(), listener);
		
		SocializeAuthListener nested = getResult(0);
		
		// Call nested
		nested.onError(new SocializeException("TESTING - IGNORE ME"));
		nested.onAuthSuccess(null);
		nested.onAuthFail(new SocializeException("TESTING - IGNORE ME"));
		
		AndroidMock.verify(drawables, authRequestDialogViewFactory, view, listener, alertDialog, builder);
	}
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		IBeanFactory.class, 
		AuthRequestDialogView.class, 
		AuthConfirmDialogFactory.class,
		Drawables.class, 
		AuthRequestListener.class, 
		FacebookButton.class,
		MockBuilder.class,
		MockAlertDialog.class,
		Drawable.class})
	public void test_show_and_cancel() throws Throwable {

		final IBeanFactory<AuthRequestDialogView> authRequestDialogViewFactory = AndroidMock.createMock(IBeanFactory.class);
		final IBeanFactory<AuthConfirmDialogFactory> authConfirmDialogFactoryFactory = AndroidMock.createMock(IBeanFactory.class);
		final AuthConfirmDialogFactory authConfirmDialogFactory = AndroidMock.createMock(AuthConfirmDialogFactory.class);
		final Drawables drawables = AndroidMock.createMock(Drawables.class);
		final AuthRequestListener listener = AndroidMock.createMock(AuthRequestListener.class);
		final AuthRequestDialogView view = AndroidMock.createNiceMock(AuthRequestDialogView.class, getContext());
		
		
		final Builder builder = AndroidMock.createMock(MockBuilder.class, getContext());

		final AlertDialog alertDialog = new MockAlertDialog(getContext()) {
			@Override
			public void setOnCancelListener(OnCancelListener listener) {
				addResult(0, listener);
			}
		};
		
		final Drawable drawable = AndroidMock.createMock(Drawable.class);
		
		final FacebookButton button = new FacebookButton(getContext()) {
			@Override
			public void setAuthListener(SocializeAuthListener listener) {
				// Do nothing
			}
		};
		
		AndroidMock.expect(authRequestDialogViewFactory.getBean()).andReturn(view);
		
		// On cancel
		AndroidMock.expect(authConfirmDialogFactoryFactory.getBean()).andReturn(authConfirmDialogFactory);
		authConfirmDialogFactory.show(getContext(), listener);
		
		AndroidMock.expect(builder.setView(view)).andReturn(builder);
		AndroidMock.expect(builder.create()).andReturn(alertDialog);
		
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png")).andReturn(drawable);
		
		AndroidMock.expect(view.getFacebookSignInButton()).andReturn(button);
		
		AndroidMock.replay(drawables, authRequestDialogViewFactory, view, authConfirmDialogFactoryFactory, builder);
		
		AuthRequestDialogFactory dialogFactory = new AuthRequestDialogFactory() {
			@Override
			protected Builder newBuilder(Context context) {
				return builder;
			}

			@Override
			protected void handleError(String msg, SocializeException error) {
				// ignore for test
			}
		};
		
		dialogFactory.setDrawables(drawables);
		dialogFactory.setAuthRequestDialogViewFactory(authRequestDialogViewFactory);
		dialogFactory.setAuthConfirmDialogFactory(authConfirmDialogFactoryFactory);
		
		AlertDialog dialog = dialogFactory.create(getContext(), listener);
		
		OnCancelListener nested = getResult(0);
		
		nested.onCancel(dialog);
		
		AndroidMock.verify(drawables, authRequestDialogViewFactory, view, authConfirmDialogFactoryFactory, builder);
	}	
	
	
	
	
}
