package com.yilegame.yile.engine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.CheckPayResult;
import com.yilegame.http.uti.RequestUrl;
import com.yilegame.http.uti.TalkingData;
import com.yilegame.yile.ui.BangActivity;
import com.yilegame.yile.ui.BaseActivity;
import com.yilegame.yile.ui.FindPassForPhoneActivity;
import com.yilegame.yile.ui.LoginActivity;
import com.yilegame.yile.ui.Utils;

import java.util.HashMap;

//import com.sharesdk.yilegame.ShowShare;

/** 
* xxxxxxxx 
* @(#) xxxxxxxxxxxx.java	1.0 
* Copyright (c) 2002 xxxx All Rights Reserved. 
* 
* 
* @version 1.0 (2015-3-4 10:15:42) 
* @author chenfeng 
* 
*/ 
public class OverSeasConductor extends BaseActivity{
	private static OverSeasConductor instance;

	private OverSeasConductor() {
	};

	public static synchronized OverSeasConductor getInstance() {
		if (instance == null) {
			instance = new OverSeasConductor();
			return instance;
		}
		return instance;
	}

	public static Activity mainClassName;
	public static Context mcon;
	public static String gameId;
	public static String channelId;
	public static Handler handler;
	public static boolean islandscape;
	public static HashMap<String, String> payParams = null;
	   private static WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();


	    public static WindowManager.LayoutParams getMywmParams() {
	        return wmParams;
	    }
	/**
	 * 游戏初始化
	 *
	 * @param Activity con  (上下文)
	 * @param Handler handler
	 * @param String gameId,
	 * @param String channelId
	 * @param String talkingdataId,
	 * @param Activity mainClassName
	 * @param boolean TestMode,		true表示测试模式
	 * @param boolean islandscape   true表示水平
	 */
	public void init(final Activity con, Handler handler, final String gameId,
			final String channelId, String talkingdataId,
			Activity mainClassName,  boolean TestMode,
			boolean islandscape) {
		this.mcon = con;
		// CrashHandler.getInstance().init(con);
		this.gameId = gameId;
		this.channelId = channelId;
		this.handler = handler;
		this.islandscape = islandscape;
		this.mainClassName = mainClassName;
		RequestUrl.getUrl(TestMode);
		OverSeasConductor.islandscape=islandscape;
		UiUtils.list.add(con);
		UserInfos.setUrl(TestMode);
		QYConstant.setTestMode(TestMode);
		QYSDKManager.getInstance().init(con, handler, gameId, channelId,
				talkingdataId, TestMode, islandscape);
		TalkingData.getInstance().init(con, talkingdataId, channelId);
		initPayPhoneCardInfo();// 加载手机卡充值信息
		doActivation(con);



//		floatWindowManager = FloatWindowManager.getInstance(con);
	}

	/*
	 * 加载手机卡充值信息
	 */
	private void initPayPhoneCardInfo() {
		new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 6; i++) {
					RequestService.Post2ServiceGetPayPhoneCard(handler, mcon,
							QYConstant.getInstance()
									.getIp(mcon, "url19payinfo"));
					if (QYConstant.Name != null && QYConstant.Name.size() > 0
							&& QYConstant.cardMoney != null
							&& QYConstant.cardMoney.size() > 0) {
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

	/**
	 * 游戏登陆
	 * 
	 * 
	 */
	public void login() {
		mcon.startActivity(new Intent(mcon, LoginActivity.class));
	}
	/**
	 * 游戏更新
	 * @param Context con
	 * @param Handler handler
	 * @param  String version
	 * 
	 */
	private AlertDialog alertDialog;
	public void bangAlert(final Activity activity,LayoutInflater Inflate)
	{
         View views=Inflate.inflate(Utils.getLayoutId(getApplicationContext(), "dialogtest"),null);
         AlertDialog.Builder ad=new AlertDialog.Builder(activity);
         View imageView= views.findViewById(Utils.getId(getApplicationContext(), "nowbangphone"));
         View cancel=views.findViewById(Utils.getId(getApplicationContext(), "yihou"));
         cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
         imageView.setOnClickListener(new View.OnClickListener() {
        	 
        	 @Override
        	 public void onClick(View arg0) {
        		
        		 alertDialog.cancel();
        		 activity.startActivity(new Intent(activity, BangActivity.class));
        	 }
         });
         ad.setView(views);
         alertDialog=ad.create();
         alertDialog.show();
	}
	public void update(final Context con, Handler handler, final String version) {
		QYSDKManager.getInstance().doUpdate(con, handler, version);
	}

	/**
	 *游戏激活
	 * @param Activity activity
	 *
	 */
	public void doActivation(Activity activity) {
		QYSDKManager.getInstance().doGameServerActivation(activity);
	}
	/**
	 *OnResume
	 * @param Activity activity
	 *
	 */
	public void OnResume(Activity activity){
//		showFloatView(activity);
		TalkingData.getInstance().onResume(activity);
	}
	/**
	 *OnStop
	 * @param Activity activity
	 *
	 */
	public void OnStop(Activity activity){
		TalkingData.getInstance().onPause(activity);
//		closeFloatView();
	}
	public void onShare(Activity activity,String title,String titleUrl,String shareText,String imagePath,String Url,String site,String siteUrl)
	{
		//ShareShow.getInstance().doShare(activity,title,titleUrl,shareText,imagePath,Url,site,siteUrl);
	}
	public void showFindPassForPhone() 
	{
		mcon.startActivity(new Intent(mcon, FindPassForPhoneActivity.class));
	}
	public void doCheckPayOrder(String orderId)
	{
		CheckPayResult.getPayResult(orderId, handler);
	}
}