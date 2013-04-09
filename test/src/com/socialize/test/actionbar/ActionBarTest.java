package com.socialize.test.actionbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.action.entity.SocializeEntityUtils;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.api.action.view.SocializeViewUtils;
import com.socialize.auth.AuthProviders;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.test.SocializeManagedActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.testapp.ActionBarActivity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class ActionBarTest extends SocializeManagedActivityTest<ActionBarActivity> {

	protected Entity entity = Entity.newInstance("http://entity1.com" + Math.random(), "no name");
	
	protected CountDownLatch globalLatch = null;
	protected AuthProviders authProviders;

	public ActionBarTest() {
        super("com.socialize.testapp", ActionBarActivity.class);
    }
	
	protected final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
		@Override
		public void getLike(Activity context, String entityKey, LikeGetListener listener) {
			listener.onGet(null); // not liked
		}
	};
	
	protected final SocializeEntityUtils mockEntityUtils = new SocializeEntityUtils() {
		@Override
		public void getEntity(Activity context, String key, final EntityGetListener listener) {
			listener.onGet(entity);
			globalLatch.countDown();
		}
	};


	protected final SocializeViewUtils mockViewUtils = new SocializeViewUtils() {
		@Override
		public void view(Activity context, Entity e, ViewAddListener listener) {
			View v = new View();
			v.setEntity(entity);
			v.setId(1L);
			listener.onCreate(v);
		}
	};


	// Don't preload
	protected final SocializeShareUtils mockShareUtils = new SocializeShareUtils() {

		@Override
		public void preloadShareDialog(Activity context) {}

		@Override
		public void preloadLinkDialog(Activity context) {}
	};
	
	protected final void waitForActionBarLoad() {

        // Make sure the activity has launched
        TestUtils.getActivity(this);

		try {
			assertTrue("Timeout waiting for action bar to load", globalLatch.await(30, TimeUnit.SECONDS));
		}
		catch (InterruptedException e) {}
	}


	@UsesMocks({AuthProviders.class})
	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		intent.putExtra("manual", isManual());
		setActivityIntent(intent);
		
		globalLatch = new CountDownLatch(1);

		authProviders = AndroidMock.createMock(AuthProviders.class);

		SocializeAccess.setBeanOverrides("socialize_proxy_beans.xml");
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		SocializeAccess.setEntityUtilsProxy(mockEntityUtils);
		SocializeAccess.setViewUtilsProxy(mockViewUtils);
		SocializeAccess.setAuthProviders(authProviders);
		SocializeAccess.setInitListener(new SocializeInitListener() {
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<AuthProviders> providers = container.getProxy("authProviders");
				if(providers != null) {
					providers.setDelegate(authProviders);
				}
			}
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});

		if(overrideShareUtils()) {
			SocializeAccess.setShareUtilsProxy(mockShareUtils);
		}
		
		TestUtils.setUp(this);

        waitForActionBarLoad();
	}
	
	protected boolean overrideShareUtils() {
		return true;
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(globalLatch != null) {
			globalLatch.countDown();
        }
		
		TestUtils.tearDown(this);
		
		super.tearDown();
	}
	
	public abstract boolean isManual();
	
}
