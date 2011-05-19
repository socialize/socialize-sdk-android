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
import com.socialize.error.SocializeApiError;

/**
 * @author Jason Polites
 *
 * @param <T>
 */
public interface SocializeProvider<T> {
	
	public SocializeSession authenticate(String key, String secret, String uuid) throws SocializeApiError;

	public List<T> list(SocializeSession session, String endpoint, String key) throws SocializeApiError;
	
	public T get(SocializeSession session, String endpoint, int[] ids) throws SocializeApiError;
	
	public List<T> put(SocializeSession session, String endpoint, T object) throws SocializeApiError;

	public List<T> post(SocializeSession session, String endpoint, T object) throws SocializeApiError;
	
}
