/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.demo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.demo.implementations.action.ActionActivity;
import com.socialize.demo.implementations.actionbar.ActionBarActivity;
import com.socialize.demo.implementations.auth.AuthButtonsActivity;
import com.socialize.demo.implementations.comment.CommentActivity;
import com.socialize.demo.implementations.entity.EntityActivity;
import com.socialize.demo.implementations.facebook.FacebookActivity;
import com.socialize.demo.implementations.like.LikeActivity;
import com.socialize.demo.implementations.location.LocationActivity;
import com.socialize.demo.implementations.share.ShareActivity;
import com.socialize.demo.implementations.subscribe.SubscriptionActivity;
import com.socialize.demo.implementations.user.UserActivity;
import com.socialize.demo.implementations.view.ViewActivity;

/**
 * @author Jason Polites
 */
public class DemoList extends ListActivity {
	
	
	static String fbAppId;
	static String twKey;
	static String twSecret;

	final String[] values = new String[] { "Config", "Linking Twitter & Facebook", "Facebook Direct",  "Action Bar", "Sharing", "Comments", "Likes", "Views", "Entities", "User Profile", "Actions (User Activity)", "Subscriptions", "Location"};
	final Class<?>[] activities = new Class<?>[] { 
			AuthButtonsActivity.class, 
			FacebookActivity.class,
			ActionBarActivity.class, 
			ShareActivity.class, 
			CommentActivity.class, 
			LikeActivity.class, 
			ViewActivity.class, 
			EntityActivity.class,
			UserActivity.class,
			ActionActivity.class, 
			SubscriptionActivity.class, 
			LocationActivity.class};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(position > 0) {
			Class<?> activityClass = activities[position-1];
			if(activityClass != null) {
				Intent intent = new Intent(this, activityClass);
				startActivity(intent);
			}			
		}
		else {
			showConfigDialog();
		}
	}
	
	protected void showConfigDialog() {
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		final View layout = inflater.inflate(R.layout.config, (ViewGroup) findViewById(R.id.config_layout));	
		final CheckBox chkRequireAuth = (CheckBox) layout.findViewById(R.id.chkRequireAuth);
		final CheckBox chkAllowAnon = (CheckBox) layout.findViewById(R.id.chkAllowAnon);
		final CheckBox chkFBSSO = (CheckBox) layout.findViewById(R.id.chkFBSSO);
		final CheckBox chkFB = (CheckBox) layout.findViewById(R.id.chkFB);
		final CheckBox chkTW = (CheckBox) layout.findViewById(R.id.chkTW);
		
		chkRequireAuth.setChecked(ConfigUtils.getConfig(DemoList.this).isAuthRequired());
		chkAllowAnon.setChecked(ConfigUtils.getConfig(DemoList.this).isAllowAnonymousUser());
		chkFBSSO.setChecked(ConfigUtils.getConfig(DemoList.this).getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true));
		
		chkFB.setChecked(Socialize.getSocialize().isSupported(AuthProviderType.FACEBOOK));
		chkTW.setChecked(Socialize.getSocialize().isSupported(AuthProviderType.TWITTER));
		
		chkFB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.FACEBOOK_APP_ID, fbAppId);
				}
				else {
					fbAppId = ConfigUtils.getConfig(DemoList.this).getProperty(SocializeConfig.FACEBOOK_APP_ID);
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.FACEBOOK_APP_ID, null);
				}
			}
		});
		
		chkTW.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, twKey);
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, twSecret);
				}
				else {
					twKey = ConfigUtils.getConfig(DemoList.this).getProperty(SocializeConfig.TWITTER_CONSUMER_KEY);
					twSecret = ConfigUtils.getConfig(DemoList.this).getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET);
					
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, null);
					ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, null);
				}
			}
		});		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Socialize Config");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.SOCIALIZE_ALLOW_ANON, String.valueOf(chkAllowAnon.isChecked()));
				ConfigUtils.getConfig(DemoList.this).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, String.valueOf(chkRequireAuth.isChecked()));
				ConfigUtils.getConfig(DemoList.this).setFacebookSingleSignOnEnabled(chkFBSSO.isChecked());
			}
		});
		builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.setView(layout);
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
}
