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

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import com.socialize.Socialize;
import com.socialize.ui.SocializeUI;

/**
 * Creates the menu for the comment list.
 * @author Jason Polites
 *
 */
public class CommentListMenuFactory {

	public boolean onCreateOptionsMenu(final Activity source, final CommentView view, Menu menu) {
		if(Socialize.getSocialize().isAuthenticated()) {
			MenuItem add = menu.add("Edit Profile");
			add.setIcon(SocializeUI.getInstance().getDrawable("ic_menu_cc.png", DisplayMetrics.DENSITY_DEFAULT, true));
			add.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					final String userId = Socialize.getSocialize().getSession().getUser().getId().toString();
					SocializeUI.getInstance().showUserProfileViewForResult(source, userId, CommentActivity.PROFILE_UPDATE);
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
