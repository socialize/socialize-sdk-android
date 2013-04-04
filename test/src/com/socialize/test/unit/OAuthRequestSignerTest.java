/*
 * Copyright (c) 2011 Socialize Inc. 
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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.oauth.DefaultOauthRequestSigner;
import com.socialize.oauth.OAuthConsumerFactory;
import com.socialize.oauth.OAuthSignListener;
import com.socialize.oauth.signpost.OAuthConsumer;
import com.socialize.oauth.signpost.signature.SigningStrategy;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DeviceUtils;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	OAuthConsumerFactory.class, 
	OAuthConsumer.class,
	SigningStrategy.class,
	SocializeSession.class,
	HttpUriRequest.class,
	DeviceUtils.class,
	OAuthSignListener.class})
public class OAuthRequestSignerTest extends SocializeUnitTest {

	public void testDefaultOauthRequestSigner() throws Exception {
		
		final String key = "foo";
		final String secret = "bar";
		
		final String token = "footoken";
		final String tokensecret = "bartoken";
		
		OAuthConsumerFactory factory = AndroidMock.createMock(OAuthConsumerFactory.class);
		OAuthConsumer consumer = AndroidMock.createMock(OAuthConsumer.class);
		SigningStrategy strategy = AndroidMock.createMock(SigningStrategy.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		OAuthSignListener listener = AndroidMock.createMock(OAuthSignListener.class);
		
		AndroidMock.expect(factory.createConsumer(key, secret)).andReturn(consumer);
		AndroidMock.expect(session.getConsumerKey()).andReturn(key);
		AndroidMock.expect(session.getConsumerSecret()).andReturn(secret);
		AndroidMock.expect(session.getConsumerToken()).andReturn(token);
		AndroidMock.expect(session.getConsumerTokenSecret()).andReturn(tokensecret);
		
		consumer.setSigningStrategy(strategy);
		consumer.setTokenWithSecret(token, tokensecret);
		
		AndroidMock.expect(consumer.sign(request, listener)).andReturn(null);
		
		AndroidMock.replay(factory);
		AndroidMock.replay(consumer);
		AndroidMock.replay(strategy);
		AndroidMock.replay(session);
		
		DefaultOauthRequestSigner signer = new DefaultOauthRequestSigner(factory, strategy);
		
		HttpUriRequest signed = signer.sign(session, request, listener);
		
		assertSame(request, signed);
		
		AndroidMock.verify(factory);
		AndroidMock.verify(consumer);
		AndroidMock.verify(strategy);
		AndroidMock.verify(session);
	}

}
