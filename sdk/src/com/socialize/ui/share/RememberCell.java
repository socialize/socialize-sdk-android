package com.socialize.ui.share;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ClickableSectionCell;

public class RememberCell extends ClickableSectionCell {
	
	private String textKey;

	public RememberCell(Context context) {
		super(context);
	}
	
	public void init() {
		setStrokeCornerOffset(1);
		super.init();
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setToggled(!isToggled());
			}
		});
		setToggled(false);
	}

	@Override
	protected ImageView makeImage() {
		return new ImageView(getContext());		
	}

	@Override
	protected View makeDisplayText() {
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(VERTICAL);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		TextView title = new TextView(getContext());
		
		title.setText(localizationService.getString(textKey));
		
		title.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
		
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
	
		layout.setLayoutParams(params);
		
		layout.addView(title);
		
		return layout;
	}
	
	public void setTextKey(String textKey) {
		this.textKey = textKey;
	}
}
