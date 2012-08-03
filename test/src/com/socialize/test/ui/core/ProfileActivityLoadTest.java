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
package com.socialize.test.ui.core;

import android.app.Activity;
import android.content.Context;
import com.socialize.ConfigUtils;
import com.socialize.SocializeAccess;
import com.socialize.UserUtils;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.User;
import com.socialize.listener.user.UserGetListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.profile.ProfileActivity;

/**
 * @author Jason Polites
 *
 */
public class ProfileActivityLoadTest extends SocializeActivityTest {
	
	public void testProfileActivityLoadsCorrectData() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		TestUtils.setUpActivityMonitor(ProfileActivity.class);
		
		final User dummy = new User();
		
		dummy.setId(69L);
		dummy.setFirstName("foo");
		dummy.setLastName("bar");
		
		SocializeUserUtils userUtils = new SocializeUserUtils() {
			@Override
			public void getUser(Context context, long id, UserGetListener listener) {
				listener.onGet(dummy);
			}

			@Override
			public User getCurrentUser(Context context) {
				return dummy;
			}
		};
		
		SocializeAccess.setUserUtilsProxy(userUtils);
		
		// Ensure facebook is enabled
		ConfigUtils.getConfig(getContext()).setFacebookAppId("1234567890");
		ConfigUtils.getConfig(getContext()).setTwitterKeySecret("U18LUnVjULkkpGoJ6CoP3A", "RiIljnFq4RWV9LEaCM1ZLsAHf053vX2KyhJhmCOlBE");
		
		UserUtils.showUserSettings(TestUtils.getActivity(this));
		
		Activity waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		// Check that the user's name is displayed
		assertTrue(TestUtils.lookForText(waitForActivity, "foo", 10000));
		assertTrue(TestUtils.lookForText(waitForActivity, "bar", 10000));
		
		waitForActivity.finish();
	}
}
