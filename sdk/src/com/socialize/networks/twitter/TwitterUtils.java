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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import com.socialize.SocializeActionProxy;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Map;


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
	 * Links the current user to their Twitter account.  This method will request authentication from the user if their credentials are not currently known.
	 * @param context The current activity.
	 * @param listener A listener to handle the result.
	 */
	public static void link (Activity context, SocializeAuthListener listener) {
		proxy.link(context, listener);
	}
	
	/**
	 * Links the current user to their Twitter account using an existing Twitter oAuth token and secret.
	 * @param context The current activity.
	 * @param token The user's Twitter token.
	 * @param secret The user's Twitter token secret.
	 * @param listener A listener to handle the result.
	 */
	public static void link (Activity context, String token, String secret, SocializeAuthListener listener) {
		proxy.link(context, token, secret, listener);
	}
	
	/**
	 * Removes the association between a user and their Twitter account.
	 * This method executes synchronously.
	 * @param context The current activity.
	 */
	public static void unlink (Context context) {
		proxy.unlink(context);
	}

	/**
	 * Determines if the current user is linked to a Twitter account.
	 * @param context The current activity.
	 * @return True if the current user is already linked to a Twitter account.  False otherwise.
	 */
	public static boolean isLinked(Context context) {
		return proxy.isLinked(context);
	}

	/**
	 * Determines if Twitter has been configured correctly for the application.
	 * @param context The current activity.
	 * @return True if Twitter is configured correctly.  False otherwise.
	 */
	public static boolean isAvailable(Context context) {
		return proxy.isAvailable(context);
	}

	/**
	 * Sets the Twitter App credentials for the application.  This sets the same configuration properties as twitter.consumer.key and twitter.consumer.secret
	 * @param context The current context. 
	 * @param consumerKey The Twitter App Consumer Key
	 * @param consumerSecret The Twitter App Consumer Secret
	 */
	public static void setCredentials(Context context, String consumerKey, String consumerSecret) {
		proxy.setCredentials(context, consumerKey, consumerSecret);
	}

	/**
	 * Returns the user's Twitter access token.
	 * @param context The current context. 
	 * @return The user's Twitter access token or null if the user is not linked to a Twitter account.
	 */
	public static String getAccessToken(Context context) {
		return proxy.getAccessToken(context);
	}

	/**
	 * Returns the user's Twitter access token secret.
	 * @param context The current context. 
	 * @return The user's Twitter access token secret or null if the user is not linked to a Twitter account.
	 */
	public static String getTokenSecret(Context context) {
		return proxy.getTokenSecret(context);
	}
	
	/**
	 * "Tweets" an entity.  A "share" event will be recorded in Socialize and the entity URL generated by Socialize will be posted to the user's Twitter feed.
	 * If the user is not currently linked to a Twitter account they will be prompted to authenticate.
	 * @param context The current activity. 
	 * @param entity The entity to be tweeted.
	 * @param text The text to be posted along with the tweet.
	 * @param listener A listener to handle the result.
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
	 * Performs a simple HTTP POST to the Twitter resource endpoint specified. If the user is not currently linked to a Twitter account they will be prompted to authenticate.
	 * @param context The current Activity.
	 * @param resource The resource to be called.  NOTE: This should NOT include the full url (e.g. http://api.twitter.com) but just the resource.  
	 * Changing the full URL endpoint can be done via the SocialNetworkPostListener onBeforePost callback.
	 * @param postData The parameters to be posted.
	 * @param listener A listener to handle the result.
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
	
	/**
	 * Performs a simple HTTP GET to the Twitter resource endpoint specified. If the user is not currently linked to a Twitter account they will be prompted to authenticate.
	 * @param context The current Activity.
	 * @param resource The resource to be called.  NOTE: This should NOT include the full url (e.g. http://api.twitter.com) but just the resource.  
	 * Changing the full URL endpoint can be done via the SocialNetworkPostListener onBeforePost callback.
	 * @param params The parameters to be posted.
	 * @param listener A listener to handle the result.
	 */
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
	 * Performs a simple "tweet". If the user is not currently linked to a Twitter account they will be prompted to authenticate.
	 * @param context The current Activity.
	 * @param tweet The Tweet to be tweeted.
	 * @param listener A listener to handle the result.
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
	
	/**
	 * Publishes a photo to a user's Twitter feed.  If the user is not currently linked to a Twitter account they will be prompted to authenticate.
	 * @param context The current Activity.
	 * @param photo The image to be tweeted.
	 * @param listener A listener to handle the result.
	 */
	public static void tweetPhoto(final Activity context, final PhotoTweet photo, final SocialNetworkPostListener listener) {
		if(proxy.isLinked(context)) {
			proxy.tweetPhoto(context, photo, listener);
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
					proxy.tweetPhoto(context, photo, listener);
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
	 * Returns image data suitable for posting to twitter.
	 * @param context The current context.
	 * @param imagePath The path to the image.
	 * @return A byte array containing the bytes to be posted.
	 * @throws IOException
	 */
	public static byte[] getImageForPost(Activity context, Uri imagePath) throws IOException {
		return proxy.getImageForPost(context, imagePath);
	}
	
	
	/**
	 * Returns image data suitable for posting to twitter.
	 * @param context The current context.
	 * @param image The image to be compressed.
	 * @param format The compression format to use (one of JPEG or PNG).
	 * @return A byte array containing the bytes to be posted.
	 * @throws IOException
	 */
	public static byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException {
		return proxy.getImageForPost(context, image, format);
	}
}
