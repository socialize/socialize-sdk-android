package com.socialize.ui.test;

import android.content.Context;
import android.view.View;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.listener.SocializeInitListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeView;

public class SocializeViewTest extends SocializeUIActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class})
	public void testOnWindowVisibilityChanged() throws Throwable {
		
//		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
//		SocializeErrorHandler errorHandler = AndroidMock.createMock(SocializeErrorHandler.class);
		
//		AndroidMock.expect(container.getBean("socializeUIErrorHandler")).andReturn(errorHandler);
		
//		AndroidMock.replay(container);
		
//		ActivityIOCProvider.getInstance().setContainer(container);
		
		final SocializeView view = new SocializeView(getActivity()) {
			
//			@Override
//			protected void onBeforeSocializeInit() {
//				addResult("onBeforeSocializeInit");
//			}

			@Override
			protected void doSocializeInit(SocializeInitListener listener) {
				addResult("doSocializeInit");
			}

//			@Override
//			public void setErrorHandler(SocializeErrorHandler errorHandler) {
//				addResult(errorHandler);
//			}

			@Override
			public View getLoadingView() {
				return null;
			}
		};
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(view);
			}
		});
		
		sleep(1000);
		
//		String onBeforeSocializeInit = getNextResult();
		String initSocialize = getNextResult();
//		SocializeErrorHandler errorHandlerAfter = getNextResult();
		
//		assertNotNull(errorHandlerAfter);
		assertNotNull(initSocialize);
//		assertNotNull(onBeforeSocializeInit);
		
//		assertSame(errorHandler, errorHandlerAfter);
		assertEquals("doSocializeInit", initSocialize);
//		assertEquals("onBeforeSocializeInit", onBeforeSocializeInit);
		
//		AndroidMock.verify(container);
	}
	

	@UsesMocks ({ SocializeInitListener.class, SocializeUI.class})
	public void testInitSocialize() {
		final SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		
		socializeUI.initSocializeAsync((Context) AndroidMock.anyObject(), AndroidMock.eq( listener ));
		
		AndroidMock.replay(socializeUI);
		
		PublicView activity = new PublicView(getActivity()) {

			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}
		};
		
		activity.initSocialize(listener);
		
		AndroidMock.verify(socializeUI);
	}
	
	@UsesMocks ({IOCContainer.class})
	public void testGetBeanSocialize() {
		final String name = "foobar";
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		AndroidMock.expect(container.getBean(name)).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicView activity = new PublicView(getActivity());
		activity.setContainer(container);
		
		activity.getBean(name);
		
		AndroidMock.verify(container);
	}
	
	class PublicView extends SocializeView {
		
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
	}
	
}
