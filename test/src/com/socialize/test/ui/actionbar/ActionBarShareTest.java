package com.socialize.test.ui.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.SocializeAccess;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.entity.Entity;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.share.ShareDialogListener;


public class ActionBarShareTest extends ActionBarTest {
	
	@Override
	public boolean isManual() {
		return false;
	}
	
	public void testShareButtonLoadsShareView() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);
		
		assertNotNull(activity);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		SocializeShareUtils shareUtils = new SocializeShareUtils() {

			@Override
			public void showShareDialog(Activity context, Entity entity, int options, SocialNetworkShareListener socialNetworkListener, ShareDialogListener dialogListener) {
				TestUtils.addResult(0, "success");
				latch.countDown();
			}
		};
		
		SocializeAccess.setShareUtilsProxy(shareUtils);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	
		
		assertNotNull(actionBar);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getShareButton().performClick());
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		String result = TestUtils.getResult(0);
		
		assertNotNull(result);
		assertEquals("success", result);
	}
	
}
