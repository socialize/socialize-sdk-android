package com.socialize.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn = (Button) findViewById(R.id.btnSample);
        Button btnMock = (Button) findViewById(R.id.btnSampleWithMocks);
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Main.this, SampleActivity.class);
				startActivity(i);
			}
		});
        
        btnMock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Main.this, SampleActivity.class);
				i.putExtra("mock", true);
				startActivity(i);
			}
		});
    }
}