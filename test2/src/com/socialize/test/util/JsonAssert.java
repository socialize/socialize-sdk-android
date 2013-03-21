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
package com.socialize.test.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Jason Polites
 * 
 */
public final class JsonAssert {
	private JsonAssert() {
	}

	public static void assertJsonArrayEquals(JSONArray expected, JSONArray actual) throws Exception {
		if (expected.length() != actual.length()) {
			assertEquals("Arrays are not of equal length", expected.toString(), actual.toString());
		}

		for (int i = 0; i < expected.length(); ++i) {
			Object expectedValue = expected.opt(i);
			Object actualValue = actual.opt(i);

			assertSame(expected.toString() + " != " + actual.toString(), expectedValue.getClass(), actualValue.getClass());

			if (expectedValue instanceof JSONObject) {
				assertJsonObjectEquals((JSONObject) expectedValue, (JSONObject) actualValue);
			}
			else if (expectedValue instanceof JSONArray) {
				assertJsonArrayEquals((JSONArray) expectedValue, (JSONArray) actualValue);
			}
			else {
				assertEquals(expectedValue, actualValue);
			}
		}
	}

	public static void assertJsonObjectEquals(JSONObject expected, JSONObject actual) throws Exception {
		if (expected.length() != actual.length()) {
			assertEquals("Objects are not of equal size", expected.toString(2), actual.toString(2));
		}

		// Both are empty so skip
		if (expected.names() == null && actual.names() == null) {
			return;
		}

		JSONArray names = expected.names();
		List<String> nameList = new ArrayList<String>(names.length());

		for (int i = 0; i < names.length(); i++) {
			nameList.add(names.getString(i));
		}

		for (String name : nameList) {
			Object expectedValue = expected.opt(name);
			Object actualValue = actual.opt(name);

			if (expectedValue != null) {
				assertNotNull(expected.toString() + " != " + actual.toString(), actualValue);
			}
			assertSame(expected.toString() + " != " + actual.toString(), expectedValue.getClass(), actualValue.getClass());

			if (expectedValue instanceof JSONObject) {
				assertJsonObjectEquals((JSONObject) expectedValue, (JSONObject) actualValue);
			}
			else if (expectedValue instanceof JSONArray) {
				assertJsonArrayEquals((JSONArray) expectedValue, (JSONArray) actualValue);
			}
			else {
				assertEquals(expectedValue, actualValue);
			}
		}
	}

	public static void assertJsonEquals(String expected, String actual) throws Exception {
		switch (expected.charAt(0)) {
		case '{':
			assertJsonObjectEquals(new JSONObject(expected), new JSONObject(actual));
			break;
		case '[':
			assertJsonArrayEquals(new JSONArray(expected), new JSONArray(actual));
			break;
		default:
			assertEquals(expected, actual);
			break;
		}
	}

	// public static void assertObjectEquals(Object expected, Object actual)
	// throws Exception {
	// if (!(expected instanceof String)) {
	// expected = JsonSerializer.serialize(expected);
	// }
	//
	// if (!(actual instanceof String)){
	// actual = JsonSerializer.serialize(actual);
	// }
	//
	// assertJsonEquals((String) expected, (String) actual);
	// }
}
