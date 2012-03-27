package com.socialize.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class PropagationInfoResponseFactory extends JSONFactory<PropagationInfoResponse> {

	@Override
	public Object instantiateObject(JSONObject object) {
		return new PropagationInfoResponse();
	}

	@Override
	protected void fromJSON(JSONObject from, PropagationInfoResponse to) throws JSONException {
		
		
	}

	@Override
	protected void toJSON(PropagationInfoResponse from, JSONObject to) throws JSONException {
		// not implemented
	}

}
