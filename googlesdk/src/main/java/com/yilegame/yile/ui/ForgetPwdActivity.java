package com.yilegame.yile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yilegame.http.Impl.HttpCallBack;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.yile.wight.AccountEditText;


public class ForgetPwdActivity extends BaseActivity {
	private TextView tv_cancle;
	private AccountEditText aet_user;
	private AccountEditText aet_question;
	private AccountEditText aet_answer;
	private TextView tv_submit;
	private String checkQuestion;
	public static ForgetPwdActivity instance;
	private String account;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		instance=this;
		checkQuestion = getIntent().getExtras().getString("checkQuestion");
		account = getIntent().getExtras().getString("account");
		setContentView(Utils.getLayoutId(getApplicationContext(), "activity_forget_pwd"));
		aet_question = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "Aet_question"));
		aet_answer = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "Aet_answer"));
		tv_submit = (TextView) findViewById(Utils.getId(getApplicationContext(), "tv_submit"));
		aet_question.setTextCantChange(checkQuestion);
		tv_cancle = (TextView) findViewById(Utils.getId(getApplicationContext(), "tv_cancle"));
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
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				returnLoginUI();
			}
		});
		tv_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(aet_answer.getText())) {
					showErrorView("答案不能为空");
					return;
				}
				showLoading();
					new Thread(){
						@Override
						public void run() 
						{		
							YiLeHttpUtils.checkusersaferequest(instance, account, checkQuestion, aet_answer.getText(), new HttpCallBack() {
								
								@Override
								public void getData(Object o) {
									boolean b=(Boolean) o;
									if (b) {
										startActivity(new Intent(instance, ReSetPwdActivity.class));
										finish();
									}else{
										showErrorView("验证失败");
									}
									hideLoading();
								}
							});
						};
					}.start();
//				YiLeHttpUtils.checkSafeRequest(ForgetPwdActivity.this, checkQuestion, aet_answer.getText());
			}
		});
	}
}
