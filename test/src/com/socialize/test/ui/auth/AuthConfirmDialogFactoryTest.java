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
import android.graphics.drawable.Drawable;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.test.mock.MockAlertDialog;
import com.socialize.test.mock.MockBuilder;
import com.socialize.test.ui.SocializeUIActivityTest;
import com.socialize.ui.auth.AuthConfirmDialogFactory;
import com.socialize.ui.auth.AuthConfirmDialogView;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class AuthConfirmDialogFactoryTest extends SocializeUIActivityTest {
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		IBeanFactory.class, 
		AuthConfirmDialogView.class, 
		Drawables.class, 
		AuthRequestListener.class, 
		SocializeButton.class,
		MockBuilder.class,
		MockAlertDialog.class,
		Drawable.class})
	public void test_show() throws Throwable {
		
		final IBeanFactory<AuthConfirmDialogView> factory = AndroidMock.createMock(IBeanFactory.class);
		final Drawables drawables = AndroidMock.createMock(Drawables.class);
		final AuthRequestListener listener = AndroidMock.createMock(AuthRequestListener.class);
		final AuthConfirmDialogView view = AndroidMock.createNiceMock(AuthConfirmDialogView.class, getContext());
		final Builder builder = AndroidMock.createMock(MockBuilder.class, getContext());

		final AlertDialog alertDialog = AndroidMock.createMock(MockAlertDialog.class, getContext());
		final Drawable drawable = AndroidMock.createMock(Drawable.class);
		
		final SocializeButton button = new SocializeButton(getContext());
		
		AndroidMock.expect(factory.getBean()).andReturn(view);
		AndroidMock.expect(builder.setView(view)).andReturn(builder);
		AndroidMock.expect(builder.create()).andReturn(alertDialog);
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png")).andReturn(drawable);
		
		alertDialog.setIcon(drawable);
		alertDialog.setTitle("Post Anonymously");
		alertDialog.show();
		alertDialog.dismiss();
		
		listener.onResult(alertDialog);
	
		AndroidMock.expect(view.getSocializeSkipAuthButton()).andReturn(button);
		
		AndroidMock.replay(drawables, factory, view, listener, alertDialog, builder);
		
		AuthConfirmDialogFactory dialogFactory = new AuthConfirmDialogFactory() {
			@Override
			protected Builder newBuilder(Context context) {
				return builder;
			}
		};
		
		dialogFactory.setDrawables(drawables);
		dialogFactory.setAuthConfirmDialogViewFactory(factory);
		
		final AlertDialog dialog = dialogFactory.create(getContext(), listener);
		 
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialog.show();
			}
		});
		
		assertTrue(button.performClick());
		
		AndroidMock.verify(drawables, factory, view, listener, alertDialog, builder);
	}
}
