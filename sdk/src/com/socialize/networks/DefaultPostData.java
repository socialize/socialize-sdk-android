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
package com.socialize.networks;

import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;

import java.util.Map;


/**
 * @author Jason Polites
 *
 */
public class DefaultPostData implements PostData {
	
	private PropagationInfo propagationInfo;
	private Map<String, Object> postValues;
	private String path;
	private Entity entity;

	@Override
	public PropagationInfo getPropagationInfo() {
		return propagationInfo;
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.PostData#getPostValues()
	 */
	@Override
	public Map<String, Object> getPostValues() {
		return postValues;
	}
	
	public void setPropagationInfo(PropagationInfo propagationInfo) {
		this.propagationInfo = propagationInfo;
	}
	
	public void setPostValues(Map<String, Object> postValues) {
		this.postValues = postValues;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.PostData#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.PostData#getEntity()
	 */
	@Override
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
