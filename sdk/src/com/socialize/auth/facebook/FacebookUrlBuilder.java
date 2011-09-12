package com.socialize.auth.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FacebookUrlBuilder {

	/**
	 * Constructs the URL to access the FB graph API and retrieve a profile image for the 
	 * user with the given facebook id.
	 * @param id
	 * @return
	 */
	public String buildProfileImageUrl(String id) {
		StringBuilder builder = new StringBuilder();
		builder.append("http://graph.facebook.com/");
		builder.append(id);
		builder.append("/picture?type=large");
		return builder.toString();
	}
	
	public URL getProfileImageUrl(String id) throws MalformedURLException {
		return new URL(buildProfileImageUrl(id));
	}
	
	public InputStream getProfileImageStream(String id) throws IOException {
		return getProfileImageUrl(id).openStream();
	}
	
}
