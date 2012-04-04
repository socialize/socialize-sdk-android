package com.socialize.android.ioc;

import java.lang.reflect.Method;


/**
 * @author Jason Polites
 *
 */
public interface MethodInvocationListener {

	public boolean isDelegate(Method m);
	
}
