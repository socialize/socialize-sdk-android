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
package com.socialize.demo.snippets;

import android.app.Activity;
import com.socialize.ViewUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.view.ViewAddListener;


/**
 * @author Jason Polites
 *
 */
public class ViewSnippets extends Activity {

public void addView() {
// begin-snippet-0
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

//The "this" argument refers to the current Activity
ViewUtils.view(this, entity, new ViewAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(View result) {
		// View was created
	}
});
// end-snippet-0
}

}
