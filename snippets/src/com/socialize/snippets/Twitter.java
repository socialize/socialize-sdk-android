package com.socialize.snippets;

import java.util.Map;
import android.app.Activity;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;

public class Twitter extends Activity {

	public void comment() {
		if(Socialize.getSocialize().isAuthenticated()) {

			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");

			String comment = "The comment to be added";

			ShareOptions options = new ShareOptions();

			// Allow display of the user's location in comments
			options.setShareLocation(true);

			// Enable sharing to twitter.
			options.setShareTo(SocialNetwork.TWITTER);

			// Listen for the outcome of the twitter post
			options.setListener(new SocialNetworkListener() {
				@Override
				public void onSocialNetworkError(SocialNetwork network, Exception error) {
					// Handle error
				}

				@Override
				public void onBeforePost(Activity parent, SocialNetwork socialNetwork, Map<String, String> params) {
					// Handle before post
				}

				
				@Override
				public void onAfterPost(Activity parent, SocialNetwork socialNetwork) {
					// Handle before post
				}
				
			});

			// Add a comment to an entity
			// The "this" reference below refers to a Context object
			Socialize.getSocialize().addComment(this, entity, comment, options, new CommentAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(Comment comment) {
					// Handle success
				}
			});
		}		
	}
}
