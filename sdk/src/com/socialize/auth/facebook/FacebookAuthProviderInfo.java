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

import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.util.ArrayUtils;
import com.socialize.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jason Polites
 */
public class FacebookAuthProviderInfo implements AuthProviderInfo {
	
	private static final long serialVersionUID = -6472972851879738516L;
	
	public static enum PermissionType {READ, WRITE}
	
	private String appId;
	
	private String[] readPermissions;
	private String[] writePermissions;
	
	private PermissionType permissionType = PermissionType.READ;
	
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
	
	public PermissionType getPermissionType() {
		return permissionType;
	}
	
	public void setPermissionType(PermissionType permissionType) {
		this.permissionType = permissionType;
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
	
	@Deprecated
	public String[] getPermissions() {
		return getWritePermissions();
	}
	
	@Deprecated
	public void setPermissions(String[] permissions) {
		this.setWritePermissions(permissions);
	}
	
	public String[] getReadPermissions() {
		return readPermissions;
	}

	public void setReadPermissions(String[] readPermissions) {
		this.readPermissions = readPermissions;
	}

	public String[] getWritePermissions() {
		return writePermissions;
	}
	
	public void setWritePermissions(String[] writePermissions) {
		this.writePermissions = writePermissions;
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
	
	public void mergeForRead(String[] newPermissions) {
		this.readPermissions = merge(newPermissions, this.readPermissions);
	}
	
	public void mergeForWrite(String[] newPermissions) {
		this.writePermissions = merge(newPermissions, this.writePermissions);
	}	
	
	String[] merge(String[] newPermissions, String[] oldPermissions) {
		Set<String> allPermissions = new HashSet<String>();
		
		if(oldPermissions != null) {
			allPermissions.addAll(Arrays.asList(oldPermissions));
		}
		
		allPermissions.addAll(Arrays.asList(newPermissions));
		
		oldPermissions = allPermissions.toArray(new String[allPermissions.size()]);
		
		Arrays.sort(oldPermissions);
		
		return oldPermissions;
	}

	@Override
	public boolean merge(AuthProviderInfo info) {
		
		if(info instanceof FacebookAuthProviderInfo) {
			FacebookAuthProviderInfo that = (FacebookAuthProviderInfo) info;
			
			if(that.readPermissions != null) {
				this.readPermissions = merge(that.readPermissions, this.readPermissions);
			}
			if(that.writePermissions != null) {
				this.writePermissions = merge(that.writePermissions, this.writePermissions);
			}			
			
			// Only overwrite if we are READ
			if(this.permissionType.equals(PermissionType.READ)) {
				this.permissionType = that.permissionType;
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
				if(that.getPermissionType().equals(PermissionType.WRITE)) {
					if(this.getPermissionType().equals(that.getPermissionType())) {
						return matches(that.writePermissions, this.writePermissions);
					}
				}
				else {
					return matches(that.readPermissions, this.readPermissions);
				}
			}
		}
		
		return false;
	}
	
	boolean matches(String[] expected, String[] actual) {
		if(Arrays.equals(actual, expected)) {
			return true;
		}
		else if(ArrayUtils.isEmpty(expected) && ArrayUtils.isEmpty(actual)) {
			return true;
		}
		else if (actual != null && expected != null){
			Arrays.sort(actual);
			for (int i = 0; i < expected.length; i++) {
				if(Arrays.binarySearch(actual, expected[i]) < 0) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
