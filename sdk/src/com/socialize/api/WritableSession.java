package com.socialize.api;

import com.socialize.entity.User;

public interface WritableSession extends SocializeSession {
	public void setConsumerToken(String token);
	public void setConsumerTokenSecret(String secret);
	public void setUser(User user);
	public void setHost(String host);
}
