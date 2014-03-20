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
package com.socialize.api.action.share;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public interface ShareSystem {

	public static final String ENDPOINT = "/share/";
	
	public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener);
	
	public void addShare(Context context, SocializeSession session, Entity entity, String text, SocialNetwork network, Location location, ShareListener listener);
	
	public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, ShareListener listener, SocialNetwork...network);
	
	public void addShare(Context context, SocializeSession session, Entity entity, ShareType shareType, ShareListener listener, SocialNetwork...network);
	
	public void getSharesByApplication(SocializeSession session, int startIndex, int endIndex, ShareListener listener);
	
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener);
	
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener);
	
	public void getSharesByUser(SocializeSession session, long userId, int startIndex, int endIndex, ShareListener listener);
	
	public void getSharesById(SocializeSession session, ShareListener listener, long... ids);
	
	public void getShare(SocializeSession session, long id, ShareListener listener);
	
	/**
	 * Returns true if sharing to the given share type is supported on this device.
	 */
	public boolean canShare(Context context, ShareType shareType);
	
	/**
	 * Shares the given action to the given social network.
	 * @param context The current context.
	 * @param session The current user session.
	 * @param comment The comment provided by the user.
	 * @param location The location of the user (may be null).
	 * @param destination The network on which to post the share.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void share(Activity context, SocializeSession session, SocializeAction action, String comment, Location location, ShareType destination, SocialNetworkListener listener);
}