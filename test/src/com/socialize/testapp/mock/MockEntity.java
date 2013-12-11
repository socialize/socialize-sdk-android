package com.socialize.testapp.mock;

import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
import com.socialize.entity.UserEntityStats;

public class MockEntity extends Entity {

	private static final long serialVersionUID = -2838591654752862523L;

	@Override
	public void setEntityStats(EntityStats stats) {
		super.setEntityStats(stats);
	}

	@Override
	public void setUserEntityStats(UserEntityStats userEntityStats) {
		super.setUserEntityStats(userEntityStats);
	}
}
