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
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class AuthConfirmDialogFactory  {
	
	private IBeanFactory<AuthConfirmDialogView> authConfirmDialogViewFactory;
	private Drawables drawables;
	private SocializeLogger logger;
	
	public AuthConfirmDialogFactory() {
		super();
	}
	
	public AlertDialog create(Context context, final AuthRequestListener listener) {
		
		AlertDialog.Builder builder = newBuilder(context);
		
		AuthConfirmDialogView view = authConfirmDialogViewFactory.getBean();
		
		builder.setView(view);
		
		final AlertDialog alertDialog = builder.create();
		
		alertDialog.setIcon(drawables.getDrawable("socialize_icon_white.png"));
		alertDialog.setTitle("Post Anonymously");
		
		view.getSocializeSkipAuthButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				listener.onResult(alertDialog);
			}
		});
		
		return alertDialog;
	}
	
	// So we can mock
	protected Builder newBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}

	public void show(Context context, final AuthRequestListener listener) {
		create(context, listener).show();
	}
	
	protected void handleError(String msg, SocializeException error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();
		}
	}

	public void setAuthConfirmDialogViewFactory(IBeanFactory<AuthConfirmDialogView> authConfirmDialogViewFactory) {
		this.authConfirmDialogViewFactory = authConfirmDialogViewFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
}
