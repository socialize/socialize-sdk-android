package com.socialize.test.ui.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.SocializeSystem;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.listener.SocializeInitListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeBaseView;

public class SocializeViewTest extends SocializeActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class})
	public void testOnWindowVisibilityChanged() throws Throwable {
		
		final SocializeBaseView view = new SocializeBaseView(TestUtils.getActivity(this)) {
			
			@Override
			protected void doSocializeInit(SocializeInitListener listener) {
				addResult("doSocializeInit");
			}

			@Override
			public View getLoadingView() {
				return null;
			}
		};
		
		final Activity activity = TestUtils.getActivity(this);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.setContentView(view);
			}
		});
		
		sleep(1000);
		
		String initSocialize = getNextResult();
		
		assertNotNull(initSocialize);
		
		assertEquals("doSocializeInit", initSocialize);
	}
	
	public void test_onViewLoad() {
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			
			@Override
			public View getLoadingView() {
				return new View(getContext());
			}

			@Override
			public void addView(View child) {
				addResult("addView");
			}

			@Override
			public void doSocializeInit(SocializeInitListener listener) {
				addResult("doSocializeInit");
			}

			@Override
			public SocializeInitListener getInitLoadListener() {
				addResult("getInitLoadListener");
				return null;
			}
			
			
		};
		
		view.onViewLoad();
		
		String addView = getNextResult();
		String getInitLoadListener = getNextResult();
		String doSocializeInit = getNextResult();
	

		assertNotNull(doSocializeInit);
		assertNotNull(addView);
		assertNotNull(getInitLoadListener);
		
		assertEquals("doSocializeInit", doSocializeInit);
		assertEquals("addView", addView);
		assertEquals("getInitLoadListener", getInitLoadListener);
	}
	
	public void test_onViewUpdate() {
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			
			@Override
			public View getLoadingView() {
				return new View(getContext());
			}

			@Override
			public void doSocializeInit(SocializeInitListener listener) {
				addResult("doSocializeInit");
			}

			@Override
			public SocializeInitListener getInitUpdateListener() {
				addResult("getInitUpdateListener");
				return null;
			}
		};
		
		view.onViewUpdate();
		
		String getInitUpdateListener = getNextResult();
		String doSocializeInit = getNextResult();
	

		assertNotNull(doSocializeInit);
		assertNotNull(getInitUpdateListener);
		
		assertEquals("doSocializeInit", doSocializeInit);
		assertEquals("getInitUpdateListener", getInitUpdateListener);
	}	
	
	@UsesMocks({IOCContainer.class})
	public void test_getInitLoadListener() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		AndroidMock.expect(container.getBean("drawables")).andReturn(null);
		AndroidMock.expect(container.getBean("localizationService")).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public void onViewLoad(IOCContainer container) {
				addResult(container);
			}
		};
		view.getInitLoadListener().onInit(TestUtils.getActivity(this), container);
		
		AndroidMock.verify(container);
		
		assertSame(container, getNextResult());
	}
	
	@UsesMocks({IOCContainer.class})
	public void test_getInitUpdateListener() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		AndroidMock.expect(container.getBean("drawables")).andReturn(null);
		AndroidMock.expect(container.getBean("localizationService")).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public void onViewUpdate(IOCContainer container) {
				addResult(container);
			}
		};
		view.getInitUpdateListener().onInit(TestUtils.getActivity(this), container);
		
		AndroidMock.verify(container);
		
		assertSame(container, getNextResult());
	}
	
	@UsesMocks ({SocializeInitListener.class})
	public void test_doSocializeInit() {
		
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public boolean isInEditMode() {
				addResult("isInEditMode");
				return false;
			}

			@Override
			public void onBeforeSocializeInit() {
				addResult("onBeforeSocializeInit");
			}

			@Override
			public void initSocialize(SocializeInitListener listener) {
				addResult(listener);
			}
		};
		
		view.doSocializeInit(listener);
		
		String isInEditMode = getNextResult();
		String onBeforeSocializeInit = getNextResult();
		SocializeInitListener listenerAfter = getNextResult();
		
		assertNotNull(isInEditMode);
		assertNotNull(onBeforeSocializeInit);
		assertNotNull(listenerAfter);
		
		assertEquals("isInEditMode", isInEditMode);
		assertEquals("onBeforeSocializeInit", onBeforeSocializeInit);
		assertSame(listener, listenerAfter);
	}
	

	@UsesMocks ({ SocializeInitListener.class, SocializeService.class, SocializeSystem.class})
	public void testInitSocialize() {
		final SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeSystem system = AndroidMock.createMock(SocializeSystem.class);
		
		final String[] config = {"foo", "bar"};
		
		AndroidMock.expect(socialize.getSystem()).andReturn(system);
		AndroidMock.expect(system.getBeanConfig()).andReturn(config);
		AndroidMock.expect(system.getSystemInitListener()).andReturn(null);
		
		socialize.initAsync((Context) AndroidMock.anyObject(), AndroidMock.eq( listener ), AndroidMock.eq(config[0]),AndroidMock.eq(config[1]));
		
		AndroidMock.replay(system);
		AndroidMock.replay(socialize);
		
		PublicView activity = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		activity.initSocialize(listener);
		
		AndroidMock.verify(system);
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({IOCContainer.class})
	public void testGetBeanSocialize() {
		final String name = "foobar";
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		AndroidMock.expect(container.getBean(name)).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicView activity = new PublicView(TestUtils.getActivity(this));
		activity.setContainer(container);
		
		activity.getBean(name);
		
		AndroidMock.verify(container);
	}
	
	class PublicView extends SocializeBaseView {
		
		public PublicView(Context context) {
			super(context);
		}

		@Override
		public <E> E getBean(String name) {
			return super.getBean(name);
		}

		@Override
		public void initSocialize(SocializeInitListener listener) {
			super.initSocialize(listener);
		}
		
		@Override
		public View getLoadingView() {
			return null;
		}

		@Override
		public SocializeInitListener getInitLoadListener() {
			return super.getInitLoadListener();
		}

		@Override
		public SocializeInitListener getInitUpdateListener() {
			return super.getInitUpdateListener();
		}

		@Override
		public void doSocializeInit(SocializeInitListener listener) {
			super.doSocializeInit(listener);
		}

		@Override
		public void onBeforeSocializeInit() {
			super.onBeforeSocializeInit();
		}
	}
	
}
