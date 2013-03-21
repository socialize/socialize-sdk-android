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
package com.socialize.test.integration.notification;

import android.os.Bundle;
import com.socialize.launcher.LaunchAction;
import com.socialize.notifications.NotificationType;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class DeveloperNotificationTest extends C2DMSimulationTest {

	final String actionId = "123";
	
	/* (non-Javadoc)
	 * @see com.socialize.test.integration.notification.C2DMSimulationTest#getNotificationMessagePacket()
	 */
	@Override
	protected JSONObject getNotificationMessagePacket() throws Exception {
		JSONObject data = new JSONObject();
		data.put("message", "Test Developer message");
		data.put("source", "socialize");
		data.put("notification_type", NotificationType.DEVELOPER_NOTIFICATION.name().toLowerCase());
		return data;
	}

	/* (non-Javadoc)
	 * @see com.socialize.test.integration.notification.C2DMSimulationTest#assertNotificationBundle(android.os.Bundle)
	 */
	@Override
	protected void assertNotificationBundle(Bundle extras) throws Exception {
	}

	@Override
	protected LaunchAction getExpectedLaunchAction() {
		return LaunchAction.HOME;
	}

	@Override
	protected String getLauncherBeanName() {
		return "homeLauncher";
	}
	
	
}
