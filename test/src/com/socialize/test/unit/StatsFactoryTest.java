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
import com.socialize.entity.Stats;
import com.socialize.entity.StatsFactory;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks(Stats.class)
public class StatsFactoryTest extends AbstractSocializeObjectFactoryTest<Stats, StatsFactory> {

	private Integer comments = 11;
	private Integer likes = 22;
	private Integer share = 33;
	private Integer views = 44;

	@Override
	protected void setupToJSONExpectations() throws JSONException {
		// nothing
	}

	@Override
	protected void doToJSONVerify() {
	}

	@Override
	protected void setupFromJSONExpectations() throws Exception {

		AndroidMock.expect(json.has("comments")).andReturn(true);
		AndroidMock.expect(json.has("likes")).andReturn(true);
		AndroidMock.expect(json.has("shares")).andReturn(true);
		AndroidMock.expect(json.has("views")).andReturn(true);

		AndroidMock.expect(json.isNull("comments")).andReturn(false);
		AndroidMock.expect(json.isNull("likes")).andReturn(false);
		AndroidMock.expect(json.isNull("shares")).andReturn(false);
		AndroidMock.expect(json.isNull("views")).andReturn(false);

		AndroidMock.expect(json.getInt("comments")).andReturn(comments);
		AndroidMock.expect(json.getInt("likes")).andReturn(likes);
		AndroidMock.expect(json.getInt("shares")).andReturn(share);
		AndroidMock.expect(json.getInt("views")).andReturn(views);

		object.setComments(comments);
		object.setLikes(likes);
		object.setShares(share);
		object.setViews(views);
	}

	@Override
	protected void doFromJSONVerify() {

	}

	@Override
	protected Class<Stats> getObjectClass() {
		return Stats.class;
	}

	@Override
	protected StatsFactory createFactory() {

		StatsFactory factory = new StatsFactory() {
			@Override
			public Stats instantiateObject(JSONObject json) {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};

		return factory;
	}

}
