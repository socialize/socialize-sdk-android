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
package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.socialize.ui.SocializeActivity;

/**
 * @author Jason Polites
 */
public class FacebookActivity extends SocializeActivity {
	
	private FacebookActivityService service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		superOnCreate(savedInstanceState);
		service = getFacebookActivityService();
		service.onCreate();
	}
	
	@Override
	public <E> E getBean(String name) {
		return super.getBean(name);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        service.onActivityResult(requestCode, resultCode, data);
    }
    
    public FacebookActivityService getFacebookActivityService() {
    	return new FacebookActivityService(this);
    }
    
    // Mockable
    protected void superOnCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			service.onCancel();
		}
		return super.onKeyDown(keyCode, event);
	}
}
