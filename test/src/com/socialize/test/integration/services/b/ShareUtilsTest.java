/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.test.integration.services.b;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.socialize.ShareUtils;
import com.socialize.SocializeAccess;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookShareCell;
import com.socialize.networks.twitter.TwitterShareCell;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.EmailCell;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.SMSCell;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 *
 */
public class ShareUtilsTest extends SocializeActivityTest {
	
	public void testGetShareExists() throws Exception {
		
		JSONObject json = TestUtils.getJSON(getContext(), "shares.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		String id = jsonObject.getString("id");
		
		long shareId = Long.parseLong(id);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getShare(TestUtils.getActivity(this), new ShareGetListener() {
			
			@Override
			public void onGet(Share entity) {
				addResult(0, entity);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		}, shareId);
		
		latch.await(20, TimeUnit.SECONDS);
		
		Share share = getResult(0);
		assertNotNull(share);
		assertEquals(shareId, share.getId().longValue());
	}
	
	public void testGetShareDoesNotExist() throws InterruptedException {
		
		long shareId = 69696966;
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getShare(TestUtils.getActivity(this), new ShareGetListener() {
			
			@Override
			public void onGet(Share entity) {
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				addResult(0, error);
				latch.countDown();
			}
		}, shareId);
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeException error = getResult(0);
		assertNotNull(error);
		assertTrue((error instanceof SocializeApiError));
		assertEquals(404,  ((SocializeApiError)error).getResultCode());
	}
	

	public void testGetSharesByUser() throws Exception {
		final User user = new User();
		final CountDownLatch latch = new CountDownLatch(1);
		
		
		JSONObject json = TestUtils.getJSON(getContext(), "shares.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		JSONObject userObject = jsonObject.getJSONObject("user");
		String userId = userObject.getString("id");	
		
		user.setId(Long.parseLong(userId));
		
		ShareUtils.getSharesByUser(TestUtils.getActivity(this), user, 0, 100, new ShareListListener() {
			@Override
			public void onList(ListResult<Share> entities) {
				addResult(entities);
				latch.countDown();
			}

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Share> items = getResult(0);
		assertNotNull(items);
		assertEquals(2, items.size());
	}
	
	public void testGetSharesByEntity() throws SocializeException, InterruptedException {
		final String entityKey = "http://entity1.com";
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getSharesByEntity(TestUtils.getActivity(this), entityKey, 0, 100, new ShareListListener() {
			@Override
			public void onList(ListResult<Share> entities) {
				addResult(entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Share> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}	
	
	public void testGetSharesByApplication() throws SocializeException, InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getSharesByApplication(TestUtils.getActivity(this), 0, 100, new ShareListListener() {
			@Override
			public void onList(ListResult<Share> entities) {
				addResult(entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Share> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}		
	
	public void testShowShareDialogDefault() throws Exception {
		final Entity entityKey = Entity.newInstance("http://entity1.com", "http://entity1.com");
		final CountDownLatch latch0 = new CountDownLatch(1);
		
		ShareUtils.showShareDialog(getContext(), entityKey, new SocialNetworkDialogListener() {
			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {
				addResult(0, dialogView);
				addResult(1, dialog);
				latch0.countDown();
			}
		}, ShareUtils.DEFAULT);
		
		latch0.await(20, TimeUnit.SECONDS);
		
		SharePanelView view = getResult(0);
		Dialog dialog = getResult(1);
		
		assertNotNull(view);
		
		final FacebookShareCell fbButton = TestUtils.findView(view, FacebookShareCell.class);
		final TwitterShareCell twButton = TestUtils.findView(view, TwitterShareCell.class);
		final EmailCell emailCell = TestUtils.findView(view, EmailCell.class);
		final SMSCell smsCell = TestUtils.findView(view, SMSCell.class);
		
		dialog.dismiss();
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		assertNotNull(emailCell);
		assertNotNull(smsCell);
		
		assertEquals(View.VISIBLE, fbButton.getVisibility());
		assertEquals(View.VISIBLE, twButton.getVisibility());
		assertEquals(View.VISIBLE, emailCell.getVisibility());
		assertEquals(View.VISIBLE, smsCell.getVisibility());
	}
	
	public void testShowShareDialogSocial() throws Exception {
		final Entity entityKey = Entity.newInstance("http://entity1.com", "http://entity1.com");
		final CountDownLatch latch0 = new CountDownLatch(1);
		
		ShareUtils.showShareDialog(getContext(), entityKey, new SocialNetworkDialogListener() {
			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {
				addResult(0, dialogView);
				addResult(1, dialog);
				latch0.countDown();
			}
		}, ShareUtils.SOCIAL);
		
		latch0.await(20, TimeUnit.SECONDS);
		
		SharePanelView view = getResult(0);
		Dialog dialog = getResult(1);
		
		assertNotNull(view);
		
		final FacebookShareCell fbButton = TestUtils.findView(view, FacebookShareCell.class);
		final TwitterShareCell twButton = TestUtils.findView(view, TwitterShareCell.class);
		final EmailCell emailCell = TestUtils.findView(view, EmailCell.class);
		final SMSCell smsCell = TestUtils.findView(view, SMSCell.class);
		
		dialog.dismiss();
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		assertNotNull(emailCell);
		assertNotNull(smsCell);
		
		assertEquals(View.VISIBLE, fbButton.getVisibility());
		assertEquals(View.VISIBLE, twButton.getVisibility());
		assertEquals(View.GONE, emailCell.getVisibility());
		assertEquals(View.GONE, smsCell.getVisibility());
	}	
	
	public void testShowShareDialogAndContinueWithoutFlowControl() throws Exception {
		final Activity context = TestUtils.getActivity(this);
		final SocialNetwork network = SocialNetwork.TWITTER;
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		
		IShareDialogFactory mockShareDialogFactory = new IShareDialogFactory() {
			@Override
			public void preload(Context context) {}
			
			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				// Immediately call onContinue
				shareDialoglistener.onContinue(null, true, network);
			}
		};
		
		
		final SocializeShareUtils mockShareUtils = new SocializeShareUtils() {
			
			@Override
			protected void doShare(Dialog dialog, Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, ShareOptions shareOptions, SocialNetwork... networks) {
				addResult(0, entity);
				addResult(1, networks);
			}
			
		};
		
		mockShareUtils.setShareDialogFactory(mockShareDialogFactory);
		
		SocializeAccess.setShareUtilsProxy(mockShareUtils);
		
		final SocialNetworkDialogListener mockListener = new SocialNetworkDialogListener() {

			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {}

			@Override
			public void onCancel(Dialog dialog) {}
			
			@Override
			public void onSimpleShare(ShareType type) {}

			@Override
			public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
				// Don't consume
				return false;
			}

			@Override
			public void onFlowInterrupted(DialogFlowController controller) {}
		};
		
		ShareUtils.showShareDialog(context, entity, mockListener);
		
		Entity entityAfter = getResult(0);
		SocialNetwork[] networkAfter = getResult(1);
		
		assertNotNull(entityAfter);
		assertNotNull(networkAfter);
		
		assertEquals(network, networkAfter[0]);
		assertEquals(entity.getKey(), entityAfter.getKey());
	}
	
	public void testShowShareDialogAndContinueWithFlowControl() throws Exception {
		final Activity context = TestUtils.getActivity(this);
		final SocialNetwork network = SocialNetwork.TWITTER;
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		final String text = "foobar";
		
		IShareDialogFactory mockShareDialogFactory = new IShareDialogFactory() {
			@Override
			public void preload(Context context) {}
			
			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				// Immediately call onContinue
				shareDialoglistener.onContinue(null, true, network);
			}
		};
		
		final SocializeShareUtils mockShareUtils = new SocializeShareUtils() {
			
			@Override
			protected void doShare(Dialog dialog, Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, ShareOptions shareOptions, SocialNetwork... networks) {
				addResult(0, entity);
				addResult(1, networks);
				addResult(2, shareOptions);
			}
		};
		
		mockShareUtils.setShareDialogFactory(mockShareDialogFactory);
		
		SocializeAccess.setShareUtilsProxy(mockShareUtils);
		
		final SocialNetworkDialogListener mockListener = new SocialNetworkDialogListener() {

			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {}

			@Override
			public void onCancel(Dialog dialog) {}
			
			@Override
			public void onSimpleShare(ShareType type) {}

			@Override
			public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
				// consume
				return true;
			}

			@Override
			public void onFlowInterrupted(DialogFlowController controller) {
				// Immediately call continue
				controller.onContinue(text);
			}
		};
		
		ShareUtils.showShareDialog(context, entity, mockListener);
		
		Entity entityAfter = getResult(0);
		SocialNetwork[] networkAfter = getResult(1);
		ShareOptions shareOptions = getResult(2);
		
		assertNotNull(entityAfter);
		assertNotNull(networkAfter);
		assertNotNull(shareOptions);
		
		assertEquals(network, networkAfter[0]);
		assertEquals(entity.getKey(), entityAfter.getKey());
		assertEquals(text, shareOptions.getText());
	}	
	
	
	public void testShowShareDialogAndCancel() throws Exception {
		final Activity context = TestUtils.getActivity(this);
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		
		IShareDialogFactory mockShareDialogFactory = new IShareDialogFactory() {
			@Override
			public void preload(Context context) {}
			
			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				// Immediately call cancel
				shareDialoglistener.onCancel(null);
			}
		};
		
		final SocializeShareUtils mockShareUtils = new SocializeShareUtils() {
			
			@Override
			protected void doShare(Dialog dialog, Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, ShareOptions shareOptions, SocialNetwork... networks) {
				addResult(0, "fail");
			}
			
		};
		
		mockShareUtils.setShareDialogFactory(mockShareDialogFactory);
		
		SocializeAccess.setShareUtilsProxy(mockShareUtils);
		
		final SocialNetworkDialogListener mockListener = new SocialNetworkDialogListener() {

			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {}
			
			@Override
			public void onSimpleShare(ShareType type) {}

			@Override
			public void onCancel(Dialog dialog) {
				addResult(0, "success");
			}

			@Override
			public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
				// Don't consume
				return false;
			}

			@Override
			public void onFlowInterrupted(DialogFlowController controller) {}
		};
		
		ShareUtils.showShareDialog(context, entity, mockListener);
		
		String result = getResult(0);
		
		assertNotNull(result);
		
		assertEquals("success", result);
	}	
	
}
