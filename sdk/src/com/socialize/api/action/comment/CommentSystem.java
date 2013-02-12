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

import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.listener.comment.CommentListener;
import com.socialize.networks.SocialNetwork;


/**
 * @author Jason Polites
 *
 */
public interface CommentSystem {
	
	public static final String ENDPOINT = "/comment/";
	
	public void deleteComment(SocializeSession session, long commentId, CommentListener listener);

	public void addComment(SocializeSession session, Comment comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks);

	public void addComment(SocializeSession session, Entity entity, String comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks);
	
	public void getCommentsByApplication(SocializeSession session, int startIndex, int endIndex, CommentListener listener);
	
	public void getCommentsByEntity(SocializeSession session, String entityKey, CommentListener listener);
	
	public void getCommentsByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, CommentListener listener);
	
	public void getCommentsByUser(SocializeSession session, long userId, CommentListener listener);
	
	public void getCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener);
	
	public void getCommentsById(SocializeSession session, CommentListener listener, long...ids);
	
	public void getComment(SocializeSession session, long id, CommentListener listener);

}