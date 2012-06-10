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


/**
 * @author Jason Polites
 *
 */
public class UserEntityStatsImpl implements UserEntityStats {
	
	private static final long serialVersionUID = 8983099444428913787L;
	
	private Integer comments;
	private Integer shares;
	private Boolean liked;
	

	/* (non-Javadoc)
	 * @see com.socialize.entity.UserStats#getComments()
	 */
	@Override
	public Integer getComments() {
		return comments;
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.UserStats#isLiked()
	 */
	@Override
	public Boolean isLiked() {
		return liked;
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.UserStats#getShares()
	 */
	@Override
	public Integer getShares() {
		return shares;
	}

	public Boolean getLiked() {
		return liked;
	}
	
	public void setLiked(Boolean liked) {
		this.liked = liked;
	}
	
	public void setComments(Integer comments) {
		this.comments = comments;
	}
	
	public void setShares(Integer shares) {
		this.shares = shares;
	}
}
