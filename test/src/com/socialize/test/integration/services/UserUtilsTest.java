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
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.profile.ProfileActivity;


/**
 * @author Jason Polites
 *
 */
public class UserUtilsTest extends SocializeActivityTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().clearSessionCache(getContext());
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetCurrentUser() throws Exception {
		
		User user = UserUtils.getCurrentUser(getActivity());
		
		assertNotNull(user);
		
		Socialize.getSocialize().destroy(true);
		
		User user2 = UserUtils.getCurrentUser(getActivity());
		
		assertEquals(user, user2);
		
	}
	
	public void testShowUserProfile()  {
		TestUtils.setUp(this);
		
		TestUtils.setUpActivityMonitor(ProfileActivity.class);
		
		UserUtils.showUserSettings(getActivity());
		
		ProfileActivity profile = TestUtils.waitForActivity(20000);
		
		assertNotNull(profile);
		
		User currentUser = UserUtils.getCurrentUser(getActivity());
		
		// Look for the ID
		assertTrue(TestUtils.findText(profile, String.valueOf(currentUser.getId()), 20000));
		
		profile.finish();
	}
	
	public void testSaveUser() throws InterruptedException  {
		
		User user = UserUtils.getCurrentUser(getActivity());
		String name = "foobar" + Math.random();
		user.setFirstName(name);
		
		long idBefore = user.getId();
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		UserUtils.saveUserSettings(getContext(), user, new UserSaveListener() {
			
			@Override
			public void onUpdate(User entity) {
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		
		latch.await(20, TimeUnit.SECONDS);
		
		Socialize.getSocialize().destroy(true);
		
		User after = UserUtils.getCurrentUser(getActivity());
		
		long idAfter = after.getId();
		
		String firstName = after.getFirstName();
		
		assertNotNull(firstName);
		assertEquals(idBefore, idAfter);
		assertEquals(name, firstName);
	}
}
