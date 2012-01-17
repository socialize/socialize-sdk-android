package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.socialize.android.ioc.IBeanFactory;
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
	
	private boolean borderOn = true;
	private boolean forceDefaultDensity = false;
	
	private int padding = 8;
	
	private int textSize = 12;
	
	private OnClickListener customClickListener;
	private OnClickListener defaultClickListener;
	private IBeanFactory<BasicLoadingView> loadingViewFactory;
	
	private ViewFlipper iconFlipper;

	public CustomCheckbox(Context context) {
		super(context);
	}
	
	public void init() {
		
		int dipPadding = deviceUtils.getDIP(padding);
		
		checkboxLabel = new TextView(getContext());
		checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		checkboxLabel.setTextColor(Color.WHITE);
		checkboxLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		checkBox = new ImageView(getContext());
		
		LayoutParams checkboxMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLabelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		checkboxLabel.setLayoutParams(checkboxLabelLayoutParams);
		checkBox.setLayoutParams(checkboxLayoutParams);
		checkBox.setPadding(dipPadding, dipPadding, dipPadding, dipPadding);
		checkboxLabel.setPadding(0, dipPadding, dipPadding, dipPadding);
		
		setLayoutParams(checkboxMasterLayoutParams);
		
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		setDisplay();
		
		checkboxLabel.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		checkboxMasterLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLabelLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		checkboxLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		setOrientation(HORIZONTAL);
		
		defaultClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(enabled) {
					checked = !checked;
					setDisplay();
					
					if(customClickListener != null) {
						customClickListener.onClick(v);
					}
				}
			}
		};
		
		BasicLoadingView loadingScreen = loadingViewFactory.getBean();
		
		LayoutParams iconFlipperParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconFlipperParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		iconFlipper = new SafeViewFlipper(getContext());
		iconFlipper.setLayoutParams(iconFlipperParams);
		iconFlipper.addView(checkBox, 0);
		iconFlipper.addView(loadingScreen, 1);
		iconFlipper.setDisplayedChild(0);		
		
		addView(iconFlipper);
		addView(checkboxLabel);
		
		// Must be super.
		super.setOnClickListener(defaultClickListener);
		
		if(StringUtils.isEmpty(textOn) && StringUtils.isEmpty(textOff)) {
			checkboxLabel.setVisibility(GONE);
		}
		
		checkboxLabel.setTextColor(checkboxLabel.getTextColors().withAlpha(255));
		
		if(borderOn) {
			GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.parseColor("#3f3f3f"), Color.parseColor("#5c5c5c") });
			GradientDrawable topRight = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.BLACK });
			GradientDrawable bottomLeft = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.GRAY, Color.GRAY });
			LayerDrawable bg = new LayerDrawable(new Drawable[] { bottomLeft, topRight, background });
			
			bg.setLayerInset(0, 1, 0, 0, 1);
			bg.setLayerInset(1, 0, 1, 1, 0);
			bg.setLayerInset(2, 1, 1, 1, 1);
			
			bg.setAlpha(96);
			
			setBackgroundDrawable(bg);
		}
	}
	
	protected void setDisplay() {
		if(checked) {
			checkboxLabel.setText(textOn);
			if(forceDefaultDensity) {
				checkBox.setImageDrawable(drawables.getDrawable(imageOn, DisplayMetrics.DENSITY_DEFAULT));
			}
			else {
				checkBox.setImageDrawable(drawables.getDrawable(imageOn));
			}
			
		}
		else {
			checkboxLabel.setText(textOff);
			if(forceDefaultDensity) {
				checkBox.setImageDrawable(drawables.getDrawable(imageOff, DisplayMetrics.DENSITY_DEFAULT));
			}
			else {
				checkBox.setImageDrawable(drawables.getDrawable(imageOff));
			}
			
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
	
	public void setTextSize(int unit, float size) {
		if(checkboxLabel != null) {
			checkboxLabel.setTextSize(unit, size);
		}
	}

	public void setTextOn(String textOn) {
		this.textOn = textOn;
		
		if(checkboxLabel != null) {
			if(checked) {
				checkboxLabel.setText(textOn);
			}
			
			checkboxLabel.setVisibility(VISIBLE);
		}
	}

	public void setTextOff(String textOff) {
		this.textOff = textOff;
		
		if(checkboxLabel != null) {
			if(checked) {
				checkboxLabel.setText(textOff);
			}
			
			checkboxLabel.setVisibility(VISIBLE);
		}
	}

	public void setBorderOn(boolean borderOn) {
		this.borderOn = borderOn;
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		this.customClickListener = l;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	public void setForceDefaultDensity(boolean forceDefaultDensity) {
		this.forceDefaultDensity = forceDefaultDensity;
	}
	
	public void showLoading() {
		if(iconFlipper != null) {
			setEnabled(false);
			iconFlipper.setDisplayedChild(1);
		}
	}
	
	public void hideLoading() {
		if(iconFlipper != null) {
			setEnabled(true);
			iconFlipper.setDisplayedChild(0);
		}
	}

	public void setLoadingViewFactory(IBeanFactory<BasicLoadingView> loadingViewFactory) {
		this.loadingViewFactory = loadingViewFactory;
	}
	
	public String getImageOn() {
		return imageOn;
	}

	public String getImageOff() {
		return imageOff;
	}
	
	public void setTextSize(int textSize) {
		this.textSize = textSize;
		
		if(checkboxLabel != null) {
			checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		super.setEnabled(enabled);
		
		checkBox.setEnabled(enabled);
		checkboxLabel.setEnabled(enabled);
		
		if(enabled) {
			checkboxLabel.setTextColor(checkboxLabel.getTextColors().withAlpha(255));
		}
		else {
			checkboxLabel.setTextColor(checkboxLabel.getTextColors().withAlpha(128));
		}
	}
}
