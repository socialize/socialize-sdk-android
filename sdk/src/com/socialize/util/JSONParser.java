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
package com.socialize.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Polites
 *
 */
public class JSONParser {
	
	private IOUtils ioUtils;
	
	public JSONObject parseObject(InputStream in) throws IOException, JSONException {
		String json = ioUtils.read(in);
		return parseObject(json);
	}

	/**
	 * Detects whether the inbound string is a JSONArray and if so wraps the array in an object with a single field called "data"
	 * @param json
	 * @return
	 */
	public JSONObject parseObject(String json) throws JSONException {
		json = json.trim();
		if(json.startsWith("[")) {
			JSONArray array = new JSONArray(json);
			JSONObject obj = new JSONObject();
			obj.put("data", array);
			return obj;
		}
		else {
			return new JSONObject(json);
		}
	}
	
	public JSONArray parseArray(InputStream in) throws IOException, JSONException {
		String json = ioUtils.read(in);
		return parseArray(json);
	}

	public JSONArray parseArray(String json) throws JSONException {
		return new JSONArray(json);
	}

	public IOUtils getIoUtils() {
		return ioUtils;
	}

	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}
}
