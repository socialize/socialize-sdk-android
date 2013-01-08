package com.socialize.test.ui.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.api.action.view.ViewSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.sample.ui.ActionBarListenerActivity;
import com.socialize.sample.ui.ActionBarListenerHolder;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.mock.MockViewSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class ActionBarListenerTest extends SocializeActivityTest {

	private ActionBarView view = null;
	private CountDownLatch latch = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
		TestUtils.setUpActivityMonitor(ActionBarListenerActivity.class);
		
		if(latch != null) {
			latch.countDown();
		}
		
		latch = new CountDownLatch(1);
		
		ActionBarListenerHolder.listener = new ActionBarListener() {
			@Override
			public void onCreate(ActionBarView view) {
				onCreateCalled(view);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown(this);
		super.tearDown();
	}
	
	public void testListenerOnCreateCalled() {
		
		Intent intent = new Intent(TestUtils.getActivity(this), ActionBarListenerActivity.class);
		
		Entity entity = new Entity();
		entity.setKey("foobar");
		entity.setName("foobar_name");
		
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		ConfigUtils.getConfig(getContext()).setProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, "false");
		TestUtils.getActivity(this).startActivity(intent);		
		
		assertNotNull(waitForActionBar(10000));
	}
	
	public void testActionBarReload() throws Throwable {
		
		Intent intent = new Intent(TestUtils.getActivity(this), ActionBarListenerActivity.class);
		
		Entity entity = new Entity();
		entity.setKey("1");
		entity.setName("foobar_name_testActionBarReload");
		
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		
		SocializeAccess.setBeanOverrides("socialize_ui_mock_beans.xml", "socialize_ui_mock_socialize_beans.xml");
		
		ConfigUtils.getConfig(getContext()).setProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, "false");
		
		TestUtils.getActivity(this).startActivity(intent);		
		
		final ActionBarView actionBar = waitForActionBar(20000);
		
		assertNotNull(actionBar);
		
		// Override default behaviour so we don't actually go to the server
		MockLikeSystem mockLikeSystem = new MockLikeSystem();
		MockViewSystem mockViewSystem = new MockViewSystem();
		
		ProxyObject<LikeSystem> likeSystemProxy = SocializeAccess.getProxy("likeSystem");
		ProxyObject<ViewSystem> viewSystemProxy = SocializeAccess.getProxy("viewSystem");
		
		likeSystemProxy.setDelegate(mockLikeSystem);
		viewSystemProxy.setDelegate(mockViewSystem);		
		
		final CountDownLatch reloadLatch = new CountDownLatch(2);
		
		actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {
			
			@Override
			public void onUpdate(ActionBarView actionBar) {
				addResult(1, "onUpdate");
				reloadLatch.countDown();
			}
			
			@Override
			public void onPostUnlike(ActionBarView actionBar) {}
			
			@Override
			public void onPostShare(ActionBarView actionBar, Share share) {}
			
			@Override
			public void onPostLike(ActionBarView actionBar, Like like) {}
			
			@Override
			public void onLoad(ActionBarView actionBar) {}
			
			@Override
			public void onGetLike(ActionBarView actionBar, Like like) {}
			
			@Override
			public void onGetEntity(ActionBarView actionBar, Entity entity) {
				addResult(0, entity);
				reloadLatch.countDown();
			}
			
			@Override
			public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
				return false;
			}
		});
		
		// Wait for initial load to complete
		sleep(2000);

		final Entity new_entity = new Entity();
		new_entity.setKey("2");
		new_entity.setName("foobar_name2_testActionBarReload");
		
		// Orchestrate the mocks
		mockLikeSystem.setEntity(new_entity);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				actionBar.setEntity(new_entity);
				actionBar.refresh();
			}
		});
		
		assertTrue(reloadLatch.await(10000, TimeUnit.MILLISECONDS));
		
		Entity found = getResult(0);
		String update = getResult(1);
		
		assertNotNull(found);
		assertNotNull(update);
		
		assertEquals("onUpdate", update);
		assertEquals("2", found.getKey());
	}	
	
	protected ActionBarView waitForActionBar(long timeout)  {
		try {
			if(latch.await(timeout, TimeUnit.MILLISECONDS)) {
				return view;
			}
		} 
		catch (InterruptedException ignore) {}
		return null;
	}
	
	protected void onCreateCalled(ActionBarView view) {
		this.view = view;
		latch.countDown();
	}
}
