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
package com.socialize.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.util.StringUtils;

public class CommentGetActivity extends CommentBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_get);
		
		Socialize.init(this);
		
		final EditText txtCommentId = (EditText) findViewById(R.id.txtCommentId);
		final TextView txtCommentGetResult = (TextView) findViewById(R.id.txtCommentGetResult);
		final Button btnCommentGet = (Button) findViewById(R.id.btnCommentGet);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			btnCommentGet.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtCommentGetResult.setText("");
					btnCommentGet.setEnabled(false);
					String id = txtCommentId.getText().toString();
					doCommentGet(id);
				}
			});
			
			// Look for an existing ID passed in
			// This happens when the user clicks an item from the comment list
			if(getIntent().getExtras() != null) {
				String id = getIntent().getExtras().getString("id");
				
				if(!StringUtils.isEmpty(id)) {
					txtCommentId.setText(id);
					doCommentGet(id);
				}
			}
	
		}
		else {
			// Not authorized, you would normally do a re-auth here
			txtCommentGetResult.setText("AUTH FAIL");
		}
	}
	
	private void doCommentGet(String id) {
		final TextView txtCommentGetResult = (TextView) findViewById(R.id.txtCommentGetResult);
		final Button btnCommentGet = (Button) findViewById(R.id.btnCommentGet);
		
		clearCommentData();
		
		if(!StringUtils.isEmpty(id)) {
			
			final ProgressDialog progress = SafeProgressDialog.show(CommentGetActivity.this, "Retrieving", "Please wait...");
			
			
			try {
				
				CommentUtils.getComment(this, new CommentGetListener() {
					
					@Override
					public void onError(SocializeException error) {
						txtCommentGetResult.setText("FAIL: " + ErrorHandler.handleApiError(CommentGetActivity.this, error));
						btnCommentGet.setEnabled(true);
						progress.dismiss();
					}
					
					@Override
					public void onGet(Comment entity) {
				
						btnCommentGet.setEnabled(true);
						
						if(entity != null) {
							txtCommentGetResult.setText("SUCCESS");
							populateCommentData(entity);
						}
						else {
							txtCommentGetResult.setText("NOT FOUND");
						}
						
						progress.dismiss();
					}
				}, Integer.parseInt(id));
			}
			catch (NumberFormatException e) {
				txtCommentGetResult.setText("FAIL: ID is not a number");
				btnCommentGet.setEnabled(true);
				progress.dismiss();
			}
		}
		else {
			txtCommentGetResult.setText("FAIL: No Key");
			btnCommentGet.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
}
