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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
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
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionListListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.listener.view.ViewGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.notifications.NotificationType;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.profile.UserProfile;

/**
 * The main Socialize Service.  This is the simplest entry point into the Socialize API.
 * @author Jason Polites
 */
public interface SocializeService extends SocializeUI {

	/**
	 * Initializes a SocializeService instance with default settings.  Should be called during the onCreate() method of your Activity.
	 * @param context The current Android context (or Activity)
	 */
	public IOCContainer init(Context context);

	/**
	 * Initializes a SocializeService instance with custom bean configurations (Expert use Only)
	 * @param context The current Android context (Activity)
	 * @param paths List of paths to config files.  Beans in paths to the right overwrite beans in paths to the left.
	 * @see "https://github.com/socialize/android-ioc"
	 */
	public IOCContainer init(Context context, String... paths);
	
	/**
	 * Initializes a SocializeService instance asynchronously with default settings.  Should be called during the onCreate() method of your Activity.
	 * @param context The current Android context (Activity)
	 * @param listener A listener to handle callbacks from the init.  Any access to Socialize objects must be done AFTER successful init.
	 */
	public void initAsync(Context context, SocializeInitListener listener);

	/**
	 * Initializes a SocializeService instance with custom bean configurations (Expert use Only)
	 * @param context The current Android context (or Activity)
	 * @param listener A listener to handle callbacks from the init.  Any access to Socialize objects must be done AFTER successful init.
	 * @param paths List of paths to config files.  Beans in paths to the right overwrite beans in paths to the left.
	 * @see "https://github.com/socialize/android-ioc"
	 */
	public void initAsync(Context context, SocializeInitListener listener, String... paths);

	/**
	 * Initializes a socialize service with a custom object container (Expert use only)
	 * @param context The current Android context (or Activity)
	 * @param container A reference to an IOC container
	 * @see "https://github.com/socialize/android-ioc"
	 */
	public void init(Context context, final IOCContainer container);

	/**
	 * Destroys the SocializeService instance.  Should be called during the onDestroy() method of your Activity.
	 */
	public void destroy();
	
	/**
	 * Force destroy (Expert only)
	 * @param force
	 */
	public void destroy(boolean force);
	
	/**
	 * Authenticates the application against the API as an anonymous user synchronously.
	 * NOTE:  This assumes the consumer key/secret have been specified in assets/socialize.properties
	 * @param context The current context.
	 */
	public SocializeSession authenticateSynchronous(Context context);
	
	/**
	 * Authenticates the application against the API as an anonymous user.
	 * NOTE:  This assumes the consumer key/secret have been specified in assets/socialize.properties
	 * @param context The current context.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticate(Context context, SocializeAuthListener authListener);
	
	/**
	 * Authenticates the application against the API as an anonymous user.
	 * @param context The current context.
	 * @param consumerKey The consumer key, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener authListener);

	/**
	 * Authenticates the application against the API.
	 * NOTE:  This assumes the consumer key/secret have been specified in assets/socialize.properties
	 * @param context The current context.
	 * @param authProvider The authentication provider.  Use AuthProviderType.SOCIALIZE for anonymous user auth.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticate(Context context, AuthProviderType authProvider, SocializeAuthListener authListener);
	
	/**
	 * Authenticates the application against the API.
	 * @param context The current context.
	 * @param consumerKey The consumer key, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authProviderInfo Information about the auth provider to be used.  May be null.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener);
	
	/**
	 * Authenticates the application against the API as a user known to your app from a given 3rd party provider.
	 * @param context The current context.
	 * @param consumerKey The consumer key, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authProviderInfo Information about the auth provider to be used. 
	 * @param userProviderCredentials Information about the user being authed.
	 * @param authListener The callback for authentication outcomes.
	 */
	@Deprecated
	public void authenticateKnownUser(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, UserProviderCredentials userProviderCredentials, SocializeAuthListener authListener);

	/**
	 * Authenticates the application against the API as a user known to your app from a given 3rd party provider.
	 * @param context The current context.
	 * @param userProviderCredentials Information about the user being authed.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticateKnownUser(Context context, UserProviderCredentials userProviderCredentials, SocializeAuthListener authListener);

	
	/**
	 * Adds a new like and associates it with the entity described.
	 * @param activity The current Activity.
	 * @param entity The entity being liked.
	 * @param likeAddListener A listener to handle callbacks from the post.
	 */
	public void like(Activity activity, Entity entity, LikeAddListener likeAddListener);
	
	/**
	 * Adds a new like and associates it with the entity described.
	 * @param activity The current Activity.
	 * @param entity The entity being liked.
	 * @param shareOptions Options for posting to external networks.
	 * @param likeAddListener A listener to handle callbacks from the post.
	 */
	public void like(Activity activity, Entity entity, ShareOptions shareOptions, LikeAddListener likeAddListener);
	
