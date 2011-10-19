package com.socialize.ui.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.sample.test.TestActivity;
import com.socialize.ui.sample.test.TestActivityCallback;
import com.socialize.ui.sample.test.TestActivityCallbackHolder;
import com.socialize.util.Drawables;

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
		socializeUI.setEntityUrl(activity, url);

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

		socialize.destroy();

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
		Intent i = new Intent(getActivity(), TestActivity.class);
		
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
}
