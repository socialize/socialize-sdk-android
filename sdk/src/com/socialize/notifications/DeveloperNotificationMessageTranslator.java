package com.socialize.notifications;

import android.content.Context;
import android.os.Bundle;

import com.socialize.error.SocializeException;

public class DeveloperNotificationMessageTranslator implements MessageTranslator<SimpleNotificationMessage> {

	@Override
	public SimpleNotificationMessage translate(Context context, Bundle data, NotificationMessage message) throws SocializeException {
		SimpleNotificationMessage msg = newSimpleNotificationMessage();
		msg.setText(message.getText());
		msg.setTitle("Developer Message");
		return msg;
	}
	
	protected SimpleNotificationMessage newSimpleNotificationMessage() {
		return new SimpleNotificationMessage();
	}
}
