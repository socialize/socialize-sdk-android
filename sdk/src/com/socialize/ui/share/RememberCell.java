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
	
	private String text;

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
		
		title.setText(text);
		
		title.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
		
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
	
		layout.setLayoutParams(params);
		
		layout.addView(title);
		
		return layout;
	}

	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
}
