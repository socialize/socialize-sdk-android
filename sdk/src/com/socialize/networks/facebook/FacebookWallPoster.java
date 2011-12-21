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
package com.socialize.networks.facebook;

import android.app.Activity;

import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;

/**
 * @author Jason Polites
 *
 */
public interface FacebookWallPoster {

	public void postLike(final Activity parent, Entity entity, String comment, SocialNetworkListener listener);

	public void postComment(final Activity parent, Entity entity, String comment, SocialNetworkListener listener);	
	
	@Deprecated
	public void postLike(final Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener);

	@Deprecated
	public void postComment(final Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener);

	public void post(final Activity parent, String message, SocialNetworkListener listener);

	public void post(final Activity parent, String appId, String linkName, String message, String link, String caption, SocialNetworkListener listener);

}