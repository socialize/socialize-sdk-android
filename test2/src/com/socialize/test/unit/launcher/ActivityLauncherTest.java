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
package com.socialize.test.unit.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.launcher.ActivityLauncher;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.AppUtils;
import com.socialize.util.EntityLoaderUtils;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ AppUtils.class, Intent.class, Activity.class, EntityLoaderUtils.class })
public class ActivityLauncherTest extends SocializeUnitTest {

	public void testActivityLauncher() {
		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		Activity activity = AndroidMock.createMock(Activity.class);
		EntityLoaderUtils utils = AndroidMock.createMock(EntityLoaderUtils.class);
		final Intent intent = AndroidMock.createMock(Intent.class);
		final Class<?> activityClass = String.class;
		final Bundle extras = new Bundle(); // can't mock

		AndroidMock.expect(appUtils.isActivityAvailable(activity, activityClass)).andReturn(true);
		AndroidMock.expect(intent.putExtras(extras)).andReturn(intent);
		activity.startActivity(intent);
		AndroidMock.expect(utils.initEntityLoader()).andReturn(null);

		AndroidMock.replay(appUtils, utils, intent, activity);

		ActivityLauncher launcher = new ActivityLauncher() {

			@Override
			public Class<?>[] getActivityClasses() {
				return new Class<?>[] { activityClass };
			}

			@Override
			protected Intent newIntent(Activity context, Class<?> activityClass) {
				addResult(0, activityClass);
				return intent;
			}

			@Override
			protected void handleIntent(Activity context, Intent intent, Bundle data) {
				addResult(1, intent);
				addResult(2, data);
			}
		};

		launcher.setEntityLoaderUtils(utils);
		launcher.setAppUtils(appUtils);

		assertTrue(launcher.launch(activity, extras));

		AndroidMock.verify(appUtils, utils, intent, activity);
	}

}
