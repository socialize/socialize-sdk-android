package com.socialize.snippets;

import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.UserActivityListListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.networks.PostData;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;

public class SocializeActivity extends Activity {

	String consumerKey;
	String consumerSecret;
	String facebookAppId;
	private String twitterConsumerKey;
	private String twitterConsumerSecret;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Socialize.init(this);

		// Your code goes here
	}

	protected void onDestroy() {
		// Destroy Socialize before calling superclass
		Socialize.destroy(this);
		super.onDestroy();
	}


	void facebookAuth() {
		
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId(facebookAppId); // Your facebook app ID

		Socialize.getSocialize().authenticate(
				this,
				consumerKey, // From your account at getsocialize.com
				consumerSecret, // From your account at getsocialize.com
				info, 
				new SocializeAuthListener() {
					public void onAuthSuccess(SocializeSession session) {
						// Success!
					}
					public void onAuthFail(SocializeException error) {
						// Handle auth fail
					}
					public void onError(SocializeException error) {
						// Handle error
					}
					public void onCancel() {
						// Handle cancel
					}

				});		
	}
	
	void twitterAuth() {
		
		TwitterAuthProviderInfo info = new TwitterAuthProviderInfo();
		info.setConsumerKey(twitterConsumerKey); // Your twitter consumer key
		info.setConsumerSecret(twitterConsumerSecret); // Your twitter consumer secret
		
		Socialize.getSocialize().authenticate(
				this,
				consumerKey, // From your account at getsocialize.com
				consumerSecret, // From your account at getsocialize.com
				info, 
				new SocializeAuthListener() {
					public void onAuthSuccess(SocializeSession session) {
						// Success!
					}
					public void onAuthFail(SocializeException error) {
						// Handle auth fail
					}
					public void onError(SocializeException error) {
						// Handle error
					}
					public void onCancel() {
						// Handle cancel
					}

				});		
	}	

	void snippet1() {

		Socialize.getSocialize().authenticate(
				this,
				AuthProviderType.FACEBOOK,
				new SocializeAuthListener() {
					public void onAuthSuccess(SocializeSession session) {
						// Success!
					}
					public void onAuthFail(SocializeException error) {
						// Handle auth fail
					}
					public void onError(SocializeException error) {
						// Handle error
					}
					public void onCancel() {
						// Handle cancel
					}
				});		
	}

	void snippet2() {

		Socialize.getSocialize().authenticate(
				this,
				consumerKey, // From your account at getsocialize.com
				consumerSecret, // From your account at getsocialize.com
				new SocializeAuthListener() {
					public void onAuthSuccess(SocializeSession session) {
						// Success!
					}
					public void onAuthFail(SocializeException error) {
						// Handle auth fail
					}
					public void onError(SocializeException error) {
						// Handle error
					}
					public void onCancel() {
						// Handle cancel
					}
				});		
	}

	void snippet3() {

		Socialize.getSocialize().authenticate(
				this,
				new SocializeAuthListener() {
					public void onAuthSuccess(SocializeSession session) {
						// Success!
					}
					public void onAuthFail(SocializeException error) {
						// Handle auth fail
					}
					public void onError(SocializeException error) {
						// Handle error
					}
					public void onCancel() {
						// Handle cancel
					}
				});		
	}	

	void snippet4() {

		if(Socialize.getSocialize().isAuthenticated()) {

			// Create a new entity with a name
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");

			Socialize.getSocialize().addEntity(this, entity, new EntityAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(Entity entity) {
					// Handle success
				}
			});
		}		
	}

	void snippet5() {
		if(Socialize.getSocialize().isAuthenticated()) {
			Socialize.getSocialize().getEntity("http://someurl.com", new EntityGetListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onGet(Entity entity) {
					// Handle success
				}
			});
		}
	}

	void snippet6() {

		if(Socialize.getSocialize().isAuthenticated()) {

			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");			

			// Record a view
			Socialize.getSocialize().view(this, entity, new ViewAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(com.socialize.entity.View view) {
					// Handle success
				}
			});
		}		
	}


	void snippet7() {


		if(Socialize.getSocialize().isAuthenticated()) {
			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");			

			// Add a like to an entity defined by its URL
			Socialize.getSocialize().like(this, entity, new LikeAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(Like like) {
					// Handle success
				}
			});
		}	
	}	

	void snippet8() {

		if(Socialize.getSocialize().isAuthenticated()) {
			// Remove an existing like based on its numeric ID
			Socialize.getSocialize().unlike(1234, new LikeDeleteListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onDelete() {
					// Handle success
				}
			});
		}		
	}

	void snippet9() {
		if(Socialize.getSocialize().isAuthenticated()) {
			// List likes for a single user
			Socialize.getSocialize().listLikesByUser(1234, new LikeListListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				@Override
				public void onList(List<Like> items, int totalSize) {
					// Handle success
				}
			});
		}		
	}

	void snippet10() {

		if(Socialize.getSocialize().isAuthenticated()) {

			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");	

			String comment = "The comment to be added";

			// Add a comment to an entity
			Socialize.getSocialize().addComment(this, entity, comment, new CommentAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(Comment comment) {
					// Handle success
				}
			});
		}	
	}
	
	void snippet10a() {

		if(Socialize.getSocialize().isAuthenticated()) {

			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");	

			String comment = "The comment to be added";
			
			ShareOptions options = new ShareOptions();
			
			// Allow display of the user's location in comments
			options.setShareLocation(true);
			
			// Enable sharing to facebook.
			options.setShareTo(SocialNetwork.FACEBOOK);
			
			// Listen for the outcome of the facebook post
			options.setListener(new SocialNetworkListener() {
				
				@Override
				public void onError(Activity context, SocialNetwork network, Exception error) {
					// Handle error
				}

				@Override
				public void onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
					// Handle before post
				}

				@Override
				public void onAfterPost(Activity activity, SocialNetwork network) {
					// Handle after post
				}
			});

			// Add a comment to an entity
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

	void snippet11() {
		if(Socialize.getSocialize().isAuthenticated()) {
			// Get a single comment based on its numeric ID
			Socialize.getSocialize().getCommentById(1234, new CommentGetListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onGet(Comment entity) {
					// Handle success
				}
			});
		}
	}

	void snippet12() {
		if(Socialize.getSocialize().isAuthenticated()) {
			// List comments for an entity without pagination (maximum 100 will be returned)
			Socialize.getSocialize().listCommentsByEntity("http://someurl.com", new CommentListListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onList(ListResult<Comment> result) {
					// Handle success
				}
			});
		}		
	}

	void snippet13() {
		if(Socialize.getSocialize().isAuthenticated()) {
			// List comments for an entity with pagination (start and end index)
			Socialize.getSocialize().listCommentsByEntity("http://someurl.com", 0, 100, new CommentListListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onList(ListResult<Comment> result) {
					// Handle success
				}
			});
		}		
	}

	void snippet14() {
		if(Socialize.getSocialize().isAuthenticated()) {
			// List comments for a single user
			Socialize.getSocialize().listCommentsByUser(1234, new CommentListListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onList(ListResult<Comment> likes) {
					// Handle success
				}
			});
		}		
	}

	void snippet15() {
		if(Socialize.getSocialize().isAuthenticated()) {

			// You can provide any user id you like, but here's how to use the current logged in user
			Long userId = Socialize.getSocialize().getSession().getUser().getId();

			// List all activity for a single user
			Socialize.getSocialize().listActivityByUser(userId, new UserActivityListListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onList(ListResult<SocializeAction> activity) {
					// Handle success
				}
			});
		}		
	}

	void snippet16() {
		if(Socialize.getSocialize().isAuthenticated()) {

			// You can provide any user id you like, but here's how to use the current logged in user
			Long userId = Socialize.getSocialize().getSession().getUser().getId();

			// Set the start/end index for pagination
			int startIndex = 0;
			int endIndex = 10;

			// List all activity for a single user
			Socialize.getSocialize().listActivityByUser(
					userId,
					startIndex,
					endIndex,
					new UserActivityListListener() {

						public void onError(SocializeException error) {
							// Handle error
						}

						public void onList(ListResult<SocializeAction> activity) {
							// Handle success
						}
					});
		}		
	}	

	void snippet17() {
		// Create or retrieve your entity object
		Entity entity = Entity.newInstance("http://someurl.com", "My Entity");	
		Socialize.getSocializeUI().showCommentView(this, entity);		
	}

	void snippet18() {
		// btnCommentView would be the name of your button in your UI
		Button btn = (Button) findViewById(R.id.btnCommentView);

		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Create or retrieve your entity object
				Entity entity = Entity.newInstance("http://someurl.com", "My Entity");					
				Socialize.getSocializeUI().showCommentView(SocializeActivity.this, entity);		
			}
		});
	}
	
	void snippet19() {
		if(Socialize.getSocialize().isAuthenticated()) {

			// Create or retrieve your entity object
			Entity entity = Entity.newInstance("http://someurl.com", "My Entity");	

			String comment = "The comment to be added";
			
			// Create a share options instance to nomimate the sharing preferences.
			ShareOptions shareOptions = new ShareOptions();
			
			// Set to share on Facebook.
			shareOptions.setShareTo(SocialNetwork.FACEBOOK);
			
			// Add a comment to an entity
			Socialize.getSocialize().share(this, entity, comment, shareOptions, new ShareAddListener() {

				public void onError(SocializeException error) {
					// Handle error
				}

				public void onCreate(Share comment) {
					// Handle success
				}
			});
		}			
	}
	
	
	
	void snippet20() {
		Entity entity = new Entity();
		entity.setKey("mykey");
		entity.setName("The Name");
		entity.setMetaData("extra_info");
		
		Socialize.getSocialize().addEntity(this, entity, new EntityAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				// Handle error
			}
			
			@Override
			public void onCreate(Entity entity) {
				// Handle success
			}
		});
	}		
}
