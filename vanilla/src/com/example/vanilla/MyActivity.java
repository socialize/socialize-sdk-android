package com.example.vanilla;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Button btn = (Button) findViewById(R.id.button);
	    btn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    startActivity(new Intent(MyActivity.this, SocializeActivity.class));
		    }
	    });
    }
}
