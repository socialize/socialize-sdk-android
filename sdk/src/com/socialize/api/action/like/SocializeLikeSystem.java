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

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.provider.SocializeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Polites
 */
public class SocializeLikeSystem extends SocializeApi<Like, SocializeProvider<Like>> implements LikeSystem {
	
	public SocializeLikeSystem(SocializeProvider<Like> provider) {
		super(provider);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#addLike(com.socialize.api.SocializeSession, com.socialize.entity.Entity, android.location.Location, com.socialize.networks.ShareOptions, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
		Like c = new Like();
		c.setEntitySafe(entity);
		
		setPropagationData(c, shareOptions, networks);
		setLocation(c);
		
		List<Like> list = new ArrayList<Like>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}	
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#deleteLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		deleteAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#getLikesByEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLikesByEntity(SocializeSession session, String key, LikeListener listener) {
		listAsync(session, ENDPOINT, key, listener);
	}	
	
	public void getLikesByUser(SocializeSession session, long userId, LikeListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, listener);
	}
	
	public void getLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#getLikesByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLikesByEntity(SocializeSession session, String key, int startIndex, int endIndex, LikeListener listener) {
		listAsync(session, ENDPOINT, key, null, null, startIndex, endIndex, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.like.LikeSystem#getLikesByApplication(com.socialize.api.SocializeSession, int, int, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLikesByApplication(SocializeSession session, int startIndex, int endIndex, LikeListener listener) {
		listAsync(session, ENDPOINT, null, null, null, startIndex, endIndex, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#getLike(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, final String entityUrl, final LikeListener listener) {
		final User user = session.getUser();
		if(user != null) {
			
			final Long userId = user.getId();
			
			String endpoint = "/user/" + userId.toString() + ENDPOINT;
			listAsync(session, endpoint, entityUrl, null, null, 0, 1, new LikeListListener() {
				
				@Override
				public void onList(List<Like> items, int totalSize) {
	
					boolean is404 = false;
					if(items != null) {
						if(items != null && items.size() > 0) {
							Like like = items.get(0);
							if(like != null) {
								listener.onGet(like);
							}
							else {
								is404 = true;
							}
						}
						else {
							is404 = true;
						}
					}
					else {
						is404 = true;
					}
					
					if(is404) {
						onError(new SocializeApiError(404, "No likes found for entity with key [" +
								entityUrl +
								"] for user [" +
								userId +
								"]"));
					}
				}
				@Override
				public void onError(SocializeException error) {
					listener.onError(error);
				}
			});
		}
		else {
			if(listener != null) {
				listener.onError(new SocializeException("No user found in current session"));
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#getLikesById(com.socialize.api.SocializeSession, com.socialize.listener.like.LikeListener, int)
	 */
	@Override
	public void getLikesById(SocializeSession session, LikeListener listener, long...ids) {
		
		if(ids != null) {
			String[] strIds = new String[ids.length];
			
			for (int i = 0; i < ids.length; i++) {
				strIds[i] = String.valueOf(ids[i]);
			}
			
			listAsync(session, ENDPOINT, null, 0, SocializeConfig.MAX_LIST_RESULTS, listener, strIds);
		}
		else {
			if(listener != null) {
				listener.onError(new SocializeException("No ids supplied"));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.LikeSystem#getLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
}
	
	
