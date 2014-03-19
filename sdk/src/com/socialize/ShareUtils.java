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

import android.app.Activity;
import android.content.Context;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.ShareUtilsProxy;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthDialogListener;

import java.lang.reflect.Proxy;


/**
 * Used to perform all default sharing operations like Facebook, Twitter, Email and SMS sharing.
 * @author Jason Polites
 */
public class ShareUtils {
	
	/**
	 * Used to instruct the dialog to display the Email option.
	 */
	public static final int EMAIL = 1;
	
	/**
	 * Used to instruct the dialog to display the SMS option.
	 */
	public static final int SMS = 1<<1;
	
	/**
	 * Used to instruct the dialog to display the Facebook option.
	 */
	public static final int FACEBOOK = 1<<2;
	
	/**
	 * Used to instruct the dialog to display the Twitter option. 
	 */
	public static final int TWITTER = 1<<3;
	
	/**
	 * Used to instruct the dialog to display the "remember these networks" checkbox.
	 */
	public static final int SHOW_REMEMBER = 1<<4;
	
	/**
	 * Used to instruct the dialog to display the "more options" link.
	 */
	public static final int MORE_OPTIONS = 1<<5;
	
	/**
	 * Used to instruct the dialog to display the Google Plus option. 
	 */
	public static final int GOOGLE_PLUS = 1<<6;
	
	/**
	 * Used to indicate that the continue button should always be enabled.
	 */
	public static final int ALWAYS_CONTINUE = 1<<7;
	
	/**
	 * Displays only Social Network options.
	 */
	public static final int SOCIAL = FACEBOOK|TWITTER|GOOGLE_PLUS;
	
	/**
	 * The default display settings for the share dialog.
	 */
	public static final int DEFAULT = EMAIL|SMS|FACEBOOK|TWITTER|GOOGLE_PLUS|MORE_OPTIONS;

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_TEXT = "text";
	public static final String EXTRA_SUBJECT = "subject";

	static ShareUtilsProxy proxy;
	
	static {
		proxy = (ShareUtilsProxy) Proxy.newProxyInstance(
				ShareUtilsProxy.class.getClassLoader(),
				new Class[]{ShareUtilsProxy.class},
				new SocializeActionProxy("shareUtils")); // Bean name
	}
	
	/**
	 * Returns the default sharing options for the user.
	 * @param context The current context.
	 * @return The default sharing options for the user.
	 */
	public static ShareOptions getUserShareOptions(Context context) {
		return proxy.getUserShareOptions(context);
	}	
	
	/**
	 * Displays the link dialog to allow the user to link to their Twitter or Facebook account.
	 * @param context The current context.
	 * @param listener A listener to handle events from the link.
	 */
	public static void showLinkDialog (Activity context, AuthDialogListener listener) {
		proxy.showLinkDialog(context, listener);
	}
	
	/**
	 * Pre-loads the share dialog in the background to improve load speed and end user responsiveness.
	 * @param context The current context.
	 */
	public static void preloadShareDialog (Activity context) {
		proxy.preloadShareDialog(context);
	}

	/**
	 * Pre-loads the link dialog in the background to improve load speed and end user responsiveness.
	 * @param context The current context.
	 */
	public static void preloadLinkDialog (Activity context) {
		proxy.preloadLinkDialog(context);
	}

	/**
	 * Displays the default share dialog.  In most cases this is the simplest version to use.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 */
	public static void showShareDialog (Activity context, Entity entity) {
		proxy.showShareDialog(context, entity, DEFAULT, null, null);
	}
	
	/**
	 * Displays the default share dialog and allows for the handling of share dialog events.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events on the dialog.
	 */
	public static void showShareDialog (Activity context, Entity entity, SocialNetworkDialogListener listener) {
		proxy.showShareDialog(context, entity, DEFAULT, listener, listener);
	}

	/**
	 * Displays the default share dialog and allows for the handling of share events.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events.
	 * @param options Display options for the dialog.  These are bit flags taken from ShareUtils.
	 * @see ShareUtils#DEFAULT
	 * @see ShareUtils#SOCIAL
	 */
	public static void showShareDialog (Activity context, Entity entity, SocialNetworkDialogListener listener, int options) {
		proxy.showShareDialog(context, entity, options, listener, listener);
	}

