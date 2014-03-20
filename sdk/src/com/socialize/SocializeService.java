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
package com.socialize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeEntityLoader;

/**
 * The main Socialize Service.  This is the simplest entry point into the Socialize API.
 * @author Jason Polites
 */
public interface SocializeService extends SocializeSessionConsumer {

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
	 * Destroys the SocializeService instance.
	 * Should NOT be called in general use.
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
	public SocializeSession authenticateSynchronous(Context context) throws SocializeException;
	
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
	 * @param permissions One or more custom permissions used by the call.  (Currently only used by FACEBOOK)
	 */
	@Deprecated
	public void authenticate(Context context, AuthProviderType authProvider, SocializeAuthListener authListener, String...permissions);
	
	/**
	 * Authenticates the application against the API.
	 * NOTE:  This assumes the consumer key/secret have been specified in assets/socialize.properties
	 * @param context The current context.
	 * @param authProvider The authentication provider.  Use AuthProviderType.SOCIALIZE for anonymous user auth.
	 * @param authListener The callback for authentication outcomes.
	 * @param permissions One or more custom permissions used by the call.  (Currently only used by FACEBOOK)
	 */
	public void authenticateForRead(Context context, AuthProviderType authProvider, SocializeAuthListener authListener, String...permissions);
	
	/**
	 * Authenticates the application against the API.
	 * NOTE:  This assumes the consumer key/secret have been specified in assets/socialize.properties
	 * @param context The current context.
	 * @param authProvider The authentication provider.  Use AuthProviderType.SOCIALIZE for anonymous user auth.
	 * @param authListener The callback for authentication outcomes.
	 * @param permissions One or more custom permissions used by the call.  (Currently only used by FACEBOOK)
	 */
	public void authenticateForWrite(Context context, AuthProviderType authProvider, SocializeAuthListener authListener, String...permissions);
	
	
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
	 * @param userProviderCredentials Information about the user being authed.
	 * @param authListener The callback for authentication outcomes.
	 */
	public void authenticateKnownUser(Context context, UserProviderCredentials userProviderCredentials, SocializeAuthListener authListener);
	
	/**
	 * Returns true if this SocializeService instance has been initialized.  
	 * @param context The current context.
	 * @return true if this SocializeService instance has been initialized.  
	 */
	public boolean isInitialized(Context context);

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
	@Deprecated
	public boolean isAuthenticated(AuthProviderType providerType);
	
	/**
	 * Returns true if the current user is already authenticated using the provider specified for READ operations.
	 * @param providerType
	 * @return
	 */
	public boolean isAuthenticatedForRead(AuthProviderType providerType, String...permissions);
	
	/**
	 * Returns true if the current user is already authenticated using the provider specified for WRITE operations.
	 * @param providerType
	 * @return
	 */
	public boolean isAuthenticatedForWrite(AuthProviderType providerType, String...permissions);	
	
	/**
	 * Returns a reference to the current session.
	 * @return The current session.
	 */
	public SocializeSession getSession();

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
	 * Returns true if the given provider type is supported and has been configured correctly.
	 * @param context TODO
	 * @param type
	 * @return True if the given provider type is supported and has been configured correctly.
	 */
	public boolean isSupported(Context context, AuthProviderType type);
	
	/**
	 * Returns true if Socialize is supported on this device.
	 * @param context
	 * @throws SocializeException if Socialize it NOT supported
	 */
	public void isSocializeSupported(Context context) throws SocializeException;
	
	/**
	 * Sets the entity loader object which allows Socialize to open entities when a user clicks on a user activity item.
	 * @param entityLoader
	 */
	public void setEntityLoader(SocializeEntityLoader entityLoader);

	public void setUserSettingsActivity(Class<?> activityClass);

	public Class<?> getUserSettingsActivity();
	
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
	 * @return True if sharing to the given share type is supported on this device.
	 */
	public boolean canShare(Context context, ShareType shareType);
	
	/**
	 * Returns the entity loader set in setEntityLoader.
	 * @return The entity loader set in setEntityLoader.
	 */
	public SocializeEntityLoader getEntityLoader();
	
	/**
	 * Returns the internal system config for Socialize. (Expert Only)
	 * @return The internal system config for Socialize. (Expert Only)
	 */
	public SocializeSystem getSystem();
	
	/**
	 * Returns the logger used by Socialize.
	 * @return The logger used by Socialize.
	 */
	public SocializeLogger getLogger();
	
	/**
	 * Should be called in the onPause method of the containing activity
	 */
	public void onPause(Activity context);
	
	/**
	 * Should be called in the onResume method of the containing activity
	 */
	public void onResume(Activity context);
	
	/**
	 * Should be called in the onCreate method of the containing activity
	 */
	public void onCreate(Activity context, Bundle savedInstanceState);

	/**
	 * Should be called in the onDestroy method of the containing activity
	 */
	public void onDestroy(Activity context);

    /**
     * Should be called in the onStart method of the containing activity
     */
    public void onStart(Activity context);

    /**
     * Should be called in the onStop method of the containing activity
     */
    public void onStop(Activity context);
}
