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
package com.socialize.test.unit;

import java.util.ArrayList;
import java.util.Collection;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.test.mock.MockContext;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeServiceImpl;
import com.socialize.SocializeServiceImpl.InitTask;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.activity.SocializeActivitySystem;
import com.socialize.api.action.comment.SocializeCommentSystem;
import com.socialize.api.action.comment.SocializeSubscriptionSystem;
import com.socialize.api.action.entity.SocializeEntitySystem;
import com.socialize.api.action.like.SocializeLikeSystem;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.api.action.user.SocializeUserSystem;
import com.socialize.api.action.user.UserSystem;
import com.socialize.api.action.view.SocializeViewSystem;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.init.SocializeInitializationAsserter;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.location.SocializeLocationProvider;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.notifications.NotificationChecker;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.PublicSocialize;
import com.socialize.test.PublicUserSystem;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarUtilsImpl;
import com.socialize.util.AppUtils;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.Drawables;
import com.socialize.util.EntityLoaderUtils;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ 
	IOCContainer.class, 
	SocializeSession.class, 
	SocializeLogger.class, 
	SocializeInitializationAsserter.class, 
	SocializeCommentSystem.class, 
	SocializeShareSystem.class, 
	SocializeLikeSystem.class, 
	SocializeEntitySystem.class, 
	SocializeViewSystem.class, 
	SocializeActivitySystem.class, 
	SocializeUserSystem.class, 
	SocializeSubscriptionSystem.class, 
	Drawables.class, 
	SocializeConfig.class, 
	SocializeProvider.class,
	AuthProviders.class,
	EntityLoaderUtils.class,
	AuthProviderInfoBuilder.class, 
	NotificationChecker.class, 
	AppUtils.class,
	SocializeLocationProvider.class})
public class SocializeServiceTest extends SocializeActivityTest {

	IOCContainer container;

	SocializeCommentSystem commentSystem;
	SocializeShareSystem shareSystem;
	SocializeLikeSystem likeSystem;
	SocializeEntitySystem entitySystem;
	SocializeViewSystem viewSystem;
	SocializeActivitySystem activitySystem;
	SocializeUserSystem userSystem;
	SocializeSubscriptionSystem subscriptionSystem;
	Drawables drawables;
	SocializeProvider<?> provider;
	AuthProviders authProviders;
	NotificationChecker notificationChecker;
	AppUtils appUtils;
	SocializeLocationProvider locationProvider;

	SocializeLogger logger;
	IBeanFactory<AuthProviderData> authProviderDataFactory;
	AuthProviderData authProviderData;
	SocializeSession session;
	SocializeAuthProviderInfo info;
	EntityLoaderUtils entityLoaderUtils;

	AuthProviderInfoBuilder authProviderInfoBuilder;

	SocializeConfig config;
	
	@SuppressWarnings("unchecked")
	private void setupMinimalMocks() {
		container = AndroidMock.createMock(IOCContainer.class);
		logger = AndroidMock.createNiceMock(SocializeLogger.class);
		authProviderDataFactory = (IBeanFactory<AuthProviderData>) AndroidMock.createMock(IBeanFactory.class);
		authProviderData = AndroidMock.createMock(AuthProviderData.class);
		session = AndroidMock.createMock(SocializeSession.class);
		provider = AndroidMock.createMock(SocializeProvider.class);
		info = AndroidMock.createMock(SocializeAuthProviderInfo.class);
		config = AndroidMock.createNiceMock(SocializeConfig.class);
		commentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		shareSystem = AndroidMock.createMock(SocializeShareSystem.class, provider);
		likeSystem = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		entitySystem = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		viewSystem = AndroidMock.createMock(SocializeViewSystem.class, provider);
		activitySystem = AndroidMock.createMock(SocializeActivitySystem.class, provider);
		userSystem = AndroidMock.createMock(SocializeUserSystem.class, provider);
		subscriptionSystem = AndroidMock.createMock(SocializeSubscriptionSystem.class, provider);
		drawables = AndroidMock.createMock(Drawables.class);
		authProviders = AndroidMock.createMock(AuthProviders.class);
		entityLoaderUtils = AndroidMock.createMock(EntityLoaderUtils.class);
		authProviderInfoBuilder = AndroidMock.createMock(AuthProviderInfoBuilder.class);
		notificationChecker = AndroidMock.createMock(NotificationChecker.class);
		appUtils = AndroidMock.createMock(AppUtils.class);
		locationProvider = AndroidMock.createMock(SocializeLocationProvider.class);
	}

