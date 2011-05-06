package com.socialize.provider;

import java.util.List;

import com.socialize.api.SocializeApiError;
import com.socialize.api.SocializeSession;

public interface SocializeProvider<T> {
	
	public SocializeSession authenticate(String key, String secret, String uuid) throws SocializeApiError;

	public List<T> list(String endpoint, String key) throws SocializeApiError;
	
	public T get(String endpoint, int[] ids) throws SocializeApiError;
	
	public List<T> put(String endpoint, T object) throws SocializeApiError;

	public List<T> post(String endpoint, T object) throws SocializeApiError;
	
}
