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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.net.Uri;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookUtils;


/**
 * @author Jason Polites
 *
 */
public class FacebookSnippets extends Activity {
public void linkFB() {
// begin-snippet-0
// The "this" argument refers to the current Activity
FacebookUtils.link(this, new SocializeAuthListener() {

	@Override
	public void onCancel() {
		// The user cancelled the operation.
	}
	
	@Override
	public void onAuthSuccess(SocializeSession session) {
		// User was authed.
	}
	
	@Override
	public void onAuthFail(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-0
}

public void linkFBWithToken() {
// begin-snippet-1
// The user's Facebook auth token
String fbToken = "ABCDEF...GHIJKL";

// The "this" argument refers to the current Activity
// Specify "true" to verify that the permissions on this token are correct
FacebookUtils.link(this, fbToken, true, new SocializeAuthListener() {

	@Override
	public void onCancel() {
		// The user cancelled the operation.
	}
	
	@Override
	public void onAuthSuccess(SocializeSession session) {
		// User was authed.
	}
	
	@Override
	public void onAuthFail(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
});
// end-snippet-1
}

public void setSingleSignOn() {
// begin-snippet-2
// Disable ONLY if you experience problems
// The "this" argument refers to the current Activity
ConfigUtils.getConfig(this).setFacebookSingleSignOnEnabled(false);	
// end-snippet-2
}
public void unlink() {
// begin-snippet-3
// Disconnect the user from their Facebook account
// The "this" argument refers to the current Activity
FacebookUtils.unlink(this);	
// end-snippet-3
}

public void postEntity() {
// begin-snippet-4
Entity entity = Entity.newInstance("http://myentity.com", "My Name");
	
// The "this" argument refers to the current Activity
FacebookUtils.postEntity(this, entity, "Text to be posted", new SocialNetworkShareListener() {
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
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
// end-snippet-4
}


public void post() {
// begin-snippet-5
// The graph API path to be called
String graphPath = "me/links";

// The data to be posted. This is based on the graphPath
// See http://developers.facebook.com/docs/reference/api/
Map<String, Object> postData = new HashMap<String, Object>();
postData.put("message", "A message to post");
postData.put("link", "http://getsocialize.com");
postData.put("name", "Socialize SDK!");
	
// Execute a POST on facebook
// The "this" argument refers to the current Activity
FacebookUtils.post(this, graphPath, postData, new SocialNetworkPostListener() {
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}
});
// end-snippet-5
}

public void get() {
// begin-snippet-6
// The graph API path to be called
String graphPath = "me/links";

// Execute a GET on facebook
// The "this" argument refers to the current Activity
FacebookUtils.get(this, graphPath, null, new SocialNetworkPostListener() {
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}
});
// end-snippet-6
}


public void delete() {
// begin-snippet-7
// The graph API path to be called
String graphPath = "me/links/1234";

// Execute a DELETE on facebook
// The "this" argument refers to the current Activity
FacebookUtils.delete(this, graphPath, null, new SocialNetworkListener() {
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}
	
	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		// Called before the post to the given network is made
		// Return true to prevent the post from occurring
		return false;
	}
});
// end-snippet-7
}




public void postWithUrl() {
final Activity context = this;
// begin-snippet-8
// Create a simple share object to get the propagation data
final Entity entity = Entity.newInstance("http://myentity.com", "My Name");

ShareOptions options = ShareUtils.getUserShareOptions(this);

// The "this" argument refers to the current Activity
ShareUtils.registerShare(this, entity, options, new ShareAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Share share) {
		
		// Get the propagation info from the result
		PropagationInfoResponse propagationInfoResponse = share.getPropagationInfoResponse();
		
		PropagationInfo propagationInfo = propagationInfoResponse.getPropagationInfo(SocialNetwork.FACEBOOK);
		
		// The graph API path to be called
		String graphPath = "me/links";

		// The data to be posted. This is based on the graphPath
		// See http://developers.facebook.com/docs/reference/api/
		Map<String, Object> postData = new HashMap<String, Object>();
		postData.put("message", "A message to post");
		postData.put("link", propagationInfo.getEntityUrl()); // Use the SmartDownload URL
		postData.put("name", entity.getDisplayName());
			
		// Execute a POST on facebook
		FacebookUtils.post(context, graphPath, postData, new SocialNetworkListener() {
			
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
				// Called after the post returned from Facebook.
				// responseObject contains the raw JSON response from Facebook.
			}
			
			@Override
			public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
				// Called before the post to the given network is made
				// Return true to prevent the post from occurring
				return false;
			}
		});		
	}
}, SocialNetwork.FACEBOOK);
// end-snippet-8
}

