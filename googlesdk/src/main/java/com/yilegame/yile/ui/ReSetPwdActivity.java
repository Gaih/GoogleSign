package com.yilegame.yile.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yilegame.http.Impl.HttpCallBack;
import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.MD5;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.yile.wight.AccountEditText;

public class ReSetPwdActivity extends BaseActivity {
		private TextView qy_cancle;
		private TextView qy_reset_ok;
		private String firstPwd;
		private String secondPwd;
		private String findPassForPhoneToken=null;
		private boolean reSetPwd;
		private static final int RESET_PWD_SUCCESS=1;
		private static final int RESET_PWD_FAIL=-1;
		private Context mcon=ReSetPwdActivity.this;
		private Handler mhandler=new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RESET_PWD_SUCCESS:
					showErrorView("修改成功");
					finish();
					returnLoginUI("resetok");
					break;
				case RESET_PWD_FAIL:
					showErrorView("修改失败,请重试");
					break;
				default:
					break;
				}
			};
		};
		private String account;
		private AccountEditText qy_et_pwd;
		private AccountEditText qy_et_pwd_again;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(Utils.getLayoutId(getApplicationContext(), "activity_resetpwd"));
			Intent i = getIntent();
			account = i.getStringExtra("account");
			if(i.getStringExtra("token")!=null)
			{
				findPassForPhoneToken=i.getStringExtra("token");

			}
			qy_et_pwd = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "att_pwd"));
			qy_et_pwd_again = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "att_pwd_again"));
			TextView close = (TextView) findViewById(Utils.getId(getApplicationContext(), "close"));
			close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					returnLoginUI();
				}
			});
			TextView title = (TextView) findViewById(Utils.getId(getApplicationContext(), "title"));
			title.setText("重置密码");
			LinearLayout ll_login = (LinearLayout) findViewById(Utils.getId(getApplicationContext(), "ll_login"));
			ll_login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					returnLoginUI();
				}
			});
			qy_reset_ok = (TextView) findViewById(Utils.getId(getApplicationContext(), "find_pwd"));
			qy_reset_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ReSetPwd();
				}
			});
			qy_cancle = (TextView) findViewById(Utils.getId(getApplicationContext(), "cancle"));
			qy_cancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					returnLoginUI();
				}
			});
		}
		private void ReSetPwd() {
			firstPwd = qy_et_pwd.getText();
			secondPwd = qy_et_pwd_again.getText();
			if (TextUtils.isEmpty(firstPwd)||TextUtils.isEmpty(secondPwd)) {
				return;
			}
			if(firstPwd.length()<6||secondPwd.length()<6){
				showErrorView("密码长度不能少于6位");
				return;
			}
			if (firstPwd.equals(secondPwd)) {
				showLoading("");
				if(findPassForPhoneToken!=null)
				{
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String pass=(MD5.md5Encoder(firstPwd)).toUpperCase();
							String sign =MD5.md5Encoder(account+pass+findPassForPhoneToken+UserInfos.key);
							YiLeHttpUtils.resetpasswordForPhone(ReSetPwdActivity.this,account,findPassForPhoneToken,pass,sign, QYConstant.getInstance().getIp(ReSetPwdActivity.this, "resetpassphone"), new HttpCallBack() {
								
								@Override
								public void getData(Object o) {
									// TODO Auto-generated method stub
									boolean b=(Boolean) o;
									if (b) {
										mhandler.sendEmptyMessage(RESET_PWD_SUCCESS);
									}else{
										mhandler.sendEmptyMessage(RESET_PWD_FAIL);
									}
									hideLoading();
								}
							});
						}
					}.start();
				}else{
					new Thread(){
						@Override
						public void run() {
							YiLeHttpUtils.resetpassword(ReSetPwdActivity.this, UserInfos.userName, firstPwd, new HttpCallBack() {
								
								@Override
								public void getData(Object o) {
									boolean b=(Boolean) o;
									if (b) {
										mhandler.sendEmptyMessage(RESET_PWD_SUCCESS);
									}else{
										mhandler.sendEmptyMessage(RESET_PWD_FAIL);
									}
									hideLoading();
									
								}
							});
						};
					}.start();
				}
			}else{
				showErrorView("两次密码必须相等");
			}
		}
}
