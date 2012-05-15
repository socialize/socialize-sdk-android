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
package com.socialize.networks.twitter;

import java.lang.reflect.Proxy;
import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeActionProxy;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public class TwitterUtils {
	
	static TwitterUtilsProxy proxy;
	
	static {
		proxy = (TwitterUtilsProxy) Proxy.newProxyInstance(
				TwitterUtilsProxy.class.getClassLoader(),
				new Class[]{TwitterUtilsProxy.class},
				new SocializeActionProxy("twitterUtils")); // Bean name
	}
	
	
	public static void link (Activity context, SocializeAuthListener listener) {
		proxy.link(context, listener);
	}
	
	public static void link (Activity context, String token, String secret, SocializeAuthListener listener) {
		proxy.link(context, token, secret, listener);
	}
	
	public static void unlink (Context context) {
		proxy.unlink(context);
	}

	public static boolean isLinked(Context context) {
		return proxy.isLinked(context);
	}

	public static boolean isAvailable(Context context) {
		return proxy.isAvailable(context);
	}

	public static void setCredentials(Context context, String consumerKey, String consumerSecret) {
		proxy.setCredentials(context, consumerKey, consumerSecret);
	}

	public static String getAccessToken(Context context) {
		return proxy.getAccessToken(context);
	}

	public static String getTokenSecret(Context context) {
		return proxy.getTokenSecret(context);
	}
	
	public static void tweet(final Activity context, final Entity entity, final String text, final SocialNetworkListener listener) {
		proxy.tweet(context, entity, text, listener);
	}
}