public void postPhoto() throws IOException {
// begin-snippet-9
//The "this" argument refers to the current Activity
final Activity context = this;
	
final Entity entity = Entity.newInstance("http://myentity.com", "My Name");	
	
// First create a Socialize share object so we get the correct URLs
ShareOptions options = ShareUtils.getUserShareOptions(context);

ShareUtils.registerShare(context, entity, options, new ShareAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Share result) {
		
		// We have the result, use the URLs to add to the post
		PropagationInfo propagationInfo = result.getPropagationInfoResponse().getPropagationInfo(ShareType.FACEBOOK);
		String link = propagationInfo.getEntityUrl();

		// Now post to Facebook.
		Map<String, Object> postData = new HashMap<String, Object>();
		
		// TODO: Get the URI of your image from the local device.
		// TODO: ***** DON'T FORGET TO USE YOUR OWN IMAGE HERE (See the sample app for a working example) ****
		Uri photoUri = null;
		
		try {
			
			// Format the picture for Facebook
			byte[] imageData = FacebookUtils.getImageForPost(context, photoUri);
			
			// Add the photo to the post
			postData.put("photo", imageData);
			
			// Add the link returned from Socialize to use SmartDownloads
			postData.put("caption", "A test photo of something " + link);
			
			// Add other fields to postData as necessary
			
			// Post to me/photos
			FacebookUtils.post(context, "me/photos", postData, new SocialNetworkPostListener() {
				
				@Override
				public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
					// Handle error
				}
				
				@Override
				public void onCancel() {
					// The user cancelled the auth process
				}
				
				@Override
				public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
					// The post was successful
				}
			});			
		}
		catch (IOException e) {
			// Handle error
		}
	}
}, SocialNetwork.FACEBOOK); // This is a Facebook-only share	
//end-snippet-9
}

public void entityType() {
// begin-snippet-10
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// MUST be a valid OG type
entity.setType("video.movie");
//end-snippet-10
}

public void postOG() {
// begin-snippet-11
Entity entity = Entity.newInstance("http://myentity.com", "My Entity Name");

// MUST be a valid OG type
entity.setType("video.movie");
	
// The "this" argument refers to the current Activity
FacebookUtils.postEntity(this, entity, "Text to be posted", new SocialNetworkShareListener() {
	
	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		// Change the post data to force an Open Graph call
		postData.setPath("me/video.watches");
		
		// me/video.watches requires a movie object type
		postData.getPostValues().put("movie", postData.getPropagationInfo().getEntityUrl());
		
		return false;
	}	
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}

});
// end-snippet-11
}


public void postOGCustom() {
// begin-snippet-12
Entity entity = Entity.newInstance("http://myentity.com", "My Entity Name");

// Set the type of the entity to include the namespace.
entity.setType("yournamespace:yourtype");
	
ShareOptions options = ShareUtils.getUserShareOptions(this);
options.setText("Text to be posted");

// The "this" argument refers to the current Activity
ShareUtils.shareViaSocialNetworks(this, entity, options, new SocialNetworkShareListener() {
	
	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		
		if(socialNetwork.equals(SocialNetwork.FACEBOOK)) {
			// Change the post data to force an Open Graph call
			postData.setPath("me/yournamespace:youraction");
			
			// Set the type to be the entity URL
			postData.getPostValues().put("yourtype", postData.getPropagationInfo().getEntityUrl());
		}
		
		return false;
	}	
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}

}, SocialNetwork.FACEBOOK);
// end-snippet-12
}

public void postOGShare() {
// begin-snippet-13
Entity entity = Entity.newInstance("http://myentity.com", "My Entity Name");

// MUST be a valid OG type
entity.setType("video.movie");

ShareOptions options = ShareUtils.getUserShareOptions(this);
options.setText("Text to be posted");

// The "this" argument refers to the current Activity
ShareUtils.shareViaSocialNetworks(this, entity, options, new SocialNetworkShareListener() {
	
	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		
		if(socialNetwork.equals(SocialNetwork.FACEBOOK)) {
			// Change the post data to force an Open Graph call
			postData.setPath("me/video.watches");
			
			// me/video.watches requires a movie object type
			postData.getPostValues().put("movie", postData.getPropagationInfo().getEntityUrl());
		}
		
		return false;
	}	
	
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
		// Called after the post returned from Facebook.
		// responseObject contains the raw JSON response from Facebook.
	}

}, SocialNetwork.FACEBOOK);
// end-snippet-13
}

}
