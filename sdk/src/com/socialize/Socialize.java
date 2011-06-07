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
package com.socialize;

import android.content.Context;
import android.util.Log;

import com.socialize.android.ioc.AndroidIOC;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;

/**
 * @author Jason Polites
 *
 */
public class Socialize {
	
	private SocializeService service;
	private IOCContainer container;
	private boolean initialized = false;
	
	public void init(Context context, IOCContainer container) {
		try {
			this.container = container;
			this.service = container.getBean("socializeService");
			this.initialized = true;
		}
		catch (Exception e) {
			Log.e("Socialize", "Failed to initialize Socialize!", e);
		}
	}
	
	public void init(Context context)  {
		IOCContainer container = new AndroidIOC();
		init(context, container);
	}
	
	public void destroy() {
		initialized = false;
		if(container != null) {
			container.destroy();
		}
	}

	public void addComment(SocializeSession session, String entity, String comment, CommentAddListener commentAddListener) {
		if(initialized) {
			service.addComment(session, entity, comment, commentAddListener);
		}
		else {
			// TODO: externalize strings
			if(commentAddListener != null) {
				commentAddListener.onError(new SocializeException("Socialize was not initialized"));
			}
			
			Log.e("Socialize", "Socialize was not initialized");
		}
	}

	public boolean isInitialized() {
		return initialized;
	}
}
