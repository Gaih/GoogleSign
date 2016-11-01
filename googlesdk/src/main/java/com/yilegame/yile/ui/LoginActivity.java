package com.yilegame.yile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yilegame.http.Impl.DeviceCallBack;
import com.yilegame.http.Mlog;
import com.yilegame.http.QYCode;
import com.yilegame.http.bean.AccountDatas;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.AES;
import com.yilegame.http.uti.Base64;
import com.yilegame.yile.engine.HttpsConductor;
import com.yilegame.yile.engine.OverSeasConductor;
import com.yilegame.yile.wight.AccountEditText;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginActivity extends BaseActivity {
			private CheckBox isshow_pwd;
			private AccountEditText att_user;
			private AccountEditText att_pwd;
			private TextView register;
			private TextView login;
			private TextView forget_pwd;
			private ArrayList<String> daoFirst;
			private String user;
			public static LoginActivity instance;
			private PopupWindow pop;
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(Utils.getLayoutId(getApplicationContext(), "activity_login"));
				UIrequest = getIntent().getStringExtra("request");
				mCon=this;
				instance=this;
				initView();
				initData();
			}

			private void initView() {
					att_user = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "att_user"));
					att_pwd = (AccountEditText) findViewById(Utils.getId(getApplicationContext(), "att_pwd"));
					register = (TextView) findViewById(Utils.getId(getApplicationContext(), "register"));
					login = (TextView) findViewById(Utils.getId(getApplicationContext(), "login"));
					forget_pwd = (TextView) findViewById(Utils.getId(getApplicationContext(), "forget_pwd"));
					close = (TextView) findViewById(Utils.getId(getApplicationContext(), "close"));
					isshow_pwd = (CheckBox) findViewById(Utils.getId(getApplicationContext(), "isshow_pwd"));
					att_user.setOnArrowDown(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							initPopup();
						}
					});
					register.setOnClickListener(this);
					login.setOnClickListener(this);
					forget_pwd.setOnClickListener(this);
