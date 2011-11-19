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
package com.socialize.ui.comment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import com.socialize.Socialize;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeUIActivity;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class CommentActivity extends SocializeUIActivity {
	
	public static final int PROFILE_UPDATE = 1347;
	
	/**
	 * Used to locate the list view (to aid in testing)
	 */
	public static final int LIST_VIEW_ID = 10001;
	
	private CommentView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras == null || !extras.containsKey(SocializeUI.ENTITY_KEY)) {
			Log.w("Socialize", "No entity url found for Comment Activity. Aborting");
			finish();
		}
		else {
			String entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			if(StringUtils.isEmpty(entityKey)) {
				Log.w("Socialize", "No entity url found for Comment Activity. Aborting");
				finish();
			}
			else {
				view = new CommentView(this);
				setContentView(view);	
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Socialize.getSocialize().destroy();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == PROFILE_UPDATE) {
			// Profile has updated... need to reload the view
			view.onProfileUpdate();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		if(Socialize.getSocialize().isAuthenticated()) {
			final String userId = Socialize.getSocialize().getSession().getUser().getId().toString();
			
			MenuItem add = menu.add("Edit Profile");
			add.setIcon(SocializeUI.getInstance().getDrawable("ic_menu_cc.png", DisplayMetrics.DENSITY_DEFAULT, true));
			
			add.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					SocializeUI.getInstance().showUserProfileViewForResult(CommentActivity.this, userId, CommentActivity.PROFILE_UPDATE);
					return true;
				}
			});
		}

		MenuItem add2 = menu.add("Refresh");
		add2.setIcon(SocializeUI.getInstance().getDrawable("ic_menu_refresh.png", DisplayMetrics.DENSITY_DEFAULT, true));
		
		add2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				view.reload();
				return true;
			}
		});
		
		return true;
	}
}
