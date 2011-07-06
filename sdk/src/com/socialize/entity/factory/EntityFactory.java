/*
 * Copyright (c) 2011 Socialize Inc* Permission is hereby granted, free of charge, to any person obtaining a copy
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
package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Entity;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class EntityFactory extends SocializeObjectFactory<Entity> {
	
	private static final String LIKES = "likes";
	private static final String SHARES = "shares";
	private static final String COMMENTS = "comments";
	private static final String VIEWS = "views";
	
	public EntityFactory() {
		super();
	}

	@Override
	public Entity instantiateObject() {
		return new Entity();
	}

	@Override
	protected void postFromJSON(JSONObject object, Entity entry) throws JSONException {
		
		if(object.has("name")) {
			entry.setName(object.getString("name"));
		}
		
		if(object.has("key")) {
			entry.setKey(object.getString("key"));
		}
		
		if(object.has(LIKES) && !object.isNull(LIKES)) {
			entry.setLikes(object.getInt(LIKES));
		}
		if(object.has(SHARES) && !object.isNull(SHARES)) {
			entry.setShares(object.getInt(SHARES));
		}
		if(object.has(COMMENTS) && !object.isNull(COMMENTS)) {
			entry.setComments(object.getInt(COMMENTS));
		}
		if(object.has(VIEWS) && !object.isNull(VIEWS)) {
			entry.setViews(object.getInt(VIEWS));
		}
	}

	@Override
	protected void postToJSON(Entity entry, JSONObject object) throws JSONException {
		if(!StringUtils.isEmpty(entry.getName())) {
			object.put("name", entry.getName());
		}
		if(!StringUtils.isEmpty(entry.getKey())) {
			object.put("key", entry.getKey());
		}
	}
}
