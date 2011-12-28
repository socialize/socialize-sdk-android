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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.Logger;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.ActivitySystem;
import com.socialize.api.action.CommentSystem;
import com.socialize.api.action.EntitySystem;
import com.socialize.api.action.LikeSystem;
import com.socialize.api.action.ShareSystem;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.UserSystem;
import com.socialize.api.action.ViewSystem;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
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
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.comment.CommentShareOptions;
import com.socialize.ui.comment.CommentView;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.ui.profile.ProfileActivity;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.Drawables;
import com.socialize.util.ResourceLocator;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
@SuppressWarnings("deprecation")
public class SocializeServiceImpl implements SocializeSessionConsumer, SocializeService , SocializeUI {
	
	private SocializeLogger logger;
	private IOCContainer container;
	private SocializeSession session;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private SocializeInitializationAsserter asserter;
	private CommentSystem commentSystem;
	private ShareSystem shareSystem;
	private LikeSystem likeSystem;
	private ViewSystem viewSystem;
	private UserSystem userSystem;
	private ActivitySystem activitySystem;
	private EntitySystem entitySystem;
	private Drawables drawables;
	
	private SocializeSystem system = new SocializeSystem();
	private SocializeConfig config = new SocializeConfig();
	private SocializeEntityLoader entityLoader;
	
	private String[] initPaths = null;
	private int initCount = 0;
	
