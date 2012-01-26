package com.socialize.test.ui.integrationtest.actionbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.socialize.Socialize;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.sample.EmptyActivity;
import com.socialize.sample.ui.ActionBarListenerActivity;
import com.socialize.sample.ui.ActionBarListenerHolder;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class ActionBarListenerTest extends ActivityInstrumentationTestCase2<EmptyActivity> {

	public ActionBarListenerTest() {
		super("com.socialize.sample.ui", EmptyActivity.class);
	}
	
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
		TestUtils.destroyActivity();
		TestUtils.tearDown();
		super.tearDown();
	}
	
	public void testListenerOnCreateCalled() {
		
		Intent intent = new Intent(getActivity(), ActionBarListenerActivity.class);
		
		Entity entity = new Entity();
		entity.setKey("foobar");
		entity.setName("foobar_name");
		
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, "false");
		getActivity().startActivity(intent);		
		
		assertNotNull(waitForActionBar(10000));
	}
	
	
	public void testActionBarReload() throws Throwable {
		
		Intent intent = new Intent(getActivity(), ActionBarListenerActivity.class);
		
		Entity entity = new Entity();
		entity.setKey("foobar_testActionBarReload");
		entity.setName("foobar_name_testActionBarReload");
		
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, "false");
		getActivity().startActivity(intent);		
		
		final ActionBarView actionBar = waitForActionBar(20000);
		
		assertNotNull(actionBar);
		
		final CountDownLatch reloadLatch = new CountDownLatch(1);
		
		final List<Entity> holder = new ArrayList<Entity>();
		
		actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {
			
			@Override
			public void onUpdate(ActionBarView actionBar) {}
			
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
				holder.add(entity);
				reloadLatch.countDown();
			}
			
			@Override
			public void onClick(ActionBarView actionBar, ActionBarEvent evt) {}
		});
		
		final Entity new_entity = new Entity();
		new_entity.setKey("foobar2_testActionBarReload");
		new_entity.setName("foobar_name2_testActionBarReload");
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				actionBar.setEntity(new_entity);
				actionBar.refresh();
			}
		});
		
		assertTrue(reloadLatch.await(20000, TimeUnit.MILLISECONDS));
		
		assertTrue(holder.size() == 1);
		Entity found = holder.get(0);
		assertNotNull(found);
		
		assertEquals("foobar2_testActionBarReload", found.getKey());
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
