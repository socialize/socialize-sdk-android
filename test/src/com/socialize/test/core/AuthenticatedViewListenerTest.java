package com.socialize.test.core;

import android.content.Context;
import android.test.mock.MockContext;
import android.view.View;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.ui.view.AuthenticatedViewListener;
import org.mockito.Mockito;

public class AuthenticatedViewListenerTest extends SocializeActivityTest {

	
	public void testOnError() {
		AuthenticatedView view = Mockito.mock(AuthenticatedView.class);
		View subView = Mockito.mock(View.class);
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = Mockito.mock(IOCContainer.class);
		SocializeListener errorListener = Mockito.mock(SocializeListener.class);

		Mockito.when(view.getView()).thenReturn(subView);
		Mockito.when(view.getOnErrorListener()).thenReturn(errorListener);

		AuthenticatedViewListener listener = new AuthenticatedViewListener(view, container);
		listener.onError(error);

        Mockito.verify(view).onAfterAuthenticate(container);
        Mockito.verify(view).removeAllViews();
        Mockito.verify(view).addView(subView);
        Mockito.verify(view).showError(null, error);
        Mockito.verify(errorListener).onError(error);
	}
	
	public void testOnAuthSuccess() {
		AuthenticatedView view = Mockito.mock(AuthenticatedView.class);
		View subView = Mockito.mock(View.class);
		IOCContainer container = Mockito.mock(IOCContainer.class);

        Mockito.when(view.getView()).thenReturn(subView);

		AuthenticatedViewListener listener = new AuthenticatedViewListener(view, container);
		listener.onAuthSuccess(null);

        Mockito.verify(view).onAfterAuthenticate(container);
        Mockito.verify(view).removeAllViews();
        Mockito.verify(view).addView(subView);
	}
	
	public void testOnAuthFail() {
		AuthenticatedView view = Mockito.mock(AuthenticatedView.class);
		View subView = Mockito.mock(View.class);
		SocializeException error =  new SocializeException("TEST ERROR - IGNORE");
		IOCContainer container = Mockito.mock(IOCContainer.class);
		SocializeListener errorListener = Mockito.mock(SocializeListener.class);

		Mockito.when(view.getView()).thenReturn(subView);
		Mockito.when(view.getOnErrorListener()).thenReturn(errorListener);

		AuthenticatedViewListener listener = new AuthenticatedViewListener(view, container);
		listener.onAuthFail(error);

        Mockito.verify(view).onAfterAuthenticate(container);
        Mockito.verify(view).removeAllViews();
        Mockito.verify(view).addView(subView);
        Mockito.verify(view).showError(null, error);
        Mockito.verify(errorListener).onError(error);
	}
	
}
