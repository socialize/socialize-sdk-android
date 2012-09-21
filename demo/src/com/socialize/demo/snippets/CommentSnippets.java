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

import java.util.List;
import org.json.JSONObject;
import android.app.Activity;
import com.socialize.CommentUtils;
import com.socialize.UserUtils;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.OnCommentViewActionListener;


/**
 * @author Jason Polites
 *
 */
public class CommentSnippets extends Activity{

public void addCommentWithAutoShare() {
// begin-snippet-0
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// The "this" argument refers to the current Activity
CommentUtils.addComment(this, entity, "This the comment", new CommentAddListener() {
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Comment result) {
		// Comment was created
	}
});
// end-snippet-0
}

public void showCommentView() {
// begin-snippet-1
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// Show the comment list view
// The "this" argument refers to the current Activity
CommentUtils.showCommentView(this, entity);
//end-snippet-1
}

public void showCommentViewWithListener() {
// begin-snippet-2
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// The "this" argument refers to the current Activity
CommentUtils.showCommentView(this, entity, new OnCommentViewActionListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onRender(CommentListView view) {
		// Called when the list view is rendered
	}
	
	@Override
	public void onReload(CommentListView view) {
		// Called when a reload event is posted to the list view.
	}
	
	@Override
	public void onPostComment(Comment comment) {
		// Called after a comment is posted.
	}
	
	@Override
	public void onCreate(CommentListView view) {
		// Called when the list view component was created (but may not be shown)
	}
	
	@Override
	public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
		// Called when a list of comments is retrieved.
	}
});
//end-snippet-2
}


public void addCommentWithManualShare() {
// begin-snippet-4
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// Get the default options for the user.
CommentOptions commentOptions = CommentUtils.getUserCommentOptions(this);

// Automatically subscribe this user to new comments on this entity (requires SmartAlerts)
commentOptions.setSubscribeToUpdates(true);

// Pass the share options in the call to create the comment.
// The "this" argument refers to the current Activity
CommentUtils.addComment(this, entity, "This the comment", commentOptions, new CommentAddListener() {
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Comment result) {
		// Comment was created
	}

	@Override
	public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
		// Failed to share to the given network
	}

	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		// Called before the post to the given network is made
		// Return true to prevent the post from occurring
		return false;
	}

	@Override
	public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
		// Called after the post to the given network is made
	}

	@Override
	public void onCancel() {
		// Called if the user canceled the auth process for social networks
	}
}, SocialNetwork.FACEBOOK, SocialNetwork.TWITTER); // Share to multiple networks simultaneously
//end-snippet-4
}


public void addCommentWithNoShare() {
// begin-snippet-8
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// Get the default options for the user.
CommentOptions commentOptions = CommentUtils.getUserCommentOptions(this);

// Do NOT promt the user to share
commentOptions.setShowShareDialog(false);

// Pass the share options in the call to create the comment.
// The "this" argument refers to the current Activity
CommentUtils.addComment(this, entity, "This the comment", commentOptions, new CommentAddListener() {
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Comment result) {
		// Comment was created
	}

});
//end-snippet-8
}


public void getCommentById() {

long commentId = 123L; 

// The "this" argument refers to the current Activity
CommentUtils.getComment(this, new CommentGetListener() {
	
	@Override
	public void onGet(Comment result) {
		// Comment found
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No comment with ID found
		}
		else {
			// Some other error
		}
	}
	
}, commentId);
}

public void getCommentsById() {
// begin-snippet-7
long commentId0 = 123;
long commentId1 = 456;

// The "this" argument refers to the current Activity
CommentUtils.getComments(this, new CommentListListener() {
	
	@Override
	public void onList(ListResult<Comment> result) {
		// Found comments
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No comment with ID found
		}
		else {
			// Some other error
		}
	}
}, commentId0, commentId1);
//end-snippet-7
}

public void getCommentsByEntity() {
// begin-snippet-6
String entityKey = "http://getsocialize.com";

// Get first 10 comments
// The "this" argument refers to the current Activity
CommentUtils.getCommentsByEntity(this, entityKey, 0, 10, new CommentListListener() {
	
	@Override
	public void onList(ListResult<Comment> result) {
		// Found comments
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-6
}

public void getCommentsByUser() throws SocializeException {
// begin-snippet-5
User user = UserUtils.getCurrentUser(this);

// Get first 10 comments by user
// The "this" argument refers to the current Activity
CommentUtils.getCommentsByUser(this, user, 0, 10, new CommentListListener() {
	
	@Override
	public void onList(ListResult<Comment> result) {
		// Found comments
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-5
}

public void getCommentsByApplication() {
// begin-snippet-9
// Get first 10 comments
// The "this" argument refers to the current Activity
CommentUtils.getCommentsByApplication(this, 0, 10, new CommentListListener() {
	
	@Override
	public void onList(ListResult<Comment> result) {
		// Found comments
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-9
}

}
