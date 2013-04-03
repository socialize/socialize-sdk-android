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
package com.socialize.launcher;

import com.socialize.log.SocializeLogger;

import java.util.Map;


/**
 * @author Jason Polites
 *
 */
public class SocializeLaunchManager implements LaunchManager {

	private Map<String, Launcher> launchers;
	private SocializeLogger logger;
	
	/* (non-Javadoc)
	 * @see com.socialize.launcher.LaunchManager#getLaucher(java.lang.String)
	 */
	@Override
	public Launcher getLaucher(String action) {
		try {
			return getLaucher(LaunchAction.valueOf(action));
		} catch (Exception e) {
			
			if(logger != null) {
				logger.error("Launch action [" +
						action +
						"] provided is not a known action", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.launcher.LaunchManager#getLaucher(com.socialize.launcher.LaunchAction)
	 */
	@Override
	public Launcher getLaucher(LaunchAction action) {
		if(launchers != null) {
			return launchers.get(action.name());
		}
		return null;
	}

	public void setLaunchers(Map<String, Launcher> launchers) {
		this.launchers = launchers;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
