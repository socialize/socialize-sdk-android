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
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareListListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


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
				fail();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Share> items = getResult(0);
		assertNotNull(items);
		assertEquals(2, items.size());
	}
	
	public void testGetSharesByEntity() throws SocializeException, InterruptedException {
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ShareUtils.getSharesByEntity(getActivity(), entity, 0, 100, new ShareListListener() {
			
			
			@Override
			public void onList(ListResult<Share> entities) {
				addResult(entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Share> items = getResult(0);
		assertNotNull(items);
		assertEquals(1, items.size());
	}	
}
