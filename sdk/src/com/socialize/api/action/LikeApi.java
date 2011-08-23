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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ActionError;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class LikeApi extends SocializeApi<Like, SocializeProvider<Like>> {

	public static final String ENDPOINT = "/like/";
	
	public LikeApi(Context context, SocializeProvider<Like> provider) {
		super(context, provider);
	}

	@Deprecated
	public LikeApi(SocializeProvider<Like> provider) {
		super(provider);
	}

	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		Like c = new Like();
		c.setEntityKey(key);
		
		setLocation(c, location);
		
		List<Like> list = new ArrayList<Like>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	public void deleteLike(SocializeSession session, int id, LikeListener listener) {
		deleteAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
	
	public void getLikesByEntity(SocializeSession session, String key, LikeListener listener) {
		listAsync(session, ENDPOINT, key, null, listener);
	}
	
	public void getLikesByEntity(SocializeSession session, String key, int startIndex, int endIndex, LikeListener listener) {
		listAsync(session, ENDPOINT, key, null, startIndex, endIndex, listener);
	}
	
	public void getLike(SocializeSession session, String key, final LikeListener listener) {
		getLikesByEntity(session, key, 0, 1, new LikeListListener() {
			
			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(error);
				}
			}
			
			@Override
			public void onList(ListResult<Like> entities) {
				
				if(listener != null) {
					if(entities != null && entities.getItems() != null && entities.getItems().size() > 0) {
						listener.onGet(entities.getItems().get(0));
					}
					else {
						List<ActionError> errors = entities.getErrors();
						
						if(errors != null && errors.size() > 0) {
							listener.onError(new SocializeException(errors.get(0).getMessage()));
						}
						else {
							listener.onError(new SocializeException("No like found"));
						}
					}
				}
			}
		});
	}

	public void getLikesById(SocializeSession session, LikeListener listener, int...ids) {
		
		if(ids != null) {
			String[] strIds = new String[ids.length];
			
			for (int i = 0; i < ids.length; i++) {
				strIds[i] = String.valueOf(ids[i]);
			}
			
			listAsync(session, ENDPOINT, null, strIds, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
		}
		else {
			if(listener != null) {
				listener.onError(new SocializeException("No ids supplied"));
			}
		}
	}
	
	public void getLike(SocializeSession session, int id, LikeListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}

}
