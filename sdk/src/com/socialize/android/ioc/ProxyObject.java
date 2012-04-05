package com.socialize.android.ioc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyObject<T> implements InvocationHandler {

	private T delegate;
	private T methodDelegate;
	private MethodInvocationListener methodInvocationListener;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(methodDelegate != null && (methodInvocationListener == null || methodInvocationListener.useDelegate(method))) {
			return method.invoke(methodDelegate, args);
		}
		else if(delegate != null) {
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
	
	public void setMethodDelegate(T methodDelegate) {
		this.methodDelegate = methodDelegate;
	}

	public MethodInvocationListener getMethodInvocationListener() {
		return methodInvocationListener;
	}
	
	public void setMethodInvocationListener(MethodInvocationListener methodInvocationListener) {
		this.methodInvocationListener = methodInvocationListener;
	}
	
}
