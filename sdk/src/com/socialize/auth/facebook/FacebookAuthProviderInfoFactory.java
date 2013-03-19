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
import com.socialize.auth.facebook.FacebookAuthProviderInfo.PermissionType;
import com.socialize.config.SocializeConfig;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.util.ArrayUtils;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProviderInfoFactory extends BaseAuthProviderInfoFactory<FacebookAuthProviderInfo> {

	private IBeanFactory<FacebookAuthProviderInfo> facebookAuthProviderInfoInstanceFactory;
	
	@Override
    public FacebookAuthProviderInfo initInstanceForRead(String... permissions) {
		
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.READ_PERMISSIONS;
		}
		
		FacebookAuthProviderInfo info = facebookAuthProviderInfoInstanceFactory.getBean();
        info.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		info.setReadPermissions(permissions);
		info.setPermissionType(PermissionType.READ);
		return info;
	}

	@Override
	public FacebookAuthProviderInfo initInstanceForWrite(String... permissions) {
		
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.WRITE_PERMISSIONS;
		}		
		
		FacebookAuthProviderInfo info = facebookAuthProviderInfoInstanceFactory.getBean();
        info.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		info.setWritePermissions(permissions);
		info.setPermissionType(PermissionType.WRITE);
		return info;
	}

	@Override
	protected void updateForRead(FacebookAuthProviderInfo info, String... permissions) {
		
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.READ_PERMISSIONS;
		}		
		
		info.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		info.mergeForRead(permissions);
		info.setPermissionType(PermissionType.READ);
	}

	@Override
	protected void updateForWrite(FacebookAuthProviderInfo info, String... permissions) {
		
		if(ArrayUtils.isEmpty(permissions)) {
			permissions = FacebookFacade.WRITE_PERMISSIONS;
		}			
		
		info.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		info.mergeForWrite(permissions);
		info.setPermissionType(PermissionType.WRITE);
	}


	public void setFacebookAuthProviderInfoInstanceFactory(IBeanFactory<FacebookAuthProviderInfo> facebookAuthProviderInfoInstanceFactory) {
		this.facebookAuthProviderInfoInstanceFactory = facebookAuthProviderInfoInstanceFactory;
	}
}
