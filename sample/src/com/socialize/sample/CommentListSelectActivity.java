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
package com.socialize.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.util.StringUtils;

public class CommentListSelectActivity extends CommentBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.comment_list_select);
		
		final EditText txtCommentKey = (EditText) findViewById(R.id.txtCommentKey);
		final TextView labelCommentKey = (TextView) findViewById(R.id.labelCommentKey);
		final Button btnCommentList = (Button) findViewById(R.id.btnCommentList);
		final EditText txtStart = (EditText) findViewById(R.id.txtStart);
		final EditText txtEnd = (EditText) findViewById(R.id.txtEnd);
		
		Intent intent = getIntent();
		
		boolean userOnly = false;
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			
			if(extras != null) {
				userOnly = extras.getBoolean("user_only");
			}
		}
		
		final boolean user_only = userOnly;
		
		if(user_only) {
			txtCommentKey.setVisibility(View.GONE);
			labelCommentKey.setVisibility(View.GONE);
		}
		else {
			txtCommentKey.setVisibility(View.VISIBLE);
			labelCommentKey.setVisibility(View.VISIBLE);
		}

		btnCommentList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = txtCommentKey.getText().toString();
				String start = txtStart.getText().toString();
				String end = txtEnd.getText().toString();
				
				if(!StringUtils.isEmpty(key) && !user_only) {
					Intent i = new Intent(CommentListSelectActivity.this, CommentListActivity.class);
					i.putExtra("key", key);
					i.putExtra("start", start);
					i.putExtra("end", end);
					startActivity(i);
				}
				else if(user_only) {
					Intent i = new Intent(CommentListSelectActivity.this, CommentUserListActivity.class);
					i.putExtra("key", key);
					i.putExtra("start", start);
					i.putExtra("end", end);
					startActivity(i);
				}
			}
		});
	}
}
