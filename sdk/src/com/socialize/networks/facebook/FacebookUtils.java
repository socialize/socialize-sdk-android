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
package com.socialize.networks.facebook;

import java.lang.reflect.Proxy;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import com.socialize.SocializeActionProxy;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;


/**
 * @author Jason Polites
 */
public class FacebookUtils {
	
	static FacebookUtilsProxy proxy;
	
	static {
		proxy = (FacebookUtilsProxy) Proxy.newProxyInstance(
				FacebookUtilsProxy.class.getClassLoader(),
				new Class[]{FacebookUtilsProxy.class},
				new SocializeActionProxy("facebookUtils")); // Bean name
	}

	/**
	 * Links the current user to a facebook account.  The user will be presented with the Facebook authentication dialog.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 */
	public static void link (Activity context, SocializeAuthListener listener){
		proxy.link(context, listener);
	}
	
	/**
	 * Links the current user to a facebook account with custom FB permissions.  The user will be presented with the Facebook authentication dialog.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 * @param permissions One or more permissions defined by http://developers.facebook.com/docs/authentication/permissions/
	 */
	public static void link (Activity context, SocializeAuthListener listener, String...permissions){
		proxy.link(context, listener, permissions);
	}	
	
	/**
	 * Links an existing Facebook access token with the current user.  No authentication dialog will be shown.
	 * @param context The current context.
	 * @param token The Facebook access token.
	 * @param listener A listener to handle the result.
	 */
	public static void link (Activity context, String token, SocializeAuthListener listener){
		proxy.link(context, token, listener);
	}
	
	/**
	 * Removes the Facebook credentials from the current user.
	 * @param context The current context.
	 */
	public static void unlink (Context context){
		proxy.unlink(context);
	}
	
	/**
	 * Returns true if the current user is already linked to Facebook.
	 * @param context The current context.
	 * @return True if the current user has linked their Facebook account.
	 */
	public static boolean isLinked(Context context){
		return proxy.isLinked(context);
	}
	
	/**
	 * Returns true if this application has Facebook configured.  Minimum requirement is a Facebook app ID.
	 * @param context The current context.
	 * @return True if the Facebook has been configured for this application.
	 */
	public static boolean isAvailable(Context context){
		return proxy.isAvailable(context);
	}
	
	/**
	 * Sets a Facebook App ID for this application.
	 * NOTE: This will not persist between app sessions.  It is recommended that the app ID be set in socialize.properties under the key facebook.app.id
	 * @param context The current context.
	 * @param appId The Facebook app id, obtained from http://developers.facebook.com/apps
	 */
	public static void setAppId(Context context, String appId){
		proxy.setAppId(context, appId);
	}
	
	/**
	 * Returns the Facebook access token for the current user.
	 * @return The access token or null if the user is not linked to Facebook.
	 */
	public static String getAccessToken(Context context){
		return proxy.getAccessToken(context);
	}
	
	/**
	 * Posts an entity to the user's Facebook wall.  Post data can be customized by implementing the onBeforePost method in the given SocialNetworkListener.
	 * @param context The current context.
	 * @param entity The Socialize entity to which the post corresponds (Required)
	 * @param text The text for the post.
	 * @param listener A listnener to handle callbacks from the post.
	 */
	public static void postEntity(final Activity context, final Entity entity, final String text, final SocialNetworkListener listener){
		
		if(proxy.isLinked(context)) {
			proxy.postEntity(context, entity, text, listener);	
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
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
					proxy.postEntity(context, entity, text, listener);	
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
					}
				}
			});
		}
	}
	
	/**
	 * Calls the Facebook graph API directly with a POST.  This will NOT create a share object in Socialize and is simply a raw call to Facebook.
	 * NOTE: The current user must first be authenticated using a call to 'link'.
	 * This method is asynchronous and can be run from the main UI thread.
	 * @param context The current context.
	 * @param graphPath The path on the Facebook graph api to be called for the CURRENT user.  E.g. me/feed
	 * @param params The data to be sent in the request.
	 * @param listener A listener to handle the result.
	 */
	public static void post(final Activity context, final String graphPath, final Map<String, String> params, final SocialNetworkPostListener listener) {
		
		if(proxy.isLinked(context)) {
			proxy.post(context, graphPath, params, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
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
					proxy.post(context, graphPath, params, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
					}
				}
			});
		}		
	}
	
	/**
	 * Calls the Facebook graph API directly with a GET.  This will NOT create a share object in Socialize and is simply a raw call to Facebook.
	 * NOTE: The current user must first be authenticated using a call to 'link'.
	 * This method is asynchronous and can be run from the main UI thread.
	 * @param context The current context.
	 * @param graphPath The path on the Facebook graph api to be called for the CURRENT user.  E.g. me/feed
	 * @param params The data to be sent in the request.
	 * @param listener A listener to handle the result.
	 */
	public static void get(final Activity context, final String graphPath, final Map<String, String> params, final SocialNetworkPostListener listener) {
		
		if(proxy.isLinked(context)) {
			proxy.get(context, graphPath, params, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
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
					proxy.get(context, graphPath, params, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
					}
				}
			});
		}			
	}
	
	/**
	 * Calls the Facebook graph API directly with a DELETE.  This will NOT create a share object in Socialize and is simply a raw call to Facebook.
	 * NOTE: The current user must first be authenticated using a call to 'link'.
	 * This method is asynchronous and can be run from the main UI thread.
	 * @param context The current context.
	 * @param graphPath The path on the Facebook graph api to be called for the CURRENT user.  E.g. me/feed
	 * @param params The data to be sent in the request.
	 * @param listener A listener to handle the result.
	 */
	public static void delete(final Activity context, final String graphPath, final Map<String, String> params, final SocialNetworkPostListener listener) {
		if(proxy.isLinked(context)) {
			proxy.delete(context, graphPath, params, listener);
		}
		else {
			proxy.link(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
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
					proxy.delete(context, graphPath, params, listener);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.FACEBOOK, error);
					}
				}
			});
		}			
	}
}
