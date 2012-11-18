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
package com.socialize.demo.implementations.actionbar;

import java.util.List;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.socialize.ActionBarUtils;
import com.socialize.demo.Debug;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Comment;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarShareEventListener;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.LinkifyCommentViewActionListener;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 */
public class DefaultActionBarActivity extends DemoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(Debug.profileActionBar) {
			android.os.Debug.startMethodTracing("actionbar");
		}
		
		View actionBar = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, null, new ActionBarListener() {
			@Override
			public void onCreate(ActionBarView actionBar) {
				// Add clickable links to comments
				actionBar.setOnCommentViewActionListener(new LinkifyCommentViewActionListener() {

					@Override
					public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
						super.onCommentList(view, comments, start, end);
						if(Debug.profileCommentList) {
							Debug.profileCommentList = false;
							android.os.Debug.stopMethodTracing();
						}
					}
				});
				
				// Add listeners for profiling.
				actionBar.setOnActionBarEventListener(new OnActionBarShareEventListener() {
					
					@Override
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						
						if(evt.equals(ActionBarEvent.SHARE) && Debug.profileShareDialog) {
							android.os.Debug.startMethodTracing("sharedialog");
						}
						else if(evt.equals(ActionBarEvent.COMMENT) && Debug.profileCommentList) {
							android.os.Debug.startMethodTracing("commentlist");
						}
						
						return super.onClick(actionBar, evt);
					}

					@Override
					public void onShow(Dialog dialog, SharePanelView dialogView) {
						super.onShow(dialog, dialogView);
						if(Debug.profileShareDialog) {
							Debug.profileShareDialog = false;
							android.os.Debug.stopMethodTracing();
						}
					}
					
					

					@Override
					public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
						Toast.makeText(DefaultActionBarActivity.this, "Share successful", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						super.onLoad(actionBar);
						if(Debug.profileActionBar) {
							Debug.profileActionBar = false;
							android.os.Debug.stopMethodTracing();
						}
					}
				});
			}
		});		
		
		setContentView(actionBar);
	}

}
