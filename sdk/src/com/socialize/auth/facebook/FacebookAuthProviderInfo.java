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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class FacebookAuthProviderInfo implements AuthProviderInfo {
	
	private static final long serialVersionUID = -6472972851879738516L;
	
	private String appId;
	private String[] permissions;

	/* (non-Javadoc)
	 * @see com.socialize.api.AuthProviderInfo#getType()
	 */
	@Override
	public AuthProviderType getType() {
		return AuthProviderType.FACEBOOK;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.AuthProviderInfo#validate()
	 */
	@Override
	public void validate() throws SocializeException {
		if(!isValid()) {
			throw new SocializeException("No facebook app ID found.");
		}
	}

	@Override
	public boolean isValid() {
		return !StringUtils.isEmpty(appId);
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String[] getPermissions() {
		return permissions;
	}
	
	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
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
		FacebookAuthProviderInfo other = (FacebookAuthProviderInfo) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		}
		else if (!appId.equals(other.appId))
			return false;
		return true;
	}
	
	public void merge(String[] p) {
		Set<String> allPermissions = new HashSet<String>();
		
		if(this.permissions != null) {
			allPermissions.addAll(Arrays.asList(this.permissions));
		}
		
		allPermissions.addAll(Arrays.asList(p));
		
		this.permissions = allPermissions.toArray(new String[allPermissions.size()]);
		
		Arrays.sort(this.permissions);
	}

	@Override
	public boolean merge(AuthProviderInfo info) {
		
		if(info instanceof FacebookAuthProviderInfo) {
			FacebookAuthProviderInfo that = (FacebookAuthProviderInfo) info;
			
			if(that.permissions != null) {
				merge(that.permissions);
			}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean matches(AuthProviderInfo info) {
		if(this.equals(info)) {
			
			if(info instanceof FacebookAuthProviderInfo) {
				FacebookAuthProviderInfo that = (FacebookAuthProviderInfo) info;
				
				// Ensure THIS object contains all permissions of other object
				if(Arrays.equals(permissions, that.permissions)) {
					return true;
				}
				else if (permissions != null && that.permissions != null){
					Arrays.sort(permissions);
					
					for (int i = 0; i < that.permissions.length; i++) {
						if(Arrays.binarySearch(permissions, that.permissions[i]) < 0) {
							return false;
						}
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
}
