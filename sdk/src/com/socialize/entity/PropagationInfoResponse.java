package com.socialize.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.socialize.api.action.ShareType;


public class PropagationInfoResponse implements Serializable {

	private static final long serialVersionUID = 5052125303244259152L;
	
	private Map<ShareType, PropagationInfo> urlSets;
	
	public Map<ShareType, PropagationInfo> getUrlSets() {
		return urlSets;
	}
	
	public void setUrlSets(Map<ShareType, PropagationInfo> urlSets) {
		this.urlSets = urlSets;
	}
	
	public synchronized void addUrlSet(ShareType network, PropagationInfo set) {
		if(urlSets == null) {
			urlSets = new HashMap<ShareType, PropagationInfo>();
		}
		
		urlSets.put(network, set);
	}

}
