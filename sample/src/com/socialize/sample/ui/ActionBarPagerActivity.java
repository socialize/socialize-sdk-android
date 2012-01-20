package com.socialize.sample.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.sample.R;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarOptions;

@Deprecated
public class ActionBarPagerActivity extends FragmentActivity {
	
	static final int NUM_ITEMS = 10;

	MyAdapter mAdapter;

	ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String entityKey = null;
		String entityName = null;
		boolean isEntityKeyUrl = true;
		
		Bundle extras = this.getIntent().getExtras();
		
		if(extras != null) {
			entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			entityName = extras.getString(SocializeUI.ENTITY_NAME);
			isEntityKeyUrl = extras.getBoolean(SocializeUI.ENTITY_URL_AS_LINK);
		}
				
		
		ActionBarOptions options = new ActionBarOptions();
		options.setEntityName(entityName);
		options.setEntityKeyUrl(isEntityKeyUrl);
		options.setAddScrollView(false);
		View actionBarWrapped = SocializeUI.getInstance().showActionBar(this, R.layout.action_bar_view_pager, entityKey, options, null);
		setContentView(actionBarWrapped);		
		
		
//		setContentView(R.layout.action_bar_view_pager);

		mAdapter = new MyAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			return ArrayListFragment.newInstance(position);
		}
	}

	public static class ArrayListFragment extends ListFragment {
		int mNum;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static ArrayListFragment newInstance(int num) {
			ArrayListFragment f = new ArrayListFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		}

		/**
		 * The Fragment's UI is just a simple text view showing its instance
		 * number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
			View tv = v.findViewById(R.id.text);
			((TextView) tv).setText("Fragment #" + mNum);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);
		}
	}
}