	private void setupDefaultMocks() {
		setupMinimalMocks();

		AndroidMock.expect(container.getBean("shareSystem")).andReturn(shareSystem);
		AndroidMock.expect(container.getBean("userSystem")).andReturn(userSystem);
		AndroidMock.expect(container.getBean("config")).andReturn(config);
		AndroidMock.expect(container.getBean("authProviders")).andReturn(authProviders);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		AndroidMock.expect(container.getBean("initializationAsserter")).andReturn(null);
		AndroidMock.expect(container.getBean("entityLoaderUtils")).andReturn(entityLoaderUtils);
		AndroidMock.expect(container.getBean("authProviderInfoBuilder")).andReturn(authProviderInfoBuilder);
		AndroidMock.expect(container.getBean("notificationChecker")).andReturn(notificationChecker);
		AndroidMock.expect(container.getBean("appUtils")).andReturn(appUtils);
		AndroidMock.expect(container.getBean("locationProvider")).andReturn(locationProvider);
		
		AndroidMock.expect(entityLoaderUtils.initEntityLoader()).andReturn(null);
		
		appUtils.checkAndroidManifest(getContext());
		
		AndroidMock.expect(authProviderInfoBuilder.validateAll()).andReturn(true);
		
	}

	private void replayDefaultMocks() {

		AndroidMock.replay(config);
		AndroidMock.replay(commentSystem);
		AndroidMock.replay(shareSystem);
		AndroidMock.replay(likeSystem);
		AndroidMock.replay(entitySystem);
		AndroidMock.replay(viewSystem);
		AndroidMock.replay(activitySystem);
		AndroidMock.replay(userSystem);
		AndroidMock.replay(subscriptionSystem);
		AndroidMock.replay(provider);
		AndroidMock.replay(drawables);

		AndroidMock.replay(container);
		AndroidMock.replay(logger);
		AndroidMock.replay(authProviderDataFactory);
		AndroidMock.replay(authProviderData);
		AndroidMock.replay(authProviders);
		AndroidMock.replay(session);
		
		AndroidMock.replay(authProviderInfoBuilder);
		AndroidMock.replay(notificationChecker);
		AndroidMock.replay(entityLoaderUtils);
		
	}

	private void verifyDefaultMocks() {

		AndroidMock.verify(config);
		AndroidMock.verify(commentSystem);
		AndroidMock.verify(shareSystem);
		AndroidMock.verify(likeSystem);
		AndroidMock.verify(entitySystem);
		AndroidMock.verify(viewSystem);
		AndroidMock.verify(activitySystem);
		AndroidMock.verify(userSystem);
		AndroidMock.verify(subscriptionSystem);
		AndroidMock.verify(provider);
		AndroidMock.verify(drawables);

		AndroidMock.verify(container);
		AndroidMock.verify(logger);
		AndroidMock.verify(authProviderDataFactory);
		AndroidMock.verify(authProviderData);
		AndroidMock.verify(authProviders);
		AndroidMock.verify(session);
		
		AndroidMock.verify(authProviderInfoBuilder);
		AndroidMock.verify(notificationChecker);
		AndroidMock.verify(entityLoaderUtils);
	}

	public void testSocializeInitDestroy() {

		setupDefaultMocks();

		container.destroy();

		replayDefaultMocks();

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);

		assertTrue(socialize.isInitialized());

		socialize.destroy();

		assertFalse(socialize.isInitialized());

