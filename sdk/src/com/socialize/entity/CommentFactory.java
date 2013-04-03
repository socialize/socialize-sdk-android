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
package com.socialize.entity;

import com.socialize.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jason Polites
 *
 */
public class CommentFactory extends SocializeActionFactory<Comment> {
	
	@Override
	protected void postFromJSON(JSONObject object, Comment comment) throws JSONException {
		final String attr = "text";
		final String notificationAttr = "subscribe";
		if(object.has(attr) && !object.isNull(attr)) {
			comment.setText(object.getString(attr));
		}
		else {
			if(logger != null && logger.isWarnEnabled()) {
				logger.warn("Attribute [" +
						attr +
						"] not found in [" +
						comment.getClass().getSimpleName() +
						"]");
			}
		}
		
		if(object.has(notificationAttr) && !object.isNull(notificationAttr)) {
			comment.setNotificationsEnabled(object.getBoolean(notificationAttr));
		}
	}

	@Override
	protected void postToJSON(Comment comment, JSONObject object) throws JSONException {
		String text = comment.getText();
		if(!StringUtils.isEmpty( text )) {
			object.put("text", text);
		}
		object.put("subscribe", comment.isNotificationsEnabled());
	}

	@Override
	public Object instantiateObject(JSONObject object) {
		return new Comment();
	}
}
