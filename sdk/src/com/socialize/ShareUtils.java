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

import java.lang.reflect.Proxy;
import android.app.Activity;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.share.ShareUtilsProxy;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.share.ShareDialogListener;


/**
 * @author Jason Polites
 *
 */
public class ShareUtils {
	
	public static final int EMAIL = 1<<0;
	public static final int SMS = 1<<1;
	public static final int FACEBOOK = 1<<2;
	public static final int TWITTER = 1<<3;
	public static final int SHOW_REMEMBER = 1<<4;
	public static final int ALLOW_NONE = 1<<5;
	public static final int MORE_OPTIONS = 1<<6;
	public static final int COMMENT_AND_LIKE = FACEBOOK|TWITTER|ALLOW_NONE;
	public static final int SOCIAL = FACEBOOK|TWITTER;
	public static final int DEFAULT = EMAIL|SMS|FACEBOOK|TWITTER|MORE_OPTIONS;
	
	static ShareUtilsProxy proxy;
	
	static {
		proxy = (ShareUtilsProxy) Proxy.newProxyInstance(
				ShareUtilsProxy.class.getClassLoader(),
				new Class[]{ShareUtilsProxy.class},
				new SocializeActionProxy("shareUtils")); // Bean name
	}
	
	public static void showLinkDialog (Activity context, AuthDialogListener listener) {
		proxy.showLinkDialog(context, listener);
	}
	
	public static void showShareDialog (Activity context, Entity entity) {
		proxy.showShareDialog(context, entity, DEFAULT, null, null);
	};	
	
	public static void showShareDialog (Activity context, Entity entity, ShareDialogListener listener) {
		proxy.showShareDialog(context, entity, DEFAULT, null, listener);
	};	
	
	public static void showShareDialog (Activity context, Entity entity, SocialNetworkDialogListener listener, int options) {
		proxy.showShareDialog(context, entity, options, listener, listener);
	};
	
	public static void shareViaEmail(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaEmail(context, entity, listener);
	};
	
	public static void shareViaOther(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaOther(context, entity, listener);
	};
	
	public static void shareViaSMS(Activity context, Entity entity, ShareAddListener listener) {
		proxy.shareViaSMS(context, entity, listener);
	};
	
	public static void shareViaSocialNetworks(Activity context, Entity entity, String text, ActionOptions shareOptions, SocialNetworkShareListener listener) {
		proxy.shareViaSocialNetworks(context, entity, text, shareOptions, listener);
	}

	public static void getShare (Activity context, ShareGetListener listener, long id) {
		proxy.getShare(context, listener, id);
	};
	
	public static void getShares (Activity context, ShareListListener listener, long...ids) {
		proxy.getShares(context, listener, ids);
	};
	
	public static void getSharesByUser (Activity context, User user, int start, int end, ShareListListener listener) {
		proxy.getSharesByUser(context, user, start, end, listener);
	};
	
	public static void getSharesByEntity (Activity context, String entityKey, int start, int end, ShareListListener listener) {
		proxy.getSharesByEntity(context, entityKey, start, end, listener);
	};
	
	public static boolean canShareViaEmail(Activity context) {
		return proxy.canShareViaEmail(context);
	};
	
	public static boolean canShareViaSMS(Activity context) {
		return proxy.canShareViaSMS(context);
	};
}
