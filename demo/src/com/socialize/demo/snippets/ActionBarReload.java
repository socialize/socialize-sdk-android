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
import com.socialize.ActionBarUtils;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;


/**
 * @author Jason Polites
 *
 */
public class ActionBarReload extends Activity {
//begin-snippet-0	
// Create a class to capture the action bar reference
public class MyActionBarListener implements ActionBarListener {
	private ActionBarView actionBarView;

	@Override
	public void onCreate(ActionBarView actionBar) {
		// Store a reference to the actionBar view
		this.actionBarView = actionBar;
	}

	public ActionBarView getActionBarView() {
		return actionBarView;
	}
}
//end-snippet-0
	void reload() {
//begin-snippet-1

// The in the Activity which renders the ActionBar
Entity entity = Entity.newInstance("http://getsocialize.com", "Socialize");

// Setup a listener to retain a reference
MyActionBarListener listener = new MyActionBarListener();

// Use the listener when you show the action bar
// The "this" argument refers to the current Activity
ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, null, listener);

// Later (After the action bar has loaded!), you can reference the view to refresh
ActionBarView view = listener.getActionBarView();

if (view != null) {
	Entity newEntity = new Entity(); // This would be your new entity
	view.setEntity(newEntity);
	view.refresh();
}	
		
//end-snippet-1
	}

	
}
