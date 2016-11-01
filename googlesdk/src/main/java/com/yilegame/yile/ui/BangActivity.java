package com.yilegame.yile.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class BangActivity extends BaseActivity{
	private AccountEditText phoneNumber;
	private EditText signCode;
	private TextView getCode;
	private TextView bPhone;
	private TextView bCancel;
	private String phoneBackId;
	private ImageButton close;
	public static final int BANG_PHONE_SENDSMS=0;
	public static final int BANG_PHONE_BINDPHONE=1;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) 
		{
			Bundle bundle=msg.getData();
			String toastInfo="";
			int code=bundle.getInt("code");
			switch (msg.what) {
			case BANG_PHONE_SENDSMS:
				if(code==0)
				{
					phoneBackId=bundle.getString("id");
					toastInfo="短信发送成功！";
				}else if(code==-1)
				{
					toastInfo="发送异常请您重新发送!";					
				}else if(code==-2)
				{
					toastInfo="验证失败,请重新发送!";			
				}else if(code==-15)
				{
					toastInfo="用户已经绑定手机";	
				}else if(code==-12)
				{
					toastInfo="用户不存在";				
				}else if(code==-16)
				{
					toastInfo="短信发送失败,请稍后重新发送!";				
				}else if(code==-19)
				{
					toastInfo="操作过于频繁";
				}
				break;

			case BANG_PHONE_BINDPHONE:
				if(code==0)
				{
					toastInfo="绑定成功！";
					finish();
				}else if(code==-1)
				{
					toastInfo="绑定异常请您重新绑定!";					
				}else if(code==-2)
				{
					toastInfo="验证失败,请重新绑定!";			
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
			Toast.makeText(BangActivity.this, toastInfo, Toast.LENGTH_SHORT).show();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(Utils.getLayoutId(getApplicationContext(), "activity_bangding"));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		phoneNumber=(AccountEditText)findViewById(Utils.getId(getApplicationContext(), "phone"));
		signCode=(EditText) findViewById(Utils.getId(getApplicationContext(), "sign"));
		getCode=(TextView) findViewById(Utils.getId(getApplicationContext(), "getCode"));
		bPhone= (TextView) findViewById(Utils.getId(getApplicationContext(), "bandphonesure"));
		bCancel=(TextView) findViewById(Utils.getId(getApplicationContext(), "bCancel"));
		close=(ImageButton)findViewById(Utils.getId(getApplicationContext(), "close"));
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BangActivity.this.finish();
			}
		});
		getCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(phoneNumber.getText().isEmpty()){
					Toast.makeText(BangActivity.this, "输入的手机号有误请检查！", Toast.LENGTH_SHORT).show();
					return;
				}
				if(isMobileNO(phoneNumber.getText()))
				{
					new Thread() {
						@Override
						public void run() {
							String phone=phoneNumber.getText();
							String sign=MD5.md5Encoder(UserInfos.userId+phone+UserInfos.key);
							YiLeHttpUtils.PostGetPhoneSignCode(handler,UserInfos.paw,UserInfos.userId, QYConstant.getInstance().getIp(BangActivity.this, "bindphonesendsms"), phone, sign);
						}

					}.start();

				}else{
					Toast.makeText(BangActivity.this, "输入的手机号有误请检查！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		bPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(signCode.getText().toString().isEmpty()){
					Toast.makeText(BangActivity.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(){
					public void run() 
					{

						String code=signCode.getText().toString();
						String sign=MD5.md5Encoder(phoneBackId+code+UserInfos.key);
						YiLeHttpUtils.PostBangPhone(handler,UserInfos.userId,UserInfos.userName, phoneBackId,QYConstant.getInstance().getIp(BangActivity.this, "bindphone"), code, sign);
					};
				}.start();

			}
		});
		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	//手机号码验证
	public boolean isMobileNO(String mobiles) {
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern
				.compile("^(1[0-9])\\d{9}$");
		Matcher m = p.matcher(mobiles);
        
		return m.matches();
	}

}
