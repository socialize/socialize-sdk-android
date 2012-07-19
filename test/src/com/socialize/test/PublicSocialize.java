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
package com.socialize.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.SocializeServiceImpl;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.entity.Comment;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeListener;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.WakeLock;
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
	public synchronized void initNotifications(Context context) {
		super.initNotifications(context);
	}

	@Override
	public synchronized void initEntityLoader() {
		super.initEntityLoader();
	}

	@Override
	public void logError(String message, Throwable error) {
		super.logError(message, error);
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
	public boolean assertAuthenticated(SocializeListener listener) {
		return super.assertAuthenticated(listener);
	}

	@Override
	public boolean assertInitialized(Context context, SocializeListener listener) {
		return super.assertInitialized(context, listener);
	}

	@Override
	public Intent newIntent(Activity context, Class<?> cls) {
		return super.newIntent(context, cls);
	}

	@Override
	public void setShareSystem(ShareSystem shareSystem) {

		super.setShareSystem(shareSystem);
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
	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {

		super.setAuthProviderInfoBuilder(authProviderInfoBuilder);
	}
	
}
