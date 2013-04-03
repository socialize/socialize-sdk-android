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
package com.socialize.entity;

import com.socialize.util.StringUtils;

import java.io.Serializable;


/**
 * @author Jason Polites
 *
 */
public class Entity extends SocializeObject implements Serializable {
	
	private static final long serialVersionUID = -6607155597255660851L;
	
	private String key;
	private String name;
	private EntityStats entityStats;
	private UserEntityStats userEntityStats;
	
	private String metaData;
	private String type;
	
	public Entity() {
		super();
	}
	
	public Entity(String key, String name) {
		super();
		this.key = key;
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		if(StringUtils.isEmpty(name)) {
			return key;
		}
		return name;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public EntityStats getEntityStats() {
		return entityStats;
	}
	
	protected void setUserEntityStats(UserEntityStats userEntityStats) {
		this.userEntityStats = userEntityStats;
	}
	
	public UserEntityStats getUserEntityStats() {
		return userEntityStats;
	}
	
	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	protected void setEntityStats(EntityStats stats) {
		this.entityStats = stats;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public static Entity newInstance(String key, String name) {
		Entity e = new Entity(key, name);
		return e;
	}
	@Override
	public String toString() {
		return getDisplayName();
	}
}
