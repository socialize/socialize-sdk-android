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
package com.socialize.ui.view;

import android.content.Context;
import android.view.View;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class AuthenticatedViewListener implements SocializeAuthListener {
	
	protected AuthenticatedView view;
	protected Context context;
	protected IOCContainer container;
	
	public AuthenticatedViewListener(Context context, AuthenticatedView view, IOCContainer container) {
		super();
		this.view = view;
		this.context = context;
		this.container = container;
	}

	@Override
	public void onError(SocializeException error) {
		// Ensure we notify any pending dialogs
		view.onAfterAuthenticate(container);
		view.removeAllViews();
		view.showError(context, error);
		SocializeLogger.e(error.getMessage(), error);
	}
	
	@Override
	public void onCancel() {
		// Nothing
	}

	@Override
	public void onAuthSuccess(SocializeSession session) {
		// Render the childView
		try {
			view.onAfterAuthenticate(container); // Dialogs dismissed here
			View v = view.getView();
			view.removeAllViews();
			if(v != null) {
				view.addView(v);
			}
			else {
				SocializeLogger.e(view.getClass().getSimpleName() + " failed to produce a view");
			}
		} 
		catch (Throwable e) {
			SocializeLogger.e("", e);
		}
	}
	
	@Override
	public void onAuthFail(SocializeException error) {
		view.onAfterAuthenticate(container);
		view.showError(context, error);
		SocializeLogger.e(error.getMessage(), error);
	}
}