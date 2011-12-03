package com.socialize.ui.comment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;

public class CommentListItem extends LinearLayout {
	
	private TextView comment;
	private TextView time;
	private TextView author;
	private ImageView userIcon;
	private DeviceUtils deviceUtils;
	private Colors colors;

	public CommentListItem(Context context) {
		super(context);
	}

	public void init() {
		
		final int eight = deviceUtils.getDIP(8);
		final int four = deviceUtils.getDIP(4);
		final int imagePadding = deviceUtils.getDIP(4);
		final int textColor = colors.getColor(Colors.BODY);
		final int titleColor = colors.getColor(Colors.TITLE);
		final int iconSize = deviceUtils.getDIP(64);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.FILL_PARENT);
		setDrawingCacheEnabled(true);
		setBackgroundColor(colors.getColor(Colors.LIST_ITEM_BG));
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.TOP);
		setPadding(eight,eight,eight,eight);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setGravity(Gravity.LEFT);
		contentLayout.setPadding(0, 0, 0, 0);
		
		LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentLayoutParams.setMargins(four, 0, 0, 0);
		contentLayout.setLayoutParams(contentLayoutParams);
		
		LinearLayout contentHeaderLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams contentHeaderLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentHeaderLayout.setLayoutParams(contentHeaderLayoutParams);
		contentHeaderLayout.setGravity(Gravity.LEFT);
		contentHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
		contentHeaderLayout.setPadding(0, 0, 0, 0);
		
		LinearLayout.LayoutParams authorLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	
		author = new TextView(getContext());
		author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		author.setMaxLines(1);
		author.setTypeface(Typeface.DEFAULT_BOLD);
		author.setTextColor(titleColor);
		author.setLayoutParams(authorLayoutParams);
		author.setSingleLine();

		LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		timeLayoutParams.gravity = Gravity.RIGHT;
		
		time = new TextView(getContext());
		time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		time.setMaxLines(1);
		time.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		time.setTextColor(titleColor);
		time.setLayoutParams(timeLayoutParams);
		time.setSingleLine();
		time.setGravity(Gravity.RIGHT);
		
		LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		comment = new TextView(getContext());
		comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		comment.setTextColor(textColor);
		comment.setLayoutParams(commentLayoutParams);
		comment.setMaxLines(3);

		contentHeaderLayout.addView(author);
		contentHeaderLayout.addView(time);
		
		contentLayout.addView(contentHeaderLayout);
		contentLayout.addView(comment);
		
		LinearLayout iconLayout = new LinearLayout(getContext());
		
		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
		iconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		iconLayout.setLayoutParams(iconLayoutParams);
		iconLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		userIcon = new ImageView(getContext());
		userIcon.setLayoutParams(iconLayoutParams);
		userIcon.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(deviceUtils.getDIP(1), Color.BLACK);
		
		userIcon.setBackgroundDrawable(imageBG);
		
		userIcon.setScaleType(ScaleType.CENTER_CROP);
		
		iconLayout.addView(userIcon);
		
		addView(iconLayout);
		addView(contentLayout);
	}

	public TextView getComment() {
		return comment;
	}

	public TextView getTime() {
		return time;
	}

	public TextView getAuthor() {
		return author;
	}

	public ImageView getUserIcon() {
		return userIcon;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
