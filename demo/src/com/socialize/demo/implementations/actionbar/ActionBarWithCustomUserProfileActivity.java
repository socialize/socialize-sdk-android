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
package com.socialize.demo.implementations.actionbar;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.demo.CustomProfileViewActivity;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.LinkifyCommentViewActionListener;


/**
 * @author Jason Polites
 */
public class ActionBarWithCustomUserProfileActivity extends DemoActivity {

	@Override
	protected void onCreate() {

//begin-snippet-0
View actionBar = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, null, new ActionBarListener() {
	@Override
	public void onCreate(ActionBarView actionBar) {

		// Add a listener to capture the settings menu item
		actionBar.setOnCommentViewActionListener(new LinkifyCommentViewActionListener() {

			@Override
			public boolean onSettingsMenuItemClick(MenuItem item) {
				// Start the custom profile activity
				Intent intent = new Intent(ActionBarWithCustomUserProfileActivity.this, CustomProfileViewActivity.class);
				ActionBarWithCustomUserProfileActivity.this.startActivity(intent);
				// Return true to indicate we have handled the click
				return true;
			}

		});
	}
});
//end-snippet-0

		setContentView(actionBar);
	}
}
