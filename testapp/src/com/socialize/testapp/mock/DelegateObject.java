package com.socialize.testapp.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DelegateObject implements InvocationHandler {

	private Object delegate;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(delegate != null) {
			return method.invoke(delegate, args);
		}
		else {
			return method.invoke(proxy, args);
		}
	}

	public Object getDelegate() {
		return delegate;
	}

	public void setDelegate(Object delegate) {
		this.delegate = delegate;
	}
}
