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
package com.socialize.notifications;

import android.content.Context;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.error.SocializeException;

/**
 * @author Jason Polites
 *
 */
public class DirectEntityMessageTranslator implements MessageTranslator<SimpleNotificationMessage> {
	/*
	 * (non-Javadoc)
	 * @see com.socialize.notifications.MessageTranslator#translateTo(android.content.Context, android.os.Bundle, com.socialize.notifications.NotificationMessage)
	 */
	@Override
	public SimpleNotificationMessage translate(Context context, Bundle data, NotificationMessage message) throws SocializeException {
		SimpleNotificationMessage notify = new SimpleNotificationMessage();

		if(message.getEntityId() != null) {
			notify.setTitle(message.getText());
			notify.setText(message.getText());
			
			data.putLong( Socialize.ENTITY_ID , message.getEntityId());
		}
		else {
			throw new SocializeException("No entity id found in notification of type [" +
					message.getNotificationType() +
					"]");
		}		
		return notify;
	}
}
