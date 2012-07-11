package com.socialize.test.mock;

import com.socialize.EntityUtils.SortOrder;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.entity.EntitySystem;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
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
	public void getEntity(SocializeSession session, long id, EntityListener listener) {
		if(listener != null) listener.onGet(entity);
	}
	
	@Override
	public Entity getEntitySynchronous(SocializeSession session, long id) throws SocializeException {
		return entity;
	}

	@Override
	public void getEntities(SocializeSession session, int start, int end, SortOrder sortOrder, EntityListener listener) {
		if(listener != null) listener.onList(entityList);
	}
	
	@Override
	public void getEntities(SocializeSession session, SortOrder sortOrder, EntityListener listener, String... entityKeys) {
		if(listener != null) listener.onList(entityList);
	}

	@Override
	public void addEntity(SocializeSession session, Entity entity, EntityListener listener) {
		if(listener != null) listener.onCreate(entity);
	}
	
	@Override
	public void getEntity(SocializeSession session, String entityKey, EntityListener listener) {
		if(listener != null) listener.onGet(entity);
	}

	public void setEntity(Entity view) {
		this.entity = view;
	}

	public void setEntityList(ListResult<Entity> viewList) {
		this.entityList = viewList;
	}

}
