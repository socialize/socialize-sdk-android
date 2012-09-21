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
import com.socialize.ActionUtils;
import com.socialize.UserUtils;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;



public class ActionSnippets extends Activity {

public void getApplicationActivity() {
// begin-snippet-0
// Get the last 10 actions performed in the app
// The "this" argument refers to the current Activity
ActionUtils.getActionsByApplication(this, 0, 10, new ActionListListener() {
	
	@Override
	public void onList(ListResult<SocializeAction> actions) {
		// Handle result
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-0
}

public void getEntityActivity() {
// begin-snippet-1
// Get the last 10 actions performed in the app for the entity with the key "key"
ActionUtils.getActionsByEntity(this, "key", 0, 10, new ActionListListener() {
	
	@Override
	public void onList(ListResult<SocializeAction> actions) {
		// Handle result
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-1
}

public void getUserEntityActivity() throws SocializeException {
// begin-snippet-2
// Get the last 10 actions performed in the app for the entity with the key "key" by the current user
User user = UserUtils.getCurrentUser(this);
	
ActionUtils.getActionsByUserAndEntity(this, user.getId(), "key", 0, 10, new ActionListListener() {
	
	@Override
	public void onList(ListResult<SocializeAction> actions) {
		// Handle result
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-2
}
	
}
