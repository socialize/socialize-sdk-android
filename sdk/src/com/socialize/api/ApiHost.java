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
package com.socialize.api;

import android.content.Context;
import android.location.Location;

import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.UserActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.ui.comment.CommentShareOptions;

/**
 * @author Jason Polites
 * 
 */
public interface ApiHost {

	public void clearSessionCache(AuthProviderType authProviderType);
	
	public void clearSessionCache();

	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer);

	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth);

	public void createEntity(SocializeSession session, String key, String name, EntityListener listener);

	public void addComment(SocializeSession session, String key, String comment, Location location, CommentShareOptions shareOptions, CommentListener listener);

	public void getComment(SocializeSession session, long id, CommentListener listener);

	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String... keys);

	public void getEntity(SocializeSession session, String key, EntityListener listener);

	/**
	 * EXPERIMENTAL
	 * @param session
	 * @param listener
	 * @param id
	 */
	public void listRecommendedEntitiesByLike(SocializeSession session, EntityListener listener, long id);

	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener);

	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener);

	public void listCommentsById(SocializeSession session, CommentListener listener, int... ids);
	
	public void listCommentsByUser(SocializeSession session, long userId, CommentListener listener);
	
	public void listCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener);
	
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener);

	public void addView(SocializeSession session, String key, Location location, ViewListener listener);

	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener);

	public void deleteLike(SocializeSession session, long id, LikeListener listener);

	public void listLikesById(SocializeSession session, LikeListener listener, int... ids);

	public void getLike(SocializeSession session, long id, LikeListener listener);

	public void getLike(SocializeSession session, String key, LikeListener listener);

	public void getUser(SocializeSession session, long id, UserListener listener);

	public void saveUserProfile(Context context, SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener);

	public void listActivityByUser(SocializeSession session, long id, UserActivityListener listener);

	public void listActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener);
	
	public void listLikesByUser(SocializeSession session, long userId, LikeListener listener);
	
	public void listLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener);

	public void destroy();
}