	@Override
	public boolean isSupported(AuthProviderType type) {
		switch(type) {
			case SOCIALIZE:
				return true;
			case FACEBOOK:
				return !StringUtils.isEmpty(getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID));
			default:
				return false;
		}
	}

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
				Logger.LOG_KEY = Socialize.LOG_KEY;
				
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
				
				this.commentSystem = container.getBean("commentSystem");
				this.shareSystem = container.getBean("shareSystem");
				this.likeSystem = container.getBean("likeSystem");
				this.viewSystem = container.getBean("viewSystem");
				this.userSystem = container.getBean("userSystem");
				this.activitySystem = container.getBean("activitySystem");
				this.entitySystem = container.getBean("entitySystem");
				this.drawables = container.getBean("drawables");
				this.logger = container.getBean("logger");
				this.authProviderDataFactory = container.getBean("authProviderDataFactory");
				this.asserter = container.getBean("initializationAsserter");
				
				SocializeConfig mainConfig = container.getBean("config");
				
				mainConfig.merge(config);
				
				this.config = mainConfig;
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
			if(userSystem != null) {
				userSystem.clearSession(type);
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
			if(userSystem != null) {
				userSystem.clearSession();
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
	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
		authenticate(null, consumerKey, consumerSecret, authProviderType, authProviderAppId, authListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(android.content.Context, java.lang.String, java.lang.String, com.socialize.auth.AuthProviderType, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderType(authProviderType);
		data.setAppId3rdParty(authProviderAppId);
		authenticate(context, consumerKey, consumerSecret, data, authListener, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(com.socialize.listener.SocializeAuthListener)
	 */
	@Deprecated
	@Override
	public void authenticate(SocializeAuthListener authListener) {
		authenticate((Context) null, authListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(android.content.Context, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticate(Context context, SocializeAuthListener authListener) {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		if(checkKeys(consumerKey, consumerSecret, authListener)) {
			authenticate(context, consumerKey, consumerSecret, authListener);
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(com.socialize.auth.AuthProviderType, com.socialize.listener.SocializeAuthListener)
	 */
	@Deprecated
	@Override
	public void authenticate(AuthProviderType authProviderType, SocializeAuthListener authListener) {
		authenticate(null, authProviderType, authListener);
	}
	
	@Override
	public void authenticate(Context context, AuthProviderType authProviderType, SocializeAuthListener authListener) {
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
				authenticate(context, consumerKey, consumerSecret, authListener);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener)  {
		authenticate(null, consumerKey, consumerSecret, authListener);
	}
	
	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(context, consumerKey, consumerSecret, data, authListener, false);
	}
	
	@Deprecated
	@Override
	public void authenticateKnownUser(String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderId, String authUserId3rdParty, String authToken3rdParty,
			SocializeAuthListener authListener) {
		authenticateKnownUser(null, consumerKey, consumerSecret, authProvider, authProviderId, authUserId3rdParty, authToken3rdParty, authListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticateKnownUser(android.content.Context, java.lang.String, java.lang.String, com.socialize.auth.AuthProviderType, java.lang.String, java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void authenticateKnownUser(Context context, String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderId, String authUserId3rdParty, String authToken3rdParty,
			SocializeAuthListener authListener) {
		
		AuthProviderData authProviderData = this.authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(authProvider);
		authProviderData.setAppId3rdParty(authProviderId);
		authProviderData.setToken3rdParty(authToken3rdParty);
		authProviderData.setUserId3rdParty(authUserId3rdParty);
		
		authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, false);
	}	
	
	protected void authenticate(
			Context context,
			String consumerKey, 
			String consumerSecret, 
			AuthProviderData authProviderData,
			SocializeAuthListener authListener, 
			boolean do3rdPartyAuth) {
		
		if(assertInitialized(authListener)) {
			userSystem.authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, this, do3rdPartyAuth);
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
		
	
	@Override
	public void addComment(final Activity activity, Entity entity, final String comment, final Location location, final ShareOptions shareOptions, final CommentAddListener commentAddListener) {
		if(assertAuthenticated(commentAddListener)) {
			if(shareOptions != null) {
				final SocialNetwork[] shareTo = shareOptions.getShareTo();
				if(shareTo == null || shareTo.length == 0) {
					commentSystem.addComment(session, entity, comment, location, shareOptions, commentAddListener);
				}
				else {
					commentSystem.addComment(session, entity, comment, location, shareOptions, new CommentAddListener() {
						@Override
						public void onError(SocializeException error) {
							if(commentAddListener != null) {
								commentAddListener.onError(error);
							}							
						}
						
						@Override
						public void onCreate(final Comment commentObject) {
							try {
								for (final SocialNetwork socialNetwork : shareTo) {
									try {
										shareSystem.shareComment(activity, commentObject.getEntity(), comment, location, socialNetwork, shareOptions.getListener());
									}
									catch(Exception e) {
										if(logger != null) {
											logger.error("Failed to share comment to [" +
													socialNetwork +
													"]", e);
										}
									}
								}
							}
							finally {
								if(commentAddListener != null) {
									commentAddListener.onCreate(commentObject);
								}
							}
						}
					});
				}
			}			
		}				
	}

	@Override
	public void addComment(Activity activity, Entity entity, String comment, ShareOptions shareOptions, CommentAddListener commentAddListener) {
		addComment(activity, entity, comment, null, shareOptions, commentAddListener);
	}
	
	@Override
	public void addComment(Activity activity, Entity entity, String comment, CommentAddListener commentAddListener) {
		addComment(activity, entity, comment, null, null, commentAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, android.location.Location, com.socialize.listener.comment.CommentAddListener)
	 */
	@Deprecated
	@Override
	public void addComment(String key, String comment, Location location, CommentShareOptions shareOptions, CommentAddListener commentAddListener) {
		if(assertAuthenticated(commentAddListener)) {
			commentSystem.addComment(session, Entity.newInstance(key, null), comment, location, shareOptions, commentAddListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, boolean, com.socialize.listener.comment.CommentAddListener)
	 */
	@Deprecated
	@Override
	public void addComment(String url, String comment, CommentShareOptions shareOptions, CommentAddListener commentAddListener) {
		addComment(url, comment, null, shareOptions, commentAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addComment(java.lang.String, java.lang.String, com.socialize.listener.comment.CommentAddListener)
	 */
	@Deprecated
	@Override
	public void addComment(String url, String comment, CommentAddListener commentAddListener) {
		addComment(url, comment, null, null, commentAddListener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#addLike(java.lang.String, com.socialize.listener.like.LikeAddListener)
	 */
	@Deprecated
	@Override
	public void like(String url, LikeAddListener likeAddListener) {
		like(url, null, likeAddListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#like(java.lang.String, android.location.Location, com.socialize.listener.like.LikeAddListener)
	 */
	@Deprecated
	@Override
	public void like(String key, Location location, LikeAddListener likeAddListener) {
		if(assertAuthenticated(likeAddListener)) {
			likeSystem.addLike(session, Entity.newInstance(key, null), location, likeAddListener);
		}
	}
	
	@Override
	public void like(Activity activity, Entity entity, LikeAddListener likeAddListener) {
		like(activity, entity, null, null, likeAddListener);
	}

	@Override
	public void like(Activity activity, Entity entity, ShareOptions shareOptions, LikeAddListener likeAddListener) {
		like(activity, entity, null, shareOptions, likeAddListener);
	}

	@Override
	public void like(final Activity activity, Entity entity, final Location location, final ShareOptions shareOptions, final LikeAddListener likeAddListener) {
		if(assertAuthenticated(likeAddListener)) {
			if(shareOptions != null) {
				final SocialNetwork[] shareTo = shareOptions.getShareTo();
				if(shareTo == null || shareTo.length == 0) {
					likeSystem.addLike(session, entity, location, likeAddListener);
				}
				else {
					likeSystem.addLike(session, entity, location, new LikeAddListener() {
						@Override
						public void onError(SocializeException error) {
							if(likeAddListener != null) {
								likeAddListener.onError(error);
							}							
						}
						
						@Override
						public void onCreate(final Like like) {
							try {
								for (final SocialNetwork socialNetwork : shareTo) {
									try {
										shareSystem.shareLike(activity, like.getEntity(), null, location, socialNetwork, shareOptions.getListener());
									}
									catch(Exception e) {
										if(logger != null) {
											logger.error("Failed to share comment to [" +
													socialNetwork +
													"]", e);
										}
									}
								}
							}
							finally {
								if(likeAddListener != null) {
									likeAddListener.onCreate(like);
								}
							}
						}
					});
				}
			}			
		}			
	}

	@Override
	public void listLikesByUser(long userId, LikeListListener likeListListener) {
		if(assertAuthenticated(likeListListener)) {
			likeSystem.getLikesByUser(session, userId, likeListListener);
		}
	}

	@Override
	public void listLikesByUser(long userId, int startIndex, int endIndex, LikeListListener likeListListener) {
		if(assertAuthenticated(likeListListener)) {
			likeSystem.getLikesByUser(session, userId, startIndex, endIndex, likeListListener);
		}
	}

	@Override
	public void listCommentsByUser(long userId, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			commentSystem.getCommentsByUser(session, userId, commentListListener);
		}		
	}

	@Override
	public void listCommentsByUser(long userId, int startIndex, int endIndex, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			commentSystem.getCommentsByUser(session, userId, startIndex, endIndex, commentListListener);
		}
	}

	@Deprecated
	@Override
	public void share(String url, String text, ShareType shareType, ShareAddListener shareAddListener) {
		share(url, text, shareType, null, shareAddListener);
	}
	
	@Deprecated
	@Override
	public void share(String key, String text, ShareType shareType, Location location, ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			shareSystem.addShare(session, Entity.newInstance(key, null), text, shareType, location, shareAddListener);
		}
	}
	
	@Override
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, ShareAddListener shareAddListener) {
		addShare(activity, entity, text, shareType, null, shareAddListener);
	}

	@Override
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, Location location, ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			shareSystem.addShare(session, entity, text, shareType, location, shareAddListener);
		}
	}

	@Override
	public void share(Activity activity, Entity entity, String text, ShareOptions options, ShareAddListener shareAddListener) {
		share(activity, entity, text, options, null, shareAddListener);
	}

	@Override
	public void share(final Activity activity, final Entity entity, final String text, final ShareOptions options, final Location location, final ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			if(options != null) {
				SocialNetwork[] shareTo = options.getShareTo();
				if(shareTo == null) {
					shareSystem.addShare(session, entity, text, ShareType.OTHER, location, shareAddListener);
				}
				else  {
					for (final SocialNetwork socialNetwork : shareTo) {
						shareSystem.addShare(session, entity, text, ShareType.valueOf(socialNetwork.name().toLowerCase()), location, new ShareAddListener() {
							@Override
							public void onError(SocializeException error) {
								if(shareAddListener != null) {
									shareAddListener.onError(error);
								}
							}
							
							@Override
							public void onCreate(Share share) {
								try {
									shareSystem.shareEntity(activity, share.getEntity(), text, location, socialNetwork, options.getListener());
								}
								finally {
									if(shareAddListener != null) {
										shareAddListener.onCreate(share);
									}
								}
							}
						});
					}
				}
			}
		}
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#view(java.lang.String, com.socialize.listener.view.ViewAddListener)
	 */
	@Deprecated
	@Override
	public void view(String url, ViewAddListener viewAddListener) {
		view(url, null, viewAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#view(java.lang.String, android.location.Location, com.socialize.listener.view.ViewAddListener)
	 */
	@Deprecated
	@Override
	public void view(String key, Location location, ViewAddListener viewAddListener) {
		if(assertAuthenticated(viewAddListener)) {
			viewSystem.addView(session, Entity.newInstance(key, null), location, viewAddListener);
		}
	}
	
	@Override
	public void view(Activity activity, Entity entity, ViewAddListener viewAddListener) {
		view(activity, entity, null, viewAddListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#view(android.app.Activity, com.socialize.entity.Entity, android.location.Location, com.socialize.listener.view.ViewAddListener)
	 */
	@Override
	public void view(Activity activity, Entity entity, Location location, ViewAddListener viewAddListener) {
		if(assertAuthenticated(viewAddListener)) {
			viewSystem.addView(session, entity, location, viewAddListener);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#deleteLike(int, com.socialize.listener.like.LikeDeleteListener)
	 */
	@Override
	public void unlike(long id, LikeDeleteListener likeDeleteListener) {
		if(assertAuthenticated(likeDeleteListener)) {
			likeSystem.deleteLike(session, id, likeDeleteListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listLikesById(com.socialize.listener.like.LikeListListener, int[])
	 */
	@Override
	public void listLikesById(LikeListListener likeListListener, long...ids) {
		if(assertAuthenticated(likeListListener)) {
			likeSystem.getLikesById(session, likeListListener, ids);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getLikeById(long, com.socialize.listener.like.LikeGetListener)
	 */
	@Override
	public void getLikeById(long id, LikeGetListener likeGetListener) {
		if(assertAuthenticated(likeGetListener)) {
			likeSystem.getLike(session, id, likeGetListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getLike(java.lang.String, com.socialize.listener.like.LikeGetListener)
	 */
	@Override
	public void getLike(String key, LikeGetListener likeGetListener) {
		if(assertAuthenticated(likeGetListener)) {
			likeSystem.getLike(session, key, likeGetListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addEntity(java.lang.String, java.lang.String, com.socialize.listener.entity.EntityAddListener)
	 */
	@Deprecated
	public void addEntity(String key, String name, EntityAddListener entityCreateListener) {
		if(assertAuthenticated(entityCreateListener)) {
			entitySystem.addEntity(session, Entity.newInstance(key, name), entityCreateListener);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#addEntity(com.socialize.entity.Entity, com.socialize.listener.entity.EntityAddListener)
	 */
	@Override
	public void addEntity(Entity entity, EntityAddListener entityAddListener) {
		if(assertAuthenticated(entityAddListener)) {
			entitySystem.addEntity(session, entity, entityAddListener);
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getEntity(java.lang.String, com.socialize.listener.entity.EntityGetListener)
	 */
	@Override
	public void getEntity(String key, EntityGetListener listener) {
		if(assertAuthenticated(listener)) {
			entitySystem.getEntity(session, key, listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getUser(int, com.socialize.listener.user.UserGetListener)
	 */
	@Override
	public void getUser(long id, UserGetListener listener) {
		if(assertAuthenticated(listener)) {
			userSystem.getUser(session, id, listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#saveCurrentUserProfile(android.content.Context, com.socialize.ui.profile.UserProfile, com.socialize.listener.user.UserSaveListener)
	 */
	@Override
	public void saveCurrentUserProfile(Context context, UserProfile profile, UserSaveListener listener) {
		if(assertAuthenticated(listener)) {
			userSystem.saveUserProfile(context, session, profile, listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listEntitiesByKey(com.socialize.listener.entity.EntityListListener, java.lang.String[])
	 */
	@Override
	public void listEntitiesByKey(EntityListListener entityListListener, String...keys) {
		if(assertAuthenticated(entityListListener)) {
			entitySystem.listEntities(session, entityListListener, keys);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listCommentsByEntity(java.lang.String, com.socialize.listener.comment.CommentListListener)
	 */
	@Override
	public void listCommentsByEntity(String url, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			commentSystem.getCommentsByEntity(session, url, commentListListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listCommentsByEntity(java.lang.String, int, int, com.socialize.listener.comment.CommentListListener)
	 */
	@Override
	public void listCommentsByEntity(String url, int startIndex, int endIndex, CommentListListener commentListListener) {
		if(assertAuthenticated(commentListListener)) {
			commentSystem.getCommentsByEntity(session, url, startIndex, endIndex, commentListListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listActivityByUser(int, com.socialize.listener.activity.ActivityListListener)
	 */
	@Override
	public void listActivityByUser(long userId, UserActivityListListener activityListListener) {
		if(assertAuthenticated(activityListListener)) {
			activitySystem.getActivityByUser(session, userId, activityListListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listActivityByUser(int, int, int, com.socialize.listener.activity.ActivityListListener)
	 */
	@Override
	public void listActivityByUser(long userId, int startIndex, int endIndex, UserActivityListListener activityListListener) {
		if(assertAuthenticated(activityListListener)) {
			activitySystem.getActivityByUser(session, userId, startIndex, endIndex, activityListListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#listCommentsById(com.socialize.listener.comment.CommentListListener, int[])
	 */
	@Override
	public void listCommentsById(CommentListListener commentListListener, long...ids) {
		if(assertAuthenticated(commentListListener)) {
			commentSystem.getCommentsById(session, commentListListener, ids);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getCommentById(int, com.socialize.listener.comment.CommentGetListener)
	 */
	@Override
	public void getCommentById(long id, CommentGetListener commentGetListener) {
		if(assertAuthenticated(commentGetListener)) {
			commentSystem.getComment(session, id, commentGetListener);
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

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSessionConsumer#setSession(com.socialize.api.SocializeSession)
	 */
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
//		if(isInitialized()) {
//			return container.getBean("config");
//		}
//		
//		if(logger != null) logger.error(SocializeLogger.NOT_INITIALIZED);
//		return null;
		
		return config;
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

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity) {
		return showActionBar(parent, resId, entity, null, null);
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener) {
		return showActionBar(parent, resId, entity, null, listener);
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options) {
		return showActionBar(parent, resId, entity, options, null);
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return showActionBar(parent, inflateView(parent, resId) , entity, options, listener);
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity) {
		return showActionBar(parent, original, entity, null, null);
	}
	
	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options) {
		return showActionBar(parent, original, entity, options, null);
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener) {
		return showActionBar(parent, original, entity, null, listener);
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		RelativeLayout barLayout = newRelativeLayout(parent);
		RelativeLayout originalLayout = newRelativeLayout(parent);
		
		ActionBarView socializeActionBar = newActionBarView(parent);
		socializeActionBar.assignId(original);
		socializeActionBar.setEntity(entity);
		
		LayoutParams barParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		barParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		LayoutParams originalParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		originalParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
		
		socializeActionBar.setLayoutParams(barParams);
		originalLayout.setLayoutParams(originalParams);
		
		if(listener != null) {
			listener.onCreate(socializeActionBar);
		}
		
		boolean addScrollView = true;
		
		if(options != null) {
			addScrollView = options.isAddScrollView();
		}
		
		if(addScrollView && !(original instanceof ScrollView) ) {
			LayoutParams scrollViewParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			ScrollView scrollView = newScrollView(parent);
			scrollView.setFillViewport(true);
			scrollView.setLayoutParams(scrollViewParams);
			scrollView.addView(original);
			scrollView.setScrollContainer(false);
			originalLayout.addView(scrollView);
		}
		else {
			originalLayout.addView(original);
		}
		
		barLayout.addView(originalLayout);
		barLayout.addView(socializeActionBar);
		
		return barLayout;
	}

	protected ActionBarView newActionBarView(Activity parent) {
		return new ActionBarView(parent);
	}

	protected Intent newIntent(Activity context, Class<?> cls) {
		return new Intent(context, cls);
	}

	protected LayoutParams newLayoutParams(int width, int height) {
		return new LayoutParams(width, height);
	}

	protected RelativeLayout newRelativeLayout(Activity parent) {
		return new RelativeLayout(parent);
	}

	protected ScrollView newScrollView(Activity parent) {
		return new ScrollView(parent);
	}

	protected View inflateView(Activity parent, int resId) {
		LayoutInflater layoutInflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		return layoutInflater.inflate(resId, null);
	}

	/**
	 * Displays the detail view for a single Socialize action.
	 * @param context
	 * @param userId
	 * @param commentId
	 * @param requestCode
	 */
	@Override
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {
		Intent i = newIntent(context, ActionDetailActivity.class);
		i.putExtra(Socialize.USER_ID, user.getId().toString());
		i.putExtra(Socialize.COMMENT_ID, action.getId().toString());
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			// Revert to legacy
			i.setClass(context, CommentDetailActivity.class);
			try {
				context.startActivityForResult(i, requestCode);
				Log.w(Socialize.LOG_KEY, "Using legacy CommentDetailActivity.  Please update your AndroidManifest.xml to use ActionDetailActivity");
			} 
			catch (ActivityNotFoundException e2) {
				Log.e(Socialize.LOG_KEY, "Could not find ActionDetailActivity.  Make sure you have added this to your AndroidManifest.xml");
			}
		}
	}

	/**
	 * Shows the comments list for the given entity.
	 * @param context
	 * @param entity
	 */
	@Override
	public void showCommentView(Activity context, Entity entity) {
		showCommentView(context, entity, null);
	}

	/**
	 * Shows the comments list for the given entity.
	 * @param context
	 * @param entity
	 * @param listener
	 */
	@Override
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {
		if(listener != null) {
			Socialize.STATIC_LISTENERS.put(CommentView.COMMENT_LISTENER, listener);
		}

		try {
			Intent i = newIntent(context, CommentActivity.class);
			i.putExtra(Socialize.ENTITY_OBJECT, entity);
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(Socialize.LOG_KEY, "Could not find CommentActivity.  Make sure you have added this to your AndroidManifest.xml");
		} 
	}

	@Override
	public void showUserProfileView(Activity context, String userId) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(Socialize.USER_ID, userId);
		try {
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(Socialize.LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}
	}

	@Override
	public void showUserProfileViewForResult(Activity context, String userId, int requestCode) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(Socialize.USER_ID, userId);
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(Socialize.LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeUI#getDrawable(java.lang.String, boolean)
	 */
	@Override
	public Drawable getDrawable(String name, boolean eternal) {
		return drawables.getDrawable(name, eternal);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeUI#getDrawable(java.lang.String, int, boolean)
	 */
	@Override
	public Drawable getDrawable(String name, int density, boolean eternal) {
		return drawables.getDrawable(name, density, eternal);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getEntityLoader()
	 */
	@Override
	public SocializeEntityLoader getEntityLoader() {
		return entityLoader;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#getSystem()
	 */
	@Override
	public SocializeSystem getSystem() {
		return system;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#setEntityLoader(com.socialize.ui.SocializeEntityLoader)
	 */
	@Override
	public void setEntityLoader(SocializeEntityLoader entityLoader) {
		this.entityLoader = entityLoader;
	}

	protected void setCommentSystem(CommentSystem commentSystem) {
		this.commentSystem = commentSystem;
	}

	protected void setShareSystem(ShareSystem shareSystem) {
		this.shareSystem = shareSystem;
	}

	protected void setLikeSystem(LikeSystem likeSystem) {
		this.likeSystem = likeSystem;
	}

	protected void setViewSystem(ViewSystem viewSystem) {
		this.viewSystem = viewSystem;
	}

	protected void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}

	protected void setActivitySystem(ActivitySystem activitySystem) {
		this.activitySystem = activitySystem;
	}

	protected void setEntitySystem(EntitySystem entitySystem) {
		this.entitySystem = entitySystem;
	}
}
