package com.socialize.test.core;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.ActivityIOCProvider;
import org.mockito.Mockito;

public class ActivityIOCProviderTest extends SocializeUnitTest {

	public void testActivityIOCProviderGetBean() {
		IOCContainer container = Mockito.mock(IOCContainer.class);
		
		final String name = "foobar";

		PublicActivityIOCProvider provider = new PublicActivityIOCProvider();
		
		provider.setContainer(container);
		provider.getBean(name);

        Mockito.verify(container).getBean(name);
	}
	
	class PublicActivityIOCProvider extends ActivityIOCProvider {
		@Override
		public <E> E getBean(String name) {
			return super.getBean(name);
		}
	}
	
}
