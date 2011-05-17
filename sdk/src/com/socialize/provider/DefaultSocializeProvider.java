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

import java.util.List;

import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.net.HttpClientFactory;

/**
 * @author Jason Polites
 *
 * @param <T>
 */
public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	protected SocializeObjectFactory<T> factory;
	protected HttpClientFactory clientFactory;
	
	public DefaultSocializeProvider(SocializeObjectFactory<T> factory, HttpClientFactory clientFactory) {
		super();
		this.factory = factory;
		this.clientFactory = clientFactory;
	}

	@Override
	public SocializeSession authenticate(String key, String secret, String uuid) {
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
}
