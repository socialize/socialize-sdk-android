package com.socialize.demo.snippets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import com.pinterest.pinit.PinIt;
import com.pinterest.pinit.PinItListener;
import com.socialize.ShareUtils;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.ui.dialog.SafeProgressDialog;

public class PinterestSnippets extends Activity {

	public void pin() {
// begin-snippet-0
// Your Pinterest client ID (CHANGE THIS)
String PINTEREST_CLIENT_ID = "123456";

// Set the partner ID on the PinIt instance (this can be done anywhere)
PinIt.setPartnerId(PINTEREST_CLIENT_ID);

final Activity context = this;

// The URL of the image being shared
final String imageUrl = "http://some.image.to.share/wow.jpg";

// Create an entity to represent the image
Entity entity = Entity.newInstance(imageUrl, "My Picture!");
ShareOptions options = ShareUtils.getUserShareOptions(this);

final ProgressDialog dialog = SafeProgressDialog.show(this);

// Create a Socialize share object to enable tracking of the share
ShareUtils.registerShare(this, entity, options, new ShareAddListener() {

	@Override
	public void onCreate(Share share) {

		// Get the propagation info based on the share type of OTHER
		PropagationInfo propagationInfo = share.getPropagationInfoResponse().getPropagationInfo(ShareType.OTHER);

		// Create a PinIt instance and set the properties you need.
		PinIt pinIt = new PinIt();
		pinIt.setImageUrl(imageUrl);

		// Use the URL from the share object
		pinIt.setUrl(propagationInfo.getEntityUrl());

		pinIt.setDescription(share.getEntityDisplayName());
		pinIt.setListener(new PinItListener() {
			@Override
			public void onComplete(boolean completed) {
				dialog.dismiss();
				if(completed) {
					Toast.makeText(context, "Shared to Pinterest!", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(context, "Share Failed!", Toast.LENGTH_LONG).show();
				}
			}
			@Override
			public void onException(Exception e) {
				dialog.dismiss();

				// Handle the error
			}
		});

		// Do the pin
		pinIt.doPinIt(context);
	}

	@Override
	public void onError(SocializeException error) {
		dialog.dismiss();

		// Handle the error
	}
});
// end-snippet-0
	}
}
