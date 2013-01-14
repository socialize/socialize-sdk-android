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
import com.socialize.demo.R;
//begin-snippet-0
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class ActionBarSampleListener extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.getsocialize.com";
		
		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		// Add more options
		ActionBarOptions options = new ActionBarOptions();
		
		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, options, new ActionBarListener() {
			
			@Override
			public void onCreate(ActionBarView actionBar) {
				
				actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

					@Override
					public void onUpdate(ActionBarView actionBar) {
						// Called when the action bar has its data updated
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
						// Called AFTER a user has removed a like
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						// Called AFTER a user has posted a share
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
						// Called AFTER a user has posted a like
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						// Called when the action bar is loaded
					}

					@Override
					public void onLoadFail(Exception error) {
						// Called when the action bar load failed
					}

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
						// Called when the action bar retrieves the like for the
						// current user
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
						// Called when the action bar retrieves the entity data
					}

					@Override
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						// Called when the user clicks on the action bar
						// Return true to indicate you do NOT want the action to continue
						return false;
					}
				});				
			}
		});
		
		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);
	}
}
//end-snippet-0


