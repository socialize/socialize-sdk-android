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
package com.socialize.oauth;


import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.oauth.signpost.OAuthConsumer;
import com.socialize.oauth.signpost.signature.SigningStrategy;
import org.apache.http.client.methods.HttpUriRequest;

public class DefaultOauthRequestSigner implements OAuthRequestSigner {

	private OAuthConsumerFactory consumerFactory;
	private SigningStrategy strategy;
	
	public DefaultOauthRequestSigner(OAuthConsumerFactory consumerFactory, SigningStrategy strategy) {
		super();
		this.strategy = strategy;
		this.consumerFactory = consumerFactory;
	}
	
	@Override
	public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
		return sign(session, request, null);
	}

	@Override
	public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
		try {
			OAuthConsumer consumer = consumerFactory.createConsumer(session.getConsumerKey(), session.getConsumerSecret());
			return sign(consumer, session, request, listener);
		}
		catch (Exception e) {
			throw new SocializeException(e.getMessage(), e);
		}
	}

	public <R extends HttpUriRequest> R sign(OAuthConsumer consumer, OAuthAuthorizer authorizer, R request, OAuthSignListener listener) throws SocializeException {
		try {
			consumer.setSigningStrategy(strategy);
			consumer.setTokenWithSecret(authorizer.getConsumerToken(), authorizer.getConsumerTokenSecret());
			consumer.sign(request, listener);
			return request;
		}
		catch (Exception e) {
			throw new SocializeException(e.getMessage(), e);
		}
	}

	@Override
	public <R extends HttpUriRequest> R sign(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, R request, OAuthSignListener listener) throws SocializeException {
		try {
			OAuthConsumer consumer = consumerFactory.createConsumer(consumerKey, consumerSecret);
			consumer.setSigningStrategy(strategy);
			consumer.setTokenWithSecret(accessToken, accessTokenSecret);
			consumer.sign(request, listener);
			return request;
		}
		catch (Exception e) {
			throw new SocializeException(e.getMessage(), e);
		}		
	}
	
	
}
