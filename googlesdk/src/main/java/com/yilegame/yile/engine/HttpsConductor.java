package com.yilegame.yile.engine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yilegame.http.Impl.DeviceCallBack;
import com.yilegame.http.Impl.OrderIdCallBack;
import com.yilegame.http.Impl.UIThreadCallBack;
import com.yilegame.http.Mlog;
import com.yilegame.http.QYCode;
import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.dao.AccountDao;
import com.yilegame.http.uti.AES;
import com.yilegame.http.uti.Base64;
import com.yilegame.http.uti.NetworkUtils;
import com.yilegame.http.uti.TalkingData;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.http.widget.MyProgressDialog;
import com.yilegame.yile.ui.BangActivity;
import com.yilegame.yile.ui.LoginActivity;
import com.yilegame.yile.ui.RegisterActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class HttpsConductor {
	protected static final int LOGIN_RETURN = 1;

	protected static final int REGISTER_RETURN = 2;

	protected static final int NET_WORK_ERROR = -111;

	protected static final int FINDPWD_RETURN = 3;
	private boolean isVisitor = false;
	private static HttpsConductor instance;
	private String paw;

	private HttpsConductor() {
	};

	public static synchronized HttpsConductor getInstance() {
		if (instance == null) {
			instance = new HttpsConductor();
			return instance;
		}
		return instance;
	}

	private AccountDao dao;
	private Handler handler = new Handler() {

		private boolean showPwdProtect = true;
		@Override
		public void handleMessage(Message msg) {
			Message message = new Message();
			switch (msg.what) {
			case LOGIN_RETURN:// 登陆返回消息
				Bundle b = new Bundle();
				b.putString("msg", loginMessage.get("msg"));
				int code = Integer.parseInt(loginMessage.get("code"));
				if (code == QYCode.QYCODE_LOGIN_SUCCESSFUL) {
					dao = new AccountDao(mcon);
					if (!isVisitor && !"".equals(pwd)) {
						saveUserPwd();
					}
					try {
						TalkingData.getInstance().setAccount(
								loginMessage.get("userid"), account);
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					UserInfos.userId = loginMessage.get("userid");
					UserInfos.paw=paw;
					// AppsFlyer.getInstance().setAppUserId(UserInfos.userId);
					b.putString("account", account);
					b.putString("userid", loginMessage.get("userid"));
					b.putString("sign", loginMessage.get("sign"));
					Mlog.i("feng",
							"returnInfo.get(userid):"
									+ loginMessage.get("userid"));
					message.what = QYCode.QYCODE_LOGIN_SUCCESSFUL;
					message.setData(b);
					OverSeasConductor.handler.sendMessage(message);
					if (showPwdProtect) {// 注册不弹出密保设置
//						int num_cishu = QYConstant.getPwdProtectedTips(mcon);
//						QYConstant.setPwdProtectedTips(mcon, num_cishu + 1);
//						if (!QYConstant.getPwdInfo(mcon)) {// 未勾选 下次不提示
//															// &&!YiLeHttpUtils.mHasMd5
//							if (UserInfos.hasSafe == 0) {// 账号未设置密保
//								if (num_cishu + 1 >= 6) {// 第3次登陆才提示
//									mcon.startActivity(new Intent(mcon,
//											SetPwdQuestionsActivity.class));
//								}
//							}
//						
//						}
						int phone_tip=QYConstant.getPwdPhoneProtectedTips(mcon);
						QYConstant.setPwdPhoneProtectedTips(mcon, phone_tip + 1);
						if(UserInfos.hasBindPhone==0){//判断是否绑定手机
							if(phone_tip+1>=6){      //第六次开始
								mcon.startActivity(new Intent(mcon,BangActivity.class));
							}
						}
					}
					showPwdProtect = true;
					// ((Activity)mcon).finish();
					if (RegisterActivity.instance != null) {
						RegisterActivity.instance.finish();
					}
					// if (FacebookLoginActivity.instance!=null) {
					// FacebookLoginActivity.instance.finish();
					// }
					if (LoginActivity.instance != null) {
						LoginActivity.instance.finish();
					}
				} else if (code == QYCode.QYCODE_LOGIN_DEVFREEZED) {
					Toast.makeText(mcon, "设备已冻结", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_DEVFREEZED;
					message.setData(b);
					OverSeasConductor.handler.sendMessage(message);
				} else if (code == QYCode.QYCODE_LOGIN_FREEZED) {
					Toast.makeText(mcon, "账户已冻结", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_FREEZED;
					message.setData(b);
					OverSeasConductor.handler.sendMessage(message);
				} else if (code == QYCode.QYCODE_LOGIN_NOTEXIST) {
					Toast.makeText(mcon, "用户不存在", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_NOTEXIST;
					message.setData(b);
					OverSeasConductor.handler.sendMessage(message);
				} else if (code == QYCode.QYCODE_LOGIN_PASSWOREDERR) {
					Toast.makeText(mcon, "账号或者密码错误", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_PASSWOREDERR;
					message.setData(b);
					OverSeasConductor.handler.sendMessage(message);
					// if (YileAutoLoginActivity.instance!=null) {
					// YileAutoLoginActivity.instance.finish();
					// }
					// if (isAuto) {
					// Intent intent=new Intent(mcon, YileLoginActivity.class);
					// intent.putExtra("account", account);
					// mcon.startActivity(intent);
					// }
				}
				hideLoading();
				break;
			case REGISTER_RETURN:// 注册返回消息
				Bundle bundle = new Bundle();
				bundle.putString("msg", registerMessage.get("msg"));
				int code2 = Integer.parseInt(registerMessage.get("code"));
				Mlog.i("feng", registerMessage.toString());
				if (code2 == QYCode.QYCODE_REGISTER_SUCCESS) {
					try {
						if(bitmap!=null&&userInfoPicName!=null){
							saveBitmap(bitmap, userInfoPicName);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Mlog.i("feng", "注册信息保存图片失败");
						e1.printStackTrace();
					}
					dao = new AccountDao(mcon);
					saveUserPwd();
					try {
						TalkingData.getInstance().setAccount(
								loginMessage.get("userid"), account);
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					message.what = QYCode.QYCODE_REGISTER_SUCCESS;
					message.setData(bundle);
					OverSeasConductor.handler.sendMessage(message);
					showPwdProtect = false;
					login(account, pwd, mcon, false, UserInfos.loginUrl);
				} else if (code2 == QYCode.QYCODE_LOGIN_DEVFREEZED) {
					Toast.makeText(mcon, "设备已冻结", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_DEVFREEZED;
					message.setData(bundle);
					OverSeasConductor.handler.sendMessage(message);
				} else if (code2 == QYCode.QYCODE_LOGIN_ALREADY_EXIST) {
					Toast.makeText(mcon, "用户名已存在", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_ALREADY_EXIST;
					message.setData(bundle);
					OverSeasConductor.handler.sendMessage(message);
				}
				hideLoading();
				break;
			case FINDPWD_RETURN:// 找回密码返回
				Bundle finPwdBundle = new Bundle();
				finPwdBundle.putString("msg", findPwdMessage.get("msg"));
				int finPwdCode = Integer.parseInt(findPwdMessage.get("code"));
				if (-1 == finPwdCode) {
					Toast.makeText(mcon, "用户不存在", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_LOGIN_NOTEXIST;
					message.setData(finPwdBundle);
					OverSeasConductor.handler.sendMessage(message);
				} else if (1 == finPwdCode) {
					Toast.makeText(mcon, "发送邮件成功", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_SEND_EMAIL_SUCCESS;
					message.setData(finPwdBundle);
					OverSeasConductor.handler.sendMessage(message);
				} else if (0 == finPwdCode) {
					Toast.makeText(mcon, "未绑定邮箱", Toast.LENGTH_SHORT).show();
					message.what = QYCode.QYCODE_UNBANDLE_EMAIL;
					message.setData(finPwdBundle);
					OverSeasConductor.handler.sendMessage(message);
				} else if (-2 == finPwdCode) {
					Mlog.i("feng", "sign error");
				}
				break;
			case NET_WORK_ERROR:
				timeOut();
				break;
			default:
				break;
			}
			hideLoading();
		}

	};
	private Map<String, String> loginMessage;
	public static Map<String, String> registerMessage;
	private Context mcon;
	private String account;
	private String pwd;
	private boolean isAuto;
	private Bitmap bitmap;
	private String userInfoPicName;

	// 登录
	public void login(final String account, final String pwd,
			final Context mcon, boolean isAuto, final String url) {
		this.account = account;
		this.mcon = mcon;
		this.pwd = pwd;
		this.isAuto = isAuto;
		showLoading("", mcon);
		new Thread() {
			@Override
			public void run() {
				httpLogin(account, pwd, mcon, url, "1");
			}

		}.start();
	}

	// 注册
	public void register(final String account, final String pwd,
			final Context mcon,Bitmap bitmap,String PicName) {
		netWorkStatus(mcon);
		this.account = account;
		this.mcon = mcon;
		this.pwd = pwd;
		this.bitmap=bitmap;
		this.userInfoPicName=PicName;
		showLoading("", mcon);
		new Thread() {
			@Override
			public void run() {
				httpRegister(account, pwd, mcon);
			}

		}.start();
	}

	// 快速注册
	public void fastRegister(final String account, final String pwd,
			final Context mcon, final Handler handler) {
		netWorkStatus(mcon);
		this.account = account;
		this.mcon = mcon;
		this.pwd = pwd;
		showLoading("", mcon);
		new Thread() {
			@Override
			public void run() {
				httpFastRegister(account, pwd, mcon, handler);
			}

		}.start();
	}

	// 游客登录
	private Map<String, String> visitorsMessage;

	public void visitors(final Context mcon) {
		netWorkStatus(mcon);
		showLoading("", mcon);
		new Thread() {

			@Override
			public void run() {
				visitorsMessage = YiLeHttpUtils.Post2Service(null, null, mcon,
						UserInfos.visitorUrl, "fastRegister",
						OverSeasConductor.gameId, OverSeasConductor.channelId,
						"5");
				if (visitorsMessage != null && visitorsMessage.size() > 0) {
					Mlog.i("feng",
							"visitorsMessage:" + visitorsMessage.toString()
									+ visitorsMessage);
					String visitorCode = visitorsMessage.get("code");
					Mlog.i("feng", "visitorCode:" + visitorCode);
					if (visitorCode.equals("1100")) {
						HttpsConductor.this.account = visitorsMessage
								.get("account");
						HttpsConductor.this.mcon = mcon;
						HttpsConductor.this.pwd = visitorsMessage
								.get("password");
						isVisitor = true;
						httpLogin(visitorsMessage.get("account"),
								visitorsMessage.get("password"), mcon,
								UserInfos.loginUrl, "5");
					}
				} else {
					handler.sendEmptyMessage(NET_WORK_ERROR);
				}
			}
		}.start();
	}

	// 找回密码
	private Map<String, String> findPwdMessage;

	public void findPwd(final String account, final Context mcon,
			final String url) {
		this.account = account;
		this.mcon = mcon;
		showLoading("", mcon);
		new Thread() {

			@Override
			public void run() {
				findPwdMessage = YiLeHttpUtils.Post2Service(account, mcon, url);
				if (findPwdMessage != null && findPwdMessage.size() > 0) {
					handler.sendEmptyMessage(FINDPWD_RETURN);
				}
			}

		}.start();
	}

	// 上传异常
	public void exceptionInfoUpLoad(final Context mcon, final String url,
			final String stack, final Map<String, String> infos) {
		this.mcon = mcon;
		new Thread() {

			@Override
			public void run() {
				// Looper.prepare();
				Mlog.i("feng", "exit1.....");
				YiLeHttpUtils.Post2Service4ExceptionInfo(mcon, url,
						OverSeasConductor.gameId, OverSeasConductor.channelId,
						stack, infos);
				// Looper.loop();
				Mlog.i("feng", "exit2.....");
				UiUtils.changeUITHread(new UIThreadCallBack() {

					@Override
					public void change2UI() {
					}
				}, mcon);
			}

		}.start();
	}

	// 平台下单 TODO type
	public void sendOrderId(final Context mcon, final String url,
			final float money, final String type, final String productName,
			final String currency, final String serverCode,
			final String extraData, final String roleId,
			final OrderIdCallBack callback) {
		this.mcon = mcon;
		new Thread() {

			@Override
			public void run() {
				final String orderId = YiLeHttpUtils.Post2ServiceCharge(mcon,
						UserInfos.userId, url, money, type, productName,
						currency, OverSeasConductor.gameId,
						OverSeasConductor.channelId, serverCode, extraData,
						roleId);
				UiUtils.changeUITHread(new UIThreadCallBack() {

					@Override
					public void change2UI() {
						if (!"".equals(orderId) && orderId != null) {
						} else {
							OverSeasConductor.handler
									.sendEmptyMessage(QYCode.QYCODE_DO_ORDERID_FAIL);
							hideLoading();
							return;
						}
						callback.getData(orderId);
					}
				}, mcon);
			}
		}.start();
	}

	private ArrayList<String> accountList;

	public void deviceAccount(final Context mcon, final DeviceCallBack callback) {
		showLoading("", mcon);
		new Thread() {
			@Override
			public void run() {
				accountList = (ArrayList<String>) YiLeHttpUtils
						.Post2ServiceDeviceLogin(OverSeasConductor.handler,
								mcon, UserInfos.deviceAccountUrl);
				Mlog.i("feng", "accountList:" + accountList + "/size:");
				if (accountList != null && accountList.size() > 0) {
					UiUtils.changeUITHread(new UIThreadCallBack() {

						@Override
						public void change2UI() {
							callback.getData(accountList);
						}
					}, mcon);
				} else {
					handler.sendEmptyMessage(NET_WORK_ERROR);
				}
			};
		}.start();
	}

	private void saveUserPwd() {
		// 登入成功，下次登入时保存用户名密码
		byte[] pwd = null;
		try {
			pwd = Base64.encode(AES.aesEncryptWithKey(UserInfos.AESkey,
					this.pwd.getBytes(), 0));
			if (pwd != null) {
				dao.insertOrUpdate(account, new String(pwd, "utf-8"));
			}
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
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void httpLogin(final String account, final String pwd,
			final Context mcon, String url, String loginType) {

		loginMessage = YiLeHttpUtils.Post2Service(account, pwd, mcon, url, "",
				OverSeasConductor.gameId, OverSeasConductor.channelId,
				loginType);
		if (loginMessage != null && loginMessage.size() > 0) {
			handler.sendEmptyMessage(LOGIN_RETURN);
			this.paw=pwd;
		} else {
			handler.sendEmptyMessage(NET_WORK_ERROR);
		}
		hideLoading();
	}

	private void httpRegister(final String account, final String pwd,
			final Context mcon) {
		registerMessage = YiLeHttpUtils.Post2Service(account, pwd, mcon,
				UserInfos.registerUrl, "normalRegister",
				OverSeasConductor.gameId, OverSeasConductor.channelId, "1");
		if (registerMessage != null && registerMessage.size() > 0) {
			handler.sendEmptyMessage(REGISTER_RETURN);
		} else {
			handler.sendEmptyMessage(NET_WORK_ERROR);
		}
	}

	private void httpFastRegister(final String account, final String pwd,
			final Context mcon, final Handler handler) {
		registerMessage = YiLeHttpUtils.Post2ServiceFast(account, pwd, mcon,
				UserInfos.fastRegisterUrl, OverSeasConductor.gameId,
				OverSeasConductor.channelId);
		if (registerMessage != null && registerMessage.size() > 0) {
			this.handler.sendEmptyMessage(REGISTER_RETURN);
			handler.sendEmptyMessage(0);
		} else {
			this.handler.sendEmptyMessage(NET_WORK_ERROR);
			handler.sendEmptyMessage(-1);
		}
		hideLoading();
	}

	MyProgressDialog progressDialog;

	private Message timeOutMessage;

	public void showLoading(String tips, Context mcon) {
		try {
			progressDialog = new MyProgressDialog(mcon, tips);
			progressDialog.setMessage(tips);
			// progressDialog.setCancelable(false);
			progressDialog.show();
		} catch (Exception e) {
		}
	}

	public void hideLoading() {
		try {
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
				progressDialog = null;
			}
		} catch (Exception e) {
		}
	}

	private void netWorkStatus(final Context mcon) {
		if (!NetworkUtils.isNetworkAvailable(mcon)) {
			Mlog.i("feng", "netWorkStatus");
			timeOut();
			return;
		}
	}

	private void timeOut() {
		timeOutMessage = new Message();
		timeOutMessage.what = QYCode.QYCODE_TIMEOUT;
		OverSeasConductor.handler.sendMessage(timeOutMessage);
		Mlog.i("feng", "timeOut");
	};
	public void saveBitmap(Bitmap bitmap, String bitName) throws IOException {
		File folder = Environment.getExternalStorageDirectory();
		Log.i("test", folder.toString());
 		if (!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(Environment.getExternalStorageDirectory().toString() +"/"+ bitName + ".jpg");
		Mlog.i("feng", "注册成功保存图片中");
		FileOutputStream out;
		if (!file.exists()) {

			try {
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
					Mlog.i("feng", "用户注册信息以图片形式存入sdk");
					out.flush();
					out.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
