package com.example.vanilla;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;

public class SocializeActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socialize);
		Socialize.onCreate(this, savedInstanceState);
		Socialize.initAsync(this, new SocializeInitListener() {
			@Override
			public void onInit(Context context, IOCContainer container) {
				Toast.makeText(SocializeActivity.this, "Socialize Initialized", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(SocializeException error) {
				Toast.makeText(SocializeActivity.this, "Oops, there was an error!", Toast.LENGTH_SHORT).show();
				Log.e("Socialize", "Error", error);
			}
		});
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