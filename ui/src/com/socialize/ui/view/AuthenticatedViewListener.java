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
package com.socialize.ui.view;

import android.content.Context;
import android.view.View;

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;

/**
 * @author Jason Polites
 *
 */
public class AuthenticatedViewListener implements SocializeAuthListener {
	
	protected AuthenticatedView view;
	protected Context context;
	
	public AuthenticatedViewListener(Context context, AuthenticatedView view) {
		super();
		this.view = view;
		this.context = context;
	}

	@Override
	public void onError(SocializeException error) {
		view.onAfterAuthenticate();
		view.showError(context, error);
		error.printStackTrace();
	}
	
	@Override
	public void onAuthSuccess(SocializeSession session) {
		// Render the childView
		view.onAfterAuthenticate();
		View v = view.getView();
		view.addView(v);
	}
	
	@Override
	public void onAuthFail(SocializeException error) {
		view.onAfterAuthenticate();
		view.showError(context, error);
		error.printStackTrace();
	}
}