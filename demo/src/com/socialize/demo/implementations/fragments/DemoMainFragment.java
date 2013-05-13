package com.socialize.demo.implementations.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.socialize.ActionBarUtils;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.config.SocializeConfig;
import com.socialize.demo.R;
import com.socialize.entity.Entity;

public class DemoMainFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.actionbar, container, false);

		SocializeConfig config = ConfigUtils.getConfig(this.getActivity());
		String entityKey = config.getProperty("entity.key");
		String entityName = config.getProperty("entity.name");
		Entity entity = Entity.newInstance(entityKey, entityName);

		return ActionBarUtils.showActionBar(getActivity(), view, entity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Socialize.onCreate(this.getActivity(), savedInstanceState);
	}

	@Override
	public void onDestroy() {
		Socialize.onDestroy(this.getActivity());
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		Socialize.onPause(this.getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		Socialize.onResume(this.getActivity());
	}
}
