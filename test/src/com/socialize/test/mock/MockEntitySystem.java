package com.socialize.test.mock;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.EntitySystem;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.listener.entity.EntityListener;

public class MockEntitySystem implements EntitySystem {

	private Entity entity;
	private ListResult<Entity> entityList;
	
	public MockEntitySystem() {
		super();
		entity = new Entity();
		entity.setKey("http://entity1.com");
		entity.setId(0L);
	}

	@Override
	public void addEntity(SocializeSession session, Entity entity, EntityListener listener) {
		if(listener != null) listener.onCreate(entity);
	}

	@Override
	public void addEntity(SocializeSession session, String entityKey, String name, EntityListener listener) {
		if(listener != null) listener.onCreate(Entity.newInstance(entityKey, name));
	}

	@Override
	public void getEntity(SocializeSession session, String entityKey, EntityListener listener) {
		if(listener != null) listener.onGet(entity);
	}

	@Override
	public void listEntities(SocializeSession session, EntityListener listener, String... entityKeys) {
		if(listener != null) listener.onList(entityList);
	}

	public void setEntity(Entity view) {
		this.entity = view;
	}

	public void setEntityList(ListResult<Entity> viewList) {
		this.entityList = viewList;
	}

}
