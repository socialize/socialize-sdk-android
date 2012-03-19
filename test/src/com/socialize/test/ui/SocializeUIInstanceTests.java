package com.socialize.test.ui;

import java.util.Properties;
import java.util.Set;

import android.app.Activity;
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
import com.socialize.android.ioc.IOCContainer;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.listener.SocializeInitListener;
import com.socialize.sample.mocks.MockSocializeUI;
import com.socialize.test.PublicSocialize;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.util.Drawables;

@Deprecated
public class SocializeUIInstanceTests extends SocializeUIActivityTest {

	@Deprecated
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

	@Deprecated
	@UsesMocks ({SocializeService.class, SocializeConfig.class, Properties.class, Set.class})
	public void testInitSocializeOld() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);

		MockContext context = new MockContext();
		
		final String[] paths = new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"};

		
		SocializeUI socializeUI = new SocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected String[] getConfig() {
				return paths;
			}
		};

		AndroidMock.expect(socialize.init(context, paths[0], paths[1])).andReturn(null);

		AndroidMock.replay(socialize);

		socializeUI.initSocialize(context);

		AndroidMock.verify(socialize);
	}
	
	@Deprecated
	@UsesMocks ({IOCContainer.class})
	public void testInitUI() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);

		AndroidMock.expect(container.getBean("drawables")).andReturn(null);

		AndroidMock.replay(container);

		SocializeUI socializeUI = new SocializeUI();
		socializeUI.initUI(container);

		AndroidMock.verify(container);
	}

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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
	
	@Deprecated
	public void testShowCommentViewOld() {
		
		final String url = "foobar";
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {

			@Override
			public void showCommentView(Activity context, Entity entity) {
				addResult(entity);
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showCommentView(getContext(), url);
		
		Entity entity = getNextResult();
		assertNotNull(entity);
		assertEquals(url, entity.getKey());
	}
	
	@Deprecated
	@UsesMocks ({SocializeInitListener.class})
	public void test_initSocializeAsync() {
		
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		final String[] mockPaths = {"a", "b"};
		
		final PublicSocialize socialize = new PublicSocialize() {
			@Override
			public void initAsync(Context context, SocializeInitListener listener, String... paths) {
				addResult(listener);
				addResult(paths);
			}
		};
		
		SocializeUI socializeUI = new SocializeUI() {

			@Override
			protected String[] getConfig() {
				return mockPaths;
			}

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		socializeUI.initSocializeAsync(getContext(), listener);
		
		SocializeInitListener listenerAfter = getResult(0);
		String[] pathsAfter = getResult(1);
		
		assertNotNull(pathsAfter);
		assertSame(mockPaths, pathsAfter);
		
		assertNotNull(listenerAfter);
		assertSame(listener, listenerAfter);
	}
	
	public void test_getConfigWithoutOverride() {
		String[] config = new PublicSocializeUI().getConfig();
		assertNotNull(config);
		assertEquals(2, config.length);
		assertEquals("socialize_core_beans.xml", config[0]);
		assertEquals("socialize_ui_beans.xml", config[1]);
	}

	@Deprecated
	@UsesMocks ({SocializeService.class, SocializeConfig.class})
	public void test_setFacebookSingleSignOnEnabled() {
		final boolean enabled = true;
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(socialize.getConfig()).andReturn(config);
		
		config.setFacebookSingleSignOnEnabled(enabled);
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		publicSocializeUI.setFacebookSingleSignOnEnabled(true);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
	}
	
	@Deprecated
	@UsesMocks ({SocializeService.class, SocializeConfig.class})
	public void test_isFacebookSupported() {
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		AndroidMock.expect(socialize.isSupported(AuthProviderType.FACEBOOK)).andReturn(true);
		AndroidMock.replay(socialize);
		
		PublicSocializeUI publicSocializeUI = new PublicSocializeUI() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		publicSocializeUI.isFacebookSupported();
		
		AndroidMock.verify(socialize);
	}	
	
	@Deprecated
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
	
	@Deprecated
	public void test_showCommentView0Old() {
		
		final String url = "foo";
		final String entityName = "bar";
		final boolean isEntityUrl = true;
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {

			@Override
			public void showCommentView(Activity context, Entity entity) {
				addResult(entity);
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showCommentView(getContext(), url, entityName, isEntityUrl);
		
		Entity entity = getNextResult();
		
		assertNotNull(entity);
		
		assertEquals(url, entity.getKey());
		assertEquals(entityName, entity.getName());
	}
	
	@Deprecated
	public void test_showUserProfileViewOld() {

		final String userId = "123";
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public void showUserProfileView(Activity context, Long userId) {
				addResult(userId);
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showUserProfileView(getContext(), userId);
		
		Long result = getNextResult();
		
		assertNotNull(result);
		
		assertEquals(result.longValue(), Long.parseLong(userId));

	}	
	
	@Deprecated
	public void test_showUserProfileViewForResultOld() {
		
		final String userId = "123";
		final int code = 69;
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			
			
			@Override
			public void showUserProfileViewForResult(Activity context, Long userId, int requestCode) {
				addResult(userId);
				addResult(requestCode);
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showUserProfileViewForResult(getContext(), userId, code);
		
		Integer result2 = getResult(1);
		Long result = getResult(0);

		assertNotNull(result);
		assertNotNull(result2);
		
		assertEquals(result.longValue(), Long.parseLong(userId));		
		assertEquals(result2.intValue(), code);
	}	
	
	@Deprecated
	public void test_showCommentDetailViewForResultOld() {
		
		final int code = 69;
		final User user = new User();
		final SocializeAction action = new Comment();
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {
				addResult(user);
				addResult(action);
				addResult(requestCode);
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionDetailViewForResult(getContext(), user, action, code);
		
		Integer requestCode = getResult(2);
		SocializeAction actionAfter = getResult(1);
		User userAfter = getResult(0);
		
		assertNotNull(requestCode);
		assertNotNull(actionAfter);
		assertNotNull(userAfter);
		
		assertEquals(code, requestCode.intValue());
		assertSame(action, actionAfter);
		assertSame(user, userAfter);
	}		
	
	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
	public void test_showActionBar0Old() {
		
		View originalActual = new View(getActivity());
		String entityKeyActual = "foo";
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, View original, Entity entity) {
				addResult(original);
				addResult(entity);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), originalActual, entityKeyActual);
		
		View original = getResult(0);
		Entity entity = getResult(1);
		assertNotNull(entity);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(originalActual, original);
	}

	@Deprecated
	@UsesMocks ({ActionBarListener.class})
	public void test_showActionBar1Old() {
		
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		View originalActual = new View(getActivity());
		String entityKeyActual = "foo";
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener) {
				addResult(original);
				addResult(entity);
				addResult(listener);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), originalActual, entityKeyActual, listenerActual);
		
		View original = getResult(0);
		Entity entity = getResult(1);
		ActionBarListener listener = getResult(2);
		
		assertNotNull(original);
		assertNotNull(entity);
		assertNotNull(listener);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(originalActual, original);		
		assertSame(listenerActual, listener);	
	}	
	
	@Deprecated
	@UsesMocks ({ActionBarListener.class, ActionBarOptions.class})
	public void test_showActionBar2Old() {
		
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		ActionBarOptions optionsActual = AndroidMock.createMock(ActionBarOptions.class);
		
		View originalActual = new View(getActivity());
		String entityKeyActual = "foo";
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
				addResult(original);
				addResult(entity);
				addResult(options);
				addResult(listener);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), originalActual, entityKeyActual, optionsActual, listenerActual);
		
		View original = getResult(0);
		Entity entity = getResult(1);
		ActionBarOptions options = getResult(2);
		ActionBarListener listener = getResult(3);
		
		assertNotNull(original);
		assertNotNull(entity);
		assertNotNull(options);
		assertNotNull(listener);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(originalActual, original);		
		assertSame(optionsActual, options);		
		assertSame(listenerActual, listener);	
	}
	
	@Deprecated
	public void test_showActionBar3Old() {

		String entityKeyActual = "foo";
		int resId = 69;
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, int resId, Entity entity) {
				addResult(resId);
				addResult(entity);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), resId, entityKeyActual);
		
		Integer original = getResult(0);
		Entity entity = getResult(1);
		assertNotNull(entity);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(resId, original.intValue());		
	}

	@Deprecated
	@UsesMocks ({ActionBarListener.class})
	public void test_showActionBar4Old() {
		
		String entityKeyActual = "foo";
		int resId = 69;
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener) {
				addResult(resId);
				addResult(entity);
				addResult(listener);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), resId, entityKeyActual, listenerActual);
		
		Integer original = getResult(0);
		Entity entity = getResult(1);
		ActionBarListener listener = getResult(2);
		
		assertNotNull(original);
		assertNotNull(entity);
		assertNotNull(listener);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(resId, original.intValue());		
		assertSame(listenerActual, listener);
	}	
	
	@Deprecated
	@UsesMocks ({ActionBarListener.class, ActionBarOptions.class})
	public void test_showActionBar5Old() {
		
		String entityKeyActual = "foo";
		int resId = 69;
		ActionBarListener listenerActual = AndroidMock.createMock(ActionBarListener.class);
		ActionBarOptions optionsActual = AndroidMock.createMock(ActionBarOptions.class);
		
		final com.socialize.SocializeUI socializeUI = new MockSocializeUI() {
			@Override
			public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
				addResult(resId);
				addResult(entity);
				addResult(listener);
				addResult(options);
				return null;
			}
		};
		
		SocializeUI socializeUIOld = new SocializeUI() {

			@Override
			public com.socialize.SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		socializeUIOld.showActionBar(getActivity(), resId, entityKeyActual, optionsActual, listenerActual);
		
		Integer original = getResult(0);
		Entity entity = getResult(1);
		ActionBarListener listener = getResult(2);
		ActionBarOptions options = getResult(3);
		
		assertNotNull(original);
		assertNotNull(entity);
		assertNotNull(listener);
		assertNotNull(options);
		
		assertEquals(entityKeyActual, entity.getKey());		
		assertSame(resId, original.intValue());		
		assertSame(listenerActual, listener);		
		assertSame(optionsActual, options);		
	}		
	
	
	@Deprecated
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
	}
}
