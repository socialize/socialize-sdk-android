package com.socialize.android.ioc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyObject<T> implements InvocationHandler {

	private T delegate;
	private MethodInvocationListener methodInvocationListener;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(delegate != null && (methodInvocationListener == null || methodInvocationListener.isDelegate(method))) {
			return method.invoke(delegate, args);
		}
		else {
			return method.invoke(proxy, args);
		}
	}

	public T getDelegate() {
		return delegate;
	}

	public void setDelegate(T delegate) {
		this.delegate = delegate;
	}

	
	public MethodInvocationListener getMethodInvocationListener() {
		return methodInvocationListener;
	}
	
	public void setMethodInvocationListener(MethodInvocationListener methodInvocationListener) {
		this.methodInvocationListener = methodInvocationListener;
	}
	
}
