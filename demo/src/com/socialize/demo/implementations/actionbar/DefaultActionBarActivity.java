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

import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.socialize.ActionBarUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarShareEventListener;
import com.socialize.ui.comment.LinkifyCommentViewActionListener;


/**
 * @author Jason Polites
 */
public class DefaultActionBarActivity extends DemoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View actionBar = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, null, new ActionBarListener() {
			@Override
			public void onCreate(ActionBarView actionBar) {
				// Add clickable links to comments
				actionBar.setOnCommentViewActionListener(new LinkifyCommentViewActionListener());
				actionBar.setOnActionBarEventListener(new OnActionBarShareEventListener() {
					@Override
					public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
						Toast.makeText(DefaultActionBarActivity.this, "Share successful", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});		
		
		setContentView(actionBar);
	}

}
