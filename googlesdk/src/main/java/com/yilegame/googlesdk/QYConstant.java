package com.yilegame.googlesdk;

public class QYConstant {
	//sdk的版本号
	public static String sdkVersion = "37sdk_release_v2.1.2_20151117";
    public static String mybackkey="qinGyou.yiLe2014_08_11#$";
	public static final int QYCODE_REQUEST_TIMEOUT = 1;

	public static final String QYCODE_REQUEST_TIMEOUT_MSG = "请求超时，请稍后重试" ;
	public static final String APP_NAME = "谷歌支付";

	public static String ip="";
	public static String loginUrl="";
	public static String payUrl= "";
	public static String updateUrl="";
	public static String notifyUrl="";
	public static String activateUrl="";
	public  static String googlepayUrl="";
	// 登陆平台成功以后返回的参数
	public static String userId="";
	public static String account ="";
	public static String sign = "";


	private static final String TEST_URL = "http://47.88.198.17:10075";
	private static final String PUBLISH_URL = "http://lfserver.gurumcompany.com";

	////服务器的测试模式，true测试ip，false外网的ip
	public static void setTestMode(boolean testMode){
		ip=testMode? TEST_URL:PUBLISH_URL;

		payUrl=ip+"/platform/sdk/channelorder.action";
		activateUrl =ip+"/platform/sdk/activating.action";
		googlepayUrl=ip+"/platform/google/googleconsumernotify.action";

		loginUrl =ip+"/platform/google/googlelogin.action";
	}

}
