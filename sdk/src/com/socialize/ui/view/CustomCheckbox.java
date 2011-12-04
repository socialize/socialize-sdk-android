package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class CustomCheckbox extends BaseView {
	
	private ImageView checkBox;
	private TextView checkboxLabel;
	private boolean checked = false;
	private boolean enabled = true;
	private Drawables drawables;
	private DeviceUtils deviceUtils;
	
	private String imageOn;
	private String imageOff;
	
	private String textOn;
	private String textOff;

	public CustomCheckbox(Context context) {
		super(context);
	}
	
	public void init() {
		
		int padding = deviceUtils.getDIP(6);
		
		checked = Socialize.getSocialize().getSession().getUser().isAutoPostToFacebook();

		checkboxLabel = new TextView(getContext());
		checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		checkboxLabel.setTextColor(Color.WHITE);
		checkboxLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		checkBox = new ImageView(getContext());
		
		LayoutParams checkboxMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLabelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		checkboxLabel.setLayoutParams(checkboxLabelLayoutParams);
		checkBox.setLayoutParams(checkboxLayoutParams);
		
		checkBox.setPadding(padding, padding, 0, padding);
		checkboxLabel.setPadding(padding, padding, padding, padding);
		
		setLayoutParams(checkboxMasterLayoutParams);
		
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		setDisplay();
		
		checkboxLabel.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		checkboxMasterLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLabelLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		setOrientation(HORIZONTAL);
		
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(enabled) {
					checked = !checked;
					setDisplay();
				}
			}
		};
		
		addView(checkBox);
		checkBox.setOnClickListener(onClickListener);
		
		addView(checkboxLabel);
		checkboxLabel.setOnClickListener(onClickListener);
		
		if(StringUtils.isEmpty(textOn) && StringUtils.isEmpty(textOff)) {
			checkboxLabel.setVisibility(GONE);
		}
	}
	
	protected void setDisplay() {
		if(checked) {
			checkboxLabel.setText(textOn);
			checkBox.setImageDrawable(drawables.getDrawable(imageOn));
		}
		else {
			checkboxLabel.setText(textOff);
			checkBox.setImageDrawable(drawables.getDrawable(imageOff));
		}		
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
		setDisplay();
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
	
	public void setImageOn(String imageOn) {
		this.imageOn = imageOn;
	}

	public void setImageOff(String imageOff) {
		this.imageOff = imageOff;
	}

	public void setTextOn(String textOn) {
		this.textOn = textOn;
	}

	public void setTextOff(String textOff) {
		this.textOff = textOff;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		super.setEnabled(enabled);
		checkBox.setEnabled(enabled);
		checkboxLabel.setEnabled(enabled);
	}
}
