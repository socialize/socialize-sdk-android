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
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.ActivityIOCProvider;
import com.socialize.ui.SocializeActivity;

public class SocializeActivityTestCase extends SocializeActivityTest {

	
	@UsesMocks ({TestSocializeActivityCallback.class, IOCContainer.class})
	public void testSocializeActivity() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		TestSocializeActivityCallback callback = AndroidMock.createMock(TestSocializeActivityCallback.class);
		
		callback.onCreate((Bundle)AndroidMock.anyObject());
		callback.setActivity((Activity)AndroidMock.anyObject());
		callback.initSocialize();
		callback.onPostSocializeInit(container);
		
		ActivityIOCProvider.getInstance().setContainer(container);
		
		AndroidMock.replay(callback);
		
		TestActivityCallbackHolder.callback = callback;
		
		Intent i = new Intent(TestUtils.getActivity(this), SampleSocializeActivity.class);
		
		int code = 69;
		
		TestUtils.getActivity(this).startActivityForResult(i, code);
		
		sleep(2000);
		
		TestUtils.getActivity(this).finishActivity(code);
		
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
			public SocializeService getSocialize() {
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
	}
}
