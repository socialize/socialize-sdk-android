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

import java.io.Serializable;

/**
 * @author Jason Polites
 *
 */
public class EntityStatsImpl implements EntityStats, Serializable{

	private static final long serialVersionUID = -1619296473659187508L;
	
	private Integer views;
	private Integer comments;
	private Integer shares;
	private Integer likes;
	private Integer totalActivityCount;
	
	@Override
	public Integer getViews() {
		return views;
	}
	
	public void setViews(Integer views) {
		this.views = views;
	}
	
	@Override
	public Integer getComments() {
		return comments;
	}
	
	public void setComments(Integer comments) {
		this.comments = comments;
	}
	
	@Override
	public Integer getShares() {
		return shares;
	}
	
	public void setShares(Integer shares) {
		this.shares = shares;
	}
	
	@Override
	public Integer getLikes() {
		return likes;
	}
	
	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	@Override
	public Integer getTotalActivityCount() {
		return totalActivityCount;
	}
	
	public void setTotalActivityCount(Integer totalActivityCount) {
		this.totalActivityCount = totalActivityCount;
	}
}
