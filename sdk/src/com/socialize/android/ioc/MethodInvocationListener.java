package com.socialize.android.ioc;

import java.lang.reflect.Method;


/**
 * @author Jason Polites
 *
 */
public interface MethodInvocationListener {

	public boolean useDelegate(Method m);
	
	public void onMethod(Object target, Method method, Object[] args);
	
}
