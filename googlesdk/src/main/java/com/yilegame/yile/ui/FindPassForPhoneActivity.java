package com.yilegame.yile.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.MD5;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.yile.wight.AccountEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class FindPassForPhoneActivity extends BaseActivity{
	private AccountEditText phone;
	private EditText signCode;
	private View getCode;
	private TextView doSure;
	private TextView cancel;
	private AccountEditText userName;
	private String phoneBackCode;
	private String findPassForToken;
	private ImageButton close;
	public static final int FINDPASS_FORPHONE_GETCODE=0;
	public static final int FINDPASS_FORPHONE_SURE=1;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) 
		{
			String toastInfo ="";
			Bundle bundle=msg.getData();
			int code=bundle.getInt("code");
			switch (msg.what) {
			case FINDPASS_FORPHONE_GETCODE:
				if(code==0)
				{
					toastInfo="短信发送成功！";
					phoneBackCode=bundle.getString("id");
				}else if(code==-1)
				{
					toastInfo="系统异常请重新获取！";
				}else if(code==-2){
					toastInfo="验证失败，请重新获取！";
				}else if(code==-23){
					toastInfo="手机号失败!";
				}else if(code==-12){
					toastInfo="用户不存在！";
				}else if(code==-16){
					toastInfo="验证短信发送失败！";
				}else if(code==-19){
					toastInfo="操作过于频繁";
				}
				break;
			case FINDPASS_FORPHONE_SURE:
				if(code==0)
				{
					toastInfo="验证码验证成功！";
					Intent intent=new Intent(FindPassForPhoneActivity.this,ReSetPwdActivity.class);
					intent.putExtra("account", userName.getText().toString());
					intent.putExtra("token", bundle.getString("token"));
					startActivity(intent);
					finish();

				}else if(code==-1)
				{
					toastInfo="验证码失败!";					
				}else if(code==-2)
				{
					toastInfo="验证失败!";			
				}else if(code==-17)
				{
					toastInfo="id过期";	
				}else if(code==-20)
				{
					toastInfo="用户id不匹配";				
				}else if(code==-18)
				{
					toastInfo="验证码不正确!";				
				}else if(code==-15)
				{
					toastInfo="用户已经绑定手机";
				}else if(code==-19)
				{
					toastInfo="用户没有手机绑定";
				}
				break;
			}
			Toast.makeText(FindPassForPhoneActivity.this, toastInfo, Toast.LENGTH_SHORT).show();	

		}

	};
	private LinearLayout back_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(Utils.getLayoutId(getApplicationContext(), "acticity_phonefindpass"));
		phone=(AccountEditText) findViewById(Utils.getId(getApplicationContext(), "findpassphone"));
		signCode=(EditText) findViewById(Utils.getId(getApplicationContext(), "findpasssign"));
		getCode=findViewById(Utils.getId(getApplicationContext(), "findpass_getcode"));
		doSure=(TextView) findViewById(Utils.getId(getApplicationContext(), "dofindpass"));
		cancel=(TextView) findViewById(Utils.getId(getApplicationContext(), "findpasscancel"));
		userName=(AccountEditText)findViewById(Utils.getId(getApplicationContext(), "finpawphoneaccount"));
		back_login=(LinearLayout)findViewById(Utils.getId(getApplicationContext(), "back_login"));
		close=(ImageButton)findViewById(Utils.getId(getApplicationContext(), "close"));
		userName.setText(""+getIntent().getStringExtra("account"));
		back_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				Intent intent=new Intent(FindPassForPhoneActivity.this,LoginActivity.class);
				startActivity(intent);
				FindPassForPhoneActivity.this.finish();
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				FindPassForPhoneActivity.this.finish();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FindPassForPhoneActivity.this.finish();
			}
		});
		getCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String account=userName.getText();
				final String phoneNum=phone.getText();
				if(phoneNum.isEmpty()){
					Toast.makeText(FindPassForPhoneActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				if(!isMobileNO(phoneNum)){
					Toast.makeText(FindPassForPhoneActivity.this, "输入的手机号有误请检查！", Toast.LENGTH_SHORT).show();
					return;					
				}
				new Thread(){
					public void run()
					{
						String sign=MD5.md5Encoder(account+phoneNum+UserInfos.key);
						YiLeHttpUtils.PostFindPassGetCodeForPhone(handler,account,QYConstant.getInstance().getIp(FindPassForPhoneActivity.this, "findpassforphonegetcode"), phoneNum, sign);
					};
				}.start();
			}
		});
		doSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String code=signCode.getText().toString();
				final String account=userName.getText();

				if(code.isEmpty()){
					Toast.makeText(FindPassForPhoneActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				new  Thread(){
					public void run() 
					{
						String sign=MD5.md5Encoder(phoneBackCode+code+UserInfos.key);
						YiLeHttpUtils.PostFindPassDoSure(handler,QYConstant.getInstance().getIp(FindPassForPhoneActivity.this, "bindphone"),account,phoneBackCode,code,sign);

					};
				}.start();
			}
		});
	}
	//手机号码验证
	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

}
