package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class CustomCheckbox extends BaseView {
	
	private LocalizationService localizationService;
	
	private ImageView checkBox;
	private TextView checkboxLabel;
	private boolean checked = false;
	private boolean enabled = true;
	private boolean changed = false;
	private Drawables drawables;
	private Colors colors;
	private DisplayUtils displayUtils;
	
	private String imageOn;
	private String imageOff;
	
	private String textOnKey;
	private String textOffKey;	
	
	private boolean borderOn = true;
	
	private int padding = 4;
	private int textPadding = 4;
	private int imageMargin = 4;
	
	private int textSize = 12;
	
	private OnClickListener customClickListener;
	private OnClickListener defaultClickListener;
	private IBeanFactory<BasicLoadingView> loadingViewFactory;
	
	private ViewFlipper iconFlipper;
	
	public CustomCheckbox(Context context) {
		super(context);
	}
	
	public void init() {
		
		int dipPadding = displayUtils.getDIP(padding);
		int leftPadding = displayUtils.getDIP(textPadding);
		int margin = displayUtils.getDIP(imageMargin);
		
		checkboxLabel = new TextView(getContext());
		checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		checkboxLabel.setTextColor(Color.WHITE);
		checkboxLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		checkBox = new ImageView(getContext());
		
		LayoutParams checkboxMasterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLabelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams checkboxLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		checkboxLayoutParams.setMargins(margin, margin, margin, margin);
		
		checkboxLabel.setLayoutParams(checkboxLabelLayoutParams);
		checkBox.setLayoutParams(checkboxLayoutParams);
		checkBox.setPadding(dipPadding, dipPadding, dipPadding, dipPadding);
		checkboxLabel.setPadding(leftPadding, dipPadding, dipPadding, dipPadding);
		
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
					changed = true;
					checked = !checked;
					
					if(customClickListener != null) {
						customClickListener.onClick(v);
					}
						
					setDisplay();
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
		
		if(StringUtils.isEmpty(textOnKey) && StringUtils.isEmpty(textOffKey)) {
			checkboxLabel.setVisibility(GONE);
		}
		
		checkboxLabel.setTextColor(checkboxLabel.getTextColors().withAlpha(255));
		
		if(borderOn) {
			
			GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { colors.getColor(Colors.CHECKBOX_BORDER_BOTTOM), colors.getColor(Colors.CHECKBOX_BORDER_TOP) });
			ColorDrawable topRight = new ColorDrawable(Color.BLACK);
			ColorDrawable bottomLeft = new ColorDrawable(Color.GRAY);
			LayerDrawable bg = new LayerDrawable(new Drawable[] { bottomLeft, topRight, background });
			
			bg.setLayerInset(0, 1, 0, 0, 1);
			bg.setLayerInset(1, 0, 1, 1, 0);
			bg.setLayerInset(2, 1, 1, 1, 1);
			
			bg.setAlpha(96);

			CompatUtils.setBackgroundDrawable(this, bg);
		}
	}
	
	protected void setDisplay() {
		if(checked) {
			setTextOnKey(textOnKey);
			checkBox.setImageDrawable(drawables.getDrawable(imageOn));
		}
		else {
			setTextOffKey(textOffKey);
			checkBox.setImageDrawable(drawables.getDrawable(imageOff));
		}		
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		if(checked != this.checked) {
			this.checked = checked;
			setDisplay();
		}
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
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

	@Deprecated
	public void setTextOn(String textOn) {
		setText(textOn);
	}

	@Deprecated
	public void setTextOff(String textOff) {
		setText(textOff);
	}
	
	public void setTextOnKey(String key) {
		this.textOnKey = key;
		setText(localizationService.getString(textOnKey));
	}

	public void setTextOffKey(String key) {
		this.textOffKey = key;
		setText(localizationService.getString(textOffKey));
	}

	protected void setText(String text) {
		if(checkboxLabel != null) {
			checkboxLabel.setText(text);
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
	
	/**
	 * Returns true if the user has clicked this control
	 * @return
	 */
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public void setTextPadding(int textPadding) {
		this.textPadding = textPadding;
	}

	public void setImageMargin(int imageMargin) {
		this.imageMargin = imageMargin;
	}
	
	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		
		if(checkboxLabel != null) {
			checkboxLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		}
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
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
