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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import android.net.Uri;
import com.socialize.log.AsyncSDCardExternalLogger;
import com.socialize.log.ExternalLogger;
import com.socialize.log.SDCardExternalLogger;
import com.socialize.log.SocializeLogger.LogLevel;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class ExternalLoggerTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SDCardExternalLogger.getExternalLogFilePaths(getContext());
	}
	
	public void testExternalLoggerWritesFile() throws URISyntaxException, IOException {
		doTestExternalLoggerWritesFile(new SDCardExternalLogger(), 0, Thread.currentThread().getName());
	}
	
	public void testExternalLoggerWritesFileAsync() throws URISyntaxException, IOException {
		doTestExternalLoggerWritesFile(new AsyncSDCardExternalLogger(), 500, "AsyncSDCardExternalLogger");
	}

	public void doTestExternalLoggerWritesFile(ExternalLogger logger, long wait, String threadName) throws URISyntaxException, IOException {
		
		long time = System.currentTimeMillis();
		
		logger.init(getContext());
		logger.log(LogLevel.INFO, time, "TEST", "foobar");
		
		if(wait > 0) {
			sleep(wait);
		}
		
		logger.destroy();
		
		Set<Uri> externalLogFilePaths = SDCardExternalLogger.getExternalLogFilePaths(getContext());
		
		assertNotNull(externalLogFilePaths);
		assertEquals(1, externalLogFilePaths.size());
		
		File file = new File(new URI(externalLogFilePaths.iterator().next().toString()));
		
		FileReader fReader = new FileReader(file);
		BufferedReader bReader = new BufferedReader(fReader);
		String line = bReader.readLine().trim();
		bReader.close();
		
		StringBuilder expected = new StringBuilder();
		expected.append( String.valueOf(time) );
		expected.append( " " );
		expected.append( "INFO" );
		expected.append( " " );
		expected.append( "TEST" );
		expected.append( " " );
		expected.append( threadName );
		expected.append( " - " );
		expected.append( "foobar" );
		
		assertEquals(expected.toString(), line);
		
		SDCardExternalLogger.clearExternalLogFiles(getContext());
		
		externalLogFilePaths = SDCardExternalLogger.getExternalLogFilePaths(getContext());
		
		assertNotNull(externalLogFilePaths);
		assertEquals(0, externalLogFilePaths.size());
	}
	
}
