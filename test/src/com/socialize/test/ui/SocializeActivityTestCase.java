package com.socialize.test.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.sample.ui.SampleSocializeActivity;
import com.socialize.sample.ui.TestActivityCallbackHolder;
import com.socialize.sample.ui.TestSocializeActivityCallback;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.SocializeActivity;

public class SocializeActivityTestCase extends SocializeUIActivityTest {

	
	@UsesMocks ({TestSocializeActivityCallback.class, IOCContainer.class})
	public void testSocializeActivity() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		TestSocializeActivityCallback callback = AndroidMock.createMock(TestSocializeActivityCallback.class);
		
		callback.onCreate((Bundle)AndroidMock.anyObject());
		callback.setActivity((Activity)AndroidMock.anyObject());
		callback.initSocialize();
		callback.onPostSocializeInit(container);
		callback.destroySocialize();
		
		ActivityIOCProvider.getInstance().setContainer(container);
		
		AndroidMock.replay(callback);
		
		TestActivityCallbackHolder.callback = callback;
		
		Intent i = new Intent(getActivity(), SampleSocializeActivity.class);
		
		int code = 69;
		
		getActivity().startActivityForResult(i, code);
		
		sleep(2000);
		
		getActivity().finishActivity(code);
		
		sleep(2000);
		
		AndroidMock.verify(callback);
	}
	
	@UsesMocks ({SocializeService.class})
	public void testInitSocialize() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		AndroidMock.expect(socialize.init((Context) AndroidMock.anyObject())).andReturn(null);
		
		AndroidMock.replay(socialize);
		
		PublicActivity activity = new PublicActivity() {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		activity.initSocialize();
		
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({SocializeService.class})
	public void testDestroySocialize() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		socialize.destroy();
		
		AndroidMock.replay(socialize);
		
		PublicActivity activity = new PublicActivity() {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		activity.destroySocialize();
		
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({IOCContainer.class})
	public void testGetBeanSocialize() {
		final String name = "foobar";
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		AndroidMock.expect(container.getBean(name)).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicActivity activity = new PublicActivity();
		activity.setContainer(container);
		
		activity.getBean(name);
		
		AndroidMock.verify(container);
	}
	
	class PublicActivity extends SocializeActivity {
		@Override
		public <E> E getBean(String name) {
			return super.getBean(name);
		}

		@Override
		public void initSocialize() {
			super.initSocialize();
		}

		@Override
		public void destroySocialize() {
			super.destroySocialize();
		}
	}
}
