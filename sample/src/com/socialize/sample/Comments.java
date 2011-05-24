package com.socialize.sample;

import android.content.Context;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;

public class Comments {
	public void addComment(final Context context, Socialize socialize, SocializeSession session, String entity, String comment) {
		socialize.addComment(session, entity, comment, new CommentAddListener() {
			@Override
			public void onCreate(Comment entity) {
				Toast.makeText(context, "Comment [" +
						entity.getText() +
						"] created", Toast.LENGTH_LONG);
			}

			@Override
			public void onError(SocializeException error) {
				Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG);
			}
			
		});
	}
}
