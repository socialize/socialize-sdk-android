package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Comment;

public class CommentFactory extends EntryFactory<Comment> {
	
	public CommentFactory() {
		super();
	}

	protected CommentFactory(FactoryService factoryService) {
		super(factoryService);
	}

	@Override
	protected void postFromJSON(JSONObject object, Comment comment) throws JSONException {
		comment.setComment(object.getString("comment"));
	}

	@Override
	protected void postToJSON(Comment comment, JSONObject object) throws JSONException {
		object.put("comment", comment.getComment());
	}

	@Override
	public Comment instantiateObject() {
		return new Comment();
	}
	
}
