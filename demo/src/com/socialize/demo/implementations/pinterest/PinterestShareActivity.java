package com.socialize.demo.implementations.pinterest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.pinterest.pinit.PinIt;
import com.pinterest.pinit.PinItListener;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.ui.dialog.SafeProgressDialog;

public class PinterestShareActivity extends Activity {

	// Picture of Machu Picchu :)
	static String IMAGE_TO_SHARE = "https://lh5.googleusercontent.com/-oPgrWjOC-6w/T9UWPfl2qoI/AAAAAAAABMQ/KtRKcWwRsJ0/s932/P1010444.JPG";

	// We are using the image AS the url, but you could have a separate URL if you needed.
	static String URL_TO_SHARE = IMAGE_TO_SHARE;

	// Your Pinterest client ID (CHANGE THIS)
	static String PINTEREST_CLIENT_ID = "1431940";

	static {
		PinIt.setPartnerId(PINTEREST_CLIENT_ID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Socialize.onCreate(this, savedInstanceState);

		setContentView(R.layout.pinterest_activity);

		Button shareButton = (Button) findViewById(R.id.btnShareToPinterest);
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doPinterestShare(URL_TO_SHARE);
			}
		});
	}

	public void doPinterestShare(String url) {

		// Create an entity to represent the image
		Entity entity = Entity.newInstance(url, "Machu Picchu");
		ShareOptions options = ShareUtils.getUserShareOptions(this);

		final ProgressDialog dialog = SafeProgressDialog.show(this);

		ShareUtils.registerShare(this, entity, options, new ShareAddListener() {
			@Override
			public void onCreate(Share share) {

				// Use the URL from the share object
				PropagationInfo propagationInfo = share.getPropagationInfoResponse().getPropagationInfo(ShareType.OTHER);

				PinIt pinIt = new PinIt();
				pinIt.setImageUrl(IMAGE_TO_SHARE);
				pinIt.setUrl(propagationInfo.getEntityUrl());
				pinIt.setDescription(share.getEntityDisplayName());
				pinIt.setListener(new PinItListener() {
					@Override
					public void onComplete(boolean completed) {
						dialog.dismiss();
						if(completed) {
							Toast.makeText(PinterestShareActivity.this, "Shared to Pinterest!", Toast.LENGTH_LONG).show();
						}
						else {
							Toast.makeText(PinterestShareActivity.this, "Share Failed!", Toast.LENGTH_LONG).show();
						}
					}
					@Override
					public void onException(Exception e) {
						dialog.dismiss();
						handleError(e);
					}
				});

				// Do the pin
				pinIt.doPinIt(PinterestShareActivity.this);
			}

			@Override
			public void onError(SocializeException error) {
				dialog.dismiss();
				handleError(error);
			}
		});
	}

	private void handleError(Exception error) {
		error.printStackTrace();
		Toast.makeText(PinterestShareActivity.this, "Failed to share to Pinterest (See logs)", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {

		Socialize.onDestroy(this);

		super.onDestroy();
	}

	@Override
	protected void onPause() {

		Socialize.onPause(this);

		super.onPause();
	}

	@Override
	protected void onResume() {

		Socialize.onResume(this);

		super.onResume();
	}
}
