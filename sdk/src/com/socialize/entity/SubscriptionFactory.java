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

import com.socialize.log.SocializeLogger;
import com.socialize.notifications.NotificationType;
import com.socialize.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * @author Jason Polites
 *
 */
public class SubscriptionFactory extends SocializeObjectFactory<Subscription> {

	private UserFactory userFactory;
	private EntityFactory entityFactory;
	private SocializeLogger logger;
	
	/* (non-Javadoc)
	 * @see com.socialize.entity.SocializeObjectFactory#postFromJSON(org.json.JSONObject, com.socialize.entity.SocializeObject)
	 */
	@Override
	protected void postFromJSON(JSONObject from, Subscription to) throws JSONException {

		try {
			
			if(from.has("user") && !from.isNull("user")) {
				JSONObject user = from.getJSONObject("user");
				if(user != null) {
					to.setUser(userFactory.fromJSON(user));
				}
			}
			
			if(from.has("entity") && !from.isNull("entity")) {
				JSONObject entity = from.getJSONObject("entity");
				if(entity != null) {
					to.setEntity(entityFactory.fromJSON(entity));
				}
			}
			
			if(from.has("subscribed") && !from.isNull("subscribed")) {
				to.setSubscribed(from.getBoolean("subscribed"));
			}			
			
			if(from.has("date") && !from.isNull("date")) {
				try {
					to.setDate(DATE_FORMAT.parse(from.getString("date")).getTime());
				}
				catch (ParseException e) {
					if(logger != null && logger.isWarnEnabled()) {
						logger.warn("Could not parse date [" +
								from.getString("date") +
								"] using format [" +
								DATE_FORMAT_STRING +
								"]");
					}
				}
			}
			
			if(from.has("type") && !from.isNull("type")) {
				try {
					to.setNotificationType(NotificationType.valueOf(from.getString("type").toUpperCase()));
				}
				catch (Exception e) {
					if(logger != null && logger.isWarnEnabled()) {
						logger.warn("Could not parse type [" +
								from.getString("type") +
								"] into " + NotificationType.class.getName());
					}
				}
			}			
		}
		catch (Throwable e) {
			throw new JSONException(e.getMessage());
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.SocializeObjectFactory#postToJSON(com.socialize.entity.SocializeObject, org.json.JSONObject)
	 */
	@Override
	protected void postToJSON(Subscription from, JSONObject to) throws JSONException {

		try {
			Entity entityObject = from.getEntity();
			User userObject = from.getUser();
			Long date = from.getDate();
			
			if(entityObject != null) {
				if(!StringUtils.isEmpty(entityObject.getName()) && !StringUtils.isEmpty(entityObject.getKey())) {
					JSONObject entity = entityFactory.toJSON(entityObject);
					to.put("entity", entity);
				}
				else if(!StringUtils.isEmpty(entityObject.getKey())) {
					to.put("entity_key", entityObject.getKey());
				}
			}
			
			if(userObject != null) {
				JSONObject user = userFactory.toJSON(userObject);
				to.put("user", user);
			}
			
			if(date != null) {
				to.put("date", DATE_FORMAT.format(date));
			}
			
			to.put("subscribed", from.isSubscribed());
			to.put("type", from.getNotificationType().name().toLowerCase());
		}
		catch (Exception e) {
			throw new JSONException(e.getMessage());
		}		
	}

	/* (non-Javadoc)
	 * @see com.socialize.entity.JSONFactory#instantiateObject(org.json.JSONObject)
	 */
	@Override
	public Object instantiateObject(JSONObject object) {
		return new Subscription();
	}

	public void setUserFactory(UserFactory userFactory) {
		this.userFactory = userFactory;
	}

	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
