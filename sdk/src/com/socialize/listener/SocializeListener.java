package com.socialize.listener;

import com.socialize.api.SocializeApiError;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeService.RequestType;
import com.socialize.entity.SocializeObject;

public interface SocializeListener<T extends SocializeObject> {

	public void onResult(RequestType type, SocializeResponse response);
	
	public void onError(SocializeApiError error);
	
}
