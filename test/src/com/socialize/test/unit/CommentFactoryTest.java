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
package com.socialize.test.unit;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Comment;
import com.socialize.entity.CommentFactory;

/**
 * 
 * @author Jason Polites
 * 
 */
public class CommentFactoryTest extends AbstractSocializeActionFactoryTest<Comment, CommentFactory> {

	@UsesMocks(Comment.class)
	@Override
	protected Class<Comment> getActionClass() {
		return Comment.class;
	}

	@Override
	protected CommentFactory createFactory() {
		return new CommentFactory() {
			@Override
			public Comment instantiateObject(JSONObject json) {
				return action;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};
	}

	@Override
	protected void setupFromJSONExpectations() throws JSONException {
		final String text = "Test Comment";
		final boolean subscribe = false;

		AndroidMock.expect(json.isNull("text")).andReturn(false);
		AndroidMock.expect(json.has("text")).andReturn(true);
		AndroidMock.expect(json.getString("text")).andReturn(text);
		action.setText(text);

		AndroidMock.expect(json.isNull("subscribe")).andReturn(false);
		AndroidMock.expect(json.has("subscribe")).andReturn(true);
		AndroidMock.expect(json.getBoolean("subscribe")).andReturn(subscribe);
		action.setNotificationsEnabled(subscribe);

	}

	@Override
	protected void doFromJSONVerify() {

	}

	@Override
	protected void setupToJSONExpectations() throws JSONException {

		final String text = "Test Comment";
		final boolean subscribe = false;

		AndroidMock.expect(action.getText()).andReturn(text);
		AndroidMock.expect(json.put("text", text)).andReturn(json);

		AndroidMock.expect(action.isNotificationsEnabled()).andReturn(subscribe);
		AndroidMock.expect(json.put("subscribe", subscribe)).andReturn(json);

	}

	@Override
	protected void doToJSONVerify() {

	}
}
