package com.socialize.entity;

import com.socialize.api.action.ShareType;
import com.socialize.networks.SocialNetwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class PropagationInfoResponse implements Serializable {

	private static final long serialVersionUID = 5052125303244259152L;
	
	private Map<ShareType, PropagationInfo> urlSets;
	
	public Map<ShareType, PropagationInfo> getUrlSets() {
		return urlSets;
	}
	
	public void setUrlSets(Map<ShareType, PropagationInfo> urlSets) {
		this.urlSets = urlSets;
	}
	
	public PropagationInfo getPropagationInfo(ShareType shareType) {
		return (urlSets == null) ? null : urlSets.get(shareType);
	}
	
	public PropagationInfo getPropagationInfo(SocialNetwork socialNetwork) {
		return getPropagationInfo(ShareType.valueOf(socialNetwork));
	}
	
	public synchronized void addUrlSet(ShareType network, PropagationInfo set) {
		if(urlSets == null) {
			urlSets = new HashMap<ShareType, PropagationInfo>();
		}
		
		urlSets.put(network, set);
	}

}
