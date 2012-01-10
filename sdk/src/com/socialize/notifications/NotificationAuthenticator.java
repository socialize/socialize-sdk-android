package com.socialize.notifications;

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;

public interface NotificationAuthenticator {
	public SocializeSession authenticate(Context context) throws SocializeException;
}
