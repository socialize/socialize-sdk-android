package com.socialize.sample.mocks;

import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;

public abstract class MockSystem<T extends SocializeAction> {

	protected T action;
	protected ListResult<T> actionList;
	
	public MockSystem(T action) {
		super();
		this.action = action;
		initAction();
	}

	protected void initAction() {
		action.setId(0L);
		Entity entity = new Entity();
		entity.setKey("http://entity1.com");
		entity.setId(0L);
		action.setEntity(entity);
	}

	public void setAction(T action) {
		this.action = action;
	}

	public void setActionList(ListResult<T> actionList) {
		this.actionList = actionList;
	}
}