		verifyDefaultMocks();
	}

	public void testSocializeMultiInitDestroy() {

		setupDefaultMocks();

		container.destroy();

		replayDefaultMocks();

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.init(getContext(), container);
		socialize.init(getContext(), container);

		assertTrue(socialize.isInitialized());

		socialize.destroy();
		socialize.destroy();

		assertTrue(socialize.isInitialized());

		socialize.destroy();

		assertFalse(socialize.isInitialized());

		verifyDefaultMocks();
	}

	@UsesMocks({ IOCContainer.class, SocializeLogger.class })
	public void testInitFail() {
		container = AndroidMock.createMock(IOCContainer.class);
		logger = AndroidMock.createNiceMock(SocializeLogger.class);

		AndroidMock.expect(container.getBean((String) AndroidMock.anyObject())).andThrow(new RuntimeException("TEST ERROR. IGNORE ME!"));
		logger.error(AndroidMock.eq(SocializeLogger.INITIALIZE_FAILED), (Exception) AndroidMock.anyObject());

		AndroidMock.replay(container);
		AndroidMock.replay(logger);

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.setLogger(logger);
		socialize.init(new MockContext(), container);

		AndroidMock.verify(container);
		AndroidMock.verify(logger);
	}
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeSession.class, UserSystem.class, AuthProvider.class, UserProviderCredentials.class, AuthProviderInfo.class })
	public void testClear3rdPartySession() {
		PublicSocialize socializeService = new PublicSocialize();
		
		AuthProvider<AuthProviderInfo> mockAuthProvider = AndroidMock.createMock(AuthProvider.class);
		UserProviderCredentials userAuthData = AndroidMock.createMock(UserProviderCredentials.class);
		AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		
		setupMinimalMocks();
		
		AndroidMock.expect(authProviders.getProvider(AuthProviderType.FACEBOOK)).andReturn(mockAuthProvider);
		AndroidMock.expect(session.getUserProviderCredentials(AuthProviderType.FACEBOOK)).andReturn(userAuthData);
		AndroidMock.expect(userAuthData.getAuthProviderInfo()).andReturn(authProviderInfo);

		mockAuthProvider.clearCache(getContext(), authProviderInfo);
		session.clear(AuthProviderType.FACEBOOK);
		userSystem.clearSession(AuthProviderType.FACEBOOK);
		
		replayDefaultMocks();
		
		AndroidMock.replay(mockAuthProvider, userAuthData, authProviderInfo);
		
		socializeService.setSession(session);
		socializeService.setAuthProviders(authProviders);
		socializeService.setUserSystem(userSystem);
		
		socializeService.clear3rdPartySession(getContext(), AuthProviderType.FACEBOOK);
		
		AndroidMock.verify(mockAuthProvider, userAuthData, authProviderInfo);
		
		verifyDefaultMocks();
	}
	

	@UsesMocks({ SocializeAuthListener.class })
	public void testCheckKeys() {
		PublicUserSystem socializeService = new PublicUserSystem(null);
		SocializeAuthListener mockAuthListener = AndroidMock.createMock(SocializeAuthListener.class);
		mockAuthListener.onError((SocializeException) AndroidMock.anyObject());
		AndroidMock.replay(mockAuthListener);
		// this should test to make sure an error is passed back to the auth
		// listener
		socializeService.checkKeys("", "", mockAuthListener);
		AndroidMock.verify(mockAuthListener);
	}

	@UsesMocks({ SocializeAuthListener.class, IBeanFactory.class, AuthProviderData.class, SocializeAuthProviderInfo.class })
	public void testAuthenticate() throws SocializeException {
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);

		final String key = "foo", secret = "bar";

		setupDefaultMocks();

		SocializeServiceImpl socialize = new SocializeServiceImpl() ;

		userSystem.authenticate(TestUtils.getActivity(this), key, secret, listener, socialize);
		
		replayDefaultMocks();

		socialize.init(getContext(), container);

		assertTrue(socialize.isInitialized());

		socialize.authenticate(TestUtils.getActivity(this), key, secret, listener);

		verifyDefaultMocks();
	}

	public PublicSocialize newSocializeServiceForAuth() {
		return new PublicSocialize() {
			@Override
			public void logErrorMessage(String msg) {
				addResult(3, msg);
			}

			@Override
			public SocializeAuthProviderInfo newSocializeAuthProviderInfo() {
				return info;
			}
		};
	}

	public PublicSocialize newSocializeServiceForShare() {
		return new PublicSocialize() {
			@Override
			public boolean assertAuthenticated(SocializeListener listener) {
				addResult(0, listener);
				return true;
			}
		};
	}

	@UsesMocks({ UserSystem.class, SocializeSession.class })
	public void testAuthenticateSynchronous() throws Exception {
		PublicSocialize publicSocialize = newSocializeServiceForAuth();

		SocializeSession mockSession = AndroidMock.createMock(SocializeSession.class);
		UserSystem mockUserSystem = AndroidMock.createMock(UserSystem.class);
		
		AndroidMock.expect(mockUserSystem.authenticateSynchronous(getContext())).andReturn(mockSession);

		publicSocialize.setUserSystem(mockUserSystem);
		AndroidMock.replay(mockUserSystem, mockSession);
		publicSocialize.authenticateSynchronous(getContext());
		AndroidMock.verify(mockUserSystem, mockSession);
	}

	@UsesMocks({ SocializeAuthListener.class })
	public void testAuthenticateWithContextAndListener() {
		
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);

		setupDefaultMocks();

		SocializeServiceImpl socialize = new SocializeServiceImpl() ;

		userSystem.authenticate(TestUtils.getActivity(this), listener, socialize);
		
		replayDefaultMocks();

		socialize.init(getContext(), container);

		assertTrue(socialize.isInitialized());

		socialize.authenticate(TestUtils.getActivity(this), listener);

		verifyDefaultMocks();		
		
	}

	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeConfig.class, AuthProviderInfoBuilder.class, AuthProviderInfoFactory.class, AuthProviderInfo.class, SocializeAuthListener.class})
	public void testAuthenticateWithContextAndAuthType() {
		
		String consumerKey = "foo";
		String consumerSecret = "bar";
		AuthProviderType authProviderType = AuthProviderType.TWITTER;
		Context context = getContext();
		
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final AuthProviderInfoBuilder authProviderInfoBuilder = AndroidMock.createMock(AuthProviderInfoBuilder.class);
		final AuthProviderInfoFactory<AuthProviderInfo> authProviderInfoFactory = AndroidMock.createMock(AuthProviderInfoFactory.class);
		final AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		final SocializeAuthListener mockListener = AndroidMock.createMock(SocializeAuthListener.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(authProviderInfoBuilder.getFactory(authProviderType)).andReturn(authProviderInfoFactory);
		AndroidMock.expect(authProviderInfoFactory.getInstance()).andReturn(authProviderInfo);
		
		AndroidMock.replay(config, authProviderInfoBuilder, authProviderInfoFactory);
		
		PublicSocialize socializeService = new PublicSocialize() {
			@Override
			public SocializeConfig getConfig() {
				return config;
			}
			
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener) {
				addResult(0, context);
				addResult(1, consumerKey);
				addResult(2, consumerSecret);
				addResult(3, authProviderInfo);
				addResult(4, authListener);
			}
		};
		
		socializeService.setAuthProviderInfoBuilder(authProviderInfoBuilder);
		socializeService.authenticate(context, authProviderType, mockListener);
		
		AndroidMock.verify(config, authProviderInfoBuilder, authProviderInfoFactory);
		
		assertSame(context, getResult(0));
		assertSame(consumerKey, getResult(1));
		assertSame(consumerSecret, getResult(2));
		assertSame(authProviderInfo, getResult(3));
		assertSame(mockListener, getResult(4));
	}

	public void testAuthenticateWithExtraParamsCallAuthenticate() throws SocializeException {
		
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);

		final String key = "foo", secret = "bar";
		final String appId = "foobar";
		
		setupDefaultMocks();
		
		FacebookAuthProviderInfo fb = new FacebookAuthProviderInfo();
		fb.setAppId(appId);


		SocializeServiceImpl socialize = new SocializeServiceImpl() ;

		userSystem.authenticate(TestUtils.getActivity(this), key, secret, fb, listener, socialize);
		
		replayDefaultMocks();

		socialize.init(getContext(), container);

		assertTrue(socialize.isInitialized());

		socialize.authenticate(TestUtils.getActivity(this), key, secret, fb, listener);

		verifyDefaultMocks();			
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserProviderCredentials.class, AuthProviderInfo.class, AuthProviders.class, AuthProvider.class})
	public void testIsAuthenticatedWithProvider() {

		UserProviderCredentials data = AndroidMock.createMock(UserProviderCredentials.class);
		AuthProviderInfo info = AndroidMock.createMock(AuthProviderInfo.class);
		AuthProviders providers = AndroidMock.createMock(AuthProviders.class);
		AuthProvider<AuthProviderInfo> provider = AndroidMock.createMock(AuthProvider.class);
		
		session = AndroidMock.createMock(SocializeSession.class);

		AndroidMock.expect(session.getUserProviderCredentials(AuthProviderType.FACEBOOK)).andReturn(data);
		AndroidMock.expect(data.getAuthProviderInfo()).andReturn(info);
		AndroidMock.expect(providers.getProvider(AuthProviderType.FACEBOOK)).andReturn(provider);
		AndroidMock.expect(provider.validateForWrite(info, FacebookFacade.DEFAULT_PERMISSIONS)).andReturn(true);
		
		AndroidMock.replay(session, data, providers, provider);

		PublicSocialize socialize = new PublicSocialize() {
			@Override
			public boolean isAuthenticated() {
				return true;
			}
		};

		socialize.setSession(session);
		socialize.setAuthProviders(providers);

		assertTrue(socialize.isAuthenticated(AuthProviderType.FACEBOOK));

		AndroidMock.verify(session, data, providers, provider);
	}	

	@UsesMocks({ IOCContainer.class, SocializeConfig.class })
	public void testInitAndGetConfig() {

		setupDefaultMocks();

		replayDefaultMocks();

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);

		SocializeConfig gotten = socialize.getConfig();

		verifyDefaultMocks();

		assertSame(config, gotten);
	}

	@UsesMocks({AuthProvider.class, UserProviderCredentials.class, AuthProviderInfo.class})
	public void testClearSessionCache() {
		setupDefaultMocks();
		
		UserProviderCredentialsMap userAuthDataMap = AndroidMock.createMock(UserProviderCredentialsMap.class);
		UserProviderCredentials userAuthData = AndroidMock.createMock(UserProviderCredentials.class);
		AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		
		Collection<UserProviderCredentials> values = new ArrayList<UserProviderCredentials>();
		values.add(userAuthData);
		
		AuthProviderType type = AuthProviderType.FACEBOOK;
		
		AndroidMock.expect(session.getUserProviderCredentials()).andReturn(userAuthDataMap);
		AndroidMock.expect(userAuthDataMap.values()).andReturn(values);
		AndroidMock.expect(userAuthData.getAuthProviderInfo()).andReturn(authProviderInfo);
		AndroidMock.expect(authProviderInfo.getType()).andReturn(type);
		userSystem.clearSession();
		
		replayDefaultMocks();
		
		AndroidMock.replay(userAuthDataMap, userAuthData, authProviderInfo);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl() {
			@Override
			public void clear3rdPartySession(Context context, AuthProviderType type) {
				addResult(0, type);
			}
		};
		
		socialize.setSession(session);
		socialize.init(getContext(), container);
		
		socialize.clearSessionCache(getContext());

		verifyDefaultMocks();
		
		AndroidMock.verify(userAuthDataMap, userAuthData, authProviderInfo);
		
		AuthProviderType typeAfter = getResult(0);
		
		assertNotNull(typeAfter);
		assertSame(type, typeAfter);
		
	}

	@UsesMocks({ MockContext.class, SocializeInitListener.class, SocializeServiceImpl.class })
	public void testInitTaskDoInBackground() throws Exception {

		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		String[] paths = { "foo", "bar" };

		AndroidMock.expect(socialize.initWithContainer(context, null, paths)).andReturn(null);

		AndroidMock.replay(socialize);

		InitTask task = new InitTask(socialize, context, paths, listener, logger);

		assertTrue(AsyncTask.class.isAssignableFrom(task.getClass()));

		task.doInBackground();

		AndroidMock.verify(socialize);

	}

	@UsesMocks({ MockContext.class, SocializeInitListener.class, SocializeServiceImpl.class, IOCContainer.class })
	public void testInitTaskOnPostExecute() throws Exception {

		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		String[] paths = { "foo", "bar" };

		listener.onInit(context, container);

		AndroidMock.replay(listener);

		InitTask task = new InitTask(socialize, context, paths, listener, logger);

		task.onPostExecuteManaged(container);

		AndroidMock.verify(listener);

	}

	@UsesMocks({ MockContext.class, SocializeInitListener.class, SocializeServiceImpl.class, IOCContainer.class })
	public void testInitTaskOnPostExecuteWithError() throws Exception {

		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		SocializeException error = new SocializeException();
		String[] paths = { "foo", "bar" };

		AndroidMock.expect(socialize.initWithContainer(context, null, paths)).andThrow(error);

		listener.onError(error);

		AndroidMock.replay(socialize);
		AndroidMock.replay(listener);

		InitTask task = new InitTask(socialize, context, paths, listener, logger);

		task.doInBackground((Void[]) null);

		task.onPostExecuteManaged(null);

		AndroidMock.verify(socialize);
		AndroidMock.verify(listener);
	}

	@UsesMocks({ SocializeIOC.class, ResourceLocator.class, ClassLoaderProvider.class, SocializeLogger.class })
	public void testInitWithContainer() throws Exception {

		final SocializeIOC socializeIOC = AndroidMock.createMock(SocializeIOC.class);
		final ClassLoaderProvider classLoaderProvider = AndroidMock.createMock(ClassLoaderProvider.class);
		final ResourceLocator resourceLocator = AndroidMock.createMock(ResourceLocator.class);
		final SocializeLogger logger = new SocializeLogger();

		final Context context = new MockContext();

		final String[] mockPaths = { "foobar" };
		final String[] initPaths = {};

		SocializeServiceImpl service = new SocializeServiceImpl() {

			@Override
			public synchronized void init(Context context, IOCContainer container, SocializeInitListener listener) {
				addResult(0, context);
				addResult(1, container);
			}

			@Override
			public boolean isInitialized() {
				return true;
			}

			@Override
			protected SocializeIOC newSocializeIOC() {
				return socializeIOC;
			}

			@Override
			protected ResourceLocator newResourceLocator() {
				return resourceLocator;
			}

			@Override
			protected ClassLoaderProvider newClassLoaderProvider() {
				return classLoaderProvider;
			}

			@Override
			protected int binarySearch(String[] array, String str) {
				// Simulate not found
				addResult(2, "binarySearch_" + str);
				return -1;
			}

			@Override
			protected void sort(Object[] array) {
				addResult(3, "sort");
			}

			@Override
			protected SocializeLogger newLogger() {
				return logger;
			}

			@Override
			protected String[] getInitPaths() {
				return initPaths;
			}
		};

		resourceLocator.setClassLoaderProvider(classLoaderProvider);
		resourceLocator.setLogger(logger);

		socializeIOC.init(context, resourceLocator, mockPaths);
		socializeIOC.setContext(context);

		AndroidMock.replay(socializeIOC);
		AndroidMock.replay(resourceLocator);

		service.initWithContainer(context, mockPaths);

		AndroidMock.verify(socializeIOC);
		AndroidMock.verify(resourceLocator);

		Context foundContext = getResult(0);
		IOCContainer foundContainer = getResult(1);
		String binarySearch = getResult(2);
		String sort = getResult(3);

		assertNotNull(binarySearch);
		assertNotNull(sort);
		assertNotNull(foundContext);
		assertNotNull(foundContainer);

		assertEquals("binarySearch_foobar", binarySearch);
		assertEquals("sort", sort);
		assertSame(context, foundContext);
		assertSame(socializeIOC, foundContainer);
	}

	public void testSetSocializeCredentials() {
		PublicSocialize socializeUI = new PublicSocialize();

		String consumerKey = "foo";
		String consumerSecret = "bar";

		socializeUI.getConfig().setSocializeCredentials(consumerKey, consumerSecret);

		assertEquals(consumerKey, socializeUI.getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY));
		assertEquals(consumerSecret, socializeUI.getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
	}

	public ActionBarUtilsImpl newSocializeServiceForActionBarTests() {
		ActionBarUtilsImpl publicSocialize = new ActionBarUtilsImpl() {
			@Override
			public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
				addResult(0, parent);
				addResult(1, original);
				addResult(2, entity);
				addResult(3, options);
				addResult(4, listener);
				return null;
			}

			@Override
			public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
				addResult(0, parent);
				addResult(1, resId);
				addResult(2, entity);
				addResult(3, options);
				addResult(4, listener);
				return null;
			}
		};

		return publicSocialize;
	}

	@UsesMocks ({ActionBarOptions.class})
	public void testShowActionBarWithParentResourceEntityOptions() {
		Activity mockParent = AndroidMock.createMock(Activity.class);
		Entity mockEntity = AndroidMock.createMock(Entity.class);
		ActionBarOptions options = AndroidMock.createMock(ActionBarOptions.class);
		ActionBarUtilsImpl publicSocialize = newSocializeServiceForActionBarTests();
		publicSocialize.showActionBar(mockParent, 1, mockEntity, options);

		assertEquals(mockParent, getResult(0));
		assertEquals(1, getResult(1));
		assertEquals(mockEntity, getResult(2));
		assertEquals(options, getResult(3));
		assertNull(getResult(4));
	}

	@UsesMocks({ Activity.class, Entity.class })
	public void testShowActionBarWithParentAndResoureAndEntity() {
		Activity mockParent = AndroidMock.createMock(Activity.class);
		Entity mockEntity = AndroidMock.createMock(Entity.class);
		int mockViewId = 3;

		ActionBarUtilsImpl publicSocialize = newSocializeServiceForActionBarTests();

		publicSocialize.showActionBar(mockParent, mockViewId, mockEntity);

		assertEquals(mockParent, getResult(0));
		assertEquals(mockViewId, getResult(1));
		assertEquals(mockEntity, getResult(2));
		assertNull(getResult(3));
		assertNull(getResult(4));
	}

	@UsesMocks ({ActionBarOptions.class})
	public void testShowActionBarWithOptions() {
		Activity mockParent = AndroidMock.createMock(Activity.class);
		View mockView = AndroidMock.createMock(View.class, getContext());
		Entity mockEntity = AndroidMock.createMock(Entity.class);
		ActionBarOptions mockOptions = AndroidMock.createMock(ActionBarOptions.class);

		ActionBarUtilsImpl publicSocialize = newSocializeServiceForActionBarTests();
		publicSocialize.showActionBar(mockParent, mockView, mockEntity, mockOptions);

		assertEquals(mockParent, getResult(0));
		assertEquals(mockView, getResult(1));
		assertEquals(mockEntity, getResult(2));
		assertEquals(mockOptions, getResult(3));
		assertNull(getResult(4));
	}

	@UsesMocks({ Activity.class, View.class, Entity.class })
	public void testShowActionBarWithParentAndOriginalAndEntity() {
		Activity mockParent = AndroidMock.createMock(Activity.class);
		View mockView = AndroidMock.createMock(View.class, getContext());
		Entity mockEntity = AndroidMock.createMock(Entity.class);

		ActionBarUtilsImpl publicSocialize = newSocializeServiceForActionBarTests();
		publicSocialize.showActionBar(mockParent, mockView, mockEntity);

		assertEquals(mockParent, getResult(0));
		assertEquals(mockView, getResult(1));
		assertEquals(mockEntity, getResult(2));
		assertNull(getResult(3));
		assertNull(getResult(4));
	}

	@Deprecated
	public void testSetFacebookUserCredentials() {
		PublicSocialize socializeUI = new PublicSocialize();

		String userId = "foo";
		String token = "bar";

		socializeUI.getConfig().setFacebookUserCredentials(userId, token);

		assertEquals(userId, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_USER_ID));
		assertEquals(token, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_USER_TOKEN));
	}

	public void testSetFacebookAppId() {
		PublicSocialize socializeUI = new PublicSocialize();

		String appId = "foobar";

		socializeUI.getConfig().setFacebookAppId(appId);

		assertEquals(appId, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID));
	}
};
