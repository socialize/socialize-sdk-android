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
package com.socialize.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Jason Polites
 *
 */
public class UrlBuilder {

	private StringBuilder builder;
	private boolean firstParam = true;
	
	public UrlBuilder() {
		super();
	}
	
	public void start(String baseUri) {
		baseUri = baseUri.trim();
		
		if(this.builder == null) {
			this.builder = new StringBuilder();
		}
		else {
			this.builder.delete(0, this.builder.length());
		}
		
		this.builder.append(baseUri);
	}
	
	public void addParams(String key, String[] values) {
		for (String val : values) {
			addParam(key, val);
		}
	}
	
	public void addParam(String key, String value) {

		try {
			value = URLEncoder.encode(value, "utf-8");
		}
		catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace();
		}

		if(firstParam) {
			firstParam = false;
			this.builder.append("?");
		}
		else {
			this.builder.append("&");
		}

		this.builder.append(key);
		this.builder.append("=");
		this.builder.append(value);
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}
}
