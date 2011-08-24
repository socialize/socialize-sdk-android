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

import android.location.Location;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.listener.comment.CommentListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class CommentApi extends SocializeApi<Comment, SocializeProvider<Comment>> {

	public static final String ENDPOINT = "/comment/";
	
	public CommentApi(SocializeProvider<Comment> provider) {
		super(provider);
	}

	public void addComment(SocializeSession session, String key, String comment, Location location, CommentListener listener) {
		Comment c = new Comment();
		c.setText(comment);
		c.setEntityKey(key);
		
		setLocation(c, location);
		
		List<Comment> list = new ArrayList<Comment>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	public void getCommentsByEntity(SocializeSession session, String key, CommentListener listener) {
		listAsync(session, ENDPOINT, key, null, listener);
	}
	
	public void getCommentsByEntity(SocializeSession session, String key, int startIndex, int endIndex, CommentListener listener) {
		listAsync(session, ENDPOINT, key, null, startIndex, endIndex, listener);
	}

	public void getCommentsById(SocializeSession session, CommentListener listener, int...ids) {
		String[] strIds = new String[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			strIds[i] = String.valueOf(ids[i]);
		}
		
		// No need for pagination here really
		listAsync(session, ENDPOINT, null, strIds, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}
	
	public void getComment(SocializeSession session, int id, CommentListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}

}
