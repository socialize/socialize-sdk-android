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
package com.socialize.demo.snippets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;


/**
 * @author Jason Polites
 *
 */
public class EntityLoaderSnippets {

//begin-snippet-0
// This is a class you need to create in your app
public class MyEntityLoader implements SocializeEntityLoader {

	// MUST define a parameterless constructor
	public MyEntityLoader() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.SocializeEntityLoader#loadEntity(android.app.Activity, com.socialize.entity.Entity)
	 */
	@Override
	public void loadEntity(Activity activity, Entity entity) {
		// This is where you would load an Activity within your app to render the entity
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.SocializeEntityLoader#canLoad(android.content.Context, com.socialize.entity.Entity)
	 */
	@Override
	public boolean canLoad(Context context, Entity entity) {
		// Return true if this entity can be loaded
		return true;
	}

}
//end-snippet-0


public class MyContentActivity extends Activity {

//begin-snippet-1
// This is a class you need to create in your app
public class MyEntityLoader implements SocializeEntityLoader {

	// MUST define a parameterless constructor
	public MyEntityLoader() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.SocializeEntityLoader#loadEntity(android.app.Activity, com.socialize.entity.Entity)
	 */
	@Override
	public void loadEntity(Activity activity, Entity entity) {
		// Launch an activity from here...
		Intent intent = new Intent(activity, MyContentActivity.class);
		
		// Add the key from the entity
		intent.putExtra("some_key", entity.getKey());
		activity.startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.SocializeEntityLoader#canLoad(android.content.Context, com.socialize.entity.Entity)
	 */
	@Override
	public boolean canLoad(Context context, Entity entity) {
		// Return true if this entity can be loaded
		return true;
	}

}
//end-snippet-1
}
}


