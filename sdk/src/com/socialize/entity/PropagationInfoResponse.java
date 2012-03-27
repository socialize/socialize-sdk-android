package com.socialize.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.socialize.networks.SocialNetwork;


public class PropagationInfoResponse implements Serializable {

	private static final long serialVersionUID = 5052125303244259152L;
	
	private Map<SocialNetwork, PropagationUrlSet> urlSets;
	
	public Map<SocialNetwork, PropagationUrlSet> getUrlSets() {
		return urlSets;
	}
	
	public void setUrlSets(Map<SocialNetwork, PropagationUrlSet> urlSets) {
		this.urlSets = urlSets;
	}
	
	public synchronized void addUrlSet(SocialNetwork network, PropagationUrlSet set) {
		if(urlSets == null) {
			urlSets = new HashMap<SocialNetwork, PropagationUrlSet>();
		}
		
		urlSets.put(network, set);
	}

}
