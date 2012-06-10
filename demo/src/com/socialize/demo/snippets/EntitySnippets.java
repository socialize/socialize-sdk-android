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

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import com.socialize.CommentUtils;
import com.socialize.EntityUtils;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;


/**
 * @author Jason Polites
 *
 */
public class EntitySnippets extends Activity {

public void createInline() {
// begin-snippet-0
Entity entity = Entity.newInstance("key", "name");

CommentUtils.addComment(this, entity, "The comment", new CommentAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Comment result) {
		// Handle success
	}
});
//end-snippet-0
}

public void createInline2() {
// begin-snippet-1
Entity entity = Entity.newInstance("key", null);

CommentUtils.addComment(this, entity, "The comment", new CommentAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Comment result) {
		// Handle success
	}
});
//end-snippet-1
}

public void createExplicit() {
// begin-snippet-2
Entity entity = Entity.newInstance("key", "name");

EntityUtils.saveEntity(this, entity, new EntityAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Entity result) {
		// Handle success
	}
});
//end-snippet-2
}

public void getEntity() {
// begin-snippet-3
EntityUtils.getEntity(this, "key", new EntityGetListener() {
	
	@Override
	public void onGet(Entity result) {
		// Handle success
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No entity found
		}
		else {
			// Handle error
		}
	}
});
//end-snippet-3
}

@SuppressWarnings("unused")
public void getEntities() {
// begin-snippet-4
EntityUtils.getEntities(this, new EntityListListener() {

	@Override
	public void onList(ListResult<Entity> result) {
		
		int count = result.getTotalCount();
		List<Entity> items = result.getItems();
		
		// Handle success
	}
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
}, "key0", "key1");
//end-snippet-4
}
public void getEntityStats() {
// begin-snippet-5
EntityUtils.getEntity(this, "key", new EntityGetListener() {
	
	@Override
	public void onGet(Entity result) {
		// Get the stats
		EntityStats entityStats = result.getEntityStats();
		
		entityStats.getLikes();
		entityStats.getComments();
		entityStats.getViews();
		entityStats.getShares();
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No entity found
		}
		else {
			// Handle error
		}
	}
});
//end-snippet-5
}
@SuppressWarnings("unused")
public void entityMetaDataWithJSON() throws JSONException {
// begin-snippet-7
	
Entity entity = Entity.newInstance("key", "name");

// Store a custom dictionary as a JSON object
JSONObject metaData = new JSONObject();
metaData.put("some_key", "some_value");

entity.setMetaData(metaData.toString());

EntityUtils.saveEntity(this, entity, new EntityAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Entity result) {
		// Entity was created
	}
});


// Later, when you retrieve the entity...
	
EntityUtils.getEntity(this, "key", new EntityGetListener() {
	
	@Override
	public void onGet(Entity result) {
		// Get the meta data
		String metaData = result.getMetaData();
		
		try {
			JSONObject json = new JSONObject(metaData);
			String myValue = json.getString("some_key");
		}
		catch (JSONException e) {
			// Handle error
		}
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No entity found
		}
		else {
			// Handle error
		}
	}
});
//end-snippet-7
}

@SuppressWarnings("unused")
public void entityMetaDataWith() throws JSONException {
// begin-snippet-6
	
Entity entity = Entity.newInstance("key", "name");
entity.setMetaData("meta data");

EntityUtils.saveEntity(this, entity, new EntityAddListener() {
	
	@Override
	public void onError(SocializeException error) {
		// Handle error
	}
	
	@Override
	public void onCreate(Entity result) {
		// Entity was created
	}
});

// Later, when you retrieve the entity...
	
EntityUtils.getEntity(this, "key", new EntityGetListener() {
	
	@Override
	public void onGet(Entity result) {
		// Get the meta data
		String metaData = result.getMetaData();
	}
	
	@Override
	public void onError(SocializeException error) {
		if(isNotFoundError(error)) {
			// No entity found
		}
		else {
			// Handle error
		}
	}
});
//end-snippet-6
}

}
