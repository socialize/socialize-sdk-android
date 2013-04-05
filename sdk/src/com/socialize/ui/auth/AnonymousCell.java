package com.socialize.ui.auth;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.i18n.I18NConstants;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ClickableSectionCell;

public class AnonymousCell extends ClickableSectionCell {
	
	public AnonymousCell(Context context) {
		super(context);
	}
	
	public void init() {
		setCanClick(false);
		setStrokeCornerOffset(1);
		super.init();
	}

	@Override
	protected ImageView makeImage() {
		setImageOn(drawables.getDrawable("user_icon.png"));
		return new ImageView(getContext());		
	}

	@Override
	protected View makeDisplayText() {
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(VERTICAL);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		TextView title = new TextView(getContext());
		TextView sub = new TextView(getContext());
		
		title.setText(localizationService.getString(I18NConstants.AUTH_ANONYMOUS));
		sub.setText(localizationService.getString(I18NConstants.AUTH_MESSAGE));

		if(colors != null) {
			title.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
			sub.setTextColor(colors.getColor(Colors.ANON_CELL_TEXT));
		}

		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		sub.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
	
		layout.setLayoutParams(params);
		
		layout.addView(title);
		layout.addView(sub);
		
		return layout;
	}
}
