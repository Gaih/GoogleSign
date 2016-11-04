package com.yilegame.yile.ui;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yilegame.http.dao.AccountDao;
import com.yilegame.http.uti.EditTextShake;
import com.yilegame.http.uti.NetworkUtils;
import com.yilegame.yile.engine.HttpsConductor;
import com.yilegame.yile.engine.OverSeasConductor;
import com.yilegame.yile.wight.AccountEditText;

import java.util.ArrayList;


public class BaseActivity extends AppCompatActivity implements OnClickListener{
	protected AppCompatActivity mCon;
	protected String UIrequest = "";
//	protected EditText et_account;
//	protected TextView tv_account;
//	protected EditText et_pwd;
	protected static ArrayList<String> accountList;
	protected AccountDao dao;
	protected TextView close;
	protected TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dao=new AccountDao(this);
//		if (OverSeasConductor.islandscape)
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 强制为横屏
//		else
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
	}

//	public void showLastSuccessLoginUserPwd(ArrayList<String> first,EditText et_account,TextView tv_account,EditText et_pwd,String UIrequest) {
//		Mlog.i("feng", "" + first);
//		String user = first.get(0);
//		String password = first.get(1);
//		Mlog.i("加密", "password:" + password);
//		if (et_account==null) {
//			this.tv_account=tv_account;
//			tv_account.setText(user);
//		}else{
//			this.et_account=et_account;
//			et_account.setText(user);
//		}
//		this.et_pwd=et_pwd;
//		this.UIrequest=UIrequest;
//		if ("".equals(password)) {
//			return;
//		}
//		byte[] decode = null;
//		try {
//			decode = AES.aesDecryptWithKey(UserInfos.AESkey,
//					Base64.decode(password), 0);
//			Mlog.i("加密", "decode:" + decode);
//		} catch (InvalidKeyException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
//		} catch (BadPaddingException e) {
//			e.printStackTrace();
//		}
//		if (decode != null&&et_pwd!=null) {
//			try {
//				if (isSetPwdRequest(UIrequest)) {
//					et_pwd.setText("");
//				} else {
//					et_pwd.setText(new String(decode, "utf-8"));
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	private boolean isSetPwdRequest(String UIrequest) {
		if ("resetok".equals(UIrequest)) {
			return true;
		}
		return false;
	}

	protected void showLoading(String tips){
		HttpsConductor.getInstance().showLoading(tips, this);
	}
	protected void showLoading(String tips,Context con){
		HttpsConductor.getInstance().showLoading(tips, con);
	}
	protected void showLoading(){
		HttpsConductor.getInstance().showLoading("", this);
	}
	
	
	 @Override
	protected void onResume() {
//		if (OverSeasConductor.islandscape)
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 强制为横屏
//		else
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		super.onResume();
		
	}
	
	protected void hideLoading(){ 
		HttpsConductor.getInstance().hideLoading();
	}
	protected void returnLoginUI(){ 
		startActivity(new Intent(this, LoginActivity.class));
	}
	protected void returnLoginUI(String params){ 
		Intent intent=new Intent(this, LoginActivity.class);
		intent.putExtra("request", params);
		startActivity(intent);
	}
	/**
	 * 登入或注册信息有误时tips
	 * 
	 * @param
	 * 
	 */

	public static boolean checkEditText(AccountEditText et_username,
			AccountEditText et_password, AccountEditText et_repassword,int type) {
		String username = et_username.getText();
		String password = et_password.getText();
		if (TextUtils.isEmpty(username)) {
			showErrorView("账号不能为空");
			EditTextShake.startAnnmation(OverSeasConductor.mcon, et_username);
			return false;
		}
		if (TextUtils.isEmpty(password)) {
			showErrorView("密码不能为空");
			EditTextShake.startAnnmation(OverSeasConductor.mcon, et_password);
			return false;
		}
		if (username.length() < 6&&type==1) {
			showErrorView("请输入至少6位账号");
			EditTextShake.startAnnmation(OverSeasConductor.mcon, et_username);
			return false;
		} else if (password.length() < 6&&type==1) {
			showErrorView("请输入至少6位密码");
			EditTextShake.startAnnmation(OverSeasConductor.mcon, et_password);
			return false;
		}
		if (et_repassword != null) {
			String repassword = et_password.getText();
			if (!password.equals(repassword)) {
				showErrorView("两次密码必须相同");
				EditTextShake.startAnnmation(OverSeasConductor.mcon, et_password);
				return false;
			}
		}
		if (!NetworkUtils.isNetworkAvailable(OverSeasConductor.mcon)) {
			// Toast.makeText(this, "网络连接不可用，请稍后再试", 1).show();
			showErrorView("网络连接异常");
			return false;
		}
		return true;
	}
	
	public static void showErrorView(String msg){
		Toast.makeText(OverSeasConductor.mcon, msg, Toast.LENGTH_LONG).show();
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
