package com.socialize.notifications;

import android.content.Context;
import android.os.Bundle;
import com.socialize.error.SocializeException;
import com.socialize.util.AppUtils;

public class DeveloperNotificationMessageTranslator implements MessageTranslator<SimpleNotificationMessage> {

	private AppUtils appUtils;
	
	@Override
	public SimpleNotificationMessage translate(Context context, Bundle data, NotificationMessage message) throws SocializeException {
		SimpleNotificationMessage msg = newSimpleNotificationMessage();
		msg.setText(message.getText());
		msg.setTitle("A message from " + appUtils.getAppName());
		return msg;
	}
	
	protected SimpleNotificationMessage newSimpleNotificationMessage() {
		return new SimpleNotificationMessage();
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
}
