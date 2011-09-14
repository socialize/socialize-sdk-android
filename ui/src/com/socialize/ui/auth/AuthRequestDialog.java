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

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Prompts the user to authenticate
 * @author Jason Polites
 */
public class AuthRequestDialog extends Dialog {
	
	private AuthRequestDialogView authRequestDialogView;
	private SocializeLogger logger;
	
	public AuthRequestDialog(Context context) {
		super(context);
	}

	public void init() {
		setTitle("Authenticate");
		setContentView(authRequestDialogView);
	}

	public void setAuthRequestDialogView(AuthRequestDialogView authRequestDialogView) {
		this.authRequestDialogView = authRequestDialogView;
	}

	public void show(final AuthRequestListener listener) {
		
		authRequestDialogView.getSocializeSkipAuthButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				listener.onResult(AuthRequestDialog.this);
			}
		});
		
		authRequestDialogView.getFacebookSignInButton().setAuthListener(new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError("Error during auth", error);
				dismiss();
				listener.onResult(AuthRequestDialog.this);
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				// TODO: Launch profile view
				dismiss();
				listener.onResult(AuthRequestDialog.this);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError("Error during auth", error);
				dismiss();
				listener.onResult(AuthRequestDialog.this);
			}
		});
		
		super.show();
	}
	
	protected void handleError(String msg, SocializeException error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
