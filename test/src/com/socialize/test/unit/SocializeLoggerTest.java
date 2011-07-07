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

	
	@UsesMocks ({SocializeConfig.class})
	public void testSocializeLoggerInit() {
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(LogLevel.VERBOSE.name());
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		
		SocializeLogger logger = new SocializeLogger();
		
		logger.init(config);
		
		AndroidMock.verify(config);
	}
	
	@UsesMocks ({SocializeConfig.class})
	public void testGetMessageById() {
		
		final int id = 69;
		String expected = "foobar";
		String logTag = "LoggerTest";
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(LogLevel.VERBOSE.name());
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_TAG)).andReturn(logTag);
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_MSG + id)).andReturn(expected);
		
		AndroidMock.replay(config);
		
		SocializeLogger logger = new SocializeLogger();
		
		logger.init(config);
		String actual = logger.getMessage(id);
		
		AndroidMock.verify(config);
		
		assertEquals(expected, actual);
	}
	
	public void testLogById() {
		
		SocializeLogger logger = new SocializeLogger() {

			@Override
			public void debug(String msg) {
				addResult(msg);
			}

			@Override
			public void info(String msg) {
				addResult(msg);
			}

			@Override
			public void warn(String msg) {
				addResult(msg);
			}

			@Override
			public void error(String msg) {
				addResult(msg);
			}

			@Override
			public void warn(String msg, Throwable error) {
				addResult(msg);
			}

			@Override
			public void error(String msg, Throwable error) {
				addResult(msg);
			}

			@Override
			public String getMessage(int id) {
				return String.valueOf(id);
			}
		};
		
		logger.debug(0);
		logger.info(1);
		logger.warn(2);
		logger.error(3);
		logger.warn(4, new Exception());
		logger.error(5, new Exception());
		
		String[] results = new String[6];
		
		for (int i = results.length-1; i >= 0; i--) {
			results[i] = getResult();
		}
		
		for (int i = 0; i < results.length; i++) {
			assertEquals(String.valueOf(i), results[i]);
		}
	}
	
	public void testSocializeLoggerMethods() {
		
		// We can't mock out the Android logger, so we're just going to have the 
		// test call all the methods to make sure they don't fail :/
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(LogLevel.VERBOSE.name());
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		
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
		
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_LEVEL)).andReturn(level.name());
		AndroidMock.expect(config.getProperty(SocializeConfig.LOG_TAG)).andReturn("LoggerTest");
		
		AndroidMock.replay(config);
		
		logger.init(config);
		
		return logger;
	}

	
}
