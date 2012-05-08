package com.socialize.test.ui.integrationtest.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ioc.SocializeIOC;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.dialog.AuthDialogFactory;

public class ShareDialogViewTest extends ActionBarAutoTest {
	
	public void testShareButtonLoadsShareView() throws Throwable {
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		extras.putSerializable(Socialize.ENTITY_OBJECT, entity);
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final AuthDialogFactory mockAuthDialogFactory = new AuthDialogFactory() {
			
			@Override
			public Dialog show(View parent, int displayOptions) {
				return new Dialog(parent.getContext());
			}
			
			@Override
			public Dialog show(View parent, SocialNetworkListener socialNetworkListener, ShareDialogListener listener, int displayOptions) {
				return new Dialog(parent.getContext());
			}
			
			@Override
			public Dialog show(Context context, ShareDialogListener listener, int displayOptions) {
				return new Dialog(context);
			}
			
			@Override
			public Dialog show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				addResult(0, "success");
				latch.countDown();
				return new Dialog(context);
			}
		};
		
		SocializeIOC.registerStub("authRequestDialogFactory", mockAuthDialogFactory);
		
		TestUtils.setupSocializeOverrides(true, true);
		
		getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 10000);	
		
		assertNotNull(actionBar);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getShareButton().performClick());
			}
		});
		
		latch.await(10, TimeUnit.SECONDS);
		
		SocializeIOC.unregisterStub("authRequestDialogFactory");
		
		String result = getResult(0);
		
		assertNotNull(result);
		assertEquals("success", result);
	}
	
	@UsesMocks({OnActionBarEventListener.class})
	public void testShareButtonCallsActionBarListener() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		extras.putSerializable(Socialize.ENTITY_OBJECT, entity);
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 10000);	
		final ActionBarView actionBarView = TestUtils.findView(getActivity(), ActionBarView.class, 10000);	
		
		assertNotNull(actionBar);
		assertNotNull(actionBarView);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				OnActionBarEventListener listener = AndroidMock.createMock(OnActionBarEventListener.class);
				
				AndroidMock.expect(listener.onClick(actionBarView, ActionBarEvent.SHARE)).andReturn(false);	
				
				actionBar.setOnActionBarEventListener(listener);
				
				AndroidMock.replay(listener);
				
				assertTrue(actionBar.getShareButton().performClick());
				
				AndroidMock.verify(listener);	
			}
		});
	}
}
