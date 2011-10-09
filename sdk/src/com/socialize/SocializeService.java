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
import android.location.Location;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewAddListener;

/**
 * The main Socialize Service.  This is the simplest entry point into the Socialize API.
 * @author Jason Polites
 */
public interface SocializeService {

	/**
	 * Initializes a SocializeService instance with default settings.  Should be called during the onCreate() method of your Activity.
	 * @param context The current Android context (or Activity)
	 */
	public void init(Context context);

	/**
	 * Initializes a SocializeService instance with custom bean configurations (Expert use Only)
	 * @param context The current Android context (Activity)
	 * @param paths List of paths to config files.  Beans in paths to the right overwrite beans in paths to the left.
	 * @see https://github.com/socialize/android-ioc
	 */
	public void init(Context context, String... paths);
	
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
	 * @see https://github.com/socialize/android-ioc
	 */
	public void initAsync(Context context, SocializeInitListener listener, String... paths);

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
	 * Force destroy (Expert only)
	 * @param force
	 */
	public void destroy(boolean force);

	/**
	 * Authenticates the application against the API as an anonymous user.
	 * @param consumerKey The consumer url, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authListener The callback for authentication outcomes.
	 * @see this{@link #authenticate(String, String, AuthProviderType, String, SocializeAuthListener)}
	 */
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener);

	/**
	 * Authenticates the application against the API.
	 * @param consumerKey The consumer url, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authProvider The authentication provider.  Use AuthProviderType.SOCIALIZE for anonymous user auth.
	 * @param authProviderAppId The ID of your app in the 3rd party system used to authenticate. (e.g. YOUR Facebook App ID)
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderAppId, SocializeAuthListener authListener);
	
	/**
	 * @deprecated Too ambiguous.
	 * @use this{@link #authenticateKnownUser(String, String, AuthProviderType, String, String, String, SocializeAuthListener)}
	 */
	@Deprecated
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderId, String authUserId3rdParty, String authToken3rdParty, SocializeAuthListener authListener);
	
	/**
	 * Authenticates the application against the API as a user known to your app from a given 3rd party provider.
	 * @param consumerKey The consumer key, obtained from registration at http://www.getsocialize.com.
	 * @param consumerSecret The consumer secret, obtained from registration at http://www.getsocialize.com.
	 * @param authProvider The authentication provider.  Use AuthProviderType.SOCIALIZE for anonymous user auth.
	 * @param authProviderId The ID of your app in the 3rd party system used to authenticate. (e.g. YOUR Facebook App ID).
	 * @param authUserId3rdParty The userId from the 3rd party provider (if available).
	 * @param authToken3rdParty The auth token from the 3rd party (if available).
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticateKnownUser(String consumerKey, String consumerSecret, AuthProviderType authProvider, String authProviderId, String authUserId3rdParty, String authToken3rdParty, SocializeAuthListener authListener);
	
	/**
	 * Adds a new like and associates it with the url described.
	 * @param url The url being liked. MUST be a valid http URL.  Defined when first creating a url, or created on the fly with this call.
	 * @param likeAddListener A listener to handle callbacks from the post.
	 */
	public void like(String url, LikeAddListener likeAddListener);
	
	/**
	 * Adds a new like and associates it with the url described.
	 * @param url The url being liked. MUST be a valid http URL.  Defined when first creating a url, or created on the fly with this call.
	 * @param location The location of the device at the time the call was made.
	 * @param likeAddListener A listener to handle callbacks from the post.
	 */
	public void like(String url, Location location, LikeAddListener likeAddListener);
	
	/**
	 * Adds a new view and associates it with the url described.
	 * @param url The url being viewed. MUST be a valid http URL.  Defined when first creating a url, or created on the fly with this call.
	 * @param viewAddListener A listener to handle callbacks from the post.
	 */
	public void view(String url, ViewAddListener viewAddListener);
	
	/**
	 * Adds a new view and associates it with the url described.
	 * @param url The url being viewed. MUST be a valid http URL.  Defined when first creating a url, or created on the fly with this call.
	 * @param location The location of the device at the time the call was made.
	 * @param viewAddListener A listener to handle callbacks from the post.
	 */
	public void view(String url, Location location, ViewAddListener viewAddListener);

	/**
	 * Removes a specific LIKE based on it's unique ID.  The ID would be returned from the original creation call.
	 * @param id The ID of the like to be deleted.
	 * @param likeDeleteListener A listener to handle callbacks from the delete.
	 */
	public void unlike(int id, LikeDeleteListener likeDeleteListener);
	
	/**
	 * Retrieves a single like previously associated with an entity.
	 * @param url The url to which the like was originally associated. MUST be a valid http URL.
	 * @param likeGetListener
	 */
	public void getLike(String url, LikeGetListener likeGetListener);
	
	/**
	 * Lists the comments associated with a url.
	 * @param url The url to which the comments are associated. MUST be a valid http URL.
	 * @param commentListListener A listener to handle callbacks from the post.
	 */
	public void listCommentsByEntity(String url, CommentListListener commentListListener);
	
	/**
	 * Lists the comments associated with a url.
	 * @param url The url to which the comments are associated. MUST be a valid http URL.
	 * @param startIndex The starting index of the results for pagination.
	 * @param endIndex The ending index of the results for pagination.
	 * @param commentListListener A listener to handle callbacks from the post.
	 */
	public void listCommentsByEntity(String url, int startIndex, int endIndex, CommentListListener commentListListener);
	
	
	/**
	 * Retrieves a single comment based on its ID.
	 * @param id The ID of the comment, returned when it was originally created.
	 * @param commentGetListener A listener to handle callbacks from the get.
	 */
	public void getCommentById(int id, CommentGetListener commentGetListener);

	/**
	 * Retrieves a single entity.
	 * @param url The unique URL associated with this entity. MUST be a valid http URL.
	 * @param entityGetListener A listener to handle callbacks from the get.
	 */
	public void getEntity(String url, EntityGetListener entityGetListener);
	
	/**
	 * Creates a new entity.
	 * @param url The unique URL associated with this entity.  MUST be a valid HTTP url.
	 * @param name The name of the entity.
	 * @param entityAddListener A listener to handle callbacks from the post.
	 */
	public void addEntity(String url, String name, EntityAddListener entityAddListener);
	
	/**
	 * Adds a new comment and associates it with the url described.
	 * @param url The url to which the comment is associated. MUST be a valid http URL. Defined when first creating a url, or created on the fly with this call.
	 * @param comment The comment to add.
	 * @param location The location of the device at the time the call was made.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(String url, String comment, Location location, CommentAddListener commentAddListener);

	/**
	 * Adds a new comment and associates it with the url described.
	 * @param url The url to which the comment is associated. MUST be a valid http URL. Defined when first creating a url, or created on the fly with this call.
	 * @param comment The comment to add.
	 * @param commentAddListener A listener to handle callbacks from the post.
	 */
	public void addComment(String url, String comment, CommentAddListener commentAddListener);

	/**
	 * Retrieves a Socialize User based on their Socialize user ID.
	 * @param id The id of the user.
	 * @param userGetListener A listener to handle callbacks from the get.
	 */
	public void getUser(int id, UserGetListener userGetListener);
	
	/**
	 * Saves the profile for the current logged in user.
	 * @param firstName User first name.
	 * @param lastName User last name.
	 * @param encodedImage A Base64 encoded PNG file used for the user's profile picture.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void saveCurrentUserProfile(Context context, String firstName, String lastName, String encodedImage, UserSaveListener listener);
	
	/**
	 * Returns true if this SocializeService instance has been initialized.
	 * @return
	 * @deprecated Init should always be called so that each corresponding call to destroy is matched.
	 */
	@Deprecated
	public boolean isInitialized();

	/**
	 * Returns true if the current session is authenticated.
	 * @return
	 */
	public boolean isAuthenticated();
	
	/**
	 * Returns true if the current user is already authenticated using the provider specified.
	 * @param providerType
	 * @return
	 */
	public boolean isAuthenticated(AuthProviderType providerType);

	
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
	 * Clears the local cache of session data.  This will cause a full authenticate 
	 * to be required upon the next call to the Socialize API.
	 */
	public void clearSessionCache();
	
	/**
	 * Clears the session of the given 3rd party auth data (logs out from Facebook/Twitter etc).
	 * @param type
	 */
	public void clear3rdPartySession(AuthProviderType type);

}
