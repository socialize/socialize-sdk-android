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
package com.socialize.provider;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.net.HttpClientFactory;

/**
 * @author Jason Polites
 * 
 * @param <T>
 */
public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	private SocializeObjectFactory<T> objectFactory;
	private HttpClientFactory clientFactory;

	public DefaultSocializeProvider() {
		super();
	}


	public DefaultSocializeProvider(SocializeObjectFactory<T> factory, HttpClientFactory clientFactory) {
		super();
		this.objectFactory = factory;
		this.clientFactory = clientFactory;
	}

	@Override
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {

		HttpClient client = clientFactory.getClient();
		HttpPost post = new HttpPost(endpoint);
		
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		
		data.add(new BasicNameValuePair("payload", "{'udid':" + uuid + "}"));
		data.add(new BasicNameValuePair("udid", uuid)); // Legacy
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
			
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(key, secret);

			// sign the request
			consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
			consumer.sign(post);
			
			HttpResponse response = client.execute(post);
			
			// Parse the response.
			// TODO: implement response parser
			
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(SocializeSession session, String endpoint, int[] ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> list(SocializeSession session, String endpoint, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> put(SocializeSession session, String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> post(SocializeSession session, String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}

	public SocializeObjectFactory<T> getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(SocializeObjectFactory<T> factory) {
		this.objectFactory = factory;
	}

	public HttpClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	
	
	public void someMethod () {
		
		SocializeObject object = new SocializeObject();
		
		object.notifyAll();
		
	}
}
