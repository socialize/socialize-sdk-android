package com.facebook;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;

import java.net.HttpURLConnection;

public class MockResponse extends Response {

	public MockResponse(Request request, HttpURLConnection connection, FacebookRequestError error) {
		super(request, connection, error);
	}

	public MockResponse(Request request, HttpURLConnection connection, GraphObject graphObject, boolean isFromCache) {
		super(request, connection, graphObject, isFromCache);
	}

	public MockResponse(Request request, HttpURLConnection connection, GraphObjectList<GraphObject> graphObjects, boolean isFromCache) {
		super(request, connection, graphObjects, isFromCache);
	}
}
