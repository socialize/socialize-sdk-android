package com.socialize.test.mock;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.ActivitySystem;
import com.socialize.entity.Comment;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.UserActivityListener;

public class MockActivitySystem extends MockSystem<SocializeAction> implements ActivitySystem {
	
	public MockActivitySystem() {
		super(new Comment()); // Just use a comment :/
	}

	@Override
	public SocializeAction getAction(SocializeSession session, long id, ActionType type) throws SocializeException {
		return action;
	}

	@Override
	public void getActivityByUser(SocializeSession session, long id, UserActivityListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener) {
		if(listener != null) listener.onList(actionList);
	}
}
