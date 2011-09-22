/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.auth.facebook;

import java.io.IOException;
import java.io.InputStream;

import com.socialize.log.SocializeLogger;
import com.socialize.util.Base64Utils;
import com.socialize.util.IOUtils;

/**
 * @author Jason Polites
 * @deprecated Facebook data is extracted on the server now.
 */
@Deprecated
public class FacebookImageRetriever {

	private IOUtils ioUtils;
	private SocializeLogger logger;
	private FacebookUrlBuilder facebookUrlBuilder;
	private Base64Utils base64Utils;
	
	/**
	 * Returns a base64 encoded string of the profile picture for the user with the given FB ID.
	 * @param id
	 * @return
	 */
	public String getEncodedProfileImage(String id) {
		String encoded = null;
		InputStream in = null;
		
		try {
			in = facebookUrlBuilder.getProfileImageStream(id);
			byte[] readBytes = ioUtils.readBytes(in);
			encoded = base64Utils.encode(readBytes);
		}
		catch (Exception e) {
			logger.error("Error retrieving facebook profile picture", e);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					logger.error("Error closing stream", e);
				}
			}
		}
		
		return encoded;
	}

	
	public IOUtils getIoUtils() {
		return ioUtils;
	}

	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public FacebookUrlBuilder getFacebookUrlBuilder() {
		return facebookUrlBuilder;
	}

	public void setFacebookUrlBuilder(FacebookUrlBuilder facebookUrlBuilder) {
		this.facebookUrlBuilder = facebookUrlBuilder;
	}

	public void setBase64Utils(Base64Utils base64Utils) {
		this.base64Utils = base64Utils;
	}

}
