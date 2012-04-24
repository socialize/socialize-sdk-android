package com.socialize.test.ui.integrationtest.actionbar;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.entity.Entity;
import com.socialize.listener.share.ShareListener;
import com.socialize.test.mock.MockShareSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.share.ShareDialogView;
import com.socialize.ui.view.SocializeButton;

public class ShareDialogViewTest extends ActionBarAutoTest {

	public void testShareButtonLoadsShareView() throws Throwable {

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
				assertTrue(actionBar.getShareButton().performClick());
				ShareDialogView shareView = TestUtils.findView(getActivity(), ShareDialogView.class, 10000);	
				assertNotNull(shareView);
				assertTrue(shareView.isShown());
			}
		});
	}
	
	public void testShareFunctions() throws Throwable {

		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		extras.putSerializable(Socialize.ENTITY_OBJECT, entity);
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 20000);	
		final ActionBarView actionBarView = TestUtils.findView(getActivity(), ActionBarView.class, 20000);	

		assertNotNull(actionBar);
		assertNotNull(actionBarView);
		
		// Override the container in the bean factories for the click handlers
		
		ProxyObject<ShareSystem> proxy = SocializeAccess.getContainer().getProxy("shareSystem");
		
		proxy.setDelegate(new MockShareSystem() {
			int count = 0;
			
			@Override
			public boolean canShare(Context context, ShareType shareType) {
				return true;
			}

			@Override
			public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
				addResult(count, shareType);
				count++;
			}
		});

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
		
		ShareDialogView shareView = TestUtils.findView(getActivity(), ShareDialogView.class, 10000);	
		
		assertNotNull(shareView);
		assertTrue(shareView.isShown());
		
		List<SocializeButton> findViews = TestUtils.findViews(shareView, SocializeButton.class);

		assertNotNull(findViews);
		
		assertEquals(4, findViews.size());
		
		final SocializeButton btnFacebook = findViews.get(0);
		final SocializeButton btnTwitter = findViews.get(1);
		final SocializeButton btnEmail = findViews.get(2);
		final SocializeButton btnSMS =findViews.get(3);
		final TextView otherShareOptions = TestUtils.findViewWithText(getActivity(), TextView.class, "More options...", 10000);
		
		assertNotNull(btnFacebook);
		assertNotNull(btnTwitter);
		assertNotNull(btnEmail);
		assertNotNull(btnSMS);
		assertNotNull(otherShareOptions);
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(btnFacebook.performClick());
				assertTrue(btnTwitter.performClick());
				assertTrue(btnEmail.performClick());
				assertTrue(btnSMS.performClick());
				assertTrue(otherShareOptions.performClick());
				latch2.countDown();
			}
		});		
		
		assertTrue(latch2.await(10, TimeUnit.SECONDS));
		
		ShareType shareType0 = getResult(0);
		ShareType shareType1= getResult(1);
		ShareType shareType2 = getResult(2);
		ShareType shareType3 = getResult(3);
		ShareType shareType4 = getResult(4);
		
		
		assertNotNull(shareType0);
		assertNotNull(shareType1);
		assertNotNull(shareType2);
		assertNotNull(shareType3);
		assertNotNull(shareType4);
		
		assertEquals(ShareType.FACEBOOK, shareType0);
		assertEquals(ShareType.TWITTER, shareType1);
		assertEquals(ShareType.EMAIL, shareType2);
		assertEquals(ShareType.SMS, shareType3);
		assertEquals(ShareType.OTHER, shareType4);
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
