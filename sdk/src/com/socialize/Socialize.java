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
package com.socialize;

import android.content.Context;

import com.socialize.android.ioc.AndroidIOC;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class Socialize {
	
	private SocializeService service;
	private SocializeLogger logger;
	private IOCContainer container;
	private boolean initialized = false;
	
	/**
	 * Initializes a Socialize instance with default settings.
	 * @param context The current Android context (or Activity)
	 */
	public void init(Context context)  {
		IOCContainer container = new AndroidIOC();
		init(context, container);
	}
	
	/**
	 * Initializes a socialize service with a custom object container (Expert use only)
	 * @param context The current Android context (or Activity)
	 * @param container A reference to an IOC container
	 * @see https://github.com/socialize/android-ioc
	 */
	public void init(Context context, IOCContainer container) {
		try {
			this.container = container;
			this.service = container.getBean("socializeService");
			this.logger = container.getBean("logger");
			this.initialized = true;
		}
		catch (Exception e) {
			logger.error(SocializeLogger.INITIALIZE_FAILED, e);
		}
	}
	
	/**
	 * Destroys the Socialize instance.  Should be called during the onDestroy() method of your Activity.
	 */
	public void destroy() {
		initialized = false;
		if(container != null) {
			container.destroy();
		}
	}
	
	/**
	 * Authenticates the application against the API
	 * @param consumerKey The consumer key, obtained from registration as a Socialize Developer.
	 * @param consumerSecret The consumer secret, obtained from registration as a Socialize Developer.
	 * @param authListener The callback for authentication outcomes.
	 * @throws SocializeException 
	 */
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener) throws SocializeException {
		service.authenticate(consumerKey, consumerSecret, authListener);
	}

	/**
	 * Adds a new comment and associates it with the entity described.
	 * @param session The current socialize session.
	 * @param entity The entity key.  Defined when first creating an entity, or created on the fly with this call.
	 * @param comment The comment to add.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(SocializeSession session, String entity, String comment, CommentAddListener commentAddListener) {
		if(assertInitialized(commentAddListener)) {
			service.addComment(session, entity, comment, commentAddListener);
		}
	}
	
	/**
	 * Lists the comments associated with an entity.
	 * @param session The current socialize session.
	 * @param entity The entity key.  Defined when first creating an entity, or created on the fly with this call.
	 * @param commentListListener A listener to handle callbacks from the post.
	 */
	public void listCommentsByEntity(SocializeSession session, String entity, CommentListListener commentListListener) {
		if(assertInitialized(commentListListener)) {
			service.listCommentsByEntity(session, entity, commentListListener);
		}
	}
	
	/**
	 * Lists the comments by comment ID.
	 * @param session The current socialize session.
	 * @param commentListListener A listener to handle callbacks from the post.
	 * @param ids Array of IDs corresponding to pre-existing comments.
	 */
	public void listCommentsById(SocializeSession session, CommentListListener commentListListener, int...ids) {
		if(assertInitialized(commentListListener)) {
			service.listCommentsById(session, commentListListener, ids);
		}
	}
	
	/**
	 * Gets a single comment based on comment ID.
	 * @param session The current socialize session.
	 * @param id The ID of the comment.
	 * @param commentGetListener A listener to handle callbacks from the post.
	 */
	public void getComment(SocializeSession session, int id, CommentGetListener commentGetListener) {
		if(assertInitialized(commentGetListener)) {
			service.getComment(session, id, commentGetListener);
		}
	}
	
	/**
	 * Returns true if this Socialize instance has been initialized.
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	private boolean assertInitialized(SocializeListener listener) {
		if(!initialized) {
			if(listener != null) {
				if(logger != null) {
					listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_INITIALIZED)));
				}
				else {
					listener.onError(new SocializeException("Not initialized"));
				}
			}
			if(logger != null) logger.error(SocializeLogger.NOT_INITIALIZED);
		}
		return initialized;
	}
	
}
