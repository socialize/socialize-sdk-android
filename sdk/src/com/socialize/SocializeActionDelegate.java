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
package com.socialize;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.http.MethodNotSupportedException;
import android.app.Activity;
import android.content.Context;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.error.AuthCanceledException;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;


/**
 * @author Jason Polites
 */
public class SocializeActionDelegate implements InvocationHandler {

	private String delegateBean;
	
	public SocializeActionDelegate(String delegateBean) {
		super();
		this.delegateBean = delegateBean;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Activity context = findContext(args);
		if(context != null) {
			SocializeListener listener = findListener(args);
			if(listener != null) {
				invoke(context, listener, delegateBean, method, args);
			}
			else {
				throw new MethodNotSupportedException("No Socialize Listener found in method arguments for method [" +
						method.getName() +
						"]");
			}
		}
		else {
			throw new MethodNotSupportedException("No activity found in method arguments for method [" +
					method.getName() +
					"]");
		}
		
		// Always void
		return null;
	}
	
	protected void invoke(Activity context, SocializeListener listener, String delegateBean, Method method, Object[] args) throws Throwable {
		SocializeService service = getSocialize();
		
		if(!service.isInitialized()) {
			doInit(context, listener, delegateBean, method, args);
		}
		else if(!service.isAuthenticated()) {
			doAuth(context, listener, delegateBean, method, args);
		}
		else {
			method.invoke(Socialize.getBean(delegateBean), args);
		}		
	}
	
	protected Activity findContext(Object[] args) {
		for (Object object : args) {
			if(object instanceof Activity) {
				return (Activity) object;
			}
		}
		return null;
	}
	
	protected SocializeListener findListener(Object[] args) {
		for (Object object : args) {
			if(object instanceof SocializeListener) {
				return (SocializeListener) object;
			}
		}
		return null;
	}

	protected synchronized <L extends SocializeListener> void doInit(final Activity context, final SocializeListener listener, final String delegateBean, final Method method, final Object[] args) throws Throwable {
		
		final SocializeService service = getSocialize();
		
		if(!service.isInitialized()) {
			
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					service.initAsync(context, new SocializeInitListener() {
						
						@Override
						public void onError(SocializeException error) {
							if(listener != null) {
								listener.onError(error);
							}
						}

						@Override
						public void onInit(Context ctx, IOCContainer container) {
							// Recurse
							try {
								invoke(context, listener, delegateBean, method, args);
							}
							catch (Throwable e) {
								if(listener != null) {
									listener.onError(SocializeException.wrap(e));
								}
							}
						}
					});					
				}
			});

		}
		else {
			// Recurse
			invoke(context, listener, delegateBean, method, args);
		}
	}	
	
	protected synchronized <L extends SocializeListener> void doAuth(final Activity context, final SocializeListener listener, final String delegateBean, final Method method, final Object[] args) throws Throwable {
		final SocializeService service = getSocialize();
		
		if(!service.isAuthenticated()) {
			
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					service.authenticate(context, new SocializeAuthListener() {
						
						@Override
						public void onError(SocializeException error) {
							if(listener != null) {
								listener.onError(error);
							}
						}
						
						@Override
						public void onCancel() {
							if(listener != null) {
								listener.onError(new AuthCanceledException("Authentication was canceled by the user"));
							}
						}

						@Override
						public void onAuthSuccess(SocializeSession session) {
							// Recurse
							try {
								invoke(context, listener, delegateBean, method, args);
							}
							catch (Throwable e) {
								if(listener != null) {
									listener.onError(SocializeException.wrap(e));
								}
							}
						}
						
						@Override
						public void onAuthFail(SocializeException error) {
							if(listener != null) {
								listener.onError(error);
							}
						}
					});	
				}
			});
		}
		else {
			// Recurse
			invoke(context, listener, delegateBean, method, args);
		}
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
