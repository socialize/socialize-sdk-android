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

import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityCreateListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 */
public class SocializeServiceImpl implements SocializeSessionConsumer, SocializeService {
	
	public static final String DEFAULT_BEAN_CONFIG = "socialize_beans.xml";
	
	private SocializeApiHost service;
	private SocializeLogger logger;
	private IOCContainer container;
	private SocializeSession session;
	private int initCount = 0;
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context)
	 */
	@Override
	public void init(Context context) {
		init(context, DEFAULT_BEAN_CONFIG);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context, java.lang.String)
	 */
	@Override
	public void init(Context context, String...paths) {
		
		if(!isInitialized()) {
			try {
				SocializeIOC container = new SocializeIOC();
				ResourceLocator locator = new ResourceLocator();
				ClassLoaderProvider provider = new ClassLoaderProvider();
				
				locator.setClassLoaderProvider(provider);
				
				container.init(context, locator, paths);
				
				init(context, container); // initCount incremented here
			}
			catch (Exception e) {
				if(logger != null) {
					logger.error(SocializeLogger.INITIALIZE_FAILED, e);
				}
				else {
					e.printStackTrace();
				}
			}
		}
		else {
			this.initCount++;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context, com.socialize.android.ioc.IOCContainer)
	 */
	@Override
	public void init(Context context, final IOCContainer container) {
		if(!isInitialized()) {
			try {
				this.container = container;
				this.service = container.getBean("socializeApiHost");
				this.logger = container.getBean("logger");
				this.initCount++;
			}
			catch (Exception e) {
				if(logger != null) {
					logger.error(SocializeLogger.INITIALIZE_FAILED, e);
				}
				else {
					e.printStackTrace();
				}
			}
		}
		else {
			this.initCount++;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#destroy()
	 */
	@Override
	public void destroy() {
		initCount--;
		
		if(initCount <= 0) {
			if(container != null) {
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Destroying IOC container");
				}
				container.destroy();
			}
			
			initCount = 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener)  {
		if(assertInitialized(authListener)) {
			service.authenticate(consumerKey, consumerSecret, authListener, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, com.socialize.listener.comment.CommentAddListener)
	 */
	@Override
	public void addComment(String entity, String comment, CommentAddListener commentAddListener) {
		if(assertAuthenticated(commentAddListener)) {
			service.addComment(session, entity, comment, commentAddListener);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#addLike(java.lang.String, com.socialize.listener.like.LikeAddListener)
	 */
	@Override
	public void like(String entity, LikeAddListener likeAddListener) {
		if(assertAuthenticated(likeAddListener)) {
			service.addLike(session, entity, likeAddListener);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#deleteLike(int, com.socialize.listener.like.LikeDeleteListener)
	 */
	@Override
	public void unlike(int id, LikeDeleteListener likeDeleteListener) {
		if(assertAuthenticated(likeDeleteListener)) {
			service.deleteLike(session, id, likeDeleteListener);
		}
	}
	
	/**
	 * Lists all the likes associated with the given ids.
	 * @param likeListListener A listener to handle callbacks from the get.
	 * @param ids
	 */
	public void listLikesById(LikeListListener likeListListener, int...ids) {
		if(assertAuthenticated(likeListListener)) {
			service.listLikesById(session, likeListListener, ids);
		}
	}
	
	/**
	 * Retrieves a single like.
	 * @param id The ID of the like
	 * @param likeGetListener A listener to handle callbacks from the get.
	 */
	public void getLike(int id, LikeGetListener likeGetListener) {
		if(assertAuthenticated(likeGetListener)) {
			service.getLike(session, id, likeGetListener);
		}
	}
	
	/**
	 * Creates a new entity.
	 * @param key The [unique] key for the entity.
	 * @param name The name for the entity.
	 * @param entityCreateListener A listener to handle callbacks from the post.
	 */
	public void createEntity(String key, String name, EntityCreateListener entityCreateListener) {
		if(assertAuthenticated(entityCreateListener)) {
			service.createEntity(session, key, name, entityCreateListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getEntity(java.lang.String, com.socialize.listener.entity.EntityGetListener)
	 */
	@Override
	public void getEntity(String key, EntityGetListener listener) {
		if(assertAuthenticated(listener)) {
			service.getEntity(session, key, listener);
		}
	}
	
	/**
	 * Lists entities matching the given keys.
	 * @param entityListListener A listener to handle callbacks from the post.
	 * @param keys Array of keys corresponding to the entities to return, or null to return all.
	 */
	public void listEntitiesByKey(EntityListListener entityListListener, String...keys) {
		if(assertAuthenticated(entityListListener)) {
			service.listEntitiesByKey(session, entityListListener, keys);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listCommentsByEntity(java.lang.String, com.socialize.listener.comment.CommentListListener)
	 */
	@Override
	public void listCommentsByEntity(String entity, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsByEntity(session, entity, commentListListener);
		}
	}
	
	/**
	 * Lists the comments by comment ID.
	 * @param session The current socialize session.
	 * @param commentListListener A listener to handle callbacks from the post.
	 * @param ids Array of IDs corresponding to pre-existing comments.
	 */
	public void listCommentsById(CommentListListener commentListListener, int...ids) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsById(session, commentListListener, ids);
		}
	}
	
	/**
	 * Gets a single comment based on comment ID.
	 * @param session The current socialize session.
	 * @param id The ID of the comment.
	 * @param commentGetListener A listener to handle callbacks from the post.
	 */
	public void getComment(int id, CommentGetListener commentGetListener) {
		if(assertAuthenticated(commentGetListener)) {
			service.getComment(session, id, commentGetListener);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#isInitialized()
	 */
	@Override
	public boolean isInitialized() {
		return this.initCount > 0;
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated() {
		return session != null;
	}
	
	private boolean assertAuthenticated(SocializeListener listener) {
		if(assertInitialized(listener)) {
			if(session != null) {
				return true;
			}
			else {
				if(listener != null) {
					if(logger != null) {
						listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_AUTHENTICATED)));
					}
					else {
						listener.onError(new SocializeException("Not authenticated"));
					}
				}
				if(logger != null) logger.error(SocializeLogger.NOT_AUTHENTICATED);
			}
		}
		
		return false;
	}
	
	private boolean assertInitialized(SocializeListener listener) {
		if(!isInitialized()) {
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
		return isInitialized();
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#getSession()
	 */
	@Override
	public SocializeSession getSession() {
		return session;
	}

	public void setSession(SocializeSession session) {
		this.session = session;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	/**
	 * Returns the configuration for this SocializeService instance.
	 * @return
	 */
	public SocializeConfig getConfig() {
		if(isInitialized()) {
			return container.getBean("config");
		}
		
		if(logger != null) logger.error(SocializeLogger.NOT_INITIALIZED);
		return null;
	}
}
