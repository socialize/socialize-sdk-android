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

import android.os.Bundle;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeUIActivity;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class CommentDetailActivity extends SocializeUIActivity {

	private CommentDetailView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();

		if (extras == null || !extras.containsKey(SocializeUI.USER_ID) || !extras.containsKey(SocializeUI.COMMENT_ID)) {
			Toast.makeText(this, "No user or comment id provided", Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
			// If WE are the user being viewed, assume a profile update
			String userId = extras.getString(SocializeUI.USER_ID);
			if(!StringUtils.isEmpty(userId) && Integer.parseInt(userId) == getSocialize().getSession().getUser().getId()) {
				setResult(CommentActivity.PROFILE_UPDATE);
			}
			
			view = new CommentDetailView(this);
			setContentView(view);
		}
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
