package com.yilegame.yile.wight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollTextView extends TextView {

	public ScrollTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public ScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * 欺骗系统 说我已经获取到了焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
