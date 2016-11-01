package com.yilegame.yile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yilegame.http.Mlog;
import com.yilegame.yile.engine.HttpsConductor;
import com.yilegame.yile.wight.AccountEditText;

import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {
	private CheckBox isshow_pwd;
	private AccountEditText att_user;
	private AccountEditText att_pwd;
	private TextView register;
	public static RegisterActivity instance;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {// 获取一键注册账号密码成功
				String strcode;
				if (HttpsConductor.registerMessage == null) {
					Mlog.i("fengge", "returnInfo == null");
					return;
				}
				strcode = HttpsConductor.registerMessage.get("code");
				if (HttpsConductor.registerMessage.size() < 4
						|| Integer.parseInt(strcode) != 1100) {
					return;
				}
				// att_user.setText(HttpsConductor.registerMessage.get("account"));
				att_pwd.setText("");
			}
			Mlog.e("feng", "hideLoading");
		};
	};
	private LinearLayout ll_login;
	private TextView cancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Utils.getLayoutId(getApplicationContext(),
				"activity_register"));
		mCon = this;
		instance = this;
		initData();
		initView();
		att_pwd.showPwd();
	}

	private void initData() {
		HttpsConductor.getInstance().fastRegister("", "", mCon, mHandler);

	}

	private void initView() {
		att_user = (AccountEditText) findViewById(Utils.getId(
				getApplicationContext(), "att_user"));
		att_pwd = (AccountEditText) findViewById(Utils.getId(
				getApplicationContext(), "att_pwd"));
		register = (TextView) findViewById(Utils.getId(getApplicationContext(),
				"register"));
		ll_login = (LinearLayout) findViewById(Utils.getId(
				getApplicationContext(), "ll_login"));
		close = (TextView) findViewById(Utils.getId(getApplicationContext(),
				"close"));
		cancle = (TextView) findViewById(Utils.getId(getApplicationContext(),
				"cancle"));
		isshow_pwd = (CheckBox) findViewById(Utils.getId(
				getApplicationContext(), "isshow_pwd"));
		register.setOnClickListener(this);
		// close.setOnClickListener(this);
		cancle.setOnClickListener(this);
		ll_login.setOnClickListener(this);
		isshow_pwd.setOnClickListener(this);
		isshow_pwd.setChecked(true);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == Utils.getId(getApplicationContext(), "register")) {
			String account = att_user.getText().toString().trim();
			String pwd = att_pwd.getText().toString().trim();
			Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{6,15}");
			if (pattern.matcher(account).matches()
					&& pattern.matcher(pwd).matches()) {
				if (checkEditText(att_user, att_pwd, null, 1)) {
					View saveRegist = view.getRootView();
					saveRegist.setDrawingCacheEnabled(true);
					Bitmap bitmap = saveRegist.getDrawingCache();
					HttpsConductor.getInstance().register(account, pwd, mCon,
							bitmap, account + pwd);
				}
			} else {
				Toast.makeText(RegisterActivity.this, "账号和密码只能是数字和字符,六位以上！",
						Toast.LENGTH_LONG).show();

			}

		}
		// else if(id==Utils.getId(getApplicationContext(), "close")){
		// finish();
		// }
		else if (id == Utils.getId(getApplicationContext(), "cancle")) {
			finish();
			startActivity(new Intent(mCon, LoginActivity.class));
		} else if (id == Utils.getId(getApplicationContext(), "ll_login")) {
			finish();
			startActivity(new Intent(mCon, LoginActivity.class));
		} else if (id == Utils.getId(getApplicationContext(), "isshow_pwd")) {
			if (isshow_pwd.isChecked()) {
				att_pwd.showPwd();
			} else {
				att_pwd.hidePwd();
			}
		}
	}

	// 随机获取图片编号
	private String getRandomCode() {
		String num = "";
		for (int i = 0; i < 10; i++) {
			int f = (int) (Math.random() * 10);
			num += f;
		}
		return num;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			returnLoginUI();
		}
		return super.onKeyDown(keyCode, event);
	}
}
