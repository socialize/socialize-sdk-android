/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.demo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.socialize.entity.Entity;


/**
 * @author Jason Polites
 *
 */
public class EntityLoaderActivity extends DemoActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entity_loader_activity);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			Entity e = (Entity)extras.get("entity");
			
			if(e != null) {
				TextView text = (TextView) findViewById(R.id.entity);
				text.setText(e.getDisplayName());
			}
		}
		
		Button button = (Button) findViewById(R.id.button1);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
