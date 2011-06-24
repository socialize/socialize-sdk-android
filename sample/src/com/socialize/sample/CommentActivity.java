package com.socialize.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.sample.util.ErrorHandler;

public class CommentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		
		Socialize.init(this);
		
		final EditText txtKey = (EditText) findViewById(R.id.txtEntityKey);
		final EditText txtName = (EditText) findViewById(R.id.txtComment);
		final TextView txtCommentCreateResult = (TextView) findViewById(R.id.txtCommentCreateResult);
		
		final Button btnCommentCreate = (Button) findViewById(R.id.btnCommentCreate);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			btnCommentCreate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					txtCommentCreateResult.setText("");
					btnCommentCreate.setEnabled(false);
					
					String key = txtKey.getText().toString();
					String name = txtName.getText().toString();
					
					Socialize.getSocialize().addComment(key, name, new CommentAddListener() {
						@Override
						public void onError(SocializeException error) {
							btnCommentCreate.setEnabled(true);
							txtCommentCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(CommentActivity.this, error));
						}
						
						@Override
						public void onCreate(Comment entity) {
							btnCommentCreate.setEnabled(true);
							txtCommentCreateResult.setText("SUCCESS");
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
