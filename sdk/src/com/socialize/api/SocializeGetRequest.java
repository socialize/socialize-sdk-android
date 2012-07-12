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
package com.socialize.api;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Jason Polites
 *
 */
public class SocializeGetRequest extends SocializeRequest {

	private String[] ids;
	private String key;
	private String idKey;
	
	private Map<String, String> extraParams;

	private int startIndex = 0;
	private int endIndex = 100;
	
	protected String[] getIds() {
		return ids;
	}

	public void setIds(String...ids) {
		this.ids = ids;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getIdKey() {
		return idKey;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}
	
	public Map<String, String> getExtraParams() {
		return extraParams;
	}
	
	public void setExtraParams(Map<String, String> extraParams) {
		this.extraParams = extraParams;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + endIndex;
		result = prime * result + ((idKey == null) ? 0 : idKey.hashCode());
		result = prime * result + Arrays.hashCode(ids);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + startIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocializeGetRequest other = (SocializeGetRequest) obj;
		if (endIndex != other.endIndex)
			return false;
		if (idKey == null) {
			if (other.idKey != null)
				return false;
		}
		else if (!idKey.equals(other.idKey))
			return false;
		if (!Arrays.equals(ids, other.ids))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		}
		else if (!key.equals(other.key))
			return false;
		if (startIndex != other.startIndex)
			return false;
		return true;
	}
	
	
}
