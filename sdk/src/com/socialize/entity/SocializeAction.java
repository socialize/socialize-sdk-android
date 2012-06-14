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

import com.socialize.api.action.ActionType;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public abstract class SocializeAction extends SocializeObject {

	private static final long serialVersionUID = -1988365314114134147L;
	
	private Application application;
	
	private Propagation propagation;
	private Propagation propagationInfoRequest;
	private PropagationInfoResponse propagationInfoResponse;
	
	private Entity entity;
	private String entityKey; // Used when only the key is supplied.
	private User user;
	private Double lon;
	private Double lat;
	private Long date;
	private boolean locationShared;
	
	private boolean notificationsEnabled;
	
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Reverts to only set the entity key if the name is not defined.  
	 * This ensures an existing entity name is not wiped out when the action is saved.
	 * @param entity
	 */
	public void setEntitySafe(Entity entity) {
		if(StringUtils.isEmpty(entity.getName())) {
			setEntityKey(entity.getKey());
		}
		else {
			setEntity(entity);
		}
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	
	public String getEntityDisplayName() {
		if(entity != null) {
			return entity.getDisplayName();
		}
		return entityKey;
	}
	
	public String getEntityKey() {
		if(StringUtils.isEmpty(entityKey)) {
			if(entity != null) {
				return entity.getKey();
			}
		}
		
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
	
	public boolean hasLocation() {
		return lon != null && lat != null;
	}

	public boolean isLocationShared() {
		return locationShared;
	}

	public void setLocationShared(boolean locationShared) {
		this.locationShared = locationShared;
	}
	
	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}
	
	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}
	
	public Propagation getPropagation() {
		return propagation;
	}
	
	public void setPropagation(Propagation propagation) {
		this.propagation = propagation;
	}
	
	public Propagation getPropagationInfoRequest() {
		return propagationInfoRequest;
	}
	
	public void setPropagationInfoRequest(Propagation propagationRequest) {
		this.propagationInfoRequest = propagationRequest;
	}
	
	public PropagationInfoResponse getPropagationInfoResponse() {
		return propagationInfoResponse;
	}
	
	public void setPropagationInfoResponse(PropagationInfoResponse propagationInfoResponse) {
		this.propagationInfoResponse = propagationInfoResponse;
	}
	public abstract ActionType getActionType();
	
	/**
	 * Returns the text to display for this action when rendering on screen (e.g. in a list)
	 * @return
	 */
	public String getDisplayText() {
		if(entity != null) {
			String name = entity.getName();
			String key = entity.getKey();
			if(!StringUtils.isEmpty(name)) {
				return name;
			}
			return key;
		}
		return entityKey;
	}
}
