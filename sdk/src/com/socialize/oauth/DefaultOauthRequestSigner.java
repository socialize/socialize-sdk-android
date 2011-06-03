package com.socialize.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.signature.SigningStrategy;

import org.apache.http.client.methods.HttpUriRequest;

import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;

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
		try {
			OAuthConsumer consumer = consumerFactory.createConsumer(session.getConsumerKey(), session.getConsumerSecret());;
			consumer.setSigningStrategy(strategy);
			consumer.setTokenWithSecret(session.getConsumerToken(), session.getConsumerTokenSecret());
			consumer.sign(request);
			return request;
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
	}
	
}
