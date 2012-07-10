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
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.dialog.SafeProgressDialog;

public class CommentCreateActivity extends CommentBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_create);
		
		Socialize.init(this);
		
		final EditText txtKey = (EditText) findViewById(R.id.txtEntityKey);
		final EditText txtName = (EditText) findViewById(R.id.txtComment);
		final TextView txtCommentCreateResult = (TextView) findViewById(R.id.txtCommentCreateResult);
		
		final Button btnCommentCreate = (Button) findViewById(R.id.btnCommentCreate);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			btnCommentCreate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext(), "Posting Comment", "Please wait...");
					
					txtCommentCreateResult.setText("");
					btnCommentCreate.setEnabled(false);
					
					String key = txtKey.getText().toString();
					String name = txtName.getText().toString();
					
					Entity entity = Entity.newInstance(key, "Test Entity");
					
					CommentUtils.addComment(CommentCreateActivity.this, entity, name, new CommentAddListener() {
						@Override
						public void onError(SocializeException error) {
							btnCommentCreate.setEnabled(true);
							txtCommentCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(CommentCreateActivity.this, error));
							progress.dismiss();
						}
						
						@Override
						public void onCreate(Comment comment) {
							btnCommentCreate.setEnabled(true);
							txtCommentCreateResult.setText("SUCCESS");
							populateCommentData(comment);
							progress.dismiss();
						}
					});
				}
			});
		}
		else {
			// Not authorized, you would normally do a re-auth here
			txtCommentCreateResult.setText("AUTH FAIL");
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
}
