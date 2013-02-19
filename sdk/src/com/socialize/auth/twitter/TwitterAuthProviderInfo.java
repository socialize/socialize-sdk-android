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
package com.socialize.auth.twitter;

import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthProviderInfo implements AuthProviderInfo {

	private static final long serialVersionUID = 2029759815212317302L;
	
	private String consumerKey;
	private String consumerSecret;

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProviderInfo#getType()
	 */
	@Override
	public AuthProviderType getType() {
		return AuthProviderType.TWITTER;
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProviderInfo#validate()
	 */
	@Override
	public void validate() throws SocializeException {
		if(StringUtils.isEmpty(consumerKey)) {
			throw new SocializeException("No twitter consumer key found.");
		}
		if(StringUtils.isEmpty(consumerSecret)) {
			throw new SocializeException("No twitter consumer secret found.");
		}
	}
	
	@Override
	public boolean isValid() {
		return !StringUtils.isEmpty(consumerKey) && !StringUtils.isEmpty(consumerSecret);
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProviderInfo#matches(com.socialize.auth.AuthProviderInfo)
	 */
	@Override
	public boolean matches(AuthProviderInfo info) {
		return this.equals(info);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.auth.AuthProviderInfo#merge(com.socialize.auth.AuthProviderInfo)
	 */
	@Override
	public boolean merge(AuthProviderInfo info) {
		if(info instanceof TwitterAuthProviderInfo) {
			TwitterAuthProviderInfo that = (TwitterAuthProviderInfo) info;
			this.consumerKey = that.consumerKey;
			this.consumerSecret = that.consumerSecret;
			return true;
		}
		return false;		
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consumerKey == null) ? 0 : consumerKey.hashCode());
		result = prime * result + ((consumerSecret == null) ? 0 : consumerSecret.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwitterAuthProviderInfo other = (TwitterAuthProviderInfo) obj;
		if (consumerKey == null) {
			if (other.consumerKey != null)
				return false;
		} else if (!consumerKey.equals(other.consumerKey))
			return false;
		if (consumerSecret == null) {
			if (other.consumerSecret != null)
				return false;
		} else if (!consumerSecret.equals(other.consumerSecret))
			return false;
		return true;
	}
	
	

}
