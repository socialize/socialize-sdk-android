package com.socialize.test.core;

import android.content.Context;
import android.view.View;
import com.socialize.SocializeAccess;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.config.SocializeConfigUtils;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.view.AuthenticatedView;
import org.mockito.Mockito;

public class AuthenticatedViewTest extends SocializeActivityTest {

	public void testOnViewLoad() {
		
        final IOCContainer container = Mockito.mock(IOCContainer.class);
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		final SocializeAuthListener listener = Mockito.mock(SocializeAuthListener.class);
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "foobar";
		
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).thenReturn(key);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).thenReturn(secret);
		Mockito.when(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).thenReturn(fbId);
		
		SocializeConfigUtils mockConfigUtils = new SocializeConfigUtils() {
			@Override
			public SocializeConfig getConfig(Context context) {
				return config;
			}
		};

		SocializeAccess.setConfigUtilsProxy(mockConfigUtils);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			public View getView() {
				return null;
			}
			
			@Override
			public View getLoadingView() {
				return null;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public SocializeAuthListener getAuthListener(IOCContainer container) {
				return listener;
            }
		};
		
		view.onViewLoad(container);

        Mockito.verify(socialize).authenticate(null, key, secret, listener);
	}
}
