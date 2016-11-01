package com.yilegame.yile.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tendcloud.tenddata.TDGAAccount;
import com.yilegame.http.Mlog;
import com.yilegame.http.QYCode;
import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.TalkingData;

import java.util.Map;

public class QYSDKManager {

	private Map<String, String> resultMap;
	private Map<String, String> parseJson;
	private Map<String, String> result;
	private String code;
	private String msg;
	private SharedPreferences sp;
	private Editor edit;
	private   Context mcon;
	public static  Handler handler;
	private Intent i;
	private  static String gameId;
	private static String channelId;
	private static QYSDKManager instance = null;
	private QYSDKManager() {

	}

	public static synchronized QYSDKManager getInstance() {
		if (instance == null) {
			instance = new QYSDKManager();
		}
		return instance;
	}

	private Map<String, String> map;
	public void init(final Context con, Handler handler, final String gameId,
			final String channelId,String talkingdataId,boolean TestMode,boolean islandscape) {
//		 con.getSharedPreferences("login", con.MODE_PRIVATE).edit().putBoolean("TestMode", TestMode);
//		 con.getSharedPreferences("login", con.MODE_PRIVATE).edit().commit();
		this.mcon=con;
		this.gameId = gameId;
		this.channelId = channelId;
		this.handler = handler;
		setScreenOrientationLandscape(con, islandscape,TestMode);
		TalkingData.getInstance().init(con, talkingdataId,
				channelId);
		initPayPhoneCardInfo();//加载手机卡充值信息
	}
	/*
	 *加载手机卡充值信息 
	 *
	 *
	 */
	private void initPayPhoneCardInfo() {
			new Thread(){
				@Override
				public void run() {
					for (int i = 0; i < 6; i++) {
					RequestService.Post2ServiceGetPayPhoneCard(handler, mcon, QYConstant.getInstance().getIp(mcon, "url19payinfo"));
					if (QYConstant.Name!=null&& QYConstant.Name.size()>0&&QYConstant.cardMoney!=null&&QYConstant.cardMoney.size()>0) {
							break;	
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					}
				};
			}.start();
	}

//	/**
//	 * 显示登入界面
//	 * 
//	 * @param
//	 * @return
//	 * */
//	public void doLogin(Context con) {
//		i = new Intent(con, DologinActivity.class);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		con.startActivity(i);
//	}

//	/**
//	 * 登入
//	 * 
//	 * @param
//	 * @return
//	 * */
//	public Map<String, String> Login(String name, String password,
//			Context mcon, String url) {
//		return RequestService.Post2Service(name, password, mcon, url, null,gameId,channelId);
//	}

		
		/**
		 *  扫尾工作
		 *
		 *
		 */
		public void doDestroy(){
			UserInfos.userId="";
			
		};
		/**
		 *  游戏激活
		 *
		 *
		 */
		private boolean activationResult;
		public void doGameServerActivation(final Context mcon){
			new Thread(){

				@Override
				public void run() {
					for (int i = 0; i <3; i++) {
						activationResult = RequestService.Post2Service( mcon, handler, QYConstant.getInstance().getIp(mcon, "activating"),gameId,channelId);
						if (activationResult) {
							 handler.sendEmptyMessage(QYCode.QYCODE_ACTIVATION_SUCCESSFUL);
							break;
						}
						if (i==2&&!activationResult) {
							 handler.sendEmptyMessage(QYCode.QYCODE_ACTIVATION_FAIL);
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
		};
	/**
	 * 游戏更新
	 * 
	 * @param
	 * @return 需更新返回url 无更新返回“0” 更新异常返回"-1"
	 * */
	String returnData = "0";

	public String doUpdate(final Context con, Handler handler,
			final String version) {
		new AsyncTask<Void, Void, Void>() {

			private Map<String, String> map;

			@Override
			protected Void doInBackground(Void... params) {
				map = RequestService.Post2Service(gameId, channelId, con,UserInfos.upDateUrl
						+ "?gameId=" + gameId + "&" + "channelId=" + channelId
						+ "&version=" + version, "update",gameId,channelId);
				return null;
			}

			protected void onPostExecute(Void result) {
				if (map != null && map.size() > 0) {
					if ("1600".equals(map.get("code"))) {
						Mlog.e("feng", "map.get('url')1" + map.get("url"));
						Message msg = new Message();
						Bundle b = new Bundle();
						b.putString("url", map.get("url"));
						msg.setData(b);
						msg.what = QYCode.QYCODE_UPDATE_SUCCESSFUL;
						QYSDKManager.handler.sendMessage(msg);
					} else if ("1601".equals(map.get("code"))) {
						sendMsg2Game(QYCode.QYCODE_UPDATE_EXCEPTION);
						returnData = "-1";
					} else if ("1602".equals(map.get("code"))) {
						sendMsg2Game(QYCode.QYCODE_UPDATE_NOUPDATE);
						returnData = "0";
					}
				}
			};

		}.execute();
		return returnData;
	}

	/**
	 * 设置横竖屏幕
	 * 
	 * @param
	 * @return
	 * */
	private void setScreenOrientationLandscape(Context mcon, boolean isLandscape,boolean TestMode) {
		sp = mcon.getSharedPreferences("login", mcon.MODE_PRIVATE);
		edit = sp.edit();
		edit.putBoolean("isLandscape", isLandscape);
		edit.putBoolean("TestMode", TestMode);
		edit.commit();
	}


//	public void showToast(String message,int duration){
//		ErrorToast.showErrorToast(message,Toast.LENGTH_LONG,(Activity) mcon);
//	}

	public static void sendMsg2Game(int QYCode) {
		Message msg = new Message();
		msg.what = QYCode;
		QYSDKManager.handler.sendMessage(msg);
	}
	public void onResume(Activity page){
		TalkingData.getInstance().onResume(page);
	}
	public void onPause(Activity page){
		TalkingData.getInstance().onPause(page);
	}
	public void setAccount(String userId,String account)
	{
		TalkingData.getInstance().setAccount(userId, account);
	}
	public void setLevel (int level)
	{
		TalkingData.getInstance().setLevel(level);
	}
	public void setGender (TDGAAccount.Gender gender)
	{
		TalkingData.getInstance().setGender(gender);
	}
	public void setAge (int age)
	{
		TalkingData.getInstance().setAge(age);
	}
	public void setGameServer(String gameServer) 
	{
		TalkingData.getInstance().setGameServer(gameServer);
	}
	public void onReward (double virtualCurrencyAmount, String reason)
	{
		TalkingData.getInstance().onReward(virtualCurrencyAmount, reason);
	}
	public void onPurchase(String item, int itemNumber, double
			priceInVirtualCurrency)
	{
		TalkingData.getInstance().onPurchase(item, itemNumber, priceInVirtualCurrency);
	}
	
	public  void onUse(String item, int itemNumber)
	{
		TalkingData.getInstance().onUse(item, itemNumber);
	}
	
	public void onBegin(String missionId)
	{
		TalkingData.getInstance().onBegin(missionId);
	}
	
	public void onCompleted(String missionId)
	{
		TalkingData.getInstance().onCompleted(missionId);
	}
	
	public void onFailed(String missionId, String cause)
	{
		TalkingData.getInstance().onFailed(missionId, cause);
	}
	
	public void onEvent (String eventId, final Map<String, Object> eventData)
	{
		TalkingData.getInstance().onEvent(eventId, eventData);
	}
	
}
