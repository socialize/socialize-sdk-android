/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test.ui.profile;

import android.app.Activity;

import com.socialize.entity.User;
import com.socialize.sample.mocks.MockSocializeApiHost;
import com.socialize.test.ui.SocializeUIActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.profile.ProfileActivity;

/**
 * @author Jason Polites
 *
 */
public class ProfileActivityLoadTest extends SocializeUIActivityTest {
	
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

	public void testProfileActivityLoadsCorrectData() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		TestUtils.setUpActivityMonitor(ProfileActivity.class);
		
		User dummy = new User();
		
		dummy.setId(69L);
		dummy.setFirstName("foo");
		dummy.setLastName("bar");
		
		MockSocializeApiHost.orchestrateUser(dummy);
		
		SocializeUI.getInstance().showUserProfileView(getActivity(), "69");
		
		Activity waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		// Check that the user's name is displayed
		assertTrue(TestUtils.lookForText(waitForActivity, "foo", 10000));
		assertTrue(TestUtils.lookForText(waitForActivity, "bar", 10000));
		
		waitForActivity.finish();
	}
}
