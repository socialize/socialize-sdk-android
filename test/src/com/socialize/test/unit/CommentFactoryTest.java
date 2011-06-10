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
import com.socialize.entity.factory.CommentFactory;

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
			public Comment instantiateObject() {
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
		
		AndroidMock.expect(json.getString("comment")).andReturn(text);
		
		action.setText(text);
		
		AndroidMock.replay(json);
		AndroidMock.replay(action);
	}

	@Override
	protected void doFromJSONVerify() {
		AndroidMock.verify(json);
		AndroidMock.verify(action);
	}

	@Override
	protected void setupToJSONExpectations() throws JSONException {
		
		final String text = "Test Comment";
		
		AndroidMock.expect(action.getText()).andReturn(text);
		AndroidMock.expect(json.put("comment", text)).andReturn(json);
		
		AndroidMock.replay(json);
		AndroidMock.replay(action);
	}

	@Override
	protected void doToJSONVerify() {
		AndroidMock.verify(json);
		AndroidMock.verify(action);
	}
}
