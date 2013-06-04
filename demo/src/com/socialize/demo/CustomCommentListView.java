package com.socialize.demo;

import android.content.Context;
import android.util.Log;
import com.socialize.ui.comment.CommentListView;

public class CustomCommentListView extends CommentListView {

	public CustomCommentListView(Context context) {
		super(context);
	}

	@Override
	public void onViewLoad() {
		super.onViewLoad();

		Log.i("Socialize", "CustomCommentListView loaded");
	}
}
