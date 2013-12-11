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

import com.socialize.api.SocializeSession;
import com.socialize.oauth.DefaultOauthRequestSigner;
import com.socialize.oauth.OAuthConsumerFactory;
import com.socialize.oauth.OAuthSignListener;
import com.socialize.oauth.signpost.OAuthConsumer;
import com.socialize.oauth.signpost.signature.SigningStrategy;
import com.socialize.test.SocializeUnitTest;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class OAuthRequestSignerTest extends SocializeUnitTest {

	public void testDefaultOauthRequestSigner() throws Exception {
		
		final String key = "foo";
		final String secret = "bar";
		
		final String token = "footoken";
		final String tokensecret = "bartoken";
		
		OAuthConsumerFactory factory = Mockito.mock(OAuthConsumerFactory.class);
		OAuthConsumer consumer = Mockito.mock(OAuthConsumer.class);
		SigningStrategy strategy = Mockito.mock(SigningStrategy.class);
		SocializeSession session = Mockito.mock(SocializeSession.class);
		HttpUriRequest request = Mockito.mock(HttpUriRequest.class);
		OAuthSignListener listener = Mockito.mock(OAuthSignListener.class);
		
		Mockito.when(factory.createConsumer(key, secret)).thenReturn(consumer);
		Mockito.when(session.getConsumerKey()).thenReturn(key);
		Mockito.when(session.getConsumerSecret()).thenReturn(secret);
		Mockito.when(session.getConsumerToken()).thenReturn(token);
		Mockito.when(session.getConsumerTokenSecret()).thenReturn(tokensecret);
		

		Mockito.when(consumer.sign(request, listener)).thenReturn(null);

		DefaultOauthRequestSigner signer = new DefaultOauthRequestSigner(factory, strategy);
		
		HttpUriRequest signed = signer.sign(session, request, listener);
		
		assertSame(request, signed);

        Mockito.verify(consumer).setSigningStrategy(strategy);
        Mockito.verify(consumer).setTokenWithSecret(token, tokensecret);
	}

}
