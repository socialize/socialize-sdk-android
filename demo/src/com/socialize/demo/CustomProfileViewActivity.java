package com.socialize.demo;

//begin-snippet-0

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserGetListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.ui.dialog.SafeProgressDialog;

/**
 * Example of a custom UI implementation for user profile.
 */
public class CustomProfileViewActivity extends Activity {

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Socialize.onCreate(this, savedInstanceState);

		setContentView(R.layout.custom_profile_view);

		// Locate the fields
		final EditText firstName = (EditText) findViewById(R.id.txtFirstName);
		final EditText lastName = (EditText) findViewById(R.id.txtLastName);
		final EditText meta = (EditText) findViewById(R.id.txtMeta);

		Button btnSave = (Button) findViewById(R.id.btnSave);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);

		// Load the profile
		// Use Socialize's SafeProgressDialog to avoid unexpected crashes when the user hits back.
		final SafeProgressDialog progress = SafeProgressDialog.show(this);

		// Get the user profile from Socialize
		UserUtils.getCurrentUserAsync(this, new UserGetListener() {
			@Override
			public void onGet(User user) {

				// Store a reference
				CustomProfileViewActivity.this.user = user;

				progress.dismiss();
				firstName.setText(user.getFirstName());
				lastName.setText(user.getLastName());
				meta.setText(user.getMetaData());
			}

			@Override
			public void onError(SocializeException error) {
				// Some sort of error.. handle accordingly
				progress.dismiss();
				Toast.makeText(CustomProfileViewActivity.this, "An error occurred retrieving the user", Toast.LENGTH_LONG).show();
				error.printStackTrace();
				finish();
			}
		});

		// Setup user actions
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

//begin-snippet-1
btnSave.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View view) {

		// Get the locally stored reference to the user
		User user = CustomProfileViewActivity.this.user;

		user.setFirstName(firstName.getText().toString());
		user.setLargeImageUri(lastName.getText().toString());
		user.setMetaData(meta.getText().toString());

		// Clear the reference
		CustomProfileViewActivity.this.user = null;

		final SafeProgressDialog progress = SafeProgressDialog.show(CustomProfileViewActivity.this);

		// Save the user
		UserUtils.saveUserAsync(CustomProfileViewActivity.this, user, new UserSaveListener() {
			@Override
			public void onUpdate(User result) {
				progress.dismiss();
				Toast.makeText(CustomProfileViewActivity.this, "User saved", Toast.LENGTH_SHORT).show();
				finish();
			}

			@Override
			public void onError(SocializeException error) {
				// Some sort of error.. handle accordingly
				progress.dismiss();
				Toast.makeText(CustomProfileViewActivity.this, "An error occurred saving the user", Toast.LENGTH_LONG).show();
				error.printStackTrace();
				finish();
			}
		});
	}
});
//end-snippet-1
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

//end-snippet-0