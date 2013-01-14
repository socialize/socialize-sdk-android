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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.android.ioc.Logger;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;


/**
 * @author Jason Polites
 *
 */
public class ActionBarWithMonitorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Socialize.onCreate(this, savedInstanceState); 
		String entityKey = "foobar"; 
		Entity entity = Entity.newInstance(entityKey, "Foobar"); 
		View actionBar = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, null, new ActionBarListener() {
			
			@Override
			public void onCreate(final ActionBarView actionBar) {
				actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
					}

					@Override
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						return false;
						
					}

					@Override
					public void onUpdate(ActionBarView actionBar) {
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
					}

					@Override
					public void onLoadFail(Exception error) {
						
						Logger.i("ActionBarWithMonitorActivity", "ActionBar load failed. Scheduling retry");
						
						ActionBarNetworkMonitor monitor = new ActionBarNetworkMonitor(ActionBarWithMonitorActivity.this, actionBar, 5000);
						monitor.reloadOnNetworkAvailable(ActionBarWithMonitorActivity.this);
					}
				});
			}
		});
		
		setContentView(actionBar);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		Socialize.onDestroy(this);
		super.onDestroy();
	}

}
