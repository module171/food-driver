package com.FoodDriver.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class DynamicHeightImageView extends ImageView {

	private float whRatio = 0;

	public DynamicHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DynamicHeightImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (whRatio != 0) {
			int width = getMeasuredWidth();
			int height = (int)(whRatio * width);
			setMeasuredDimension(width, height);
		}
	}
}