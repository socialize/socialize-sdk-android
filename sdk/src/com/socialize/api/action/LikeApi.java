/*
 * Copyright (c) 2011 SocializeService Inc.
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

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Like;
import com.socialize.listener.like.LikeListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class LikeApi extends SocializeApi<Like, SocializeProvider<Like>> {

	public static final String ENDPOINT = "/like/";
	
	public LikeApi(SocializeProvider<Like> provider) {
		super(provider);
	}

	public void addLike(SocializeSession session, String key, LikeListener listener) {
		Like c = new Like();
		c.setEntityKey(key);
		
		List<Like> list = new ArrayList<Like>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	public void getLikesByEntity(SocializeSession session, String key, LikeListener listener) {
		listAsync(session, ENDPOINT, key, null, listener);
	}

	public void getLikesById(SocializeSession session, LikeListener listener, int...ids) {
		String[] strIds = new String[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			strIds[i] = String.valueOf(ids[i]);
		}
		
		listAsync(session, ENDPOINT, null, strIds, listener);
	}
	
	public void getLike(SocializeSession session, int id, LikeListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}

}
