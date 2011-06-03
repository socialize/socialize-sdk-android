package com.socialize.oauth;

import org.apache.http.client.methods.HttpUriRequest;

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;

public interface OAuthRequestSigner {

	public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException;
	
}
