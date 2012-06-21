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
package com.socialize.networks.twitter;

import java.lang.reflect.Proxy;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeActionProxy;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;


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
	
	
	/**
	 * 
	 * @param context
	 * @param listener
	 */
	public static void link (Activity context, SocializeAuthListener listener) {
		proxy.link(context, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param token
	 * @param secret
	 * @param listener
	 */
	public static void link (Activity context, String token, String secret, SocializeAuthListener listener) {
		proxy.link(context, token, secret, listener);
	}
	
	/**
	 * 
	 * @param context
	 */
	public static void unlink (Context context) {
		proxy.unlink(context);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLinked(Context context) {
		return proxy.isLinked(context);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAvailable(Context context) {
		return proxy.isAvailable(context);
	}

	/**
	 * 
	 * @param context
	 * @param consumerKey
	 * @param consumerSecret
	 */
	public static void setCredentials(Context context, String consumerKey, String consumerSecret) {
		proxy.setCredentials(context, consumerKey, consumerSecret);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getAccessToken(Context context) {
		return proxy.getAccessToken(context);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getTokenSecret(Context context) {
		return proxy.getTokenSecret(context);
	}
	
	/**
	 * 
	 * @param context
	 * @param entity
	 * @param text
	 * @param listener
	 */
	public static void tweetEntity(final Activity context, final Entity entity, final String text, final SocialNetworkShareListener listener) {
		if(proxy.isLinked(context)) {
			proxy.tweetEntity(context, entity, text, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
				
				@Override
				public void onCancel() {
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					proxy.tweetEntity(context, entity, text, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});
		}			
	}
	
	/**
	 * 
	 * @param context
	 * @param resource
	 * @param postData
	 * @param listener
	 */
	public static void post(final Activity context, final String resource, final Map<String, Object> postData, final SocialNetworkPostListener listener) {
		if(proxy.isLinked(context)) {
			proxy.post(context, resource, postData, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
				
				@Override
				public void onCancel() {
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					proxy.post(context, resource, postData, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});			
		}
	}
	
	public static void get(final Activity context, final String resource, final Map<String, Object> params, final SocialNetworkPostListener listener) {
		if(proxy.isLinked(context)) {
			proxy.get(context, resource, params, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
				
				@Override
				public void onCancel() {
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					proxy.get(context, resource, params, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});			
		}
	}	
	
	/**
	 * 
	 * @param context
	 * @param tweet
	 * @param listener
	 */
	public static void tweet(final Activity context, final Tweet tweet, final SocialNetworkListener listener) {
		if(proxy.isLinked(context)) {
			proxy.tweet(context, tweet, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
				
				@Override
				public void onCancel() {
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					proxy.tweet(context, tweet, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});			
		}
	}
}
