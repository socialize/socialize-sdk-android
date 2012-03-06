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
import android.location.Location;
import android.view.View;

import com.socialize.SocializeServiceImpl;
import com.socialize.api.action.ActivitySystem;
import com.socialize.api.action.CommentSystem;
import com.socialize.api.action.EntitySystem;
import com.socialize.api.action.LikeSystem;
import com.socialize.api.action.ShareSystem;
import com.socialize.api.action.UserSystem;
import com.socialize.api.action.ViewSystem;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 *
 */
public class PublicSocialize extends SocializeServiceImpl {

	
	@Deprecated
	@Override
	public void authenticate(Context context, String consumerKey,
			String consumerSecret, AuthProviderType authProviderType,
			String authProviderAppId, SocializeAuthListener authListener) {
		super.authenticate(context, consumerKey, consumerSecret, authProviderType,
				authProviderAppId, authListener);
	}
	@Override
	public void addComment(Activity activity, Entity entity, String comment,
			Location location, ShareOptions shareOptions,
			CommentAddListener commentAddListener) {
		super.addComment(activity, entity, comment, location, shareOptions,
				commentAddListener);
	}

	@Override
	public void addComment(Activity activity, Comment comment,
			Location location, ShareOptions shareOptions,
			CommentAddListener commentAddListener) {
		super.addComment(activity, comment, location, shareOptions, commentAddListener);
	}

	@Override
	public void addComment(Activity activity, Entity entity, String comment,
			ShareOptions shareOptions, CommentAddListener commentAddListener) {

		super.addComment(activity, entity, comment, shareOptions, commentAddListener);
	}

	@Override
	public void addComment(Activity activity, Entity entity, String comment,
			CommentAddListener commentAddListener) {

		super.addComment(activity, entity, comment, commentAddListener);
	}

	@Override
	public void authenticate(Context context, SocializeAuthListener authListener) {
		super.authenticate(context, authListener);
	}

	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret,
			SocializeAuthListener authListener) {
		super.authenticate(consumerKey, consumerSecret, authListener);
	}

	@Override
	public void authenticate(Context context, String consumerKey,
			String consumerSecret, SocializeAuthListener authListener) {
		super.authenticate(context, consumerKey, consumerSecret, authListener);
	}
	
	@Override
	public View showActionBar(Activity parent, int resId, Entity entity) {

		return super.showActionBar(parent, resId, entity);
	}


	@Override
	public View showActionBar(Activity parent, int resId, Entity entity,
			ActionBarListener listener) {

		return super.showActionBar(parent, resId, entity, listener);
	}

	
	@Override
	public View showActionBar(Activity parent, int resId, Entity entity,
			ActionBarOptions options) {

		return super.showActionBar(parent, resId, entity, options);
	}


	@Override
	public View showActionBar(Activity parent, int resId, Entity entity,
			ActionBarOptions options, ActionBarListener listener) {

		return super.showActionBar(parent, resId, entity, options, listener);
	}


	@Override
	public View showActionBar(Activity parent, View original, Entity entity) {

		return super.showActionBar(parent, original, entity);
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity,
			ActionBarOptions options) {

		return super.showActionBar(parent, original, entity, options);
	}

	
	@Override
	public View showActionBar(Activity parent, View original, Entity entity,
			ActionBarListener listener) {

		return super.showActionBar(parent, original, entity, listener);
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity,
			ActionBarOptions options, ActionBarListener listener) {

		return super.showActionBar(parent, original, entity, options, listener);
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
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener authListener, boolean do3rdPartyAuth) {
		super.authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, do3rdPartyAuth);
	}

	@Override
	public boolean checkKeys(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		return super.checkKeys(consumerKey, consumerSecret, authListener);
	}

	@Override
	public void logErrorMessage(String message) {
		super.logErrorMessage(message);
	}

	@Override
	protected boolean checkKey(String name, String key,
			SocializeAuthListener authListener) {
		return super.checkKey(name, key, authListener);
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
	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		super.setAuthProviderInfoBuilder(authProviderInfoBuilder);
	}
	
	
}
