package com.socialize.api;

import com.socialize.entity.Entity;

public interface ShareMessageBuilder {

	public String buildShareLink(Entity entity);

	public String buildShareSubject(Entity entity);

	public String getEntityLink(Entity entity, boolean html);

	public String buildShareMessage(Entity entity, String comment, boolean html, boolean includeSocialize);

}