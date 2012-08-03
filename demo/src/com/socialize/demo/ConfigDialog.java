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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;


/**
 * @author Jason Polites
 *
 */
public class ConfigDialog {

	public static String fbAppId;
	public static String twKey;
	public static String twSecret;

	public static void showConfigDialog(final Activity mContext) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.config, (ViewGroup) mContext.findViewById(R.id.config_layout));	
		final CheckBox chkRequireAuth = (CheckBox) layout.findViewById(R.id.chkRequireAuth);
		final CheckBox chkAllowAnon = (CheckBox) layout.findViewById(R.id.chkAllowAnon);
		final CheckBox chkFBSSO = (CheckBox) layout.findViewById(R.id.chkFBSSO);
		final CheckBox chkFB = (CheckBox) layout.findViewById(R.id.chkFB);
		final CheckBox chkTW = (CheckBox) layout.findViewById(R.id.chkTW);
		
		chkRequireAuth.setChecked(ConfigUtils.getConfig(mContext).isAuthRequired());
		chkAllowAnon.setChecked(ConfigUtils.getConfig(mContext).isShareRequired());
		chkFBSSO.setChecked(ConfigUtils.getConfig(mContext).getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true));
		
		chkFB.setChecked(Socialize.getSocialize().isSupported(AuthProviderType.FACEBOOK));
		chkTW.setChecked(Socialize.getSocialize().isSupported(AuthProviderType.TWITTER));
		
		chkFB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.FACEBOOK_APP_ID, fbAppId);
				}
				else {
					fbAppId = ConfigUtils.getConfig(mContext).getProperty(SocializeConfig.FACEBOOK_APP_ID);
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.FACEBOOK_APP_ID, null);
				}
			}
		});
		
		chkTW.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, twKey);
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, twSecret);
				}
				else {
					twKey = ConfigUtils.getConfig(mContext).getProperty(SocializeConfig.TWITTER_CONSUMER_KEY);
					twSecret = ConfigUtils.getConfig(mContext).getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET);
					
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, null);
					ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, null);
				}
			}
		});		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Socialize Config");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_SHARE, String.valueOf(chkAllowAnon.isChecked()));
				ConfigUtils.getConfig(mContext).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, String.valueOf(chkRequireAuth.isChecked()));
				ConfigUtils.getConfig(mContext).setFacebookSingleSignOnEnabled(chkFBSSO.isChecked());
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
