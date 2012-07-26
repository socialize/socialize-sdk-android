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
package com.socialize.ui.action;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.ui.SocializeUIActivity;

/**
 * @author Jason Polites
 */
public class ActionDetailActivity extends SocializeUIActivity {

	private ActionDetailView view;

	@Override
	protected void onCreateSafe(Bundle savedInstanceState) {
		Intent intent = getIntent();
		doActivityLoad(intent);
	}

	@Override
	protected void onNewIntentSafe(Intent intent) {
		Bundle extras = intent.getExtras();
		if(extras != null && view != null) {
			view.reload(extras.getString(Socialize.USER_ID), extras.getString(Socialize.ACTION_ID));
		}
	}

	protected void doActivityLoad(Intent intent) {

		SocializeSession session = getSocialize().getSession();

		if(session == null) {
			finish();
		}
		else {
			User user = session.getUser();

			if(user == null) {
				finish();
			}
			else {
				view = new ActionDetailView(this);
				setContentView(view);
			}
		}	
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == SocializeUIActivity.PROFILE_UPDATE) {
			// Profile has updated... need to reload the view
			view.onProfileUpdate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			// If we were launched directly, re-launch the main app
			if(isTaskRoot() && view != null) {
				SocializeAction currentAction = view.getCurrentAction();
				if(currentAction != null) {
					CommentUtils.showCommentView(this, currentAction.getEntity());
					finish();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(view != null) {
			return view.onCreateOptionsMenu(this, menu);
		}
		return false;
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
