package com.socialize.oauth.signpost.basic;

import com.socialize.oauth.signpost.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class HttpURLConnectionResponseAdapter implements HttpResponse {

    private HttpURLConnection connection;

    public HttpURLConnectionResponseAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    public InputStream getContent() throws IOException {
        return connection.getInputStream();
    }

    public int getStatusCode() throws IOException {
        return connection.getResponseCode();
    }

    public String getReasonPhrase() throws Exception {
        return connection.getResponseMessage();
    }

    public Object unwrap() {
        return connection;
    }
}
