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
package com.socialize.api.action.like;

import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 *
 */
public interface LikeSystem {
	
	public static final String ENDPOINT = "/like/";

	public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks);
	
	public void deleteLike(SocializeSession session, long id, LikeListener listener);

	public void getLikesByEntity(SocializeSession session, String entityKey, LikeListener listener);

	public void getLikesByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, LikeListener listener);

	public void getLikesByApplication(SocializeSession session, int startIndex, int endIndex, LikeListener listener);

	public void getLike(SocializeSession session, String entityKey, LikeListener listener);

	public void getLikesById(SocializeSession session, LikeListener listener, long... ids);

	public void getLike(SocializeSession session, long id, LikeListener listener);
	
	public void getLikesByUser(SocializeSession session, long userId, LikeListener listener);
	
	public void getLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener);
}