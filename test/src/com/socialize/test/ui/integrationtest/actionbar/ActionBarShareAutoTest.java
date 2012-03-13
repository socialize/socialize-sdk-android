package com.socialize.test.ui.integrationtest.actionbar;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.BeanFactory;
import com.socialize.android.ioc.Container;
import com.socialize.sample.ui.ActionBarAutoActivity;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.share.ShareClickListener;
import com.socialize.ui.share.ShareDialogView;
import com.socialize.ui.view.SocializeButton;

@Deprecated
public class ActionBarShareAutoTest extends ActivityInstrumentationTestCase2<ActionBarAutoActivity> {

	public ActionBarShareAutoTest() {
		super("com.socialize.sample.ui", ActionBarAutoActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown();
		super.tearDown();
	}
	
	public void testShareButtonLoadsShareView() throws Throwable {

		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(SocializeUI.ENTITY_KEY, "http://entity1.com");
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
				assertTrue(actionBar.getShareButton().performClick());
				ShareDialogView shareView = TestUtils.findView(getActivity(), ShareDialogView.class, 10000);	
				assertNotNull(shareView);
				assertTrue(shareView.isShown());
			}
		});
	}
	
	@UsesMocks ({
		ShareClickListener.class,
		Container.class
	})
	// Testing all share functions together to save too much copy/paste of test code (is that bad?.. probably :/)
	public void testShareFunctions() throws Throwable {

		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(SocializeUI.ENTITY_KEY, "http://entity1.com");
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 10000);	
		final ActionBarView actionBarView = TestUtils.findView(getActivity(), ActionBarView.class, 10000);	

		assertNotNull(actionBar);
		assertNotNull(actionBarView);
		
		// Override the container in the bean factories for the click handlers
		Container mockContainer = AndroidMock.createMock(Container.class);
		
		BeanFactory<ShareClickListener> emailShareClickListenerFactory = SocializeAccess.getContainer().getBean("emailShareClickListenerFactory");
		BeanFactory<ShareClickListener> facebookShareClickListenerFactory = SocializeAccess.getContainer().getBean("facebookShareClickListenerFactory");
		BeanFactory<ShareClickListener> twitterShareClickListenerFactory = SocializeAccess.getContainer().getBean("twitterShareClickListenerFactory");
		BeanFactory<ShareClickListener> smsShareClickListenerFactory = SocializeAccess.getContainer().getBean("smsShareClickListenerFactory");
		
		emailShareClickListenerFactory.setContainer(mockContainer);
		facebookShareClickListenerFactory.setContainer(mockContainer);
		twitterShareClickListenerFactory.setContainer(mockContainer);
		smsShareClickListenerFactory.setContainer(mockContainer);
		
		ShareClickListener emailShareClickListener = AndroidMock.createMock(ShareClickListener.class);
		ShareClickListener facebookShareClickListener = AndroidMock.createMock(ShareClickListener.class);
		ShareClickListener twitterShareClickListener = AndroidMock.createMock(ShareClickListener.class);
		ShareClickListener smsShareClickListener = AndroidMock.createMock(ShareClickListener.class);
		
		AndroidMock.expect(mockContainer.getBean(AndroidMock.eq("emailShareClickListener"), AndroidMock.anyObject())).andReturn(emailShareClickListener);
		AndroidMock.expect(mockContainer.getBean(AndroidMock.eq("facebookShareClickListener"), AndroidMock.anyObject())).andReturn(facebookShareClickListener);
		AndroidMock.expect(mockContainer.getBean(AndroidMock.eq("twitterShareClickListener"), AndroidMock.anyObject())).andReturn(twitterShareClickListener);
		AndroidMock.expect(mockContainer.getBean(AndroidMock.eq("smsShareClickListener"), AndroidMock.anyObject())).andReturn(smsShareClickListener);
		
		AndroidMock.expect(emailShareClickListener.isAvailableOnDevice(getActivity())).andReturn(true);
		AndroidMock.expect(facebookShareClickListener.isAvailableOnDevice(getActivity())).andReturn(true);
		AndroidMock.expect(twitterShareClickListener.isAvailableOnDevice(getActivity())).andReturn(true);
		AndroidMock.expect(smsShareClickListener.isAvailableOnDevice(getActivity())).andReturn(true);
		
		AndroidMock.replay(emailShareClickListener);
		AndroidMock.replay(facebookShareClickListener);
		AndroidMock.replay(twitterShareClickListener);
		AndroidMock.replay(smsShareClickListener);			
		
		AndroidMock.replay(mockContainer);

		final CountDownLatch latch = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getShareButton().performClick());
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
	
		// Wait for view to show
		Thread.sleep(5000);
		
		AndroidMock.verify(mockContainer);
		AndroidMock.verify(emailShareClickListener);
		AndroidMock.verify(facebookShareClickListener);
		AndroidMock.verify(twitterShareClickListener);
		AndroidMock.verify(smsShareClickListener);		
		
		ShareDialogView shareView = TestUtils.findView(getActivity(), ShareDialogView.class, 10000);	
		
		assertNotNull(shareView);
		assertTrue(shareView.isShown());
		
		AndroidMock.resetToDefault(emailShareClickListener);
		AndroidMock.resetToDefault(facebookShareClickListener);
		AndroidMock.resetToDefault(twitterShareClickListener);
		AndroidMock.resetToDefault(smsShareClickListener);
		
		List<SocializeButton> findViews = TestUtils.findViews(shareView, SocializeButton.class);
		
		assertNotNull(findViews);
		assertEquals(4, findViews.size());
		
		final SocializeButton btnFacebook = findViews.get(0);
		final SocializeButton btnTwitter = findViews.get(1);
		final SocializeButton btnEmail = findViews.get(2);
		final SocializeButton btnSMS =findViews.get(3);
		
		assertNotNull(btnFacebook);
		assertNotNull(btnTwitter);
		assertNotNull(btnEmail);
		assertNotNull(btnSMS);
		
		emailShareClickListener.onClick(btnEmail);
		facebookShareClickListener.onClick(btnFacebook);
		twitterShareClickListener.onClick(btnTwitter);
		smsShareClickListener.onClick(btnSMS);
		
		AndroidMock.replay(emailShareClickListener);
		AndroidMock.replay(facebookShareClickListener);
		AndroidMock.replay(twitterShareClickListener);
		AndroidMock.replay(smsShareClickListener);				
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(btnFacebook.performClick());
				assertTrue(btnTwitter.performClick());
				assertTrue(btnEmail.performClick());
				assertTrue(btnSMS.performClick());
				latch2.countDown();
			}
		});		
		
		assertTrue(latch2.await(10, TimeUnit.SECONDS));
		
		AndroidMock.verify(emailShareClickListener);
		AndroidMock.verify(facebookShareClickListener);
		AndroidMock.verify(twitterShareClickListener);
		AndroidMock.verify(smsShareClickListener);		
	}	

	@UsesMocks({OnActionBarEventListener.class})
	public void testShareButtonCallsActionBarListener() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(SocializeUI.ENTITY_KEY, "http://entity1.com");
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
				
				listener.onClick(actionBarView, ActionBarEvent.SHARE);	
				
				actionBar.setOnActionBarEventListener(listener);
				
				AndroidMock.replay(listener);
				
				assertTrue(actionBar.getShareButton().performClick());
				
				AndroidMock.verify(listener);	
			}
		});
	}
}
