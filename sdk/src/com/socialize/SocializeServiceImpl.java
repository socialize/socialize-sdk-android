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
import java.util.Collection;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.socialize.api.action.SubscriptionSystem;
import com.socialize.api.action.UserSystem;
import com.socialize.api.action.ViewSystem;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
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
import com.socialize.listener.subscription.SubscriptionAddListener;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionListListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.NotificationChecker;
import com.socialize.notifications.NotificationType;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.notifications.WakeLock;
import com.socialize.share.ShareHandlerListener;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.comment.CommentView;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.ui.profile.ProfileActivity;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.AppUtils;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.Drawables;
import com.socialize.util.EntityLoaderUtils;
import com.socialize.util.ResourceLocator;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeServiceImpl implements SocializeSessionConsumer, SocializeService , SocializeUI {
	
	static final String receiver = SocializeC2DMReceiver.class.getName();
	
	private SocializeLogger logger;
	private IOCContainer container;
	private SocializeSession session;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	private SocializeInitializationAsserter asserter;
	private CommentSystem commentSystem;
	private ShareSystem shareSystem;
	private LikeSystem likeSystem;
	private ViewSystem viewSystem;
	private UserSystem userSystem;
	private ActivitySystem activitySystem;
	private EntitySystem entitySystem;
	private SubscriptionSystem subscriptionSystem;
	private Drawables drawables;
	private AuthProviders authProviders;
	private NotificationChecker notificationChecker;
	private AppUtils appUtils;
	
	private SocializeSystem system = SocializeSystem.getInstance();
	private SocializeConfig config = new SocializeConfig();
	
	private SocializeEntityLoader entityLoader;
	
	private String[] initPaths = null;
	private int initCount = 0;
	
	public SocializeServiceImpl() {
		super();
		this.logger = newLogger();
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#handleBroadcastIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	public boolean handleBroadcastIntent(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(action != null && (SocializeC2DMReceiver.C2DM_INTENT.equals(action) || SocializeC2DMReceiver.REGISTRATION_CALLBACK_INTENT.equals(action))) {
			
			if(SocializeC2DMReceiver.C2DM_INTENT.equals(action)) {
				// Check the bundle data for source
				Bundle extras = intent.getExtras();
				if(extras != null) {
					String source = extras.getString(C2DMCallback.SOURCE_KEY);
					if(source != null && source.trim().equalsIgnoreCase(C2DMCallback.SOURCE_SOCIALIZE)) {
						handleIntent(context, intent);
						return true;
					}
				}
			}
			else {
				// Handle registration, but don't return true.
				handleIntent(context, intent);
			}
		}
		
		return false;
	}
	
	protected void handleIntent(Context context, Intent intent) {
		getWakeLock().acquire(context);
		intent.setClassName(context, receiver);
		context.startService(intent);
	}
	
	// So we can mock
	protected WakeLock getWakeLock() {
		return WakeLock.getInstance();
	}

	@Override
	public boolean isSupported(AuthProviderType type) {
		return authProviderInfoBuilder.isSupported(type);
	}

	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context)
	 */
	@Override
	public IOCContainer init(Context context) {
		return init(context, getSystem().getBeanConfig());
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.SocializeService#init(android.content.Context, java.lang.String)
	 */
	@Override
	public IOCContainer init(Context context, String...paths) {
		try {
			return initWithContainer(context, paths);
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error(SocializeLogger.INITIALIZE_FAILED, e);
			}
			else {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#initAsync(android.content.Context, com.socialize.listener.SocializeInitListener)
	 */
	@Override
	public void initAsync(Context context, SocializeInitListener listener) {
		initAsync(context, listener, getSystem().getBeanConfig());
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#initAsync(android.content.Context, com.socialize.listener.SocializeInitListener, java.lang.String[])
	 */
	@Override
	public void initAsync(Context context, SocializeInitListener listener, String... paths) {
		new InitTask(this, context, paths, listener, logger).execute();
	}

	public synchronized IOCContainer initWithContainer(Context context, String...paths) throws Exception {
		return initWithContainer(context, getSystem().getSystemInitListener(), paths);
	}
	
	public synchronized IOCContainer initWithContainer(Context context, SocializeInitListener listener, String...paths) throws Exception {
		boolean init = false;
		
		String[] localPaths = getInitPaths();
		
		if(paths != null) {

			if(isInitialized()) {
				
				if(localPaths != null) {
					for (String path : paths) {
						if(binarySearch(localPaths, path) < 0) {
							
							if(logger != null && logger.isInfoEnabled()) {
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
					String msg = "Socialize reported as initialize, but no initPaths were found.  This should not happen!";
					if(logger != null) {
						logger.error(msg);
					}
					else {
						System.err.println(msg);
					}
					
					destroy();
					init = true;
				}
			}
			else {
				init = true;
			}
			
			if(init) {
				try {
					Logger.LOG_KEY = Socialize.LOG_KEY;
					Logger.logLevel = Log.WARN;
					
					this.initPaths = paths;
					
					sort(this.initPaths);
					
					SocializeIOC container = newSocializeIOC();
					ResourceLocator locator = newResourceLocator();
					
					locator.setLogger(newLogger());
					
					ClassLoaderProvider provider = newClassLoaderProvider();
					
					locator.setClassLoaderProvider(provider);
					
					if(logger != null && logger.isDebugEnabled()) {
						
						for (String path : paths) {
							logger.debug("Initializing Socialize with path [" +
									path +
									"]");
						}
					}			
					
					container.init(context, locator, paths);
					
					init(context, container, listener); // initCount incremented here
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
		}
		else {
			String msg = "Attempt to initialize Socialize with null bean config paths";
			if(logger != null) {
				logger.error(msg);
			}
			else {
				System.err.println(msg);
			}
		}
		
		return container;
	}

	// So we can mock
	protected String[] getInitPaths() {
		return initPaths;
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
		return new SocializeLogger(Socialize.DEFAULT_LOG_LEVEL);
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
		init(context, container, getSystem().getSystemInitListener());
	}
	
	public synchronized void init(Context context, final IOCContainer container, SocializeInitListener listener) {
		if(!isInitialized()) {
			try {
				this.container = container;
				
				this.logger = container.getBean("logger");
				
				this.commentSystem = container.getBean("commentSystem");
				this.shareSystem = container.getBean("shareSystem");
				this.likeSystem = container.getBean("likeSystem");
				this.viewSystem = container.getBean("viewSystem");
				this.userSystem = container.getBean("userSystem");
				this.activitySystem = container.getBean("activitySystem");
				this.entitySystem = container.getBean("entitySystem");
				this.subscriptionSystem = container.getBean("subscriptionSystem");
				this.drawables = container.getBean("drawables");
				this.authProviderDataFactory = container.getBean("authProviderDataFactory");
				this.asserter = container.getBean("initializationAsserter");
				this.authProviders = container.getBean("authProviders");
				this.authProviderInfoBuilder = container.getBean("authProviderInfoBuilder");
				this.notificationChecker = container.getBean("notificationChecker");
				this.appUtils = container.getBean("appUtils");
				
				SocializeConfig mainConfig = container.getBean("config");
				
				mainConfig.merge(config);
				
				this.config = mainConfig;
				this.initCount++;
				
				verify3rdPartyAuthConfigured();
				
				// Create the entity loader if we have one
				initEntityLoader();
				
				initNotifications(context);
				
				// Check we are configured ok
				appUtils.checkAndroidManifest(context);
				
				ActivityIOCProvider.getInstance().setContainer(container);
				
				if(listener != null) {
					listener.onInit(context, container);
				}
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
	
	/**
	 * @param context
	 */
	protected synchronized void initNotifications(Context context) {
		if(notificationChecker != null) {
			notificationChecker.checkRegistrations(context);
		}
	}

	protected synchronized void initEntityLoader() {
		EntityLoaderUtils entityLoaderUtils = container.getBean("entityLoaderUtils");
		entityLoaderUtils.initEntityLoader();
	}
	
	protected void verify3rdPartyAuthConfigured() {
		authProviderInfoBuilder.validateAll();
	}
	
	@Override
	public void saveSession(Context context) {
		userSystem.saveSession(context, session);
	}

	@Override
	public void clear3rdPartySession(Context context, AuthProviderType type) {
		try {
			if(session != null) {
				AuthProvider<AuthProviderInfo> provider = authProviders.getProvider(type);
				
				if(provider != null) {
					UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(type);
					
					if(userProviderCredentials != null) {
						AuthProviderInfo authProviderInfo = userProviderCredentials.getAuthProviderInfo();
						if(authProviderInfo != null) {
							provider.clearCache(context, authProviderInfo);
						}
					}
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
				
				UserProviderCredentialsMap userProviderCredentialsMap = session.getUserProviderCredentials();
				
				if(userProviderCredentialsMap != null) {
					Collection<UserProviderCredentials> values = userProviderCredentialsMap.values();
					for (UserProviderCredentials userProviderCredentials : values) {
						AuthProviderInfo authProviderInfo = userProviderCredentials.getAuthProviderInfo();
						if(authProviderInfo != null) {
							clear3rdPartySession(context, authProviderInfo.getType());
						}
					}
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
	public synchronized void destroy(boolean force) {
		if(force) {
			if(container != null) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Destroying IOC container");
				}
				container.destroy();
			}
			
			system.destroy();
			
			initCount = 0;
			initPaths = null;
			entityLoader = null;
		}
		else {
			destroy();
		}
	}
	
	@Override
	public synchronized void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener) {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderInfo(authProviderInfo);
		authenticate(context, consumerKey, consumerSecret, data, authListener, true);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeService#authenticate(android.content.Context, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public synchronized void authenticate(Context context, SocializeAuthListener authListener) {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		authenticate(context, consumerKey, consumerSecret, authListener);
	}
	
	public synchronized SocializeSession authenticateSynchronous(Context context) throws SocializeException {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);		
		if(checkKeys(consumerKey, consumerSecret)) {
			return userSystem.authenticateSynchronous(context, consumerKey, consumerSecret, this);
		}
		else {
			throw new SocializeException("Consumer key and/or secret not provided");
		}
	}

	@Override
	public synchronized void authenticate(Context context, AuthProviderType authProviderType, SocializeAuthListener authListener) {
		SocializeConfig config = getConfig();
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		AuthProviderInfo authProviderInfo = authProviderInfoBuilder.getFactory(authProviderType).getInstance();
		authenticate(context, consumerKey, consumerSecret, authProviderInfo, authListener);
	}

	@Override
	public synchronized void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		if(assertInitialized(authListener)) {
			AuthProviderData data = this.authProviderDataFactory.getBean();
			SocializeAuthProviderInfo info = newSocializeAuthProviderInfo();
			data.setAuthProviderInfo(info);
			authenticate(context, consumerKey, consumerSecret, data, authListener, false);
		}
	}
	
	// So we can mock
	protected SocializeAuthProviderInfo newSocializeAuthProviderInfo() {
		return new SocializeAuthProviderInfo();
	}
	
	@Override
	public synchronized void authenticateKnownUser(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, UserProviderCredentials userProviderCredentials, SocializeAuthListener authListener) {
		if(assertInitialized(authListener)) {
			AuthProviderData authProviderData = this.authProviderDataFactory.getBean();
			authProviderData.setAuthProviderInfo(authProviderInfo);
			authProviderData.setToken3rdParty(userProviderCredentials.getAccessToken());
			authProviderData.setSecret3rdParty(userProviderCredentials.getTokenSecret());
			authProviderData.setUserId3rdParty(userProviderCredentials.getUserId());
			authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, false);	
		}
	}

	protected synchronized void authenticate(
			Context context,
			String consumerKey, 
			String consumerSecret, 
			AuthProviderData authProviderData,
			SocializeAuthListener authListener, 
			boolean do3rdPartyAuth) {
		
		if(checkKeys(consumerKey, consumerSecret, authListener)) {
			if(assertInitialized(authListener)) {
				userSystem.authenticate(context, consumerKey, consumerSecret, authProviderData, authListener, this, do3rdPartyAuth);
			}
		}
	}

	protected boolean checkKeys(String consumerKey, String consumerSecret) {
		return checkKeys(consumerKey, consumerSecret, null);
	}
	
	protected boolean checkKeys(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		return  checkKey("consumer key", consumerKey, authListener) &&
				checkKey("consumer secret", consumerSecret, authListener);
	}
	protected boolean checkKey(String name, String key, SocializeAuthListener authListener) {
		if(StringUtils.isEmpty(key)) {
			String msg = "No key specified in authenticate";
			if(authListener != null) {
				authListener.onError(new SocializeException(msg));
			}
			logErrorMessage(msg);
			return false;
		} else {
			return true;	
		}
	}
	protected void logErrorMessage(String message) {
		if(logger != null) {
			logger.error(message);
		}
		else {
			System.err.println(message);
		}
	}
 	@Override
	public void addComment(final Activity activity, Entity entity, final String comment, final ShareOptions shareOptions, final CommentAddListener commentAddListener) {
		Comment c = newComment();
		c.setText(comment);
		c.setEntity(entity);
		addComment(activity, c, shareOptions, commentAddListener);
	}
	
	// So we can mock
	protected Comment newComment() {
		return new Comment();
	}
	
	@Override
	public void addComment(Activity activity, Entity entity, String comment, CommentAddListener commentAddListener) {
		addComment(activity, entity, comment, null, commentAddListener);
	}

	@Override
	public void like(Activity activity, Entity entity, LikeAddListener likeAddListener) {
		like(activity, entity, null, likeAddListener);
	}

	@Override
	public void addComment(final Activity activity, final Comment comment, final ShareOptions shareOptions, final CommentAddListener commentAddListener) {
		if(assertAuthenticated(commentAddListener)) {
			if(shareOptions != null) {
				final SocialNetwork[] shareTo = shareOptions.getShareTo();
				final boolean autoAuth = shareOptions.isAutoAuth();
				if(shareTo == null || shareTo.length == 0) {
					commentSystem.addComment(session, comment, shareOptions, commentAddListener);
				}
				else {
					commentSystem.addComment(session, comment, shareOptions, new CommentAddListener() {
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
									handleActionShare(activity, socialNetwork, commentObject, commentObject.getText(), shareOptions.getLocation(), autoAuth, shareOptions.getListener());
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
			else {
				commentSystem.addComment(session, comment, shareOptions, commentAddListener);
			}
		}				
	}
	
	@Override
	public void like(final Activity activity, Entity entity, final ShareOptions shareOptions, final LikeAddListener likeAddListener) {
		if(assertAuthenticated(likeAddListener)) {
			if(shareOptions != null) {
				final SocialNetwork[] shareTo = shareOptions.getShareTo();
				final boolean autoAuth = shareOptions.isAutoAuth();
				if(shareTo == null || shareTo.length == 0) {
					likeSystem.addLike(session, entity, shareOptions, likeAddListener);
				}
				else {
					likeSystem.addLike(session, entity, shareOptions, new LikeAddListener() {
						@Override
						public void onError(SocializeException error) {
							if(likeAddListener != null) {
								likeAddListener.onError(error);
							}							
						}
						
						@Override
						public void onCreate(final Like like) {
							try {
								if(like != null && shareSystem != null) {
									for (final SocialNetwork socialNetwork : shareTo) {
										handleActionShare(activity, socialNetwork, like, null, shareOptions.getLocation(), autoAuth, shareOptions.getListener());
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
			else {
				likeSystem.addLike(session, entity, shareOptions, likeAddListener);
			}
		}			
	}
	
	@Override
	public void share(final Activity activity, final Entity entity, final String text, final ShareOptions shareOptions, final ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			if(shareOptions != null) {
				SocialNetwork[] shareTo = shareOptions.getShareTo();
				final boolean autoAuth = shareOptions.isAutoAuth();
				if(shareTo == null) {
					shareSystem.addShare(activity, session, entity, text, ShareType.OTHER, shareOptions.getLocation(), shareAddListener);
				}
				else  {
					for (final SocialNetwork socialNetwork : shareTo) {
						shareSystem.addShare(activity, session, entity, text, socialNetwork, shareOptions.getLocation(), new ShareAddListener() {
							@Override
							public void onError(SocializeException error) {
								if(shareAddListener != null) {
									shareAddListener.onError(error);
								}
							}
							
							@Override
							public void onCreate(Share share) {
								try {
									if(share != null && shareSystem != null && !shareOptions.isSelfManaged()) {
										handleActionShare(activity, socialNetwork, share, text, shareOptions.getLocation(), autoAuth, shareOptions.getListener());
									}
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
	
	@Override
	public void share(Activity activity, Entity entity, String text, ShareType shareType, ShareAddListener shareAddListener) {
		share(activity, entity, text, shareType, null, shareAddListener);
	}

	@Override
	public void share(final Activity activity, Entity entity, final String text, final ShareType shareType, final Location location, final ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			shareSystem.addShare(activity, session, entity, text, shareType, location, new ShareAddListener() {
				@Override
				public void onError(SocializeException error) {
					if(shareAddListener != null) {
						shareAddListener.onError(error);
					}
				}
				
				@Override
				public void onCreate(Share share) {
					if(share != null && shareSystem != null) {
						handleActionShare(activity, shareType, share, text, location, true, shareAddListener);
					}
				}
			});
		}		
	}
	
	protected void handleActionShare(Activity activity, final ShareType shareType, final Share share, String shareText, Location location, boolean autoAuth, final ShareAddListener shareAddListener) {
		shareSystem.share(activity, session, share, shareText, location, shareType, autoAuth, new ShareHandlerListener() {
			@Override
			public void onError(Activity parent, SocializeAction action, String message, Throwable error) {
				if(logger != null) {
					logger.error("Failed to share action to [" +
							shareType +
							"]", error);
				}
				
				if(shareAddListener != null) {
					shareAddListener.onError(SocializeException.wrap(error));
				}
			}
			
			@Override
			public void onBeforePost(Activity parent) {}
			
			@Override
			public void onAfterPost(Activity parent, SocializeAction action) {
				if(shareAddListener != null) {
					shareAddListener.onCreate(share);
				}
			}
		});	
	}

	protected void handleActionShare(Activity activity, final SocialNetwork socialNetwork, SocializeAction action, String shareText, Location location, boolean autoAuth, final SocialNetworkListener listener) {
		shareSystem.share(activity, session, action, shareText, location, ShareType.valueOf(socialNetwork), autoAuth, new ShareHandlerListener() {
			@Override
			public void onError(Activity parent, SocializeAction action, String message, Throwable error) {
				if(logger != null) {
					logger.error("Failed to share action to [" +
							socialNetwork +
							"]", error);
				}
				
				if(listener != null) {
					listener.onError(parent, socialNetwork, message, error);
				}
			}
			
			@Override
			public void onBeforePost(Activity parent) {
				if(listener != null) {
					listener.onBeforePost(parent, socialNetwork);
				}
			}
			
			@Override
			public void onAfterPost(Activity parent, SocializeAction action) {
				if(listener != null) {
					listener.onAfterPost(parent, socialNetwork);
				}
			}
		});	
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

	@Override
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, ShareAddListener shareAddListener) {
		addShare(activity, entity, text, shareType, null, shareAddListener);
	}

	@Override
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, Location location, ShareAddListener shareAddListener) {
		if(assertAuthenticated(shareAddListener)) {
			shareSystem.addShare(activity, session, entity, text, shareType, location, shareAddListener);
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
	 * @see com.socialize.SocializeService#addEntity(com.socialize.entity.Entity, com.socialize.listener.entity.EntityAddListener)
	 */
	@Override
	public void addEntity(Activity activity, Entity entity, EntityAddListener entityAddListener) {
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
	 * @see com.socialize.SocializeService#getEntityById(long, com.socialize.listener.entity.EntityGetListener)
	 */
	@Override
	public void getEntityById(long id, EntityGetListener listener) {
		if(assertAuthenticated(listener)) {
			entitySystem.getEntity(session, id, listener);
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
	
	

	@Override
	public void subscribe(Context context, Entity entity, NotificationType type, SubscriptionAddListener subscriptionAddListener) {
		if(assertAuthenticated(subscriptionAddListener)) {
			subscriptionSystem.addSubscription(session, entity, type, subscriptionAddListener);
		}		
	}

	@Override
	public void unsubscribe(Context context, Entity entity, NotificationType type, SubscriptionAddListener subscriptionAddListener) {
		if(assertAuthenticated(subscriptionAddListener)) {
			subscriptionSystem.removeSubscription(session, entity, type, subscriptionAddListener);
		}				
	}

	@Override
	public void listSubscriptions(SubscriptionListListener subscriptionListListener) {
		if(assertAuthenticated(subscriptionListListener)) {
			subscriptionSystem.listSubscriptions(session, subscriptionListListener);
		}			
	}

	@Override
	public void listSubscriptions(int startIndex, int endIndex, SubscriptionListListener subscriptionListListener) {
		if(assertAuthenticated(subscriptionListListener)) {
			subscriptionSystem.listSubscriptions(session, startIndex, endIndex, subscriptionListListener);
		}			
	}

	@Override
	public void getSubscription(Entity entity, SubscriptionGetListener subscriptionGetListener) {
		if(assertAuthenticated(subscriptionGetListener)) {
			subscriptionSystem.getSubscription(session, entity, subscriptionGetListener);
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
			
			UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(providerType);
			
			if(userProviderCredentials == null) {
				// Legacy
				return isAuthenticatedLegacy(providerType);
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isAuthenticatedLegacy(AuthProviderType providerType) {
		AuthProviderType authProviderType = session.getAuthProviderType();
		if(authProviderType == null) {
			return false;
		}
		else {
			return (authProviderType.equals(providerType));
		}
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
					if(logger != null && logger.isInitialized()) {
						listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_AUTHENTICATED)));
					}
					else {
						listener.onError(new SocializeException("Not authenticated"));
					}
				}
				if(logger != null && logger.isInitialized()) {
					logger.error(SocializeLogger.NOT_AUTHENTICATED);
				}
				else {
					System.err.println("Not authenticated");
				}
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
				if(logger != null && logger.isInitialized()) {
					listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_INITIALIZED)));
				}
				else {
					listener.onError(new SocializeException("Not initialized"));
				}
			}
			if(logger != null) {
				if(logger.isInitialized()) {
					logger.error(SocializeLogger.NOT_INITIALIZED);
				}
				else {
					logger.error("Socialize Not initialized!");
				}
			}
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
				// Force null listener.  This will be called in postExecute
				return service.initWithContainer(context, null, paths);
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
						listener.onError(SocializeException.wrap(error));
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
		RelativeLayout parentRelLyout = newRelativeLayout(parent);
		RelativeLayout.LayoutParams barLayoutParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		parentRelLyout.setLayoutParams(barLayoutParams);
		
		ActionBarView socializeActionBar = newActionBarView(parent);
		socializeActionBar.setActionBarListener(listener);
		socializeActionBar.assignId(original);
		socializeActionBar.setEntity(entity);
		
		LayoutParams socializeActionBarParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		socializeActionBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		socializeActionBar.setLayoutParams(socializeActionBarParams);
		
		boolean addScrollView = true;
		
		if(options != null) {
			addScrollView = options.isAddScrollView();
		}
		
		View contentView = null;
		
		if(addScrollView && !(original instanceof ScrollView) && !(original instanceof ListView) ) {
			RelativeLayout.LayoutParams scrollViewParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			
			scrollViewParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
			
			ScrollView scrollView = newScrollView(parent);
			scrollView.setFillViewport(true);
			scrollView.setScrollContainer(false);
			scrollView.setLayoutParams(scrollViewParams);
			scrollView.addView(original);
			
			contentView = scrollView;
		}
		else {
			ViewGroup.LayoutParams originalParams = original.getLayoutParams();
			LayoutParams updatedOriginalParams = null;
			
			if(originalParams != null) {
				updatedOriginalParams = newLayoutParams(originalParams);
			}
			else {
				updatedOriginalParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			}
			
			updatedOriginalParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
			
			original.setLayoutParams(updatedOriginalParams);
			
			contentView = original;
		}		

		parentRelLyout.addView(contentView);
		parentRelLyout.addView(socializeActionBar);
		
		return parentRelLyout;
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
	
	protected RelativeLayout.LayoutParams newLayoutParams(android.view.ViewGroup.LayoutParams source) {
		return new RelativeLayout.LayoutParams(source);
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

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeUI#showActionDetailViewForResult(android.app.Activity, com.socialize.entity.User, com.socialize.entity.SocializeAction, int)
	 */
	@Override
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {
		Intent i = newIntent(context, ActionDetailActivity.class);
		i.putExtra(Socialize.USER_ID, user.getId().toString());
		i.putExtra(Socialize.ACTION_ID, action.getId().toString());
		
		try {
			
			// MUST be FLAG_ACTIVITY_SINGLE_TOP because we only code to onNewIntent
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
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
	public void showUserProfileView(Activity context, Long userId) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(Socialize.USER_ID, userId.toString());
		try {
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(Socialize.LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}
	}

	@Override
	public void showUserProfileViewForResult(Activity context, Long userId, int requestCode) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(Socialize.USER_ID, userId.toString());
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(Socialize.LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}	
	}

	@Override
	public boolean canShare(Context context, ShareType shareType) {
		return shareSystem.canShare(context, shareType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeUI#getDrawable(java.lang.String, boolean)
	 */
	@Override
	public Drawable getDrawable(String name, boolean eternal) {
		return (drawables != null) ? drawables.getDrawable(name, eternal) : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.SocializeUI#getDrawable(java.lang.String)
	 */
	@Override
	public Drawable getDrawable(String name) {
		return (drawables != null) ? drawables.getDrawable(name) : null; 
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

	protected void setAuthProviders(AuthProviders authProviders) {
		this.authProviders = authProviders;
	}

	protected void setActivitySystem(ActivitySystem activitySystem) {
		this.activitySystem = activitySystem;
	}

	protected void setEntitySystem(EntitySystem entitySystem) {
		this.entitySystem = entitySystem;
	}

	protected void setSubscriptionSystem(SubscriptionSystem subscriptionSystem) {
		this.subscriptionSystem = subscriptionSystem;
	}

	protected void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}
}
