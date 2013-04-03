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
 * Represents the data to be used when posting to external social networks.
 * @author Jason Polites
 */
public interface PostData {

	/**
	 * Returns the propagation information supplied by the Socialize API, in which is contained the SmartDownload URLs to be used in the post.
	 * @return The propagation information supplied by the Socialize API, in which is contained the SmartDownload URLs to be used in the post.
	 */
	public PropagationInfo getPropagationInfo();
	
	/**
	 * Returns the map of values to be sent to the Social Network in the POST body.
	 * @return The map of values to be sent to the Social Network in the POST body.
	 */
	public Map<String, Object> getPostValues();
	
	/**
	 * Returns the entity being posted.  May be null if the post does not correspond to an entity.
	 * @return The entity being posted.  May be null if the post does not correspond to an entity.
	 */
	public Entity getEntity();
	
	/**
	 * Sets (overrides) the path to which this data will be posted.
	 * @param path
	 */
	public void setPath(String path);
	
	/**
	 * Returns the path to which this data will be posted.
	 * @return
	 */
	public String getPath();
}