//					close.setOnClickListener(this);
					isshow_pwd.setOnClickListener(this);
			}
			private void initData() {
//				att_user.setText(account);
				 daoFirst = dao.getFirst();
				if (daoFirst == null || daoFirst.size() < 1) {
					//从服务器获取历史账号列表
					getAccountList();
				} else {
					//填充最近一次登陆的账号密码
					showLastSuccessLoginUserPwd(daoFirst);
				}
			}

			@Override
			public void onClick(View view) {
				int id = view.getId();
				 if(id==Utils.getId(getApplicationContext(), "register")){
					 finish();
					 startActivity(new Intent(this, RegisterActivity.class));	
				}else if(id==Utils.getId(getApplicationContext(), "login")){
					login();
				}else if(id==Utils.getId(getApplicationContext(), "forget_pwd")){
				    startActivity(new Intent(this, FindPwdActivity.class));	
					finish();
				}
//				else if(id==Utils.getId(getApplicationContext(), "close")){
//					OverSeasConductor.handler.sendEmptyMessage(QYCode.QYCODE_LOGIN_CANCEL);
//					finish();
//				}
				else if(id==Utils.getId(getApplicationContext(), "isshow_pwd")){
					if (isshow_pwd.isChecked()) {
						att_pwd.showPwd();
					} else {
						att_pwd.hidePwd();
					}
				}
			}

			private void login() {
				String account = att_user.getText();
				String pwd = att_pwd.getText();
				if (checkEditText(att_user, att_pwd,null,2)) {
					HttpsConductor.getInstance().login(account, pwd, this,false,UserInfos.loginUrl);
				}
			}
			private void initPopup() {
//				ListView listView = new ListView(getApplicationContext());
//				listView.setCacheColorHint(0x00000000);
//				// listView.setDivider(null);// 分割线
//				listView.setVerticalScrollBarEnabled(false);
//
//				adapter = new MyAdapter();
//				listView.setAdapter(adapter);
//
//				pop = new PopupWindow(listView, mUserName.getWidth(),
//						LayoutParams.WRAP_CONTENT, true);
//				pop.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.qy_pull_down_bg));
//				pop.showAsDropDown(mUserName, 0, 0);
				
				ListView listView = new ListView(getApplicationContext());
				listView.setCacheColorHint(0x00000000);
				listView.setVerticalScrollBarEnabled(false);

				MyAdapter adapter = new MyAdapter();
				listView.setAdapter(adapter);

				pop = new PopupWindow(listView, att_user.getWidth(),
						LayoutParams.WRAP_CONTENT, true);
				pop.setBackgroundDrawable(getResources().getDrawable(
						Utils.getDrawableId(getApplicationContext(), "pop_bg")));
				 listView.setDivider(null);// 分割线
				pop.showAsDropDown(att_user, 0, 0);
			}
			public void showLastSuccessLoginUserPwd(ArrayList<String> first) {
				String password;
				Mlog.i("feng", "" + first);
				user = first.get(0);
				password = first.get(1);
				Mlog.i("加密", "password:" + password);
				att_user.setText(user);
				if ("".equals(password)) {
					return;
				}
				byte[] decode = null;
				try {
					decode = AES.aesDecryptWithKey(UserInfos.AESkey,
							Base64.decode(password), 0);
					Mlog.i("加密", "decode:" + decode);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
				if (decode != null) {
					try {
						if (isSetPwdRequest(UIrequest)) {
							att_pwd.setText("");
						} else {
							att_pwd.setText(new String(decode, "utf-8"));
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			private boolean isSetPwdRequest(String UIrequest) {
				if ("resetok".equals(UIrequest)) {
					return true;
				}
				return false;
			}
			private class MyAdapter extends BaseAdapter {
				private List<AccountDatas> list;
				private int pos;
				private String adpterUser;

				MyAdapter() {
					list = dao.findAll();
				}

				public int getCount() {
					if (list.size() > 0 && list != null) {
						if (list.size() >= 5) {
							return 5;
						}
						return list.size();
					}
					return 1;
				}

				public long getItemId(int position) {
					return position;
				}

				public View getView(final int pos, View convertView, ViewGroup parent) {
					View view = View.inflate(mCon, Utils.getLayoutId(getApplicationContext(), "pop_item"), null);
					if (list.size() == 0) {
						return view;
					}
					TextView tv_name = (TextView) view.findViewById(Utils.getId(getApplicationContext(), "qy_tv_name"));
					TextView tv_delete = (TextView) view
							.findViewById(Utils.getId(getApplicationContext(), "qy_tv_delete"));
					adpterUser = list.get(pos).getUser();
					tv_name.setText("" + adpterUser);

					tv_name.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							adpterUser = list.get(pos).getUser();
							att_user.setText("" + adpterUser);
							try {
							String queryPasswordByUser = dao
										.queryPasswordByUser(adpterUser);
								Mlog.i("MyAdapter", "queryPasswordByUser:"
										+ queryPasswordByUser);
								if (queryPasswordByUser == null
										|| queryPasswordByUser.equals("")) {
									att_pwd.setText("");
								} else {
									att_pwd.setText(""
											+ (new String(AES.aesDecryptWithKey(
													UserInfos.AESkey,
													Base64.decode(queryPasswordByUser),
													0), "utf-8")));
								}
								if (pop != null) {
									pop.dismiss();
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (InvalidKeyException e) {
								e.printStackTrace();
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (NoSuchPaddingException e) {
								e.printStackTrace();
							} catch (IllegalBlockSizeException e) {
								e.printStackTrace();
							} catch (BadPaddingException e) {
								e.printStackTrace();
							}
						}
					});

					tv_delete.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							list = dao.findAll();
							if (list.size() > 0) {
								dao.delete(list.get(pos).getUser());
								list.remove(pos);
							}
							notifyDataSetChanged();
						}
					});
					return view;
				}

				@Override
				public Object getItem(int arg0) {
					return null;
				}

			}	
			private void getAccountList() {
				HttpsConductor.getInstance().deviceAccount(mCon, new DeviceCallBack() {
					@Override
					public void getData(ArrayList<String> accountList) {
						BaseActivity.accountList=accountList;
						for (int i = accountList.size() - 1; i >= 0; i--) {
							dao.insertOrUpdate(accountList.get(i), "");
						}
						hideLoading();
						ArrayList<String> daoFirst = dao.getFirst();
						if (daoFirst == null) {
							return;
						}
//							UiUtils.listViewParams(lv, mcon, daoFirst);
//							lv.setAdapter(new AccountPullAdapter(dao, mcon,lv));
					}
				});
			}
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode==KeyEvent.KEYCODE_BACK) {
				OverSeasConductor.handler.sendEmptyMessage(QYCode.QYCODE_LOGIN_CANCEL);
			}	
			return super.onKeyDown(keyCode, event);
			}
}
