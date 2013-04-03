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
package com.socialize.api.action.comment;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.listener.comment.CommentListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.provider.SocializeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Polites
 */
public class SocializeCommentSystem extends SocializeApi<Comment, SocializeProvider<Comment>> implements CommentSystem {
	
	public SocializeCommentSystem(SocializeProvider<Comment> provider) {
		super(provider);
	}
	
	@Override
	public void addComment(SocializeSession session, Comment comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks) {
		
		if(commentOptions != null) {
			comment.setNotificationsEnabled(commentOptions.isSubscribeToUpdates());
		}
		
		setPropagationData(comment, commentOptions, networks);
		setLocation(comment);
		
		List<Comment> list = new ArrayList<Comment>(1);
		list.add(comment);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	@Override
	public void deleteComment(SocializeSession session, long id, CommentListener listener) {
		deleteAsync(session, ENDPOINT, String.valueOf(id), listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#addComment(com.socialize.api.SocializeSession, com.socialize.entity.Entity, java.lang.String, android.location.Location, com.socialize.networks.ShareOptions, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void addComment(SocializeSession session, Entity entity, String comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks) {
		Comment c = new Comment();
		c.setText(comment);
		c.setEntitySafe(entity);
		addComment(session, c, commentOptions, listener, networks);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getCommentsByEntity(SocializeSession session, String key, CommentListener listener) {
		listAsync(session, ENDPOINT, key, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getCommentsByEntity(SocializeSession session, String key, int startIndex, int endIndex, CommentListener listener) {
		listAsync(session, ENDPOINT, key, null, null, startIndex, endIndex, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.comment.CommentSystem#getCommentsByApplication(com.socialize.api.SocializeSession, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getCommentsByApplication(SocializeSession session, int startIndex, int endIndex, CommentListener listener) {
		listAsync(session, ENDPOINT, null, null, null, startIndex, endIndex, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getCommentsByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getCommentsByUser(SocializeSession session, long userId, CommentListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getCommentsByUser(com.socialize.api.SocializeSession, long, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, startIndex, endIndex, listener);
	}	

	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getCommentsById(com.socialize.api.SocializeSession, com.socialize.listener.comment.CommentListener, int)
	 */
	@Override
	public void getCommentsById(SocializeSession session, CommentListener listener, long...ids) {
		String[] strIds = new String[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			strIds[i] = String.valueOf(ids[i]);
		}
		
		// No need for pagination here really
		listAsync(session, ENDPOINT, null, 0, SocializeConfig.MAX_LIST_RESULTS, listener, strIds);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.CommentSystem#getComment(com.socialize.api.SocializeSession, long, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}

}
