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
package com.socialize.ui.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactory  {
	
	private IBeanFactory<AuthRequestDialogView> authRequestDialogViewFactory;
	private IBeanFactory<AuthConfirmDialogFactory> authConfirmDialogFactory;
	private Drawables drawables;
	private SocializeLogger logger;
	
	public void show(final Context context, final AuthRequestListener listener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		AuthRequestDialogView view = authRequestDialogViewFactory.getBean();
		
		builder.setView(view);
		
		final AlertDialog alertDialog = builder.create();
		
		alertDialog.setIcon(drawables.getDrawable("socialize_icon_white.png"));
		alertDialog.setTitle("Sign in to post comments");
		
		alertDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Render the confirm dialog
				AuthConfirmDialogFactory dialogFactory = authConfirmDialogFactory.getBean();
				dialogFactory.show(context, listener);
			}
		});
		
		view.getFacebookSignInButton().setAuthListener(new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError("Error during auth", error);
				alertDialog.dismiss();
				listener.onResult(alertDialog);
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				// TODO: Launch profile view
				alertDialog.dismiss();
				listener.onResult(alertDialog);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError("Error during auth", error);
				alertDialog.dismiss();
				listener.onResult(alertDialog);
			}

			@Override
			public void onCancel() {
				// Do nothing
			}
		});
		
	    alertDialog.show();
	}
	
	protected void handleError(String msg, SocializeException error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();
		}
	}

	public void setAuthRequestDialogView(IBeanFactory<AuthRequestDialogView> authRequestDialogViewFactory) {
		this.authRequestDialogViewFactory = authRequestDialogViewFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setAuthConfirmDialogFactory(IBeanFactory<AuthConfirmDialogFactory> authConfirmDialogFactory) {
		this.authConfirmDialogFactory = authConfirmDialogFactory;
	}

	public void setAuthRequestDialogViewFactory(IBeanFactory<AuthRequestDialogView> authRequestDialogViewFactory) {
		this.authRequestDialogViewFactory = authRequestDialogViewFactory;
	}

}
