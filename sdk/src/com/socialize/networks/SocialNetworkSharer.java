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
package com.socialize.networks;

import android.app.Activity;

import com.socialize.entity.Entity;

/**
 * Shares entities to a social network.
 * @author Jason Polites
 *
 */
public interface SocialNetworkSharer {
	
	/**
	 * Shares the given entity to the given social network.
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param comment The comment provided by the user.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void shareEntity(Activity context, Entity entity, String comment, SocialNetworkListener listener);

	/**
	 * Shares a comment against the given entity to the given social network.
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param comment The comment provided by the user.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void shareComment(Activity context, Entity entity, String comment, SocialNetworkListener listener);
	
	/**
	 * Shares a like against the given entity to the given social network.
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param comment The comment provided by the user.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void shareLike(Activity context, Entity entity, String comment, SocialNetworkListener listener);
}
