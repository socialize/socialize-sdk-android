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
package com.socialize.api.action;

import android.app.Activity;
import android.location.Location;

import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.util.DelegateOnly;

/**
 * @author Jason Polites
 *
 */
public interface ShareSystem {

	public static final String ENDPOINT = "/share/";
	
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener);

	@DelegateOnly
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener);

	@DelegateOnly
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener);
	
	/**
	 * Shares the given entity to the given social network
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param comment The comment provided by the user.
	 * @param location The location of the user (may be null)
	 * @param destination The network on which to post the share.
	 * @param autoAuth If true authentication will be attempted automatically.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void shareEntity(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener);
	
	/**
	 * Shares a single comment to a given social network
	 * @param context
	 * @param entity
	 * @param comment
	 * @param location
	 * @param destination
	 * @param autoAuth If true authentication will be attempted automatically.
	 * @param listener
	 */
	public void shareComment(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener);
	
	/**
	 * Shares a like to a given social network.
	 * @param context
	 * @param entity
	 * @param location
	 * @param destination
	 * @param autoAuth If true authentication will be attempted automatically.
	 * @param listener
	 */
	public void shareLike(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener);
	
}