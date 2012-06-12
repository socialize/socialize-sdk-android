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
package com.socialize.test.unit.notifications;

import android.content.Context;
import android.os.Bundle;
import com.socialize.api.SocializeSession;
import com.socialize.notifications.NotificationMessage;
import com.socialize.notifications.SocializeC2DMCallback;


/**
 * @author Jason Polites
 *
 */
public class PublicSocializeC2DMCallback extends SocializeC2DMCallback {
	
	@Override
	public void handleNotification(Context context, Bundle data, SocializeSession session) {
		super.handleNotification(context, data, session);
	}

	@Override
	public int getNotificationIcon(Context context) {
		return super.getNotificationIcon(context);
	}

	@Override
	public int getNotificationId(NotificationMessage message) {
		return super.getNotificationId(message);
	}

	@Override
	public void handleError(String msg) {
		super.handleError(msg);
	}

	@Override
	public void handleError(String msg, Exception e) {
		super.handleError(msg, e);
	}

}
