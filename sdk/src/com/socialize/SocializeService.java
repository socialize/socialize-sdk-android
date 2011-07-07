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
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;

/**
 * @author Jason Polites
 *
 */
public interface SocializeService {

	/**
	 * Initializes a SocializeService instance with default settings.
	 * @param context The current Android context (or Activity)
	 * @param context
	 */
	public void init(Context context);

	/**
	 * Initializes a SocializeService instance with default settings.
	 * @param context
	 * @param paths List of paths to config files.  Beans in paths to the right overwrite beans in paths to the left.
	 */
	public void init(Context context, String... paths);

	/**
	 * Initializes a socialize service with a custom object container (Expert use only)
	 * @param context The current Android context (or Activity)
	 * @param container A reference to an IOC container
	 * @see https://github.com/socialize/android-ioc
	 */
	public void init(Context context, final IOCContainer container);

	/**
	 * Destroys the SocializeService instance.  Should be called during the onDestroy() method of your Activity.
	 */
	public void destroy();

	/**
	 * Authenticates the application against the API
	 * @param consumerKey The consumer key, obtained from registration as a SocializeService Developer.
	 * @param consumerSecret The consumer secret, obtained from registration as a SocializeService Developer.
	 * @param authListener The callback for authentication outcomes.
	 * @throws SocializeException 
	 */
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener);

	/**
	 * Adds a new like and associates it with the entity described.
	 * @param entity The entity key.  Defined when first creating an entity, or created on the fly with this call.
	 * @param likeAddListener A listener to handle callbacks from the post.
	 */
	public void like(String entity, LikeAddListener likeAddListener);

	/**
	 * Removes a specific LIKE based on it's unique ID.  The ID would be returned from the original creation call.
	 * @param id
	 * @param likeDeleteListener
	 */
	public void unlike(int id, LikeDeleteListener likeDeleteListener);

	/**
	 * Returns true if this SocializeService instance has been initialized.
	 * @return
	 */
	public boolean isInitialized();

	/**
	 * Returns true if the current session is authenticated.
	 * @return
	 */
	public boolean isAuthenticated();

	
	/**
	 * Returns a reference to the current session.
	 * @return
	 */
	public SocializeSession getSession();

	/**
	 * Returns a reference to the config being used.
	 * <br/>
	 * This can be modified BEFORE calling init() if alternate config is required.
	 * @return
	 */
	public SocializeConfig getConfig();

	/**
	 * Lists the comments associated with an entity.
	 * @param session The current socialize session.
	 * @param entity The entity key.  Defined when first creating an entity, or created on the fly with this call.
	 * @param commentListListener A listener to handle callbacks from the post.
	 */
	public void listCommentsByEntity(String key, CommentListListener commentListListener);

	/**
	 * Retrieves a single entity
	 * @param key
	 * @param listener
	 */
	public void getEntity(String key, EntityGetListener entityGetListener);

	/**
	 * Adds a new comment and associates it with the entity described.
	 * @param entity The entity key.  Defined when first creating an entity, or created on the fly with this call.
	 * @param comment The comment to add.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(String key, String name, CommentAddListener commentAddListener);

}
