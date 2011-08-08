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

import java.sql.Date;

import android.widget.TextView;

import com.socialize.activity.SocializeActivity;
import com.socialize.entity.Comment;

public abstract class CommentBaseActivity extends SocializeActivity {
	
	protected void clearCommentData() {
		final TextView txtCommentIdCreated = (TextView) findViewById(R.id.txtCommentIdCreated);
		final TextView txtCommentTextCreated = (TextView) findViewById(R.id.txtCommentTextCreated);
		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
		final TextView txtCommentUserCreated = (TextView) findViewById(R.id.txtCommentUserCreated);
		final TextView txtCommentApplicationCreated = (TextView) findViewById(R.id.txtCommentApplicationCreated);
		final TextView txtCommentDateCreated = (TextView) findViewById(R.id.txtCommentDateCreated);
		final TextView txtCommentLatCreated = (TextView) findViewById(R.id.txtCommentLatCreated);
		final TextView txtCommentLongCreated = (TextView) findViewById(R.id.txtCommentLongCreated);
		
		txtCommentIdCreated.setText("");
		txtCommentTextCreated.setText("");
		txtEntityKeyCreated.setText("");
		txtCommentUserCreated.setText("");
		txtCommentApplicationCreated.setText("");
		txtCommentDateCreated.setText("");
		txtCommentLatCreated.setText("");
		txtCommentLongCreated.setText("");
	}
	
	protected void populateCommentData(Comment comment) {
		final TextView txtCommentIdCreated = (TextView) findViewById(R.id.txtCommentIdCreated);
		final TextView txtCommentTextCreated = (TextView) findViewById(R.id.txtCommentTextCreated);
		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
		final TextView txtCommentUserCreated = (TextView) findViewById(R.id.txtCommentUserCreated);
		final TextView txtCommentApplicationCreated = (TextView) findViewById(R.id.txtCommentApplicationCreated);
		final TextView txtCommentDateCreated = (TextView) findViewById(R.id.txtCommentDateCreated);
		final TextView txtCommentLatCreated = (TextView) findViewById(R.id.txtCommentLatCreated);
		final TextView txtCommentLongCreated = (TextView) findViewById(R.id.txtCommentLongCreated);
		
		if(comment.getId() != null) {
			txtCommentIdCreated.setText(comment.getId().toString());
		}
		
		if(comment.getText() != null) {
			txtCommentTextCreated.setText(comment.getText());
		}
		
		if(comment.getEntity() != null) {
			txtEntityKeyCreated.setText(comment.getEntity().getKey());
		}

		if(comment.getUser() != null) {
			txtCommentUserCreated.setText(String.valueOf(comment.getUser().getId()));
		}
		
		if(comment.getApplication() != null) {
			txtCommentApplicationCreated.setText(comment.getApplication().getName());
		}
		
		if(comment.getDate() != null) {
			txtCommentDateCreated.setText(new Date(comment.getDate()).toGMTString());
		}
		
		if(comment.getLat() != null) {
			txtCommentLatCreated.setText(String.valueOf(comment.getLat()));
		}
		
		if(comment.getLon() != null) {
			txtCommentLongCreated.setText(String.valueOf(comment.getLon()));
		}
	}
}
