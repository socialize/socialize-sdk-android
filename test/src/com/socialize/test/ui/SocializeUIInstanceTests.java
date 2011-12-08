package com.socialize.test.ui;

import java.util.Properties;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.mock.MockContext;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.SocializeServiceImpl;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.sample.ui.SampleSocializeActivity;
import com.socialize.sample.ui.TestActivityCallback;
import com.socialize.sample.ui.TestActivityCallbackHolder;
import com.socialize.test.mock.MockRelativeLayoutParams;
import com.socialize.test.mock.TestUIFactory;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeUIBeanOverrider;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.profile.ProfileActivity;
import com.socialize.util.Drawables;

@UsesMocks ({Intent.class, Activity.class})
public class SocializeUIInstanceTests extends SocializeUIActivityTest {

	public void testSetSocializeCredentials() {
		SocializeUI socializeUI = new SocializeUI();

		String consumerKey = "foo";
		String consumerSecret = "bar";

		socializeUI.setSocializeCredentials(consumerKey, consumerSecret);

		assertEquals(consumerKey, socializeUI.getCustomProperties().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY));
		assertEquals(consumerSecret, socializeUI.getCustomProperties().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
	}

	public void testSetFacebookUserCredentials() {
		SocializeUI socializeUI = new SocializeUI();

		String userId = "foo";
		String token = "bar";

		socializeUI.setFacebookUserCredentials(userId, token);

		assertEquals(userId, socializeUI.getCustomProperties().getProperty(SocializeConfig.FACEBOOK_USER_ID));
		assertEquals(token, socializeUI.getCustomProperties().getProperty(SocializeConfig.FACEBOOK_USER_TOKEN));
	}

	public void testSetFacebookAppId() {
		SocializeUI socializeUI = new SocializeUI();

		String appId = "foobar";

		socializeUI.setFacebookAppId(appId);

		assertEquals(appId, socializeUI.getCustomProperties().getProperty(SocializeConfig.FACEBOOK_APP_ID));
	}

	public void testSetDebugMode() {
		SocializeUI socializeUI = new SocializeUI();

		boolean debug = true;

		socializeUI.setDebugMode(debug);

		assertEquals(String.valueOf(debug), socializeUI.getCustomProperties().getProperty(SocializeConfig.SOCIALIZE_DEBUG_MODE));
	}

	@UsesMocks({Intent.class, Activity.class})
	public void testSetEntityUrl() {
		Activity activity = AndroidMock.createMock(Activity.class);
		Intent intent = AndroidMock.createMock(Intent.class);

		Bundle extras = new Bundle();
		final String url = "foobar";

		AndroidMock.expect(activity.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.getExtras()).andReturn(extras);
		AndroidMock.expect(intent.putExtras(extras)).andReturn(intent);

		AndroidMock.replay(activity);
		AndroidMock.replay(intent);

		SocializeUI socializeUI = new SocializeUI();
		socializeUI.setEntityKey(activity, url);

		AndroidMock.verify(activity);
		AndroidMock.verify(intent);

		assertNotNull(extras.getString(SocializeUI.ENTITY_KEY));
		assertEquals(url, extras.getString(SocializeUI.ENTITY_KEY));

	}

