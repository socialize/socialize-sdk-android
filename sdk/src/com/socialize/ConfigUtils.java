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

import android.content.Context;
import com.socialize.config.ConfigUtilsProxy;
import com.socialize.config.SocializeConfig;

import java.lang.reflect.Proxy;


/**
 * Provides access to Socialize configuration (socialize.properties)
 * @author Jason Polites
 */
public class ConfigUtils {
	
	static ConfigUtilsProxy proxy;
	static SocializeConfig preInitConfig = new SocializeConfig();
	
	static {
		proxy = (ConfigUtilsProxy) Proxy.newProxyInstance(
				ConfigUtilsProxy.class.getClassLoader(),
				new Class[]{ConfigUtilsProxy.class},
				new SocializeActionProxy("configUtils"));	// Bean name
	}
	
	/**
	 * Returns the internal Socialize configuration object.
	 * @param context The current context.
	 * @return The global configuration object which can be used to programmatically set config.
	 */
	public static SocializeConfig getConfig(Context context) {
		return proxy.getConfig(context);
	}

	/**
	 * Returns a config object that is valid prior to init, but not valid thereafter.
	 * @return
	 */
	public static SocializeConfig getPreInitConfig() {
		return preInitConfig;
	}
}
