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
package com.socialize.listener;


import com.socialize.api.SocializeApi.RequestType;
import com.socialize.api.SocializeEntityResponse;
import com.socialize.api.SocializeResponse;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;


/**
 * @author Jason Polites
 *
 * @param <T>
 */
public abstract class AbstractSocializeListener<T extends SocializeObject> implements SocializeActionListener {

	@Override
	public void onResult(RequestType type, SocializeResponse response) {

		@SuppressWarnings("unchecked")
		SocializeEntityResponse<T> entityResponse = (SocializeEntityResponse<T>) response;

		switch (type) {
			case GET:
				onGet(entityResponse.getFirstResult());
				break;
			case LIST_AS_GET:
				onGet(entityResponse.getFirstResult());
				break;				
			case LIST:
				onList(entityResponse.getResults());
				break;
			case LIST_WITHOUT_ENTITY:
				onList(entityResponse.getResults());
				break;				
			case PUT:
				onUpdate(entityResponse.getFirstResult());
				break;
			case PUT_AS_POST:
				onUpdate(entityResponse.getFirstResult());
				break;
			case POST:
				onCreate(entityResponse.getFirstResult());
				break;
			case DELETE:
				onDelete();
				break;
		}
	}

	public abstract void onGet(T result);

	public abstract void onList(ListResult<T> result);

	public abstract void onUpdate(T result);

	public abstract void onCreate(T result);
	
	public abstract void onDelete();
	
	// Subclasses override
	public void onCancel() {}

	@Override
	public abstract void onError(SocializeException error);
	
	/**
	 * Returns true if the error reported is a 404 (not found)
	 * @param error
	 * @return
	 */
	protected final boolean isNotFoundError(SocializeException error) {
		if(error instanceof SocializeApiError) {
			return ((SocializeApiError)error).getResultCode() == 404;
		}
		return false;
	}

}
