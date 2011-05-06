package com.socialize.listener;

import java.util.List;

import com.socialize.api.SocializeEntityResponse;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeService.RequestType;
import com.socialize.entity.SocializeObject;

public abstract class AbstractSocializeListener<T extends SocializeObject> implements SocializeListener<T> {

	@Override
	public final void onResult(RequestType type, SocializeResponse response) {

		// if(HttpUtils.isHttpError(response.getResultCode())) {
		// SocializeError error = new SocializeError();
		// error.setMessage(HttpUtils.getMessageFor(response.getResultCode()));
		// onError(error);
		// }
		// else {

		@SuppressWarnings("unchecked")
		SocializeEntityResponse<T> entityResponse = (SocializeEntityResponse<T>) response;

		switch (type) {
		case GET:
			onGet(entityResponse.getFirstResult());
			break;
		case LIST:
			onList(entityResponse.getResults());
			break;
		case POST:
			onUpdate(entityResponse.getFirstResult());
			break;
		case PUT:
			onCreate(entityResponse.getFirstResult());
			break;
		}
	}

	public abstract void onGet(T entity);

	public abstract void onList(List<T> entities);

	public abstract void onUpdate(T entity);

	public abstract void onCreate(T entity);

}