	/**
	 * Removes a specific LIKE based on it's unique ID.  The ID would be returned from the original creation call.
	 * @param id The ID of the like to be deleted.
	 * @param likeDeleteListener A listener to handle callbacks from the delete.
	 */
	public void unlike(long id, LikeDeleteListener likeDeleteListener);
	
	/**
	 * Adds a new view and associates it with the key described.
	 * @param activity The current Activity.
	 * @param entity The entity being viewed.
	 * @param viewAddListener A listener to handle callbacks from the post.
	 */
	public void view(Activity activity, Entity entity, ViewAddListener viewAddListener);	
	
	/**
	 * Adds a new view and associates it with the key described.
	 * @param activity The current Activity.
	 * @param entity The entity being viewed.
	 * @param location The location of the device at the time the call was made.
	 * @param viewAddListener A listener to handle callbacks from the post.
	 */
	public void view(Activity activity, Entity entity, Location location, ViewAddListener viewAddListener);	
	
	/**
	 * Retrieves a single view.
	 * @param id The ID of the view
	 * @param viewGetListener A listener to handle callbacks from the get.
	 */
	public void getViewById(long id, ViewGetListener viewGetListener);
	
	/**
	 * Records a share event against the given key. 
	 * @param activity The current Activity.
	 * @param entity The entity being shared.
	 * @param text The text being shared.
	 * @param options Options for the share
	 * @param shareAddListener A listener to handle callbacks from the post.
	 */
	public void share(Activity activity, Entity entity, String text, ShareOptions options, ShareAddListener shareAddListener);
	
	/**
	 * Records a share event against the given key. 
	 * @param activity The current Activity.
	 * @param entity The entity being shared.
	 * @param text The text being shared.
	 * @param shareType The type of the share.
	 * @param location The location (may be null)
	 * @param shareAddListener A listener to handle callbacks from the post.
	 */
	public void share(Activity activity, Entity entity, String text, ShareType shareType, Location location, ShareAddListener shareAddListener);
	
	/**
	 * Records a share event against the given key. 
	 * @param activity The current Activity.
	 * @param entity The entity being shared.
	 * @param text The text being shared.
	 * @param shareType The type of the share.
	 * @param shareAddListener A listener to handle callbacks from the post.
	 */
	public void share(Activity activity, Entity entity, String text, ShareType shareType, ShareAddListener shareAddListener);
	
