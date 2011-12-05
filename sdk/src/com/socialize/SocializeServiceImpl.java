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

import java.util.Arrays;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.init.SocializeInitializationAsserter;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.activity.UserActivityListListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.comment.CommentShareOptions;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeServiceImpl implements SocializeSessionConsumer, SocializeService {
	
	private SocializeApiHost service;
	private SocializeLogger logger;
	private IOCContainer container;
	private SocializeSession session;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private SocializeInitializationAsserter asserter;
	private int initCount = 0;
	
	private String[] initPaths = null;
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context)
	 */
	@Override
	public void init(Context context) {
		init(context, SocializeConfig.SOCIALIZE_BEANS_PATH);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context, java.lang.String)
	 */
	@Override
	public void init(Context context, String...paths) {
		try {
			initWithContainer(context, paths);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#initAsync(android.content.Context, com.socialize.listener.SocializeInitListener)
	 */
	@Override
	public void initAsync(Context context, SocializeInitListener listener) {
		initAsync(context, listener, SocializeConfig.SOCIALIZE_BEANS_PATH);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#initAsync(android.content.Context, com.socialize.listener.SocializeInitListener, java.lang.String[])
	 */
	@Override
	public void initAsync(Context context, SocializeInitListener listener, String... paths) {
		new InitTask(this, context, paths, listener, logger).execute((Void)null);
	}

	public synchronized IOCContainer initWithContainer(Context context, String...paths) throws Exception {
		boolean init = false;

		if(isInitialized()) {
			for (String path : paths) {
				if(binarySearch(initPaths, path) < 0) {
					
					if(logger != null) {
						logger.info("New path found for beans [" +
								path +
								"].  Re-initializing Socialize");
					}
					
					this.initCount = 0;
					
					// Destroy the container so we don't double up on caches etc.
					destroy();
					
					init = true;
					
					break;
				}
			}
		}
		else {
			init = true;
		}
		
		if(init) {
			try {
				initPaths = paths;
				
				sort(initPaths);
				
				SocializeIOC container = newSocializeIOC();
				ResourceLocator locator = newResourceLocator();
				
				locator.setLogger(newLogger());
				
				ClassLoaderProvider provider = newClassLoaderProvider();
				
				locator.setClassLoaderProvider(provider);
				
				container.init(context, locator, paths);
				
				init(context, container); // initCount incremented here
			}
			catch (Exception e) {
				throw e;
			}
		}
		else {
			this.initCount++;
		}
		
		// Always set the context on the container
		if(container != null) {
			container.setContext(context);
		}
		
		return container;
	}

	// So we can mock
	protected SocializeIOC newSocializeIOC() {
		return new SocializeIOC();
	}
	
	// So we can mock
	protected ResourceLocator newResourceLocator() {
		return new ResourceLocator();
	}
	
	// So we can mock
	protected SocializeLogger newLogger() {
		return new SocializeLogger();
	}
	
	// So we can mock
	protected ClassLoaderProvider newClassLoaderProvider() {
		return new ClassLoaderProvider();
	}
	
	// So we can mock
	protected int binarySearch(String[] array, String str) {
		return Arrays.binarySearch(array, str);
	}
	
	// So we can mock
	protected void sort(Object[] array) {
		Arrays.sort(array);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context, com.socialize.android.ioc.IOCContainer)
	 */
	@Override
	public synchronized void init(Context context, final IOCContainer container) {
		if(!isInitialized()) {
			try {
				this.container = container;
				this.service = container.getBean("socializeApiHost");
				this.logger = container.getBean("logger");
				this.authProviderDataFactory = container.getBean("authProviderDataFactory");
				this.asserter = container.getBean("initializationAsserter");
				this.initCount++;
				
				ActivityIOCProvider.getInstance().setContainer(container);
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
	
	@Override
	public void clear3rdPartySession(Context context, AuthProviderType type) {
		// TODO: implement for specific/multiple provider types.
		try {
			if(session != null) {
				AuthProvider authProvider = session.getAuthProvider();
				String get3rdPartyAppId = session.get3rdPartyAppId();
				if(authProvider != null && !StringUtils.isEmpty(get3rdPartyAppId)) {
					authProvider.clearCache(context, get3rdPartyAppId);
				}
				
				session.clear(type);
			}
		}
		finally {
			if(service != null) {
				service.clearSessionCache(type);
			}
		}
	}

	@Override
	public void clearSessionCache(Context context) {
		try {
			if(session != null) {
				AuthProvider authProvider = session.getAuthProvider();
				String get3rdPartyAppId = session.get3rdPartyAppId();
				
				if(authProvider != null && !StringUtils.isEmpty(get3rdPartyAppId)) {
					authProvider.clearCache(context, get3rdPartyAppId);
				}
				
				session = null;
			}
		}
		finally {
			if(service != null) {
				service.clearSessionCache();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#destroy()
	 */
	@Override
	public void destroy() {
		initCount--;
		
		if(initCount <= 0) {
			destroy(true);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#destroy(boolean)
	 */
	@Override
	public void destroy(boolean force) {
		if(force) {
			if(container != null) {
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Destroying IOC container");
				}
				container.destroy();
			}
			
			initCount = 0;
			initPaths = null;
		}
		else {
			destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(java.lang.String, java.lang.String, com.socialize.provider.AuthProvider, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderType(authProviderType);
		data.setAppId3rdParty(authProviderAppId);
		authenticate(consumerKey, consumerSecret, data, authListener, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(SocializeAuthListener authListener) {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		
		if(checkKeys(consumerKey, consumerSecret, authListener)) {
			authenticate(consumerKey, consumerSecret, authListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(com.socialize.auth.AuthProviderType, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(AuthProviderType authProviderType, SocializeAuthListener authListener) {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		
		if(checkKeys(consumerKey, consumerSecret, authListener)) {
			// TODO: Add aother auth providers
			switch (authProviderType) {
			
			case FACEBOOK:
					String authProviderAppId = config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
					
					if(StringUtils.isEmpty(authProviderAppId)) {
						String msg = "No facebook app id specified";
						if(authListener != null) {
							authListener.onError(new SocializeException(msg));
						}
						
						if(logger != null) {
							logger.error(msg);
						}
						else {
							System.err.println(msg);
						}
					}
					else {
						authenticate(consumerKey, consumerSecret, authProviderType, authProviderAppId, authListener);
					}
					break;

			default:
				authenticate(consumerKey, consumerSecret, authListener);
				break;
			}
		}
	}

	protected boolean checkKeys(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		if(StringUtils.isEmpty(consumerKey)) {
			String msg = "No consumer key specified";
			if(authListener != null) {
				authListener.onError(new SocializeException(msg));
			}
			
			if(logger != null) {
				logger.error(msg);
			}
			else {
				System.err.println(msg);
			}
			
			return false;
		}
		
		if(StringUtils.isEmpty(consumerSecret)) {
			String msg = "No consumer secret specified";
			if(authListener != null) {
				authListener.onError(new SocializeException(msg));
			}
			
			if(logger != null) {
				logger.error(msg);
			}
			else {
				System.err.println(msg);
			}
			
			return false;
		}	
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener)  {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(consumerKey, consumerSecret, data, authListener, false);
	}
	
	@Override
	public void authenticateKnownUser(String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderId, String authUserId3rdParty, String authToken3rdParty,
			SocializeAuthListener authListener) {
		
		AuthProviderData authProviderData = this.authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(authProvider);
		authProviderData.setAppId3rdParty(authProviderId);
		authProviderData.setToken3rdParty(authToken3rdParty);
		authProviderData.setUserId3rdParty(authUserId3rdParty);
		
		authenticate(consumerKey, consumerSecret, authProviderData, authListener, false);
	}

	private void authenticate(
			String consumerKey, 
			String consumerSecret, 
			AuthProviderData authProviderData,
			SocializeAuthListener authListener, 
			boolean do3rdPartyAuth) {
		
		if(assertInitialized(authListener)) {
			service.authenticate(consumerKey, consumerSecret, authProviderData, authListener, this, do3rdPartyAuth);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, android.location.Location, com.socialize.listener.comment.CommentAddListener)
	 */
	@Override
	public void addComment(String url, String comment, Location location, CommentShareOptions shareOptions, CommentAddListener commentAddListener) {
		if(assertAuthenticated(commentAddListener)) {
			service.addComment(session, url, comment, location, shareOptions, commentAddListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, boolean, com.socialize.listener.comment.CommentAddListener)
	 */
	@Override
	public void addComment(String url, String comment, CommentShareOptions shareOptions, CommentAddListener commentAddListener) {
		addComment(url, comment, null, shareOptions, commentAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, com.socialize.listener.comment.CommentAddListener)
	 */
	@Override
	public void addComment(String url, String comment, CommentAddListener commentAddListener) {
		addComment(url, comment, null, null, commentAddListener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#addLike(java.lang.String, com.socialize.listener.like.LikeAddListener)
	 */
	@Override
	public void like(String url, LikeAddListener likeAddListener) {
		like(url, null, likeAddListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#like(java.lang.String, android.location.Location, com.socialize.listener.like.LikeAddListener)
	 */
	@Override
	public void like(String url, Location location, LikeAddListener likeAddListener) {
		if(assertAuthenticated(likeAddListener)) {
			service.addLike(session, url, location, likeAddListener);
		}
	}
	
	
	@Override
	public void listLikesByUser(long userId, LikeListListener likeListListener) {
		if(assertAuthenticated(likeListListener)) {
			service.listLikesByUser(session, userId, likeListListener);
		}
	}

	@Override
	public void listLikesByUser(long userId, int startIndex, int endIndex, LikeListListener likeListListener) {
		if(assertAuthenticated(likeListListener)) {
			service.listLikesByUser(session, userId, startIndex, endIndex, likeListListener);
		}
	}

	@Override
	public void listCommentsByUser(long userId, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsByUser(session, userId, commentListListener);
		}		
	}

	@Override
	public void listCommentsByUser(long userId, int startIndex, int endIndex, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsByUser(session, userId, startIndex, endIndex, commentListListener);
		}
	}

	@Override
	public void share(String url, String text, ShareType shareType, ShareAddListener shareAddListener) {
		share(url, text, shareType, null, shareAddListener);
	}
	
	@Override
	public void share(String url, String text, ShareType shareType, Location location, ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			service.addShare(session, url, text, shareType, location, shareAddListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#view(java.lang.String, com.socialize.listener.view.ViewAddListener)
	 */
	@Override
	public void view(String url, ViewAddListener viewAddListener) {
		view(url, null, viewAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#view(java.lang.String, android.location.Location, com.socialize.listener.view.ViewAddListener)
	 */
	@Override
	public void view(String url, Location location, ViewAddListener viewAddListener) {
		if(assertAuthenticated(viewAddListener)) {
			service.addView(session, url, location, viewAddListener);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#deleteLike(int, com.socialize.listener.like.LikeDeleteListener)
	 */
	@Override
	public void unlike(long id, LikeDeleteListener likeDeleteListener) {
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
	public void getLikeById(long id, LikeGetListener likeGetListener) {
		if(assertAuthenticated(likeGetListener)) {
			service.getLike(session, id, likeGetListener);
		}
	}
	
	/**
	 * Retrieves a single like based on the entity liked.
	 * @param key The entity key corresponding to the like.
	 * @param likeGetListener A listener to handle callbacks from the get.
	 */
	public void getLike(String key, LikeGetListener likeGetListener) {
		if(assertAuthenticated(likeGetListener)) {
			service.getLike(session, key, likeGetListener);
		}
	}
	
	/**
	 * Creates a new entity.
	 * @param key The [unique] key for the entity.
	 * @param name The name for the entity.
	 * @param entityCreateListener A listener to handle callbacks from the post.
	 */
	public void addEntity(String key, String name, EntityAddListener entityCreateListener) {
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
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getUser(int, com.socialize.listener.user.UserGetListener)
	 */
	@Override
	public void getUser(long id, UserGetListener listener) {
		if(assertAuthenticated(listener)) {
			service.getUser(session, id, listener);
		}
	}
	
	
	
	@Override
	public void saveCurrentUserProfile(Context context, UserProfile profile, UserSaveListener listener) {
		if(assertAuthenticated(listener)) {
			service.saveUserProfile(context, session, profile, listener);
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
	public void listCommentsByEntity(String url, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsByEntity(session, url, commentListListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listCommentsByEntity(java.lang.String, int, int, com.socialize.listener.comment.CommentListListener)
	 */
	@Override
	public void listCommentsByEntity(String url, int startIndex, int endIndex, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			service.listCommentsByEntity(session, url, startIndex, endIndex, commentListListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listActivityByUser(int, com.socialize.listener.activity.ActivityListListener)
	 */
	@Override
	public void listActivityByUser(long userId, UserActivityListListener activityListListener) {
		if(assertAuthenticated(activityListListener)) {
			service.listActivityByUser(session, userId, activityListListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listActivityByUser(int, int, int, com.socialize.listener.activity.ActivityListListener)
	 */
	@Override
	public void listActivityByUser(long userId, int startIndex, int endIndex, UserActivityListListener activityListListener) {
		if(assertAuthenticated(activityListListener)) {
			service.listActivityByUser(session, userId, startIndex, endIndex, activityListListener);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getCommentById(int, com.socialize.listener.comment.CommentGetListener)
	 */
	@Override
	public void getCommentById(long id, CommentGetListener commentGetListener) {
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
		return isInitialized() && session != null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#isAuthenticated(com.socialize.auth.AuthProviderType)
	 */
	@Override
	public boolean isAuthenticated(AuthProviderType providerType) {
		if(isAuthenticated()) {
			
			if(providerType.equals(AuthProviderType.SOCIALIZE)) {
				return true;
			}
			
			AuthProviderType authProviderType = session.getAuthProviderType();
			
			if(authProviderType == null) {
				return false;
			}
			else {
				return (authProviderType.equals(providerType));
			}
		}
		return false;
	}

	protected boolean assertAuthenticated(SocializeListener listener) {
		if(asserter != null) {
			return asserter.assertAuthenticated(this, session, listener);
		}
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
	
	protected boolean assertInitialized(SocializeListener listener) {
		if(asserter != null) {
			return asserter.assertInitialized(this, listener);
		}
		
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

	@Override
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
	
	public static class InitTask extends AsyncTask<Void, Void, IOCContainer> {
		private Context context;
		private String[] paths;
		private Exception error;
		private SocializeInitListener listener;
		private SocializeServiceImpl service;
		private SocializeLogger logger;
		
		public InitTask(
				SocializeServiceImpl service, 
				Context context, 
				String[] paths, 
				SocializeInitListener listener, 
				SocializeLogger logger) {
			super();
			this.context = context;
			this.paths = paths;
			this.listener = listener;
			this.service = service;
			this.logger = logger;
		}

		@Override
		public IOCContainer doInBackground(Void... params) {
			try {
				return service.initWithContainer(context, paths);
			}
			catch (Exception e) {
				error = e;
				return null;
			}
		}

		@Override
		public void onPostExecute(IOCContainer result) {
			if(result == null) {
				final String errorMessage = "Failed to initialize Socialize instance";
				
				if(listener != null) {
					if(error != null) {
						if(error instanceof SocializeException) {
							listener.onError((SocializeException) error);
						}
						else {
							listener.onError(new SocializeException(error));
						}
					}
					else {
						listener.onError(new SocializeException(errorMessage));
					}
				}
				else {
					if(logger != null) {
						if(error != null) {
							logger.error(errorMessage, error);
						}
						else {
							logger.error(errorMessage);
						}
					}
					else {
						if(error != null) {
							error.printStackTrace();
						}
						else {
							System.err.println(errorMessage);
						}
					}
				}
			}
			else {
				if(listener != null) {
					listener.onInit(context, result);
				}
			}
		}
	};
	
	/**
	 * EXPERT ONLY (Not documented)
	 * @return
	 */
	IOCContainer getContainer() {
		return container;
	}
}
