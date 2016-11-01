package com.yilegame.yile.wight;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yilegame.http.uti.EditTextShake;
import com.yilegame.yile.ui.Utils;

public class AccountEditText extends LinearLayout{
	private TypedArray typedarray;
	private EditText et;
	private ImageView arrow;
	public AccountEditText(Context context) {
		this(context, null);
	}
	public AccountEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		 int sysopti_pref[];

		
			//login
			sysopti_pref = new int[6];
	        sysopti_pref[0] = Utils.getAttrId(context, "summary");
	        sysopti_pref[1] = Utils.getAttrId(context, "backg"); 
	        sysopti_pref[2] = Utils.getAttrId(context, "ispwd");
	        sysopti_pref[3] = Utils.getAttrId(context, "hasarrow");
	        sysopti_pref[4] =Utils.getAttrId(context, "isnum");
	        sysopti_pref[5] =Utils.getAttrId(context, "isEdite");
		
		typedarray = context.obtainStyledAttributes(attrs, sysopti_pref, 0, 0);
		
		LayoutInflater.from(context).inflate(Utils.getLayoutId(context, "account_edittext_item"), this,
				true);
		et = (EditText) findViewById(Utils.getId(context, "et"));
		arrow = (ImageView) findViewById(Utils.getId(context, "arrow"));
		et.clearFocus();
		TextView tv_name = (TextView)findViewById(Utils.getId(context, "tv_name"));
		tv_name.setText(typedarray.getString(0));
		if (typedarray.getBoolean(2, false)) {
			EditTextShake.hidePwd(et);
		}else{
			EditTextShake.showPwd(et);
		}
		if(typedarray.getBoolean(4, false)){//是否位数字
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		
		if(typedarray.getBoolean(5, true)){//是否可编辑
			et.setEnabled(true);
		}else{
			et.setEnabled(false);
		}
		
		if (typedarray.getBoolean(3, false)) {
			arrow.setVisibility(View.VISIBLE);
		}else{
			arrow.setVisibility(View.INVISIBLE);
		}
		typedarray.recycle();
	}
	public String getText(){
		return et.getText().toString().trim();
	}
	public void setOnArrowDown(OnClickListener listener){
		arrow.setOnClickListener(listener);
	}
	public void setText(String s){
		//		et.setTextSize(14);
		//		et.setEnabled(false);
		//		et.setText(s);
		et.setText(s);
	}
	public void setTextCantChange(String s){
		//		et.setTextSize(14);
		et.setEnabled(false);
		//		et.setText(s);
		et.setText(s);
	}
	//	public void setUser(String s){
	//		
	//	}
	public void setShowText(String s){
		//		et.setTextSize(14);
		et.setEnabled(false);
		//		et.setText(s);
		et.setText(s);
	}
	public  void showPwd() {
		et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
				| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	}

	public  void hidePwd() {
		et.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}
}
