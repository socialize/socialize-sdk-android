package com.socialize.provider;

import java.util.List;

import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;

public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	protected SocializeObjectFactory<T> factory;
	
	public DefaultSocializeProvider(SocializeObjectFactory<T> factory) {
		super();
		this.factory = factory;
	}

	@Override
	public SocializeSession authenticate(String key, String secret, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(SocializeSession session, String endpoint, int[] ids) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<T> list(SocializeSession session, String endpoint, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> put(SocializeSession session, String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> post(SocializeSession session, String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}
}
