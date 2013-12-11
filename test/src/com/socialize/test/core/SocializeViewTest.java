package com.socialize.test.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.socialize.SocializeService;
import com.socialize.SocializeSystem;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.listener.SocializeInitListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.SocializeBaseView;
import org.mockito.Mockito;

public class SocializeViewTest extends SocializeActivityTest {

	public void testOnWindowVisibilityChanged() throws Throwable {
		
		final SocializeBaseView view = new SocializeBaseView(TestUtils.getActivity(this)) {

			@Override
			protected void doSocializeInit(Context context, SocializeInitListener listener) {
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
			public void doSocializeInit(Context context, SocializeInitListener listener) {
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
			public void doSocializeInit(Context context, SocializeInitListener listener) {
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
	
	public void test_getInitLoadListener() {
		IOCContainer container = Mockito.mock(IOCContainer.class);
		
		Mockito.when(container.getBean("drawables")).thenReturn(null);
		Mockito.when(container.getBean("localizationService")).thenReturn(null);
		
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public void onViewLoad(IOCContainer container) {
				addResult(container);
			}
		};
		view.getInitLoadListener().onInit(TestUtils.getActivity(this), container);
		
		assertSame(container, getNextResult());
	}
	
	public void test_getInitUpdateListener() {
		IOCContainer container = Mockito.mock(IOCContainer.class);
		
		Mockito.when(container.getBean("drawables")).thenReturn(null);
		Mockito.when(container.getBean("localizationService")).thenReturn(null);
		
		PublicView view = new PublicView(TestUtils.getActivity(this)) {
			@Override
			public void onViewUpdate(IOCContainer container) {
				addResult(container);
			}
		};
		view.getInitUpdateListener().onInit(TestUtils.getActivity(this), container);
		
		assertSame(container, getNextResult());
	}
	
	public void test_doSocializeInit() {
		
		SocializeInitListener listener = Mockito.mock(SocializeInitListener.class);
		final Context context = TestUtils.getActivity(this);
		
		PublicView view = new PublicView(context) {
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
			public void initSocialize(Context context, SocializeInitListener listener) {
				addResult(listener);
			}
		};
		
		view.doSocializeInit(context, listener);
		
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
	

	public void testInitSocialize() {
		final SocializeInitListener listener = Mockito.mock(SocializeInitListener.class);
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		final SocializeSystem system = Mockito.mock(SocializeSystem.class);
		
		final String[] config = {"foo", "bar"};
		final Context context = TestUtils.getActivity(this);
		
		Mockito.when(socialize.getSystem()).thenReturn(system);
		Mockito.when(system.getBeanConfig(context)).thenReturn(config);
		Mockito.when(system.getSystemInitListener()).thenReturn(null);
		

		PublicView activity = new PublicView(context) {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}
		};
		
		activity.initSocialize(context, listener);

        Mockito.verify(socialize).initAsync(Mockito.eq(context), Mockito.eq(listener), Mockito.eq(config[0]), Mockito.eq(config[1]));
    }
	
	public void testGetBeanSocialize() {
		final String name = "foobar";
		
		IOCContainer container = Mockito.mock(IOCContainer.class);
		
		PublicView activity = new PublicView(TestUtils.getActivity(this));
		activity.setContainer(container);
		
        activity.getBean(name);
		
		Mockito.verify(container).getBean(name);
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
		public void initSocialize(Context context, SocializeInitListener listener) {
			super.initSocialize(context, listener);
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
		public void doSocializeInit(Context context, SocializeInitListener listener) {
			super.doSocializeInit(context, listener);
		}

		@Override
		public void onBeforeSocializeInit() {
			super.onBeforeSocializeInit();
		}
	}
	
}
