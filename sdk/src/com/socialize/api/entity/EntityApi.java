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
package com.socialize.api.entity;

import com.socialize.api.SocializeApi;
import com.socialize.entity.SocializeObject;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 */
public abstract class EntityApi<T extends SocializeObject> extends SocializeApi<T, SocializeProvider<T>>{
	
	public EntityApi(SocializeProvider<T> provider) {
		super(provider);
	}

	public void authenticate(String key, String secret, String uuid, SocializeAuthListener listener) {
		super.authenticateAsync(key, secret, uuid, listener);
	}
}
