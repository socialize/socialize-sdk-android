package com.socialize.testapp;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean("exit")) {
            finish();
        }
    }
}