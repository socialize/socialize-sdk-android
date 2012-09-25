package com.socialize.notifications;

import android.content.Context;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;

public interface NotificationAuthenticator {
	public SocializeSession authenticate(Context context) throws SocializeException;
	
	public void authenticateAsync(Context context, SocializeAuthListener listener) throws SocializeException;
}
