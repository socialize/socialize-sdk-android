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

import android.app.Activity;
import android.os.Bundle;

import com.socialize.Socialize;

public class CommentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		
		Socialize.init(this);
		
//		final EditText txtKey = (EditText) findViewById(R.id.txtEntityKey);
//		final EditText txtName = (EditText) findViewById(R.id.txtComment);
//		final TextView txtCommentCreateResult = (TextView) findViewById(R.id.txtCommentCreateResult);
//		
//		final Button btnCommentCreate = (Button) findViewById(R.id.btnCommentCreate);
//		
//		if(Socialize.getSocialize().isAuthenticated()) {
//			
//			btnCommentCreate.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					txtCommentCreateResult.setText("");
//					btnCommentCreate.setEnabled(false);
//					
//					String key = txtKey.getText().toString();
//					String name = txtName.getText().toString();
//					
//					Socialize.getSocialize().addComment(key, name, new CommentAddListener() {
//						@Override
//						public void onError(SocializeException error) {
//							btnCommentCreate.setEnabled(true);
//							txtCommentCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(CommentActivity.this, error));
//						}
//						
//						@Override
//						public void onCreate(Comment comment) {
//							btnCommentCreate.setEnabled(true);
//							txtCommentCreateResult.setText("SUCCESS");
//							populateCommentData(comment);
//						}
//					});
//				}
//			});
//		}
//		else {
//			// Not authorized, you would normally do a re-auth here
//			txtCommentCreateResult.setText("AUTH FAIL");
//		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
//	private void populateCommentData(Comment comment) {
//		final TextView txtCommentIdCreated = (TextView) findViewById(R.id.txtCommentIdCreated);
//		final TextView txtCommentTextCreated = (TextView) findViewById(R.id.txtCommentTextCreated);
//		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
//		
//		if(comment.getId() != null) {
//			txtCommentIdCreated.setText(comment.getId().toString());
//		}
//		
//		if(comment.getText() != null) {
//			txtCommentTextCreated.setText(comment.getText());
//		}
//		
//		if(comment.getEntity() != null) {
//			txtEntityKeyCreated.setText(comment.getEntity().getKey());
//		}
//	}
}
