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
import com.socialize.Socialize;
import com.socialize.launcher.LaunchAction;
import com.socialize.notifications.NotificationType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * @author Jason Polites
 *
 */
public class DirectEntityNotificationTest extends C2DMSimulationTest {
	
	// https://docs.google.com/a/getsocialize.com/document/d/1KlsIM4VFyNkJY7g4E9VdH3rv_FxuNJhn3VCiStWGyEY/edit?authkey=CK223CU#heading=h.t8xa2p6i4big
	@Override
	protected JSONObject getNotificationMessagePacket() throws JSONException, IOException {
		JSONObject data = new JSONObject();
		data.put("message", "Test DirectEntity message");
		data.put("entity_id", getEntityId());
		data.put("source", "socialize");
		data.put("notification_type", NotificationType.DEVELOPER_DIRECT_ENTITY.name().toLowerCase());
		return data;
	}
	
	@Override
	protected void assertNotificationBundle(Bundle extras) throws IOException, JSONException {
		Object id = extras.get(Socialize.ENTITY_ID);
		assertNotNull(id);
		assertTrue(id instanceof Long);
		assertEquals(getEntityId(), ((Long)id).longValue());
	}

	@Override
	protected LaunchAction getExpectedLaunchAction() {
		return LaunchAction.ENTITY;
	}
	
	@Override
	protected String getLauncherBeanName() {
		return "entityLauncher";
	}
}
