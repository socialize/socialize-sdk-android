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
package com.socialize.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import android.util.Log;
import com.socialize.log.SocializeLogger;

/**
 * Original exmaple taken from Rafael Sanches.
 * @see http://blog.rafaelsanches.com/2011/01/29/upload-using-multipart-post-using-httpclient-in-android/
 * @author Jason Polites, Rafael Sanches
 * 
 */
public class SimpleMultipartEntity implements HttpEntity {

	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private String boundary = null;

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	public SimpleMultipartEntity() {
		final StringBuffer buf = new StringBuffer();
		final Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = buf.toString();

	}

	public void writeFirstBoundaryIfNeeds() throws IOException {
		if (!isSetFirst) {
			out.write(("--" + boundary + "\r\n").getBytes());
		}
		isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds() throws IOException {
		if (isSetLast) {
			return;
		}
		out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		isSetLast = true;
	}

	public void addPart(final String key, final String value) throws IOException {
		writeFirstBoundaryIfNeeds();
		out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
		out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
		out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
		out.write(value.getBytes());
		out.write(("\r\n--" + boundary + "\r\n").getBytes());
	}

	public void addPart(final String key, final String fileName, final InputStream fin) throws IOException {
		addPart(key, fileName, fin, "application/octet-stream");
	}

	public void addPart(final String key, final String fileName, final InputStream fin, String type) throws IOException {
		writeFirstBoundaryIfNeeds();
		try {
			type = "Content-Type: " + type + "\r\n";
			out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
			out.write(type.getBytes());
			out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			final byte[] tmp = new byte[4096];
			int l = 0;
			while ((l = fin.read(tmp)) != -1) {
				out.write(tmp, 0, l);
			}
			out.flush();
		}
		finally {
			if(fin != null) {
				fin.close();
			}
		}
	}


	@Override
	public long getContentLength() {
		try {
			writeLastBoundaryIfNeeds();
			return out.toByteArray().length;
		}
		catch (IOException e) {
			Log.e(SocializeLogger.LOG_TAG, e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		outstream.write(out.toByteArray());
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public void consumeContent() throws IOException, UnsupportedOperationException {
		if (isStreaming()) {
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
		}
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		return new ByteArrayInputStream(out.toByteArray());
	}
}
