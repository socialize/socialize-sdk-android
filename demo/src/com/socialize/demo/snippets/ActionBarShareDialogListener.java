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
package com.socialize.demo.snippets;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.api.action.ShareType;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarShareEventListener;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.SharePanelView;

//begin-snippet-0
public class ActionBarShareDialogListener extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.getsocialize.com";
		
		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		// Add more options
		ActionBarOptions options = new ActionBarOptions();
		
		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, options, new ActionBarListener() {
			@Override
			public void onCreate(ActionBarView actionBar) {
				// Add event listener for share dialog events
				actionBar.setOnActionBarEventListener(new OnActionBarShareEventListener() {
					
					/******************************************************************
					 * Standard event listener callbacks (Optional)
					 ******************************************************************/
					
					@Override
					public void onUpdate(ActionBarView actionBar) {
						// Called when the action bar has its data updated
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
						// Called AFTER a user has removed a like
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						// Called AFTER a user has posted a share
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
						// Called AFTER a user has posted a like
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						// Called when the action bar is loaded
					}
					
					@Override
					public void onLoadFail(Exception error) {
						// Called when the action bar load failed
					}					

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
						// Called when the action bar retrieves the like for the
						// current user
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
						// Called when the action bar retrieves the entity data
					}

					@Override
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						// Called when the user clicks on the action bar
						// Return true to indicate you do NOT want the action to continue
						return false;
					}

					/******************************************************************
					 * Share dialog callbacks (Optional)
					 ******************************************************************/
					
					@Override
					public void onShow(Dialog dialog, SharePanelView dialogView) {
						// The dialog was shown.
					}
					
					@Override
					public void onCancel(Dialog dialog) {
						// User cancelled.
					}
					
					@Override
					public void onSimpleShare(ShareType type) {
						// User performed a simple share operation (e.g. Email or SMS)
					}
					
					@Override
					public void onFlowInterrupted(DialogFlowController controller) {
						// This will only be called if onContinue returns true
						
						// Obtain share text (e.g. from the user via a dialog)
						String text = "Add another message here";
						
						// Call continue when you want flow to resume
						controller.onContinue(text);
					}
					
					@Override
					public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
						// Return true if you want to interrupt the flow
						return true;
					}
					
					/******************************************************************
					 * Social Network Callbacks (Optional)
					 ******************************************************************/
					
					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						// Handle error
					}
					
					@Override
					public void onCancel() {
						// The user cancelled the operation.
					}
					
					@Override
					public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
						// Called after the post returned from the social network.
						// responseObject contains the raw JSON response from the social network.
					}
					
					@Override
					public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
						// Called just prior to the post.
						// postData contains the dictionary (map) of data to be posted.  
						// You can change this here to customize the post.
						// Return true to prevent the post from occurring.
						return false;
					}
				});
			}
		});
		
		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);
	}
}
//end-snippet-0


