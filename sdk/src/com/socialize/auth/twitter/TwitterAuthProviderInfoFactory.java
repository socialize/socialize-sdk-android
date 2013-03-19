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
package com.socialize.auth.twitter;

import com.socialize.auth.BaseAuthProviderInfoFactory;
import com.socialize.config.SocializeConfig;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthProviderInfoFactory extends BaseAuthProviderInfoFactory<TwitterAuthProviderInfo> {

	protected TwitterAuthProviderInfo initInstance(String...permissions) {
		TwitterAuthProviderInfo info = new TwitterAuthProviderInfo();
		return info;
	}

	protected void update(TwitterAuthProviderInfo info, String... permissions) {
		info.setConsumerKey(config.getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
		info.setConsumerSecret(config.getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
	}

	@Override
    public TwitterAuthProviderInfo initInstanceForRead(String... permissions) {
		return initInstance(permissions);
	}

	@Override
    public TwitterAuthProviderInfo initInstanceForWrite(String... permissions) {
		return initInstance(permissions);
	}

	@Override
	protected void updateForRead(TwitterAuthProviderInfo info, String... permissions) {
		update(info, permissions);
	}

	@Override
	protected void updateForWrite(TwitterAuthProviderInfo info, String... permissions) {
		update(info, permissions);
	}
	
	
}
