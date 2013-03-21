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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeUnitTest;
import com.socialize.test.util.JsonAssert;
import com.socialize.util.HttpUtils;
import com.socialize.util.IOUtils;
import com.socialize.util.JSONParser;
import com.socialize.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Polites
 * 
 */
public class IOUtilsTest extends SocializeUnitTest {

	public void testIOUtilsRead() throws IOException {
		String text = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		ByteArrayInputStream bin = new ByteArrayInputStream(text.getBytes());
		IOUtils utils = new IOUtils();
		String read = utils.read(bin);
		assertEquals(text, read);
	}

	@UsesMocks(InputStream.class)
	public void testIOReadSafe() {
		InputStream in = AndroidMock.createMock(InputStream.class);

		try {
			AndroidMock.expect(in.read((byte[]) AndroidMock.anyObject())).andThrow(new IOException("DUMMY EXCEPTION - IGNORE ME!"));

			AndroidMock.replay(in);

			IOUtils utils = new IOUtils();
			String read = utils.readSafe(in);

			AndroidMock.verify(in);

			assertNotNull(read);
			assertEquals("", read);
		} catch (IOException e) {
			fail();
		}
	}

	public void testStringUtils() {
		String empty = "";
		String nulll = null;
		String untrimmed = "  ";
		String nonEmpty = "foobar";

		assertTrue(StringUtils.isEmpty(empty));
		assertTrue(StringUtils.isEmpty(nulll));
		assertTrue(StringUtils.isEmpty(untrimmed));
		assertFalse(StringUtils.isEmpty(nonEmpty));
	}

	@UsesMocks({ IOUtils.class, InputStream.class })
	public void testJSONParseObject() throws Exception {

		final String json = "{foo:bar}";

		IOUtils ioUtils = AndroidMock.createMock(IOUtils.class);
		InputStream in = AndroidMock.createMock(InputStream.class);

		AndroidMock.expect(ioUtils.read(in)).andReturn(json);

		AndroidMock.replay(ioUtils);

		JSONParser parser = new JSONParser();
		parser.setIoUtils(ioUtils);

		JSONObject object = parser.parseObject(in);

		JSONObject expected = new JSONObject(json);

		JsonAssert.assertJsonObjectEquals(expected, object);

		object = parser.parseObject(json);

		JsonAssert.assertJsonObjectEquals(expected, object);

		AndroidMock.verify(ioUtils);
	}

	@UsesMocks({ IOUtils.class, InputStream.class })
	public void testJSONParseArray() throws Exception {
		final String json = "[foo1,bar1,foo2,bar2,foo3,bar3]";

		IOUtils ioUtils = AndroidMock.createMock(IOUtils.class);
		InputStream in = AndroidMock.createMock(InputStream.class);

		AndroidMock.expect(ioUtils.read(in)).andReturn(json);

		AndroidMock.replay(ioUtils);

		JSONParser parser = new JSONParser();
		parser.setIoUtils(ioUtils);

		JSONArray object = parser.parseArray(in);

		JSONArray expected = new JSONArray(json);

		JsonAssert.assertJsonArrayEquals(expected, object);

		object = parser.parseArray(json);

		JsonAssert.assertJsonArrayEquals(expected, object);

		AndroidMock.verify(ioUtils);

	}

	@UsesMocks({ HttpResponse.class, StatusLine.class })
	public void testHttpUtilsIsError() {

		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		StatusLine line = AndroidMock.createMock(StatusLine.class);

		AndroidMock.expect(response.getStatusLine()).andReturn(line);
		AndroidMock.expect(line.getStatusCode()).andReturn(404);

		AndroidMock.replay(response);
		AndroidMock.replay(line);

		HttpUtils utils = new HttpUtils();
		assertTrue(utils.isHttpError(response));

		AndroidMock.verify(response);
		AndroidMock.verify(line);

	}
}
