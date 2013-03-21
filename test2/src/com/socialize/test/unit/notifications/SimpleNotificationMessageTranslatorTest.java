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

import com.socialize.api.action.ActionType;
import com.socialize.error.SocializeException;
import com.socialize.notifications.NotificationMessage;
import com.socialize.notifications.SimpleNotificationMessage;
import com.socialize.notifications.SimpleNotificationMessageTranslator;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class SimpleNotificationMessageTranslatorTest extends SocializeUnitTest {

	public void testTranslate() throws SocializeException {
		
		NotificationMessage message = new NotificationMessage();
		
		final String text = "foo";
		final String user = "foo_user";
		final String entity = "foo_entity";
		final ActionType actionType = ActionType.COMMENT;
		
		message.setUser(user);
		message.setActionType(actionType);
		message.setEntity(entity);
		message.setText(text);
		
		SimpleNotificationMessageTranslator translator = new SimpleNotificationMessageTranslator();
		
		SimpleNotificationMessage translated = translator.translate(getContext(), message);
		
		assertNotNull(translated.getText());
		assertNotNull(translated.getTitle());
		
		assertEquals("foo_user commented on foo_entity", translated.getTitle());
		assertEquals("foo", translated.getText());
	}
	
}
