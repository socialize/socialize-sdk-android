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


import com.socialize.log.SocializeLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jason Polites
 *
 */
public class IOUtils {
	
	private static final int IO_BUFFER_SIZE = 4 * 1024; 
	
	private SocializeLogger logger;

	public String readSafe(InputStream in) {
		try {
			return read(in);
		}
		catch (IOException e) {
			if(logger != null) {
				logger.error("", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
		}
		return "";
	}
	
	public byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pipe(in, out, IO_BUFFER_SIZE);
		return out.toByteArray();
	}
	
	public String read(InputStream in) throws IOException {
		return new String(readBytes(in), "UTF-8");
	}
	
	public long pipe(InputStream in, OutputStream out, int bufferSize) throws IOException {
		int read = 0;
		long total = 0L;
		byte[] buffer = new byte[bufferSize];
		
		while((read = in.read(buffer)) >= 0) {
			total+=read;
			out.write(buffer, 0, read);
		}
		out.flush();
		return total;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
