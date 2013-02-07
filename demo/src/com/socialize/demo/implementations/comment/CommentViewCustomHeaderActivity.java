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
package com.socialize.demo.implementations.comment;

import java.util.List;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.socialize.CommentUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.OnCommentViewActionListener;


/**
 * @author Jason Polites
 *
 */
public class CommentViewCustomHeaderActivity extends DemoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommentUtils.showCommentView(this, entity, new OnCommentViewActionListener() {
			
			@Override
			public void onError(SocializeException error) {}
			
			@Override
			public void onRender(CommentListView view) {
				CommentViewCustomHeaderActivity.this.finish();
			}
			
			@Override
			public void onReload(CommentListView view) {}
			
			@Override
			public void onPostComment(Comment comment) {}
			
			@Override
			public void onCreate(CommentListView view) {
				view.setCustomHeaderText("Custom Header Text");
				view.setShowCommentCountInHeader(false);
				view.getHeader().setBackground(new ColorDrawable(Color.LTGRAY));
				view.getHeader().getTitleText().setTextColor(Color.BLACK);
			}
			
			@Override
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {}
			
			@Override
			public void onBeforeSetComment(Comment comment, CommentListItem item) {}
			
			@Override
			public void onAfterSetComment(Comment comment, CommentListItem item) {}
		});
	}
}
