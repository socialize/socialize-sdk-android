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

import android.content.Context;

import com.socialize.SocializeServiceImpl;
import com.socialize.auth.AuthProviderData;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookWallPoster;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 *
 */
public class PublicSocialize extends SocializeServiceImpl {

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
	public boolean assertAuthenticated(SocializeListener listener) {
		return super.assertAuthenticated(listener);
	}

	@Override
	public boolean assertInitialized(SocializeListener listener) {
		return super.assertInitialized(listener);
	}

	@Override
	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		super.setFacebookWallPoster(facebookWallPoster);
	}

}
