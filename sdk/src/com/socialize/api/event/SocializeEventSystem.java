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
package com.socialize.api.event;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.provider.SocializeProvider;


/**
 * @author Jason Polites
 *
 */
public class SocializeEventSystem extends SocializeApi<SocializeEvent, SocializeProvider<SocializeEvent>> implements EventSystem {

	public SocializeEventSystem(SocializeProvider<SocializeEvent> provider) {
		super(provider);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.event.EventSystem#addEvent(com.socialize.api.SocializeSession, com.socialize.api.event.SocializeEvent, com.socialize.api.event.EventListener)
	 */
	@Override
	public void addEvent(SocializeSession session, SocializeEvent event, EventListener eventListener) {
		postAsync(session, ENDPOINT, event, false, eventListener);		
	}

}
