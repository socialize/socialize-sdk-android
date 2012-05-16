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
package com.socialize.init;

import android.content.Context;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeListener;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class DefaultInitializationAsserter implements SocializeInitializationAsserter {

	private SocializeLogger logger;
	
	/* (non-Javadoc)
	 * @see com.socialize.init.SocializeInitializationAsserter#assertAuthenticated(com.socialize.api.SocializeSession, com.socialize.listener.SocializeListener)
	 */
	@Override
	public boolean assertAuthenticated(SocializeService service, SocializeSession session, SocializeListener listener) {
		if(session != null) {
			return true;
		}
		else {
			if(listener != null) {
				if(logger != null) {
					listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_AUTHENTICATED)));
				}
				else {
					listener.onError(new SocializeException("Not authenticated"));
				}
			}
			if(logger != null) logger.error(SocializeLogger.NOT_AUTHENTICATED);
		}
		return false;
	}

	@Override
	public boolean assertInitialized(Context context, SocializeService service, SocializeListener listener) {
		boolean initialized = service.isInitialized(context);
		if(!initialized) {
			if(listener != null) {
				if(logger != null) {
					listener.onError(new SocializeException(logger.getMessage(SocializeLogger.NOT_INITIALIZED)));
				}
				else {
					listener.onError(new SocializeException("Not initialized"));
				}
			}
			if(logger != null) logger.error(SocializeLogger.NOT_INITIALIZED);
		}
		return initialized;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
