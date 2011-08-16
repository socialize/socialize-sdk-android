package com.socialize.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.util.DeviceUtils;

public class CommentListItem extends LinearLayout {
	
	private TextView comment;
	private TextView time;
	private TextView author;
	private ImageView userIcon;
	private DeviceUtils deviceUtils;

	public CommentListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommentListItem(Context context) {
		super(context);
	}

	public void init() {
		
		final int eight = deviceUtils.getDIP(8);
		final int four = deviceUtils.getDIP(4);
		final int textColor = Color.parseColor("#222222");
		
//		GradientDrawable background = new GradientDrawable(
//				GradientDrawable.Orientation.BOTTOM_TOP,
//                new int[] {  Color.parseColor("#2f383f"),Color.parseColor("#3b464f") });
//		setBackgroundDrawable(background);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
		
		setBackgroundColor(Color.WHITE);
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.TOP);
		setPadding(eight,eight,eight,eight);
		
		LinearLayout iconLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		iconLayout.setLayoutParams(iconLayoutParams);
		iconLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		iconLayout.setPadding(four,four,four,four);

		
		userIcon = new ImageView(getContext());
		GradientDrawable iconBorder = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
                new int[] { Color.DKGRAY, Color.DKGRAY });
		
		iconBorder.setStroke(1, Color.DKGRAY);
		
		userIcon.setBackgroundDrawable(iconBorder);
		userIcon.setLayoutParams(iconLayoutParams);
		
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
		author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		author.setMaxLines(1);
		author.setTypeface(Typeface.DEFAULT_BOLD);
		author.setTextColor(textColor);
//		author.setShadowLayer(1, 1, 1, Color.BLACK);
		author.setLayoutParams(authorLayoutParams);
		author.setSingleLine();

		LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		timeLayoutParams.gravity = Gravity.RIGHT;
		
		time = new TextView(getContext());
		time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		time.setMaxLines(1);
		time.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		time.setTextColor(textColor);
//		time.setShadowLayer(1, 1, 1, Color.BLACK);
		time.setLayoutParams(timeLayoutParams);
		time.setSingleLine();
		time.setGravity(Gravity.RIGHT);
		
		LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		comment = new TextView(getContext());
		comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		comment.setTextColor(textColor);
		comment.setLayoutParams(commentLayoutParams);
		
		iconLayout.addView(userIcon);

		contentHeaderLayout.addView(author);
		contentHeaderLayout.addView(time);
		
		contentLayout.addView(contentHeaderLayout);
		contentLayout.addView(comment);
		
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

	public DeviceUtils getDeviceUtils() {
		return deviceUtils;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
}
