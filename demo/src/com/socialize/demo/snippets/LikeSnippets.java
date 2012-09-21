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
import com.socialize.LikeUtils;
import com.socialize.UserUtils;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;


/**
 * @author Jason Polites
 *
 */
public class LikeSnippets extends Activity {

public void addLikeWithAutoShare() {
// begin-snippet-0
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// The "this" argument refers to the current Activity
LikeUtils.like(this, entity, new LikeAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Like result) {
		// Like was created
	}
});
// end-snippet-0
}


public void unlike() {
// begin-snippet-1
// Just use the entity key of the entity that was liked
// The "this" argument refers to the current Activity
LikeUtils.unlike(this, "http://myentity.com", new LikeDeleteListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onDelete() {
		// Like was deleted
	}
});
// end-snippet-1
}


public void addLikeWithManualShare() {
// begin-snippet-4
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// Get the default options for the user.
LikeOptions likeOptions = LikeUtils.getUserLikeOptions(this);

// Pass the share options in the call to create the like.
// The "this" argument refers to the current Activity
LikeUtils.like(this, entity, likeOptions, new LikeAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Like result) {
		// Like was created
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

public void getLikesByEntity() {
// begin-snippet-6
String entityKey = "http://getsocialize.com";

// Get first 10 likes
// The "this" argument refers to the current Activity
LikeUtils.getLikesByEntity(this, entityKey, 0, 10, new LikeListListener() {
	
	@Override
	public void onList(List<Like> likes, int totalCount) {
		// Found likes
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-6
}

public void getLikesByUser() throws SocializeException {
// begin-snippet-5
User user = UserUtils.getCurrentUser(this);

// Get first 10 likes by user
// The "this" argument refers to the current Activity
LikeUtils.getLikesByUser(this, user, 0, 10, new LikeListListener() {
	
	@Override
	public void onList(List<Like> likes, int totalCount) {
		// Found likes
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-5
}
	

public void getLikesByApplication() {
// begin-snippet-7
// Get first 10 likes
// The "this" argument refers to the current Activity
LikeUtils.getLikesByApplication(this, 0, 10, new LikeListListener() {
	
	@Override
	public void onList(List<Like> likes, int totalCount) {
		// Found likes
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
//end-snippet-7
}
}
