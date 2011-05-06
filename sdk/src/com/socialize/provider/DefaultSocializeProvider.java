package com.socialize.provider;

import java.util.List;

import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.FactoryService;

public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	protected FactoryService factoryService;
	
	public DefaultSocializeProvider(FactoryService factoryService) {
		super();
		this.factoryService = factoryService;
	}

	@Override
	public SocializeSession authenticate(String key, String secret, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(String endpoint, int[] ids) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<T> list(String endpoint, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> put(String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> post(String endpoint, T object) {
		// TODO Auto-generated method stub
		return null;
	}

}
