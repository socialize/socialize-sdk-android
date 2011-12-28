package com.socialize.android.ioc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyObject<T> implements InvocationHandler {

	private T delegate;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(delegate != null) {
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
}