	/**
	 * Shares the given entity via email.  This method with launch the default email application on the device.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events.
	 */
	public static void shareViaEmail(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaEmail(context, entity, listener);
	}


	/**
	 * Shares the given entity via Google+.  This method with launch the Google+ application on the device.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events.
	 */
	public static void shareViaGooglePlus(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaGooglePlus(context, entity, listener);
	}

	/**
	 * Shares the given entity via a user selected medium.  This method with launch the default application share dialog on the device.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events.
	 */	
	public static void shareViaOther(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaOther(context, entity, listener);
	}

	/**
	 * Shares the given entity via SMS.  This method with launch the default SMS application on the device.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param listener A listener to handle events.
	 */	
	public static void shareViaSMS(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaSMS(context, entity, listener);
	}

	/**
	 * Shares the given entity via a Social Network such as Twitter or Facebook.  This will prompt the user to select a network.
	 * @param context The current context.
	 * @param entity The entity being shared.
	 * @param shareOptions Options for the share.  If text is available for the share it can be specified here.
	 * @param listener A listener to handle events.
	 */	
	public static void shareViaSocialNetworks(Activity context, Entity entity, ShareOptions shareOptions, SocialNetworkShareListener listener, SocialNetwork...networks) {
		proxy.shareViaSocialNetworks(context, entity, shareOptions, listener, networks);
	}

	/**
	 * Retrieves a single share event based on ID.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 * @param id The ID of the share.
	 */
	public static void getShare (Activity context, ShareGetListener listener, long id) {
		proxy.getShare(context, listener, id);
	}

	/**
	 * Retrieves multiple share events based on ID.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 * @param ids One or more IDs for the shares returned.
	 */
	public static void getShares (Activity context, ShareListListener listener, long...ids) {
		proxy.getShares(context, listener, ids);
	}

	/**
	 * Retrieves all share events performed by the given user.
	 * @param context The current context.
	 * @param user The user who performed the share(s).
	 * @param start The start index for the result set (0 indexed).
	 * @param end The end index for the result set.
	 * @param listener A listener to handle the result.
	 */
	public static void getSharesByUser (Activity context, User user, int start, int end, ShareListListener listener) {
		proxy.getSharesByUser(context, user, start, end, listener);
	}

	/**
	 * Retrieves all share events performed on the given entity.
	 * @param context The current context.
	 * @param entityKey The key of the entity that was shared.
	 * @param start The start index for the result set (0 indexed).
	 * @param end The end index for the result set.
	 * @param listener A listener to handle the result.
	 */
	public static void getSharesByEntity (Activity context, String entityKey, int start, int end, ShareListListener listener) {
		proxy.getSharesByEntity(context, entityKey, start, end, listener);
	}
	
	/**
	 * Retrieves all share events across all entities.
	 * @param context The current context.
	 * @param start The start index for the result set (0 indexed).
	 * @param end The end index for the result set.
	 * @param listener A listener to handle the result.
	 */
	public static void getSharesByApplication (Activity context, int start, int end, ShareListListener listener) {
		proxy.getSharesByApplication(context, start, end, listener);
	}

	/**
	 * Creates a simple Socialize Share object.  
	 * NOTE: This does NOT perform any actual sharing (propagation).  It is used to simply record the fact that a share is taking place.
	 * This is useful when you want to utilize the auto-generated entity links returned in the propagation info response to post directly 
	 * to Facebook or Twitter.
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param shareOptions Options for the share allowing text to be specified.
	 * @param listener A listener to handle the result.
	 * @param networks The Social Networks you intend to share to.
	 */
	public static void registerShare(Activity context, Entity entity, ShareOptions shareOptions, ShareAddListener listener, SocialNetwork...networks)  {
		proxy.registerShare(context, entity, shareOptions, listener, networks);
	}
	
	/**
	 * Determines if the current device is capable of sharing via email.
	 * @param context The current context.
	 * @return True if the device is capable of sharing via email, false otherwise.
	 */
	public static boolean canShareViaEmail(Activity context) {
		return proxy.canShareViaEmail(context);
	}

	/**
	 * Determines if the current device is capable of sharing via SMS.
	 * @param context The current context.
	 * @return True if the device is capable of sharing via SMS, false otherwise.
	 */	
	public static boolean canShareViaSMS(Activity context) {
		return proxy.canShareViaSMS(context);
	}
}
