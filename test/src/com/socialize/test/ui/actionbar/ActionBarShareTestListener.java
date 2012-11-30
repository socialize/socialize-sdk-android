package com.socialize.test.ui.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeAccess;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.share.ShareDialogListener;


public class ActionBarShareTestListener extends ActionBarTest {
	
	@Override
	public boolean isManual() {
		return false;
	}
	

	@UsesMocks({OnActionBarEventListener.class})
	public void testShareButtonCallsActionBarListener() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);
		
		final Like mockLike = new Like();
		mockLike.setEntity(entity);
		mockLike.setId(0L);
		
		SocializeLikeUtils likeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(mockLike);
			}
		};
		
		SocializeShareUtils shareUtils = new SocializeShareUtils() {
			@Override
			public void showShareDialog(Activity context, Entity entity, int options, SocialNetworkShareListener socialNetworkListener, ShareDialogListener dialogListener) {
				
			}
		};
		
		SocializeAccess.setShareUtilsProxy(shareUtils);
		SocializeAccess.setLikeUtilsProxy(likeUtils);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	
		final ActionBarView actionBarView = TestUtils.findView(activity, ActionBarView.class, 10000);	
		
		assertNotNull(actionBar);
		assertNotNull(actionBarView);

		OnActionBarEventListener listener = AndroidMock.createMock(OnActionBarEventListener.class);
		
		AndroidMock.expect(listener.onClick(actionBarView, ActionBarEvent.SHARE)).andReturn(false);	
		
		actionBar.setOnActionBarEventListener(listener);	
		
		AndroidMock.replay(listener);
		
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
		
		AndroidMock.verify(listener);	
	}	
	
}
