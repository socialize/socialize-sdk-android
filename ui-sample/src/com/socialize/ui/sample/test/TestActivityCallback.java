package com.socialize.ui.sample.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public interface TestActivityCallback {
	public void onCreate(Bundle savedInstanceState);
	public void startActivity(Intent intent);
	public void setActivity(Activity activity);
}