	@UsesMocks ({SocializeService.class, SocializeConfig.class})
	public void testInitSocialize() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);

		MockContext context = new MockContext();
		SocializeUI socializeUI = new SocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};

		final String[] paths = new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"};

		socialize.init(context, paths);
		AndroidMock.expect(socialize.getConfig()).andReturn(config);
		config.merge(socializeUI.getCustomProperties());

		AndroidMock.replay(socialize);

		socializeUI.initSocialize(context);

		AndroidMock.verify(socialize);
	}

	@UsesMocks ({IOCContainer.class})
	public void testInitUI() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);

		AndroidMock.expect(container.getBean("drawables")).andReturn(null);

		AndroidMock.replay(container);

		SocializeUI socializeUI = new SocializeUI();
		socializeUI.initUI(container);

		AndroidMock.verify(container);
	}

	@UsesMocks ({SocializeService.class})
	public void testDestroy() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		MockContext context = new MockContext();

		SocializeUI socializeUI = new SocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};

		socialize.destroy(false);

		AndroidMock.replay(socialize);

		socializeUI.destroy(context);

		AndroidMock.verify(socialize);
	}

	@UsesMocks ({IOCContainer.class})
	public void testGetView() {
		final String name = "foobar";
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);

		AndroidMock.expect(container.getBean(name)).andReturn(null);

		AndroidMock.replay(container);

		SocializeUI socializeUI = new SocializeUI();
		socializeUI.setContainer(container);

		socializeUI.getView(name);

		AndroidMock.verify(container);	
	}

	@UsesMocks ({Drawables.class, IOCContainer.class})
	public void testGetDrawable() {
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);

		String name = "foobar";
		boolean eternal = false;
		
		AndroidMock.expect( container.getBean("drawables")).andReturn(drawables);
		AndroidMock.expect( drawables.getDrawable(name, eternal) ).andReturn(null);
		
		AndroidMock.replay(container);
		AndroidMock.replay(drawables);

		SocializeUI socializeUI = new SocializeUI();
		socializeUI.initUI(container);

		socializeUI.getDrawable(name, eternal);

		AndroidMock.verify(container);
		AndroidMock.verify(drawables);	
	}
	
	public void testShowCommentView() {
		
		final String url = "foobar";

		TestActivityCallback callback = new TestActivityCallback() {
			
			Activity activity;
			
			@Override
			public void setActivity(Activity activity) {
				this.activity = activity;
			}

			@Override
			public void onCreate(Bundle savedInstanceState) {
				SocializeUI socializeUI = new SocializeUI();
				socializeUI.showCommentView(activity, url);
				
				addResult(true);
			}

			@Override
			public void startActivity(Intent intent) {
				ComponentName component = intent.getComponent();
				assertNotNull(component);
				assertEquals(CommentActivity.class.getName(), component.getClassName());
				
				Bundle extras = intent.getExtras();
				
				assertNotNull(extras);
				
				String key = extras.getString(SocializeUI.ENTITY_KEY);
				
				assertNotNull(key);
				assertEquals(url, key);
				
				addResult(true);
			}
		};
		
		TestActivityCallbackHolder.callback = callback;
		Intent i = new Intent(getActivity(), SampleSocializeActivity.class);
		
		int requestCode = 69;
		
		getActivity().startActivityForResult(i, requestCode);
		
		sleep(2000);
		
		Boolean didStart = getNextResult();
		Boolean didRun = getNextResult();
		
		getActivity().finishActivity(requestCode);
		
		assertNotNull(didStart);
		assertNotNull(didRun);
		
		assertTrue(didStart);
		assertTrue(didRun);
	}
	
	@UsesMocks ({SocializeInitListener.class, SocializeException.class, SocializeConfig.class, IOCContainer.class})
	public void test_initSocializeAsync() {
		
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		final String[] mockPaths = {};
		
		listener.onError(error);
		config.merge((Properties)AndroidMock.anyObject());
		
		listener.onInit(getContext(), container);
		
		AndroidMock.replay(listener);
		AndroidMock.replay(config);
		
		final SocializeServiceImpl socialize = new SocializeServiceImpl() {

			@Override
			public void initAsync(Context context, SocializeInitListener listener, String... paths) {
				addResult(1, listener);
				addResult(2, paths);
			}

			@Override
			public SocializeConfig getConfig() {
				return config;
			}
		};
		
		SocializeUI socializeUI = new SocializeUI() {

			@Override
			protected String[] getConfig() {
				addResult(0, "getConfig");
				return mockPaths;
			}

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
			
		};
		
		socializeUI.initSocializeAsync(getContext(), listener);
		
		// Call the listener to make sure it's doing what it should
		String getConfig = getResult(0);
		String[] pathsAfter = getResult(2);
		SocializeInitListener listenerAfter = getResult(1);
		
		assertNotNull(getConfig);
		assertEquals("getConfig", getConfig);
		
		assertNotNull(pathsAfter);
		assertSame(mockPaths, pathsAfter);
		
		assertNotNull(listenerAfter);
		
		listenerAfter.onError(error);
		listenerAfter.onInit(getContext(), container);
		
		AndroidMock.verify(listener);
		AndroidMock.verify(config);
	}
	
	public void test_getConfigWithoutOverride() {
		String[] config = new PublicSocializeUI().getConfig();
		assertNotNull(config);
		assertEquals(2, config.length);
		assertEquals("socialize_beans.xml", config[0]);
		assertEquals("socialize_ui_beans.xml", config[1]);
	}
	
	public void test_getConfigWithOverride() {
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI();
		SocializeUIBeanOverrider overrider = new SocializeUIBeanOverrider();
		overrider.setBeanOverrides(publicSocializeUI, "foobar");
		String[] config = publicSocializeUI.getConfig();
		assertNotNull(config);
		assertEquals(3, config.length);
		assertEquals("socialize_beans.xml", config[0]);
		assertEquals("socialize_ui_beans.xml", config[1]);
		assertEquals("foobar", config[2]);
	}
	
	public void test_setFacebookSingleSignOnEnabled() {
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public void setCustomProperty(String key, String value) {
				addResult(key);
				addResult(value);
			}
		};
		
		publicSocializeUI.setFacebookSingleSignOnEnabled(true);
		
		String key = getNextResult();
		String value = getNextResult();
		
		assertEquals(SocializeConfig.FACEBOOK_SSO_ENABLED, key);
		assertEquals("true", value);
	}
	
	
	public void test_isFacebookSupported() {
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public String getCustomConfigValue(String key) {
				addResult(key);
				return key;
			}
		};
		
		publicSocializeUI.isFacebookSupported();
		
		String key = getNextResult();
		
		assertEquals(SocializeConfig.FACEBOOK_APP_ID, key);
		assertTrue(publicSocializeUI.isFacebookSupported());
	}	
	
	@UsesMocks ({SocializeService.class, SocializeConfig.class})
	public void test_getCustomConfigValue() {
		
		final String key = "foobar";
		final String value = "foobar_value";
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(socialize.getConfig()).andReturn(config);
		AndroidMock.expect(config.getProperty(key)).andReturn(value);
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		String valueAfter = publicSocializeUI.getCustomConfigValue(key);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
		
		assertEquals(value, valueAfter);
	}
	
	
	public void test_showCommentView0() {
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		final String url = "foo";
		final String entityName = "bar";
		final boolean isEntityUrl = true;
		
		AndroidMock.expect(intent.putExtra(SocializeUI.ENTITY_KEY, url)).andReturn(intent);
		AndroidMock.expect(intent.putExtra(SocializeUI.ENTITY_NAME, entityName)).andReturn(intent);
		AndroidMock.expect(intent.putExtra(SocializeUI.ENTITY_URL_AS_LINK, isEntityUrl)).andReturn(intent);
		
		context.startActivity(intent);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				assertEquals(cls, CommentActivity.class);
				return intent;
			}
		};	
		
		publicSocializeUI.showCommentView(context, url, entityName, isEntityUrl);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
	}
	
	public void test_showUserProfileView() {
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		final String userId = "foo";
		
		AndroidMock.expect(intent.putExtra(SocializeUI.USER_ID, userId)).andReturn(intent);
		
		context.startActivity(intent);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				assertEquals(cls, ProfileActivity.class);
				return intent;
			}
		};	
		
		publicSocializeUI.showUserProfileView(context, userId);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
	}	
	
	public void test_showUserProfileViewForResult() {
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		final String userId = "foo";
		final int requestCode = 69;
		
		AndroidMock.expect(intent.putExtra(SocializeUI.USER_ID, userId)).andReturn(intent);
		
		context.startActivityForResult(intent, requestCode);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				assertEquals(cls, ProfileActivity.class);
				return intent;
			}
		};	
		
		publicSocializeUI.showUserProfileViewForResult(context, userId, requestCode);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
	}	
	
	public void test_showCommentDetailViewForResult() {
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		final String userId = "foo";
		final String commentId = "bar";
		final int requestCode = 69;
		
		AndroidMock.expect(intent.putExtra(SocializeUI.USER_ID, userId)).andReturn(intent);
		AndroidMock.expect(intent.putExtra(SocializeUI.COMMENT_ID, commentId)).andReturn(intent);
		
		context.startActivityForResult(intent, requestCode);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				assertEquals(cls, CommentDetailActivity.class);
				return intent;
			}
		};	
		
		publicSocializeUI.showCommentDetailViewForResult(context, userId, commentId, requestCode);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
	}		
	
	public void test_setEntityName() {
		
		// Can't mock bundle :/
		final Bundle mockBundle = new Bundle();
		final String name = "foo";
		
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		AndroidMock.expect(context.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.putExtras(mockBundle)).andReturn(intent);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Bundle getExtras(Intent intent) {
				addResult(intent);
				return mockBundle;
			}
		};	
		
		publicSocializeUI.setEntityName(context, name);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
		
		Intent intentAfter = getNextResult();
		
		assertNotNull(intentAfter);
		assertSame(intent, intentAfter);
		
		String nameAfter = mockBundle.getString(SocializeUI.ENTITY_NAME);
		
		assertNotNull(nameAfter);
		assertEquals(name, nameAfter);
	}
	
	public void test_setUseEntityUrlAsLink() {
		
		// Can't mock bundle :/
		final Bundle mockBundle = new Bundle();
		final boolean asLink = true;
		
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		AndroidMock.expect(context.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.putExtras(mockBundle)).andReturn(intent);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Bundle getExtras(Intent intent) {
				addResult(intent);
				return mockBundle;
			}
		};	
		
		publicSocializeUI.setUseEntityUrlAsLink(context, asLink);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
		
		Intent intentAfter = getNextResult();
		
		assertNotNull(intentAfter);
		assertSame(intent, intentAfter);
		
		boolean asLinkAfter = mockBundle.getBoolean(SocializeUI.ENTITY_URL_AS_LINK);
		
		assertEquals(asLink, asLinkAfter);
	}	
	
	public void test_setUserId() {
		
		// Can't mock bundle :/
		final Bundle mockBundle = new Bundle();
		final String userId = "foo";
		
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Activity context = AndroidMock.createMock(Activity.class);
		
		AndroidMock.expect(context.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.putExtras(mockBundle)).andReturn(intent);
		
		AndroidMock.replay(intent);
		AndroidMock.replay(context);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public Bundle getExtras(Intent intent) {
				addResult(intent);
				return mockBundle;
			}
		};	
		
		publicSocializeUI.setUserId(context, userId);
		
		AndroidMock.verify(intent);
		AndroidMock.verify(context);
		
		Intent intentAfter = getNextResult();
		
		assertNotNull(intentAfter);
		assertSame(intent, intentAfter);
		
		String nameAfter = mockBundle.getString(SocializeUI.USER_ID);
		
		assertNotNull(nameAfter);
		assertEquals(userId, nameAfter);
	}
	
	public void test_showActionBar0() {
		
		View originalActual = new View(getActivity());
		String entityKeyActual = "foo";
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}
		};	
		
		publicSocializeUI.showActionBar(getActivity(), originalActual, entityKeyActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNull(entityName);
		
		assertTrue(isEntityKeyUrl);
		assertTrue(addScrollView);
		
		assertNull(listener);
	}

	
	@UsesMocks ({ActionBarListener.class})
	public void test_showActionBar1() {
		
		View originalActual = new View(getActivity());
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		String entityKeyActual = "foo";
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}
		};	
		
		publicSocializeUI.showActionBar(getActivity(), originalActual, entityKeyActual, listenerActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNull(entityName);
		
		assertTrue(isEntityKeyUrl);
		assertTrue(addScrollView);
		
		assertNotNull(listener);
		assertSame(listenerActual, listener);
	}	
	
	
	@UsesMocks ({ActionBarListener.class, ActionBarOptions.class})
	public void test_showActionBar2() {
		
		View originalActual = new View(getActivity());
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		ActionBarOptions optionsActual = AndroidMock.createMock(ActionBarOptions.class);
		
		String entityKeyActual = "foo";
		String entityNameActual = "bar";
		boolean isEntityKeyActual = false;
		boolean isScroll = true;
		 
		AndroidMock.expect(optionsActual.getEntityName()).andReturn(entityNameActual);
		AndroidMock.expect(optionsActual.isEntityKeyUrl()).andReturn(isEntityKeyActual);
		AndroidMock.expect(optionsActual.isAddScrollView()).andReturn(isScroll);
		
		AndroidMock.replay(optionsActual);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}
		};	
		
		publicSocializeUI.showActionBar(getActivity(), originalActual, entityKeyActual, optionsActual, listenerActual);
		
		AndroidMock.verify(optionsActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNotNull(entityName);
		assertEquals(entityNameActual, entityName);
		
		assertEquals(isEntityKeyActual, isEntityKeyUrl.booleanValue());
		assertEquals(isScroll, addScrollView.booleanValue());
		
		assertNotNull(listener);
		assertSame(listenerActual, listener);
	}
	
	
	public void test_showActionBar3() {
		
		final View originalActual = new View(getActivity());
		int resId = 69;
		String entityKeyActual = "foo";
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}
			
			@Override
			public View inflateView(Activity parent, int resId) {
				return originalActual;
			}
		};	
		
		publicSocializeUI.showActionBar(getActivity(), resId, entityKeyActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNull(entityName);
		
		assertTrue(isEntityKeyUrl);
		assertTrue(addScrollView);
		
		assertNull(listener);
	}

	
	@UsesMocks ({ActionBarListener.class})
	public void test_showActionBar4() {
		
		final View originalActual = new View(getActivity());
		int resId = 69;
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		String entityKeyActual = "foo";
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}
			
			@Override
			public View inflateView(Activity parent, int resId) {
				return originalActual;
			}
		};	
		
		publicSocializeUI.showActionBar(getActivity(), resId, entityKeyActual, listenerActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNull(entityName);
		
		assertTrue(isEntityKeyUrl);
		assertTrue(addScrollView);
		
		assertNotNull(listener);
		assertSame(listenerActual, listener);
	}	
	
	
	@UsesMocks ({ActionBarListener.class, ActionBarOptions.class})
	public void test_showActionBar5() {
		
		final View originalActual = new View(getActivity());
		int resId = 69;
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		ActionBarOptions optionsActual = AndroidMock.createMock(ActionBarOptions.class);
		
		String entityKeyActual = "foo";
		String entityNameActual = "bar";
		boolean isEntityKeyActual = false;
		boolean isScroll = true;
		 
		AndroidMock.expect(optionsActual.getEntityName()).andReturn(entityNameActual);
		AndroidMock.expect(optionsActual.isEntityKeyUrl()).andReturn(isEntityKeyActual);
		AndroidMock.expect(optionsActual.isAddScrollView()).andReturn(isScroll);
		
		AndroidMock.replay(optionsActual);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			
			public ActionBarView showActionBar(
					Activity parent, 
					View original, 
					String entityKey, 
					String entityName,
					boolean isEntityKeyUrl, 
					boolean addScrollView, 
					ActionBarListener listener) {
				addResult(0,original);
				addResult(1,entityKey);
				addResult(2,entityName);
				addResult(3,isEntityKeyUrl);
				addResult(4,addScrollView);
				addResult(5,listener);
				return null;
			}

			@Override
			public View inflateView(Activity parent, int resId) {
				return originalActual;
			}
			
			
		};	
		
		publicSocializeUI.showActionBar(getActivity(), resId, entityKeyActual, optionsActual, listenerActual);
		
		AndroidMock.verify(optionsActual);
		
		View original = getResult(0);
		String entityKey = getResult(1);
		String entityName = getResult(2);
		Boolean isEntityKeyUrl = getResult(3); 
		Boolean addScrollView = getResult(4);
		ActionBarListener listener = getResult(5);
		
		assertNotNull(original);
		assertSame(originalActual, original);
		
		assertNotNull(entityKey);
		assertEquals(entityKeyActual, entityKey);
		
		assertNotNull(entityName);
		assertEquals(entityNameActual, entityName);
		
		assertEquals(isEntityKeyActual, isEntityKeyUrl.booleanValue());
		assertEquals(isScroll, addScrollView.booleanValue());
		
		assertNotNull(listener);
		assertSame(listenerActual, listener);
	}		
	
	/**
272	        
277	        public View showActionBar(Activity parent, int resId, String entityKey) {
278	                return showActionBar(parent, resId, entityKey, null, true, true, null);
279	        }
280	        
281	        public View showActionBar(Activity parent, int resId, String entityKey, ActionBarListener listener) {
282	                return showActionBar(parent, resId, entityKey, null, true, true, listener);
283	        }
284	        
285	        public View showActionBar(Activity parent, int resId, String entityKey, ActionBarOptions options, ActionBarListener listener) {
286	                return showActionBar(parent, resId, entityKey, options.getEntityName(), options.isEntityKeyUrl(), options.isEntityKeyUrl(), listener);
287	        }
	 */
	
	@UsesMocks ({RelativeLayout.class, ActionBarView.class, View.class, MockRelativeLayoutParams.class, ScrollView.class, TestUIFactory.class, ActionBarListener.class})
	public void test_showActionBar() {
		
		final int width = 100, height = 50;
		
		final TestUIFactory factory =  AndroidMock.createMock(TestUIFactory.class);
		
		final String entityKey = "foo"; 
		final String entityName = "bar"; 
		final boolean isEntityKeyUrl = true; 
		final boolean addScrollView = true;
		final int actionBarId = 69;
		
		final Activity parent = getActivity();
		View original = AndroidMock.createMock(View.class, parent);
		
		ActionBarListener listener = AndroidMock.createMock(ActionBarListener.class);
		RelativeLayout barLayout = AndroidMock.createMock(RelativeLayout.class, parent);
		RelativeLayout originalLayout = AndroidMock.createMock(RelativeLayout.class, parent);
		ScrollView scrollView = AndroidMock.createMock(ScrollView.class, parent);
		ActionBarView socializeActionBar = AndroidMock.createMock(ActionBarView.class, parent);
		LayoutParams barParams = AndroidMock.createMock(MockRelativeLayoutParams.class, width, height);
		LayoutParams originalParams = AndroidMock.createMock(MockRelativeLayoutParams.class, width, height);
		LayoutParams scrollViewParams = AndroidMock.createMock(MockRelativeLayoutParams.class, width, height);
		
		AndroidMock.expect(factory.newRelativeLayout(parent)).andReturn(barLayout).once();
		AndroidMock.expect(factory.newRelativeLayout(parent)).andReturn(originalLayout).once();
		
		AndroidMock.expect(factory.newActionBarView(parent)).andReturn(socializeActionBar);
		AndroidMock.expect(factory.newScrollView(parent)).andReturn(scrollView);
		
		AndroidMock.expect(factory.newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)).andReturn(barParams);
		AndroidMock.expect(factory.newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)).andReturn(originalParams);
		AndroidMock.expect(factory.newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)).andReturn(scrollViewParams);
		
		AndroidMock.expect(socializeActionBar.getId()).andReturn(actionBarId);
		
		socializeActionBar.assignId(original);
		socializeActionBar.setEntityKey(entityKey);
		socializeActionBar.setEntityName(entityName);
		socializeActionBar.setEntityKeyIsUrl(isEntityKeyUrl);
		
		barParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		originalParams.addRule(RelativeLayout.ABOVE, actionBarId);
		
		socializeActionBar.setLayoutParams(barParams);
		originalLayout.setLayoutParams(originalParams);
		
		listener.onCreate(socializeActionBar);
		
		scrollView.setFillViewport(true);
		scrollView.setScrollContainer(false);
		scrollView.setLayoutParams(scrollViewParams);
		scrollView.addView(original);
		originalLayout.addView(scrollView);
		
		barLayout.addView(originalLayout);
		barLayout.addView(socializeActionBar);
		
		AndroidMock.replay(listener);
		AndroidMock.replay(barLayout);
		AndroidMock.replay(originalLayout);
		AndroidMock.replay(scrollView);
		AndroidMock.replay(socializeActionBar);
		AndroidMock.replay(barParams);
		AndroidMock.replay(originalParams);
		AndroidMock.replay(scrollViewParams);
		AndroidMock.replay(factory);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public RelativeLayout newRelativeLayout(Activity parent) {
				return factory.newRelativeLayout(parent);
			}

			@Override
			public ActionBarView newActionBarView(Activity parent) {
				return factory.newActionBarView(parent);
			}

			@Override
			public LayoutParams newLayoutParams(int width, int height) {
				return factory.newLayoutParams(width, height);
			}

			@Override
			public ScrollView newScrollView(Activity parent) {
				return factory.newScrollView(parent);
			}
		};	
		
		publicSocializeUI.showActionBar(parent, original, entityKey, entityName, isEntityKeyUrl, addScrollView, listener);
		
		AndroidMock.verify(listener);
		AndroidMock.verify(barLayout);
		AndroidMock.verify(originalLayout);
		AndroidMock.verify(scrollView);
		AndroidMock.verify(socializeActionBar);
		AndroidMock.verify(barParams);
		AndroidMock.verify(originalParams);
		AndroidMock.verify(scrollViewParams);
		AndroidMock.verify(factory);
	}
	
	
	protected class PublicSocializeUI extends SocializeUI {

		@Override
		public String[] getConfig() {
			return super.getConfig();
		}

		@Override
		public void setCustomProperty(String key, String value) {
			super.setCustomProperty(key, value);
		}

		@Override
		public Bundle getExtras(Intent intent) {
			return super.getExtras(intent);
		}

		@Override
		public Intent newIntent(Activity context, Class<?> cls) {
			return super.newIntent(context, cls);
		}

		@Override
		public RelativeLayout newRelativeLayout(Activity parent) {
			return super.newRelativeLayout(parent);
		}

		@Override
		public ActionBarView newActionBarView(Activity parent) {
			return super.newActionBarView(parent);
		}

		@Override
		public LayoutParams newLayoutParams(int width, int height) {
			return super.newLayoutParams(width, height);
		}

		@Override
		public ScrollView newScrollView(Activity parent) {
			return super.newScrollView(parent);
		}

		@Override
		public View showActionBar(Activity parent, int resId, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
			return super.showActionBar(parent, resId, entityKey, entityName, isEntityKeyUrl, addScrollView, listener);
		}

		@Override
		public View showActionBar(Activity parent, View original, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
			return super.showActionBar(parent, original, entityKey, entityName, isEntityKeyUrl, addScrollView, listener);
		}

		@Override
		public View inflateView(Activity parent, int resId) {
			return super.inflateView(parent, resId);
		}
	}
}
