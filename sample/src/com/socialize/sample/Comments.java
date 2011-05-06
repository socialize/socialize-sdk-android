package com.socialize.sample;

import android.content.Context;
import android.widget.Toast;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.listener.comment.CommentAddListener;

public class Comments {
	public void addComment(final Context context, Socialize socialize, String entity, String comment) {
		socialize.addComment(entity, comment, new CommentAddListener() {
			@Override
			public void onCreate(Comment entity) {
				Toast.makeText(context, "Comment [" +
						entity.getComment() +
						"] created", Toast.LENGTH_LONG);
			}
		});
	}
}