	/**
	 * Records a share event against the given key.  
	 * NOTE: This does NOT perform sharing to any 3rd party social network.  
	 * It simply records a share event within Socialize.
	 * @param activity The current Activity.
	 * @param entity The entity being shared.
	 * @param text The share text provided by the user.
	 * @param shareType  The social network on which the share occurred. 
	 * @param location
	 * @param shareAddListener A listener to handle callbacks from the post.
	 */
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, Location location, ShareAddListener shareAddListener);
	
	/**
	 * Records a share event against the given key.  
	 * NOTE: This does NOT perform sharing to any 3rd party social network.  
	 * It simply records a share event within Socialize.
	 * @param activity The current Activity.
	 * @param entity The entity being shared.
	 * @param text The share text provided by the user.
	 * @param shareType  The social network on which the share occurred. 
	 * @param shareAddListener A listener to handle callbacks from the post.
	 */
	public void addShare(Activity activity, Entity entity, String text, ShareType shareType, ShareAddListener shareAddListener);
	
	/**
	 * Retrieves a single like previously associated with an entity.
	 * @param key The entity to which the like was originally associated. 
	 * @param likeGetListener
	 */
	public void getLike(String key, LikeGetListener likeGetListener);
	
	/**
	 * Retrieves a single like.
	 * @param id The ID of the like
	 * @param likeGetListener A listener to handle callbacks from the get.
	 */
	public void getLikeById(long id, LikeGetListener likeGetListener);
	
	/**
	 * Lists all the likes associated with the given ids.
	 * @param likeListListener A listener to handle callbacks from the get.
	 * @param ids
	 */
	public void listLikesById(LikeListListener likeListListener, long...ids);
	
	/**
	 * Lists the likes associated with a single user.
	 * @param userId The user
	 * @param likeListListener A listener to handle callbacks from the get.
	 */
	public void listLikesByUser(long userId, LikeListListener likeListListener);
	
	/**
	 * Lists the likes associated with a single user.
	 * @param userId The user
	 * @param startIndex The starting index of the results for pagination.
	 * @param endIndex The ending index of the results for pagination.
	 * @param likeListListener A listener to handle callbacks from the get.
	 */
	public void listLikesByUser(long userId, int startIndex, int endIndex, LikeListListener likeListListener);	
	
	/**
	 * Lists the comments associated with a key.
	 * @param key The entity to which the comments are associated. 
	 * @param commentListListener A listener to handle callbacks from the post.
	 */
	public void listCommentsByEntity(String key, CommentListListener commentListListener);
	
	/**
	 * Lists the comments associated with a key.
	 * @param key The entity to which the comments are associated. 
	 * @param startIndex The starting index of the results for pagination.
	 * @param endIndex The ending index of the results for pagination.
	 * @param commentListListener A listener to handle callbacks from the get.
	 */
	public void listCommentsByEntity(String key, int startIndex, int endIndex, CommentListListener commentListListener);
	
	/**
	 * Lists the comments associated with a single user.
	 * @param userId The user
	 * @param commentListListener A listener to handle callbacks from the get.
	 */
	public void listCommentsByUser(long userId, CommentListListener commentListListener);
	
	/**
	 * Lists the comments associated with a single user.
	 * @param userId The user
	 * @param startIndex The starting index of the results for pagination.
	 * @param endIndex The ending index of the results for pagination.
	 * @param commentListListener A listener to handle callbacks from the get.
	 */
	public void listCommentsByUser(long userId, int startIndex, int endIndex, CommentListListener commentListListener);
	
	/**
	 * Lists the comments by comment ID.
	 * @param commentListListener A listener to handle callbacks from the post.
	 * @param ids Array of IDs corresponding to pre-existing comments.
	 */
	public void listCommentsById(CommentListListener commentListListener, long...ids);
	
	/**
	 * Retrieves a single comment based on its ID.
	 * @param id The ID of the comment, returned when it was originally created.
	 * @param commentGetListener A listener to handle callbacks from the get.
	 */
	public void getCommentById(long id, CommentGetListener commentGetListener);

	/**
	 * Retrieves a single entity.
	 * @param key The unique key associated with this entity. 
	 * @param entityGetListener A listener to handle callbacks from the get.
	 */
	public void getEntity(String key, EntityGetListener entityGetListener);
	
	/**
	 * Retrieves a single entity based on its ID.
	 * @param id The ID of the entity.
	 * @param entityGetListener A listener to handle callbacks from the get.
	 */
	public void getEntityById(long id, EntityGetListener entityGetListener);
	
	/**
	 * Lists entities matching the given keys.
	 * @param entityListListener A listener to handle callbacks from the post.
	 * @param keys Array of keys corresponding to the entities to return, or null to return all.
	 */
	public void listEntitiesByKey(EntityListListener entityListListener, String...keys);
	
	/**
	 * Creates a new entity.
	 * @param activity The current activity.
	 * @param entity The entity to create.
	 * @param entityAddListener A listener to handle callbacks from the post.
	 */
	public void addEntity(Activity activity, Entity entity, EntityAddListener entityAddListener);
	
	/**
	 * Adds a new comment and associates it with the key described.
	 * @param activity The current activity.
	 * @param comment The comment object.  MUST contain an Entity object.
	 * @param location The location of the device at the time the call was made.
	 * @param shareOptions Options for sharing to facebook and sharing location.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(Activity activity, Comment comment, ShareOptions shareOptions, CommentAddListener commentAddListener);	
	
	/**
	 * Adds a new comment and associates it with the key described.
	 * @param activity The current activity.
	 * @param entity The entity to which the comment is associated. Defined when first creating an entity.
	 * @param comment The comment to add.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(Activity activity, Entity entity, String comment, CommentAddListener commentAddListener);
	
	/**
	 * Adds a new comment and associates it with the key described.
	 * @param activity The current activity.
	 * @param entity The entity to which the comment is associated. Defined when first creating an entity.
	 * @param comment The comment to add.	
	 * @param shareOptions Options for sharing to facebook and sharing location.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(Activity activity, Entity entity, String comment, ShareOptions shareOptions, CommentAddListener commentAddListener);	

	/**
	 * Retrieves a Socialize User based on their Socialize user ID.
	 * @param id The id of the user.
	 * @param userGetListener A listener to handle callbacks from the get.
	 */
	public void getUser(long id, UserGetListener userGetListener);
	
	/**
	 * Saves the profile for the current logged in user.
	 * @param context The current context.
	 * @param profile The profile for the user.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void saveCurrentUserProfile(Context context, UserProfile profile, UserSaveListener listener);
	
	/**
	 * Returns true if this SocializeService instance has been initialized.  
	 * PLEASE NOTE: Init should always be called so that each corresponding call to destroy is matched.
	 * @return true if this SocializeService instance has been initialized.  
	 */
	public boolean isInitialized();

	/**
	 * Returns true if the current session is authenticated.
	 * @return true if the current session is authenticated.
	 */
	public boolean isAuthenticated();
	
	/**
	 * Returns true if the current user is already authenticated using the provider specified.
	 * @param providerType
	 * @return true if the current user is already authenticated using the provider specified.
	 */
	public boolean isAuthenticated(AuthProviderType providerType);
	
	/**
	 * Returns a reference to the current session.
	 * @return The current session.
	 */
	public SocializeSession getSession();

	/**
	 * Returns a reference to the config being used.
	 * <br/>
	 * This can be modified BEFORE calling init() if alternate config is required.
	 * @return The current config object.
	 */
	public SocializeConfig getConfig();
	
	/**
	 * Clears the local cache of session data.  This will cause a full authenticate 
	 * to be required upon the next call to the Socialize API.
	 */
	public void clearSessionCache(Context context);
	
	/**
	 * Clears the session of the given 3rd party auth data (logs out from Facebook/Twitter etc).
	 * @param type
	 */
	public void clear3rdPartySession(Context context, AuthProviderType type);
	
	/**
	 * Saves the current session to disk.
	 * @param context
	 */
	public void saveSession(Context context);
	
	/**
	 * Lists a user's activity.
	 * @param userId The ID of the user for whom activity will be listed.
	 * @param activityListListener A listener to handle callbacks from the get.
	 */
	public void listActivityByUser(long userId, UserActivityListListener activityListListener);
	
	/**
	 * Lists a user's activity with pagination.
	 * @param userId The ID of the user for whom activity will be listed.
	 * @param startIndex The starting index of the results for pagination.
	 * @param endIndex The ending index of the results for pagination.
	 * @param activityListListener A listener to handle callbacks from the get.
	 */
	public void listActivityByUser(long userId, int startIndex, int endIndex, UserActivityListListener activityListListener);
	
	/**
	 * Subscribes the currently logged in user to notifications for the given entity.
	 * @param context
	 * @param entity
	 * @param subscriptionAddListener
	 */
	public void subscribe(Context context, Entity entity, NotificationType type, SubscriptionResultListener subscriptionAddListener);
	
	/**
	 * Unsubscribes the currently logged in user from notifications for the given entity.
	 * (The listener is an "add" listener because the subscription object is actually just updated)
	 * @param context
	 * @param entity
	 * @param subscriptionAddListener
	 */
	public void unsubscribe(Context context, Entity entity, NotificationType type, SubscriptionResultListener subscriptionAddListener);	
	
	/**
	 * Lists subscriptions for the current user.
	 * @param subscriptionListListener
	 */
	public void listSubscriptions(SubscriptionListListener subscriptionListListener);
	
	/**
	 * Lists subscriptions for the current user with pagination
	 * @param subscriptionListListener
	 */
	public void listSubscriptions(int startIndex, int endIndex, SubscriptionListListener subscriptionListListener);	

	/**
	 * Retrieves a current subscription for the user if available.
	 * @param entity
	 * @param subscriptionGetListener
	 */
	public void getSubscription(Entity entity, SubscriptionGetListener subscriptionGetListener);
	
	/**
	 * Returns true if the given provider type is supported and has been configured correctly.
	 * @param type
	 * @return
	 */
	public boolean isSupported(AuthProviderType type);
	
	/**
	 * Sets the entity loader object which allows Socialize to open entities when a user clicks on a user activity item.
	 * @param entityLoader
	 */
	public void setEntityLoader(SocializeEntityLoader entityLoader);
	
	/**
	 * Handles broadcast messages.  Used for push notifications.
	 * @param context
	 * @param intent
	 * @return True if the broadcast message was handled by Socialize.  False if it is a simple registration request or it's not a message for Socialize.
	 */
	public boolean handleBroadcastIntent(Context context, Intent intent);
	
	/**
	 * Returns true if sharing to the given share type is supported on this device.
	 * @param shareType
	 * @return
	 */
	public boolean canShare(Context context, ShareType shareType);
	
	/**
	 * Returns the entity loader set in setEntityLoader.
	 * @return
	 */
	public SocializeEntityLoader getEntityLoader();
	
	/**
	 * Returns the internal system config for Socialize. (Expert Only)
	 * @return
	 */
	public SocializeSystem getSystem();
	
	/**
	 * Returns the logger used by Socialize.
	 * @return
	 */
	public SocializeLogger getLogger();
	
	/**
	 * Should be called in the onPause method of the containing activity
	 */
	public void onPause(Context context);
	
	/**
	 * Should be called in the onResume method of the containing activity
	 */
	public void onResume(Context context);
}
