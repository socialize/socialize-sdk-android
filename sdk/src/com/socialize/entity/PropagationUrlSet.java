package com.socialize.entity;

import java.io.Serializable;


public class PropagationUrlSet implements Serializable {

	private static final long serialVersionUID = -2383065650286877555L;
	
	private String appUrl;
	private String entityUrl;
	
	public String getAppUrl() {
		return appUrl;
	}
	
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
	
	public String getEntityUrl() {
		return entityUrl;
	}
	
	public void setEntityUrl(String entityUrl) {
		this.entityUrl = entityUrl;
	}
}
