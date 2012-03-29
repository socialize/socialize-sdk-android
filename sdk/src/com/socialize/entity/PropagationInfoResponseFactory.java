package com.socialize.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.socialize.api.action.ShareType;
import com.socialize.log.SocializeLogger;


public class PropagationInfoResponseFactory extends JSONFactory<PropagationInfoResponse> {

	private SocializeLogger logger;
	
	@Override
	public Object instantiateObject(JSONObject object) {
		return new PropagationInfoResponse();
	}

	@Override
	protected void fromJSON(JSONObject from, PropagationInfoResponse to) throws JSONException {
		JSONArray names = from.names();
		
		if(names != null) {

			int length = names.length();
			
			for (int i = 0; i < length; i++) {
				String name = names.get(i).toString().trim();
				
				// Make sure the name is a valid share type
				try {
					ShareType shareType = ShareType.valueOf(name.toUpperCase());
					
					JSONObject jsonObject = getJSONObject(from, name);
					
					if(jsonObject != null) {
						PropagationInfo set = new PropagationInfo();
						set.setAppUrl(getString(jsonObject, "application_url"));
						set.setEntityUrl(getString(jsonObject, "entity_url"));
						to.addUrlSet(shareType, set);
					}				
				}
				catch (Exception ignore) {
					if(logger != null) {
						logger.error("Invalid share type [" +
								name +
								"]");
					}
				}
			}
		}
		
	}

	@Override
	protected void toJSON(PropagationInfoResponse from, JSONObject to) throws JSONException {
		// not implemented
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
