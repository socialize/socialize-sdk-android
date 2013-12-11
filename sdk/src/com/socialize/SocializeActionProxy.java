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
package com.socialize;

import android.app.Activity;
import android.content.Context;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.annotations.NoAuth;
import com.socialize.annotations.Synchronous;
import com.socialize.api.SocializeSession;
import com.socialize.error.AuthCanceledException;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.log.SocializeLogger;
import org.apache.http.MethodNotSupportedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Proxies all requests to top level Socialize interfaces to ensure that clients are both initialized and authenticated.
 * @author Jason Polites
 */
public class SocializeActionProxy implements InvocationHandler {

	private String delegateBean;
	private boolean synchronous = false;
	
	public SocializeActionProxy(String delegateBean) {
		super();
		this.delegateBean = delegateBean;
	}
	
	public SocializeActionProxy(String delegateBean, boolean synchronous) {
		this(delegateBean);
		this.synchronous = synchronous;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if(synchronous || method.isAnnotationPresent(Synchronous.class) || !isVoidMethod(method)) {
				
				Context context = findContext(args);
				
				if(context != null) {
					synchronized (this) {
                        SocializeService socialize = Socialize.getSocialize();

                        if(!socialize.isInitialized(context)) {
							socialize.init(context);
							if(!socialize.isAuthenticated() && !method.isAnnotationPresent(NoAuth.class)) {
								socialize.authenticateSynchronous(context);
							}
						}
					}
				}
				
				return method.invoke(getBean(), args);
			}
			else {
				Activity context = findActivity(args);
				
				if(context != null) {
					SocializeListener listener = findListener(args);
					
					// Always init to set the context
					invokeWithInit(context, listener, method, args);
				}
				else {
					throw new MethodNotSupportedException("No activity found in method arguments for method [" +
							method.getName() +
							"]");
				}
			}
			
			// Always void
			return null;
		}
		catch (Throwable e) {
			SocializeLogger.e(e.getMessage(), e);
			throw e;
		}
	}
	
	protected boolean isVoidMethod(Method method) {
		Class<?> returnType = method.getReturnType();
		return (returnType == null || returnType.equals(Void.TYPE));
	}
	
	protected void invokeWithInit(final Activity context, final SocializeListener listener, final Method method, final Object[] args) throws Throwable {
		
		SocializeServiceImpl service = getSocialize();
		
		if(service.isInitialized(context)) {
			service.setContext(context);
			invoke(context, listener, method, args);
		}
		else {
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
						invoke(context, listener, method, args);
					}
					catch (Throwable e) {
						if(listener != null) {
							listener.onError(SocializeException.wrap(e));
						}
					}
				}
			});	
		}
	}
	
	protected void invoke(Activity context, SocializeListener listener, Method method, Object[] args) throws Throwable {

		SocializeService service = getSocialize();
		
		if(!service.isAuthenticated() && !method.isAnnotationPresent(NoAuth.class)) {
			doAuthAsync(context, listener, method, args);
		}
		else {
			method.invoke(getBean(), args);
		}		
	}
	
	protected Object getBean() throws MethodNotSupportedException {
		Object bean = Socialize.getBean(delegateBean);
		
		if(bean != null) {
			return bean;
		}
		else {
			throw new MethodNotSupportedException("No bean with name [" +
					delegateBean +
					"] found");
		}		
	}
	
	protected Activity findActivity(Object[] args) {
		for (Object object : args) {
			if(object instanceof Activity) {
				return (Activity) object;
			}
		}
		return null;
	}
	
	protected Context findContext(Object[] args) {
		for (Object object : args) {
			if(object instanceof Context) {
				return (Context) object;
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

	protected synchronized <L extends SocializeListener> void doAuthAsync(final Activity context, final SocializeListener listener, final Method method, final Object[] args) throws Throwable {
		final SocializeService service = getSocialize();
		
		if(!service.isAuthenticated()) {
			
			synchronized (this) {
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
										invoke(context, listener, method, args);
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
					invoke(context, listener, method, args);
				}
			}
		}
		else {
			// Recurse
			invoke(context, listener, method, args);
		}
	}
	
	protected SocializeServiceImpl getSocialize() {
		return (SocializeServiceImpl) Socialize.getSocialize();
	}
}
