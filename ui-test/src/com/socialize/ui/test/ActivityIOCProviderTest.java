package com.socialize.ui.test;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.ui.ActivityIOCProvider;

public class ActivityIOCProviderTest extends SocializeUITest {

	@UsesMocks (IOCContainer.class)
	public void testActivityIOCProviderGetBean() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		
		final String name = "foobar";
		
		AndroidMock.expect(container.getBean(name)).andReturn(null);
		
		AndroidMock.replay(container);
		
		PublicActivityIOCProvider provider = new PublicActivityIOCProvider();
		
		provider.setContainer(container);
		provider.getBean(name);
		
		AndroidMock.verify(container);
		
	}
	
	class PublicActivityIOCProvider extends ActivityIOCProvider {
		@Override
		public <E> E getBean(String name) {
			return super.getBean(name);
		}
	}
	
}
