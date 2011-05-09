package com.socialize.provider;

import java.util.List;

import com.socialize.api.SocializeApiError;
import com.socialize.api.SocializeSession;

public interface SocializeProvider<T> {
	
	public SocializeSession authenticate(String key, String secret, String uuid) throws SocializeApiError;

	public List<T> list(SocializeSession session, String endpoint, String key) throws SocializeApiError;
	
	public T get(SocializeSession session, String endpoint, int[] ids) throws SocializeApiError;
	
	public List<T> put(SocializeSession session, String endpoint, T object) throws SocializeApiError;

	public List<T> post(SocializeSession session, String endpoint, T object) throws SocializeApiError;
	
}
