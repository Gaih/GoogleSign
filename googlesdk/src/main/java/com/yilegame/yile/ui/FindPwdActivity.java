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
import android.widget.Toast;

import com.yilegame.http.Impl.HttpCallBack;
import com.yilegame.http.Mlog;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.yile.wight.AccountEditText;

import org.json.JSONObject;

public class FindPwdActivity extends BaseActivity {
		private TextView qy_find_pwd;
		private final static int FIND_PWD_OK=1;
		private final static int FIND_PWD_FAIL=-1;
		private final static int INTENET_ERROR=-2;
		private Context mcon=FindPwdActivity.this;
		private Handler mhandler=new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case FIND_PWD_OK:
					break;
				case FIND_PWD_FAIL:
					showErrorView("未设置密保问题或其他原因");
					break;
				case INTENET_ERROR:
					showErrorView("连接失败");
					break;

				default:
					break;
				}
			};
		};
		private TextView qy_cancle;
		private String account;
		private String tempAccount;
		private AccountEditText att_user;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(Utils.getLayoutId(getApplicationContext(), "activity_find_pwd"));
			Intent i = getIntent();
			tempAccount = i.getStringExtra("account");
			qy_find_pwd = (TextView) findViewById(Utils.getId(getApplicationContext(), "qy_find_pwd"));
			att_user = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "att_user"));
			att_user.setText(tempAccount);
			LinearLayout ll_login = (LinearLayout) findViewById(Utils.getId(getApplicationContext(), "ll_login"));
			ll_login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					returnLoginUI();
				}
			});
			close = (TextView) findViewById(Utils.getId(getApplicationContext(), "close"));
			close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					returnLoginUI();
				}
			});	
			qy_find_pwd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					account = att_user.getText();
					Mlog.i("feng", "account:"+account);
					if (TextUtils.isEmpty(account)) {
						showErrorView("请输入账号");
						return;
					}
					showLoading("");
					new Thread(){
					@Override
					public void run() {
						//先通过验证是否绑定手机
						YiLeHttpUtils.getsaferequest(FindPwdActivity.this, account,true,new HttpCallBack(){


							private String myjsonStr;
                            private int isbandingPhone;
                            private int ishavesafeRequest;
							@Override
							public void getData(Object o) {
								String request=(String) o;
								String code = null;
								if (request.contains(" ")) {
									try {
										code=request.split(" ")[0];
										myjsonStr= request.split(" ")[1];
									} catch (Exception e) {
									}
								}else{
									code=request;
								}
								if ("-2".equals(code)) {
									showErrorView("验证失败");
								}else if("-12".equals(code)){
									showErrorView("用户名不存在");
								}else if("-1".equals(code)){
									showErrorView("异常");
								}
								else if("0".equals(code)){
									try {
										JSONObject myjson=new JSONObject(myjsonStr);
										isbandingPhone=myjson.getInt("bindPhone");
										ishavesafeRequest=myjson.getInt("safeRequest");
									} catch (Exception e) {
										e.printStackTrace();
									}
									if(isbandingPhone!=0){
										Intent intent = new Intent(FindPwdActivity.this,FindPassForPhoneActivity.class);
									   intent.putExtra("account",""+account);
										startActivity(intent);
									    finish();
									}else if(ishavesafeRequest!=0){//密保问题找回密码，再次请求网络
										
										new Thread(){
											@Override
											public void run() {
												//先通过
												YiLeHttpUtils.getsaferequest(FindPwdActivity.this, account,false,new HttpCallBack(){


						                            private String request;
													@Override
													public void getData(Object o) {
														String request=(String) o;
														String code = null;
														if (request.contains(" ")) {
															try {
																code=request.split(" ")[0];
																myjsonStr= request.split(" ")[1];
															} catch (Exception e) {
															}
														}else{
															code=request;
														}
														if ("-2".equals(code)) {
															showErrorView("验证失败");
														}else if("-4".equals(code)){
															showErrorView("未设置密保问题或其他原因");
														}else if("-1".equals(code)){
															showErrorView("异常");
														}
														else if("0".equals(code)){
															try {
																JSONObject myjson=new JSONObject(myjsonStr);
																request=myjson.getString("request");
																Intent intent = new Intent(FindPwdActivity.this, ForgetPwdActivity.class);
															    intent.putExtra("checkQuestion",""+request);
															    intent.putExtra("account",""+account);
																startActivity(intent);
															    finish();
															} catch (Exception e) {
																e.printStackTrace();
															}
															
														}
													}
													
												});
											
												hideLoading();
											};
										}.start();
										
									}else{
										Toast.makeText(FindPwdActivity.this,"没有设置绑定，无法找回密码",Toast.LENGTH_SHORT).show();
									}
									
								}
							}
							
						});
						hideLoading();
					};
				}.start();
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
}
