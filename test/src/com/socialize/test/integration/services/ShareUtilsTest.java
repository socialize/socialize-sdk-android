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
package com.socialize.test.integration.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Dialog;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.facebook.FacebookShareCell;
import com.socialize.networks.twitter.TwitterShareCell;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.share.EmailCell;
import com.socialize.ui.share.SMSCell;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 *
 */
public class ShareUtilsTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		Socialize.getSocialize().destroy(true);
		super.tearDown();
	}
	
	public void testGetShareExists() throws Exception {
		
		JSONObject json = TestUtils.getJSON(getContext(), "shares.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		String id = jsonObject.getString("id");
		
		long shareId = Long.parseLong(id);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getShare(getActivity(), new ShareGetListener() {
			
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
		
		ShareUtils.getShare(getActivity(), new ShareGetListener() {
			
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
		
		ShareUtils.getSharesByUser(getActivity(), user, 0, 100, new ShareListListener() {
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
		
		ShareUtils.getSharesByEntity(getActivity(), entityKey, 0, 100, new ShareListListener() {
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
				latch0.countDown();
			}
		}, ShareUtils.DEFAULT);
		
		latch0.await(20, TimeUnit.SECONDS);
		
		SharePanelView view = getResult(0);
		
		assertNotNull(view);
		
		final FacebookShareCell fbButton = TestUtils.findView(view, FacebookShareCell.class);
		final TwitterShareCell twButton = TestUtils.findView(view, TwitterShareCell.class);
		final EmailCell emailCell = TestUtils.findView(view, EmailCell.class);
		final SMSCell smsCell = TestUtils.findView(view, SMSCell.class);
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		assertNotNull(emailCell);
		assertNotNull(smsCell);
	}
	
	public void testShowShareDialogSocial() throws Exception {
		final Entity entityKey = Entity.newInstance("http://entity1.com", "http://entity1.com");
		final CountDownLatch latch0 = new CountDownLatch(1);
		
		ShareUtils.showShareDialog(getContext(), entityKey, new SocialNetworkDialogListener() {
			@Override
			public void onShow(Dialog dialog, SharePanelView dialogView) {
				addResult(0, dialogView);
				latch0.countDown();
			}
		}, ShareUtils.SOCIAL);
		
		latch0.await(20, TimeUnit.SECONDS);
		
		SharePanelView view = getResult(0);
		
		assertNotNull(view);
		
		final FacebookShareCell fbButton = TestUtils.findView(view, FacebookShareCell.class);
		final TwitterShareCell twButton = TestUtils.findView(view, TwitterShareCell.class);
		final EmailCell emailCell = TestUtils.findView(view, EmailCell.class);
		final SMSCell smsCell = TestUtils.findView(view, SMSCell.class);
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		assertNull(emailCell);
		assertNull(smsCell);
	}	
}
