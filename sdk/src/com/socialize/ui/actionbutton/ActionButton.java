package com.socialize.ui.actionbutton;

import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;

@Deprecated
public interface ActionButton<A extends SocializeAction> {

	public Entity getEntity();

	public void setEntity(Entity entity);

	public void refresh();

	public ActionButtonState getState();

	public void setState(ActionButtonState state);

	public OnActionButtonEventListener<A> getOnActionButtonEventListener();

	public ActionButtonConfig getConfig();

}