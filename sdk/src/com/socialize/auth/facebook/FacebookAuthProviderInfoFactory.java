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
package com.socialize.auth.facebook;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.BaseAuthProviderInfoFactory;
import com.socialize.config.SocializeConfig;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProviderInfoFactory extends BaseAuthProviderInfoFactory<FacebookAuthProviderInfo> {

	private IBeanFactory<FacebookAuthProviderInfo> facebookAuthProviderInfoInstanceFactory;
	
	@Override
	protected FacebookAuthProviderInfo initInstance(String...permissions) {
		FacebookAuthProviderInfo info = facebookAuthProviderInfoInstanceFactory.getBean();
		info.setPermissions(permissions);
		return info;
	}

	@Override
	protected void update(FacebookAuthProviderInfo info, String... permissions) {
		info.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		// Merge the permissions
		info.merge(permissions);
	}

	public void setFacebookAuthProviderInfoInstanceFactory(IBeanFactory<FacebookAuthProviderInfo> facebookAuthProviderInfoInstanceFactory) {
		this.facebookAuthProviderInfoInstanceFactory = facebookAuthProviderInfoInstanceFactory;
	}
}
