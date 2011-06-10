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

import java.util.Properties;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;
import com.socialize.log.SocializeLogger.LogLevel;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
public class SocializeLoggerTest extends SocializeActivityTest {

	
	@UsesMocks ({SocializeConfig.class, Properties.class})
	public void testSocializeLoggerInit() {
		

		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		Properties props = AndroidMock.createMock(Properties.class);
		
		AndroidMock.expect(config.getProperties()).andReturn(props).atLeastOnce();
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(LogLevel.VERBOSE.name());
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		AndroidMock.replay(props);
		
		SocializeLogger logger = new SocializeLogger();
		
		logger.init(config);
		
		AndroidMock.verify(config);
		AndroidMock.verify(props);
		

	}
	
	
	public void testSocializeLoggerMethods() {
		
		// We can't mock out the Android logger, so we're just going to have the 
		// test call all the methods to make sure they don't fail :/
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		Properties props = AndroidMock.createMock(Properties.class);
		
		AndroidMock.expect(config.getProperties()).andReturn(props).atLeastOnce();
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(LogLevel.VERBOSE.name());
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		AndroidMock.replay(props);
		
		SocializeLogger logger = new SocializeLogger();
		
		logger.init(config);
		
		logger.debug("Test message");
		logger.info("Test message");
		logger.warn("Test message");
		logger.warn("Test message", new Exception("Test exception - IGNORE ME"));
		
		logger.error("Test message");
		logger.error("Test message", new Exception("Test exception - IGNORE ME"));
	}
	
	public void testSocializeLoggerConfigVerbose() {
		SocializeLogger logger = setupLoggerConfigTest(LogLevel.VERBOSE);

		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isVerboseEnabled());
	}
	
	public void testSocializeLoggerConfigDebug() {
		SocializeLogger logger = setupLoggerConfigTest(LogLevel.DEBUG);

		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isVerboseEnabled());
	}
	
	
	public void testSocializeLoggerConfigInfo() {
		SocializeLogger logger = setupLoggerConfigTest(LogLevel.INFO);

		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isInfoEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isVerboseEnabled());
	}
	
	
	public void testSocializeLoggerConfigWarn() {
		SocializeLogger logger = setupLoggerConfigTest(LogLevel.WARN);

		assertTrue(logger.isWarnEnabled());
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isVerboseEnabled());
	}
	
	public void testSocializeLoggerConfigError() {
		SocializeLogger logger = setupLoggerConfigTest(LogLevel.ERROR);

		assertFalse(logger.isWarnEnabled());
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isVerboseEnabled());
	}
	
	
	private SocializeLogger setupLoggerConfigTest(LogLevel level) {
		SocializeLogger logger = new SocializeLogger();
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		Properties props = AndroidMock.createMock(Properties.class);
		
		AndroidMock.expect(config.getProperties()).andReturn(props).atLeastOnce();
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(level.name());
		AndroidMock.expect(props.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		AndroidMock.replay(props);
		
		logger.init(config);
		
		return logger;
	}

	
}
