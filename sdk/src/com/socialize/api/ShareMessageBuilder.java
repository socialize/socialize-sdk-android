package com.socialize.api;

import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;

public interface ShareMessageBuilder {

	public String buildShareSubject(Entity entity);
	
	public String getEntityLink(Entity entity, PropagationInfo urlSet, boolean html);

	public String buildShareMessage(Entity entity, PropagationInfo urlSet, String comment, boolean html, boolean includeAppLink);
}