package com.socialize.ui.test;

import android.content.Context;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.SocializeView;

public class SocializeViewTest extends SocializeUIActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class})
	public void testOnAttachedToWindow() throws Throwable {
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeErrorHandler errorHandler = AndroidMock.createMock(SocializeErrorHandler.class);
		
		AndroidMock.expect(container.getBean("socializeUIErrorHandler")).andReturn(errorHandler);
		
		AndroidMock.replay(container);
		
		ActivityIOCProvider.getInstance().setContainer(container);
		
		final SocializeView view = new SocializeView(getActivity()) {
			
			@Override
			protected void initSocialize() {
				addResult("initSocialize");
			}

			@Override
			protected void onBeforeSocializeInit() {
				addResult("onBeforeSocializeInit");
			}

			@Override
			protected void onPostSocializeInit(IOCContainer container) {
				addResult(container);
			}

			@Override
			public void setErrorHandler(SocializeErrorHandler errorHandler) {
				addResult(errorHandler);
			}
		};
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(view);
			}
		});
		
		sleep(1000);
		
		String onBeforeSocializeInit = getNextResult();
		String initSocialize = getNextResult();
		SocializeErrorHandler errorHandlerAfter = getNextResult();
		IOCContainer containerAfter = getNextResult();
		
		assertNotNull(containerAfter);
		assertNotNull(errorHandlerAfter);
		assertNotNull(initSocialize);
		assertNotNull(onBeforeSocializeInit);
		
		assertSame(container, containerAfter);
		assertSame(errorHandler, errorHandlerAfter);
		assertEquals("initSocialize", initSocialize);
		assertEquals("onBeforeSocializeInit", onBeforeSocializeInit);
		
		AndroidMock.verify(container);
	}
	

	@UsesMocks ({SocializeService.class})
	public void testInitSocialize() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		socialize.init((Context) AndroidMock.anyObject());
		
		AndroidMock.replay(socialize);
		
		PublicView activity = new PublicView(getActivity()) {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		activity.initSocialize();
		
		AndroidMock.verify(socialize);
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
		public void initSocialize() {
			super.initSocialize();
		}
	}
	
}
