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
package com.socialize.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import com.socialize.SocializeServiceImpl;
import com.socialize.api.action.ActivitySystem;
import com.socialize.api.action.CommentSystem;
import com.socialize.api.action.EntitySystem;
import com.socialize.api.action.LikeSystem;
import com.socialize.api.action.ShareSystem;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SubscriptionSystem;
import com.socialize.api.action.UserSystem;
import com.socialize.api.action.ViewSystem;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.entity.Comment;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.notifications.WakeLock;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 *
 */
public class PublicSocialize extends SocializeServiceImpl {

	@Override
	public void handleIntent(Context context, Intent intent) {

		super.handleIntent(context, intent);
	}

	@Override
	public WakeLock getWakeLock() {

		return super.getWakeLock();
	}

	@Override
	public String[] getInitPaths() {

		return super.getInitPaths();
	}

	@Override
	public SocializeIOC newSocializeIOC() {

		return super.newSocializeIOC();
	}

	@Override
	public ResourceLocator newResourceLocator() {

		return super.newResourceLocator();
	}

	@Override
	public SocializeLogger newLogger() {

		return super.newLogger();
	}

	@Override
	public ClassLoaderProvider newClassLoaderProvider() {

		return super.newClassLoaderProvider();
	}

	@Override
	public int binarySearch(String[] array, String str) {

		return super.binarySearch(array, str);
	}

	@Override
	public void sort(Object[] array) {

		super.sort(array);
	}

	@Override
	public void verify3rdPartyAuthConfigured() {

		super.verify3rdPartyAuthConfigured();
	}

	@Override
	public SocializeAuthProviderInfo newSocializeAuthProviderInfo() {

		return super.newSocializeAuthProviderInfo();
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener authListener, boolean do3rdPartyAuth) {

		super.authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, do3rdPartyAuth);
	}

	@Override
	public boolean checkKeys(String consumerKey, String consumerSecret) {

		return super.checkKeys(consumerKey, consumerSecret);
	}

	@Override
	public boolean checkKeys(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {

		return super.checkKeys(consumerKey, consumerSecret, authListener);
	}

	@Override
	public boolean checkKey(String name, String key, SocializeAuthListener authListener) {

		return super.checkKey(name, key, authListener);
	}

	@Override
	public void logErrorMessage(String message) {

		super.logErrorMessage(message);
	}

	@Override
	public Comment newComment() {

		return super.newComment();
	}

	@Override
	public void handleActionShare(Activity activity, ShareType shareType, Share share, String shareText, Location location, boolean autoAuth, ShareAddListener shareAddListener) {

		super.handleActionShare(activity, shareType, share, shareText, location, autoAuth, shareAddListener);
	}

	@Override
	public void handleActionShare(Activity activity, SocialNetwork socialNetwork, SocializeAction action, String shareText, Location location, boolean autoAuth, SocialNetworkListener listener) {

		super.handleActionShare(activity, socialNetwork, action, shareText, location, autoAuth, listener);
	}

	@Override
	public boolean isAuthenticatedLegacy(AuthProviderType providerType) {

		return super.isAuthenticatedLegacy(providerType);
	}

	@Override
	public boolean assertAuthenticated(SocializeListener listener) {

		return super.assertAuthenticated(listener);
	}

	@Override
	public boolean assertInitialized(SocializeListener listener) {

		return super.assertInitialized(listener);
	}

	@Override
	public ActionBarView newActionBarView(Activity parent) {

		return super.newActionBarView(parent);
	}

	@Override
	public Intent newIntent(Activity context, Class<?> cls) {

		return super.newIntent(context, cls);
	}

	@Override
	public LayoutParams newLayoutParams(int width, int height) {

		return super.newLayoutParams(width, height);
	}

	@Override
	public LayoutParams newLayoutParams(android.view.ViewGroup.LayoutParams source) {

		return super.newLayoutParams(source);
	}

	@Override
	public RelativeLayout newRelativeLayout(Activity parent) {

		return super.newRelativeLayout(parent);
	}

	@Override
	public ScrollView newScrollView(Activity parent) {

		return super.newScrollView(parent);
	}

	@Override
	public View inflateView(Activity parent, int resId) {

		return super.inflateView(parent, resId);
	}

	@Override
	public void setCommentSystem(CommentSystem commentSystem) {

		super.setCommentSystem(commentSystem);
	}

	@Override
	public void setShareSystem(ShareSystem shareSystem) {

		super.setShareSystem(shareSystem);
	}

	@Override
	public void setLikeSystem(LikeSystem likeSystem) {

		super.setLikeSystem(likeSystem);
	}

	@Override
	public void setViewSystem(ViewSystem viewSystem) {

		super.setViewSystem(viewSystem);
	}

	@Override
	public void setUserSystem(UserSystem userSystem) {

		super.setUserSystem(userSystem);
	}

	@Override
	public void setAuthProviders(AuthProviders authProviders) {

		super.setAuthProviders(authProviders);
	}

	@Override
	public void setActivitySystem(ActivitySystem activitySystem) {

		super.setActivitySystem(activitySystem);
	}

	@Override
	public void setEntitySystem(EntitySystem entitySystem) {

		super.setEntitySystem(entitySystem);
	}

	@Override
	public void setSubscriptionSystem(SubscriptionSystem subscriptionSystem) {

		super.setSubscriptionSystem(subscriptionSystem);
	}

	@Override
	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {

		super.setAuthProviderInfoBuilder(authProviderInfoBuilder);
	}
	
}
