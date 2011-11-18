package com.socialize.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.view.BaseView;

public class AutoPostFacebookOption extends BaseView {
	
	private CheckBox checkBox;
	private TextView checkboxLabel;

	public AutoPostFacebookOption(Context context) {
		super(context);
	}
	
	public void init() {

		checkboxLabel = new TextView(getContext());
		checkboxLabel.setText("Let my facebook friends in on the action!");
		checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		checkboxLabel.setTextColor(Color.WHITE);
		checkboxLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		checkBox = new CheckBox(getContext());
		
		LayoutParams checkboxMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLabelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		
		checkboxLabel.setLayoutParams(checkboxLabelLayoutParams);
		checkBox.setLayoutParams(checkboxLayoutParams);
		setLayoutParams(checkboxMasterLayoutParams);
		
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		checkBox.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		checkboxLabel.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		checkboxMasterLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLabelLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		setOrientation(HORIZONTAL);
		
		addView(checkBox);
		addView(checkboxLabel);
		
		checkboxLabel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkBox.setChecked(!checkBox.isChecked());
			}
		});
	}
	
	public boolean isChecked() {
		return checkBox.isChecked();
	}
	
	public void setChecked(boolean checked) {
		checkBox.setChecked(checked);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		checkBox.setEnabled(enabled);
		checkboxLabel.setEnabled(enabled);
	}
}
