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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jason Polites
 *
 */
public abstract class AbstractEntityFactory<T extends Entity> extends SocializeObjectFactory<T> {
	
	private static final String LIKES = "likes";
	private static final String SHARES = "shares";
	private static final String COMMENTS = "comments";
	private static final String VIEWS = "views";
	private static final String TOTAL = "total_activity";
	
	private static final String USER_ACTION_SUMMARY = "user_action_summary";
	
	public AbstractEntityFactory() {
		super();
	}

	@Override
	protected void postFromJSON(JSONObject object, T entry) throws JSONException {
		entry.setName(getString(object, "name"));
		entry.setKey(getString(object, "key"));
		entry.setMetaData(getString(object, "meta"));
		entry.setType(getString(object, "type"));
		
		EntityStatsImpl stats = newEntityStatsImpl();
		
		stats.setLikes(getInt(object, LIKES));
		stats.setShares(getInt(object, SHARES));
		stats.setComments(getInt(object, COMMENTS));
		stats.setViews(getInt(object, VIEWS));
		stats.setTotalActivityCount(getInt(object, TOTAL));
		
		entry.setEntityStats(stats);
		
		if(object.has(USER_ACTION_SUMMARY) && !object.isNull(USER_ACTION_SUMMARY)) {
			
			JSONObject userStats = object.getJSONObject(USER_ACTION_SUMMARY);
			
			UserEntityStatsImpl userEntityStats = newUserEntityStatsImpl();
			userEntityStats.setComments(getInt(userStats, COMMENTS));
			userEntityStats.setShares(getInt(userStats, SHARES));
			int liked = getInt(userStats, LIKES);
			
			userEntityStats.setLiked(liked>0);
			
			entry.setUserEntityStats(userEntityStats);
		}
	}

	@Override
	protected void postToJSON(T entry, JSONObject object) throws JSONException {
		String name = entry.getName();
		String key = entry.getKey();
		String meta = entry.getMetaData();
		String type = entry.getType();
		
		if(!StringUtils.isEmpty(name)) {
			object.put("name", name);
		}
		
		if(!StringUtils.isEmpty(key)) {
			object.put("key", key);
		}
		
		if(!StringUtils.isEmpty(type)) {
			object.put("type", type);
		}			
		
		if(!StringUtils.isEmpty(meta)) {
			object.put("meta", meta);
		}	
		else {
			object.put("meta", "");
		}
	}
	
	// So we can mock.
	protected EntityStatsImpl newEntityStatsImpl() {
		return new EntityStatsImpl();
	}
	
	// So we can mock.
	protected UserEntityStatsImpl newUserEntityStatsImpl() {
		return new UserEntityStatsImpl();
	}
	
}
