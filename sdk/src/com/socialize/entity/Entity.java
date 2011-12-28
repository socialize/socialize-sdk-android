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
package com.socialize.entity;

import java.io.Serializable;

import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class Entity extends SocializeObject implements Serializable {
	
	private static final long serialVersionUID = -6607155597255660851L;
	
	private String key;
	private String name;
	private EntityStats entityStats;
	
	private String metaData;
	
	@Deprecated
	public Integer getViews() {
		return (entityStats == null) ? 0 : entityStats.getViews();
	}
	
	@Deprecated
	public void setViews(Integer views) {
		if(entityStats == null) entityStats = new EntityStatsImpl();
		((EntityStatsImpl)entityStats).setViews(views);			
	}
	
	@Deprecated
	public Integer getLikes() {
		return (entityStats == null) ? 0 : entityStats.getLikes();
	}
	
	@Deprecated
	public void setLikes(Integer likes) {
		if(entityStats == null) entityStats = new EntityStatsImpl();
		((EntityStatsImpl)entityStats).setLikes(likes);				
	}
	
	@Deprecated
	public Integer getComments() {
		return (entityStats == null) ? 0 : entityStats.getComments();
	}
	
	@Deprecated
	public void setComments(Integer comments) {
		if(entityStats == null) entityStats = new EntityStatsImpl();
		((EntityStatsImpl)entityStats).setComments(comments);
	}
	
	@Deprecated
	public Integer getShares() {
		return (entityStats == null) ? 0 : entityStats.getShares();
	}
	
	@Deprecated
	public void setShares(Integer shares) {
		if(entityStats == null) entityStats = new EntityStatsImpl();
		((EntityStatsImpl)entityStats).setShares(shares);		
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
	
	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	protected void setEntityStats(EntityStats stats) {
		this.entityStats = stats;
	}


	/**
	 * Convenience method to create a new Entity based on a key only.
	 * @param key
	 * @return
	 * @deprecated use newInstance(String key, String name)
	 */
	@Deprecated
	public static Entity fromkey(String key) {
		return newInstance(key, null);
	}
	
	public static Entity newInstance(String key, String name) {
		Entity e = new Entity();
		e.setKey(key);
		e.setName(name);
		return e;
	}
}
